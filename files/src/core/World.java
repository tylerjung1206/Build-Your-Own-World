package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import java.util.*;


public class World {

    private int targetFillSize;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private TERenderer r = new TERenderer();
    private TETile[][] world;
    private Set<roomNode> visited = new HashSet<>();
    private List<Edge> treeEdges = new ArrayList<>();
    private List<roomNode> rooms = new ArrayList<>();
    private Graph<roomNode> ourWorld = new Graph<>();
    private Random rand;


    public World(Random r) {
        rand = r;
        targetFillSize = (HEIGHT * WIDTH) / (2);
        world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }   
    }

    //added new constructor for loading specifically
    public World(TETile[][] loadedWorld) {
        this.world = loadedWorld;
        this.r = new TERenderer();
        r.initialize(WIDTH, HEIGHT);
    }


    public static int getWIDTH() {
        return WIDTH;
    }
    public static int getHEIGHT() {
        return HEIGHT;
    }

    public TERenderer getR() {
        return r;
    }

    public void generateWorld() {
        // Your seeds are:
        //4407533860057379002L
        //5047384665506568955L
        //322519479379984614L
        //5453889226179017116L
        //3814341371185290937L

        //make vertices
        while (targetFillSize > 0) {
            int w = rand.nextInt(6) + 3;
            int h = rand.nextInt(6) + 3;
            int x = rand.nextInt(WIDTH - 2* w - 1) + w + 1; // numbers added to serve as a border so no vertices are at the edge
            int y = rand.nextInt(HEIGHT - 2* h - 3) + h + 1; // changed for HUD
            int currSize = (w + 2) * (h + 2);

            roomNode currNode = new roomNode(x, y, w, h);

            boolean overlaps = false;
            for (roomNode existingRoom : rooms) {
                if (currNode.overlaps(existingRoom)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                rooms.add(currNode);
                ourWorld.addVertex(currNode); // Add to graph

                targetFillSize -= currSize;
            }
        }

        //connect all vertices to each other to get a fully connected graph
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                ourWorld.addEdge(rooms.get(i), rooms.get(j));
            }
        }

        //dfs(rooms.get(0));

        //room drawer
        for (roomNode node : rooms) {
            for (int x = node.x - node.w/2 - 1; x <=node.x + node.w/2 + 1; x++) {
                for (int y = node.y - node.h/2 - 1; y <=node.h/2 + node.y + 1; y++) {
                    if (x < WIDTH && y < HEIGHT && x>= 0 && y>=0) {
                        if (x == node.x - node.w/2 - 1 || y == node.y - node.h/2 - 1 || x == node.x + node.w/2 + 1 || y == node.h/2 + node.y + 1) {
                            if (world[x][y] != Tileset.FLOOR) {
                                world[x][y] = Tileset.WALL;
                            }
                        } else {
                            world[x][y] = Tileset.FLOOR;
                        }
                    }
                }
            }
        }

        Set<roomNode> inMST = new HashSet<>();
        Map<roomNode, Double> minDist = new HashMap<>();
        Map<roomNode, roomNode> parent  = new HashMap<>();

        for (roomNode r : rooms) {
            minDist.put(r, Double.POSITIVE_INFINITY);
        }
        roomNode start = rooms.get(0);
        minDist.put(start, 0.0);

        PriorityQueue<roomNode> pq = new PriorityQueue<>(Comparator.comparingDouble(minDist::get));
        pq.addAll(rooms);

        // build MST
        while (!pq.isEmpty()) {
            roomNode u = pq.poll();
            inMST.add(u);
            if (u != start) {
                treeEdges.add(new Edge(parent.get(u), u));
            }
            for (roomNode v : rooms) {
                if (inMST.contains(v)) continue;
                double dx = u.getCenterX() - v.getCenterX();
                double dy = u.getCenterY() - v.getCenterY();
                double w  = Math.hypot(dx, dy);
                if (w < minDist.get(v)) {
                    minDist.put(v, w);
                    parent .put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        //hallway drawer
        for( Edge e : treeEdges) {
            roomNode roomA = e.a;
            roomNode roomB = e.b;

            // Try to draw a straight horizontal connection first
            if (tryHorizontalConnection(roomA, roomB)) {
                continue; // Success, move to next edge
            }

            // If horizontal failed, try a straight vertical connection
            if (tryVerticalConnection(roomA, roomB)) {
                continue; // Success, move to next edge
            }

            // If both straight connections failed, draw an L-shaped hallway using centers
            drawLShapedHallwaySegmented(roomA.getCenterX(), roomA.getCenterY(), roomB.getCenterX(), roomB.getCenterY());
        }


        r.initialize(WIDTH, HEIGHT);
        r.renderFrame(world);


//// ——— overlay MST edges ———
//        StdDraw.setPenColor(StdDraw.RED);
//        StdDraw.setPenRadius(0.005);
//        for (Edge e : treeEdges) {
//            // tile i,j is drawn at (i+0.5)/WIDTH , (j+0.5)/HEIGHT in StdDraw coords
//            double x0 = (e.a.getCenterX());
//            double y0 = (e.a.getCenterY());
//            double x1 = (e.b.getCenterX());
//            double y1 = (e.b.getCenterY());
//            StdDraw.line(x0, y0, x1, y1);
//        }
//        StdDraw.show();

    }



    private void drawLShapedHallwaySegmented(int x0, int y0, int x1, int y1) {
        boolean horizontalFirst = rand.nextBoolean();
        if (horizontalFirst) {
            drawLHorizontalHallwaySegment(x0, x1, y0); // Horizontal part at y0
            drawLVerticalHallwaySegment(y0, y1, x1);   // Vertical part at x1 (turn point)
        } else {
            drawLVerticalHallwaySegment(y0, y1, x0);   // Vertical part at x0
            drawLHorizontalHallwaySegment(x0, x1, y1); // Horizontal part at y1 (turn point)
        }
    }

    private boolean tryVerticalConnection(roomNode roomA, roomNode roomB) {
        int overlapLeft = Math.max(roomA.getLeftX(), roomB.getLeftX());
        int overlapRight = Math.min(roomA.getRightX(), roomB.getRightX());

        if (overlapLeft > overlapRight) {
            return false;
        }

        int connectX;
        if (roomA.getCenterX() >= overlapLeft && roomA.getCenterX() <= overlapRight) {
            connectX = roomA.getCenterX(); // Prefer room A's center if in overlap
        } else if (roomB.getCenterX() >= overlapLeft && roomB.getCenterX() <= overlapRight) {
            connectX = roomB.getCenterX();
        } else {
            connectX = overlapLeft; // Fallback to left of overlap
        }

        int startY, endY;
        if (roomA.getCenterY() < roomB.getCenterY()) { // roomA is below roomB
            startY = roomA.getTopY();
            endY = roomB.getBottomY();
        } else { // roomB is below roomA
            startY = roomB.getTopY();
            endY = roomA.getBottomY();
        }

        drawVerticalHallwaySegment(startY, endY, connectX);
        return true;
    }

    private boolean tryHorizontalConnection(roomNode roomA, roomNode roomB) {
        int overlapBottom = Math.max(roomA.getBottomY(), roomB.getBottomY());
        int overlapTop = Math.min(roomA.getTopY(), roomB.getTopY());

        if (overlapBottom > overlapTop) {
            return false;
        }

        int connectY;
        if (roomA.getCenterY() >= overlapBottom && roomA.getCenterY() <= overlapTop) {
            connectY = roomA.getCenterY(); // Prefer room A's center if in overlap
        } else if (roomB.getCenterY() >= overlapBottom && roomB.getCenterY() <= overlapTop) {
            connectY = roomB.getCenterY();
        } else {
            connectY = overlapBottom; // Fallback to bottom of overlap
        }

        int startX, endX;
        if (roomA.getCenterX() < roomB.getCenterX()) { // roomA is left of roomB
            startX = roomA.getRightX();
            endX = roomB.getLeftX();
        } else { // roomB is left of roomA
            startX = roomB.getRightX();
            endX = roomA.getLeftX();
        }


        drawHorizontalHallwaySegment(startX, endX, connectY);
        return true;
    }

    private void drawHorizontalHallwaySegment(int x_start, int x_end, int y) {
        int start = Math.min(x_start, x_end);
        int end = Math.max(x_start, x_end);
        for (int x = start - 2; x <= end; x++) {
            carveHallway(x, y);
        }
    }

    private void drawVerticalHallwaySegment(int y_start, int y_end, int x) {
        int start = Math.min(y_start, y_end);
        int end = Math.max(y_start, y_end);
        for (int y = start - 2; y <= end; y++) {
            carveHallway(x, y);
        }
    }

    private void drawLHorizontalHallwaySegment(int x_start, int x_end, int y) {
        int start = Math.min(x_start, x_end);
        int end = Math.max(x_start, x_end);
        for (int x = start; x <= end; x++) {
            carveHallway(x, y);
        }
    }

    private void drawLVerticalHallwaySegment(int y_start, int y_end, int x) {
        int start = Math.min(y_start, y_end);
        int end = Math.max(y_start, y_end);
        for (int y = start; y <= end; y++) {
            carveHallway(x, y);
        }
    }

    private void carveHallway(int x, int y) {
        if (x <= 0 || x >= WIDTH - 1 || y <= 0 || y >= HEIGHT - 1) return;

        world[x][y] = Tileset.FLOOR;

        // Place walls around this floor tile if adjacent is NOTHING
        int[] dx = {0, 0, 1, -1, 1, 1, -1, -1};
        int[] dy = {1, -1, 0, 0, 1, -1, 1, -1};
        for (int i = 0; i < 8; i++) {
            int wallX = x + dx[i];
            int wallY = y + dy[i];
            if (wallX >= 0 && wallX < WIDTH && wallY >= 0 && wallY < HEIGHT) {
                if (world[wallX][wallY] == Tileset.NOTHING) {
                    world[wallX][wallY] = Tileset.WALL;
                }
            }
        }
        int[] dirX = {0, 0, 1, -1};  // up, down, right, left
        int[] dirY = {1, -1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int midX1 = x + dirX[i];
            int midY1 = y + dirY[i];

            int midX2 = x + dirX[i] * 2;
            int midY2 = y + dirY[i] * 2;

            int farX = x + dirX[i] * 3;
            int farY = y + dirY[i] * 3;

            // Bounds check for double-wall + floor case
            if (midX2 >= 0 && midX2 < WIDTH && midY2 >= 0 && midY2 < HEIGHT &&
                    farX >= 0 && farX < WIDTH && farY >= 0 && farY < HEIGHT) {

                if (world[midX1][midY1] == Tileset.WALL &&
                        world[midX2][midY2] == Tileset.WALL &&
                        world[farX][farY] == Tileset.FLOOR) {

                    world[midX1][midY1] = Tileset.FLOOR;
                    world[midX2][midY2] = Tileset.FLOOR;
                }
            }

            // Single-wall + floor case
            if (midX1 >= 0 && midX1 < WIDTH && midY1 >= 0 && midY1 < HEIGHT &&
                    midX2 >= 0 && midX2 < WIDTH && midY2 >= 0 && midY2 < HEIGHT) {

                if (world[midX2][midY2] == Tileset.FLOOR && world[midX1][midY1] == Tileset.WALL) {
                    world[midX1][midY1] = Tileset.FLOOR;
                }
            }
        }



    }

    // Edge class to hold two rooms
    private static class Edge {
        final roomNode a, b;
        Edge(roomNode a, roomNode b) { this.a = a; this.b = b; }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public List<roomNode> getRooms() {
        return rooms;
    }

}
