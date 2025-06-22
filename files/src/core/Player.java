package core;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import java.util.*;

public class Player {
    private static World w;
    private static int avatarX;
    private static int avatarY;
    private static int avatarA;
    private static int avatarB;
    private static boolean player1Won;
    private static long time;
    private static final int VISIBILITY_RADIUS = 5;
    private static boolean isLosEnabled = false;
    private static TETile[][] visibleWorld;

    public static void startGame(Random seed, boolean n) {
        w = new World(seed);
        w.generateWorld();
        avatarY = w.getRooms().get(0).y;
        avatarX = w.getRooms().get(0).x;
        w.getWorld()[avatarX][avatarY] = Tileset.AVATAR;

        visibleWorld = new TETile[World.getWIDTH()][World.getHEIGHT()];
        updateVisibleWorld();

        avatarA = avatarX + 1;
        avatarB = avatarY + 1;

        time = System.currentTimeMillis();

        if (n) {
            w.getWorld()[avatarA][avatarB] = Tileset.FLOWER;
            movement(true);
        } else {
            movement(false);
        }
    }

    private static void updateVisibleWorld() {
        TETile[][] originalWorld = w.getWorld();
        for (int x = 0; x < World.getWIDTH(); x++) {
            for (int y = 0; y < World.getHEIGHT(); y++) {
                int dx = x - avatarX;
                int dy = y - avatarY;
                int distance = Math.max(Math.abs(dx), Math.abs(dy));

                if (distance <= VISIBILITY_RADIUS && inLineOfSight(avatarX, avatarY, x, y)) {
                    if (x >= 0 && x < originalWorld.length && y >= 0 && y < originalWorld[0].length) {
                        visibleWorld[x][y] = originalWorld[x][y];
                    } else {
                        visibleWorld[x][y] = Tileset.NOTHING;
                    }
                } else {
                    visibleWorld[x][y] = Tileset.NOTHING;
                }
            }
        }
        if (avatarX >= 0 && avatarX < World.getWIDTH() && avatarY >= 0 && avatarY < World.getHEIGHT()) {
            visibleWorld[avatarX][avatarY] = Tileset.AVATAR;
        }
    }


    private static boolean inLineOfSight(int xStart, int yStart, int xTarget, int yTarget) {
        TETile[][] world = w.getWorld();
        int width = World.getWIDTH();
        int height = World.getHEIGHT();
        boolean[][] visitedPos = new boolean[width][height];

        Queue<int[]> queue = new LinkedList<>();
        int[] startPosition = new int[3];
        startPosition[0] = xStart;
        startPosition[1] = yStart;

        queue.add(startPosition);
        visitedPos[xStart][yStart] = true;
        int[] y_change = new int[4];
        y_change[0] = 0;
        y_change[1] = 0;
        y_change[2] = -1;
        y_change[3] = 1;

        int[] x_change = new int[4];
        x_change[0] = -1;
        x_change[1] = 1;
        x_change[2] = 0;
        x_change[3] = 0;


        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            int dist = current[2];

            if (dist <= VISIBILITY_RADIUS)
                if (y == yTarget && x == xTarget)
                    return true;

            for (int i = 0; i < 4; i++) {
                int new_x = x_change[i] + x;
                int new_y = y_change[i] + y;

                if (width > new_x && new_y >= 0 && height > new_y && new_x >= 0)
                    if (!visitedPos[new_x][new_y])
                        if (world[x][y] != Tileset.WALL) {
                            int[] newPosition = new int[3];
                            newPosition[0] = new_x;
                            newPosition[1] = new_y;
                            newPosition[2] = dist + 1;

                            visitedPos[new_x][new_y] = true;
                            queue.add(newPosition);
                        }
            }
        }
        return false;
    }

    public static boolean move(int x, int y, boolean multi) {
        if (multi) {
            if (w.getWorld()[avatarA + x][avatarB + y] == Tileset.WALL) {
                return false;
            }
            w.getWorld()[avatarA][avatarB] = Tileset.FLOOR;
            avatarA = avatarA + x;
            avatarB = avatarB + y;
            w.getWorld()[avatarA][avatarB] = Tileset.FLOWER;
        } else {
            if (w.getWorld()[avatarX + x][avatarY + y] == Tileset.WALL) {
                return false;
            }
            w.getWorld()[avatarX][avatarY] = Tileset.FLOOR;
            avatarX = avatarX + x;
            avatarY = avatarY + y;
            w.getWorld()[avatarX][avatarY] = Tileset.AVATAR;
        }

        return true;
    }




    //basically a copy of the previous class, but with the loaded world, not a random seed
    public static void startLoadedGame(World loadedWorld) {
        w = loadedWorld;
        TETile[][] tiles = w.getWorld();
        boolean multi = false;
        time = 0L;
        //locate avatar
        for (int x = 0; x < World.getWIDTH(); x++) {
            for (int y = 0; y < World.getHEIGHT(); y++) {
                if (tiles[x][y] == Tileset.AVATAR) {
                    avatarX = x;
                    avatarY = y;
                }
                if (tiles[x][y] == Tileset.FLOWER) {
                    avatarA = x;
                    avatarB = y;
                    time = System.currentTimeMillis();
                    multi = true;
                }
            }
        }
        visibleWorld = new TETile[World.getWIDTH()][World.getHEIGHT()];
        updateVisibleWorld();

       movement(multi);
    }

    private static void movement(boolean multi) {
        char c;
        boolean colon = false;
        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);

                switch (c) {
                    case 'w':
                        move(0, 1, false);
                        break;
                    case 'a':
                        move(-1, 0, false);
                        break;
                    case 's':
                        move(0, -1, false);
                        break;
                    case 'd':
                        move(1, 0, false);
                        break;
                    case 'i':
                        if (multi){
                        move(0, 1, true);
                        }
                        break;
                    case 'j':
                        if (multi){
                        move(-1, 0, true);
                        }
                        break;
                    case 'k':
                        if (multi){
                        move(0, -1, true);
                        }
                        break;
                    case 'l':
                        if (multi){
                        move(1, 0, true);
                        }
                        break;
                    case 'v': // Use 'v' for visibility toggle
                        isLosEnabled = !isLosEnabled;
                        colon = false;
                        break;
                    case ':':
                        colon = true;
                        break;
                    case 'q':
                        /*if (multi) {
                            break;
                        }*/
                        if (colon) {
                            SL.saveWorld(w, "save.txt");
                            System.exit(0);
                        }
                        colon = false;
                        break;
                    default:
                        colon = false;
                        break;
                }

            }
            player1Won = false;
            if (multi) {
                if (avatarX == avatarA && avatarY == avatarB) {
                    player1Won = true;
                }
            }
            TETile[][] worldToRender;
            if (isLosEnabled) {
                updateVisibleWorld();
                worldToRender = visibleWorld;
            } else {
                worldToRender = w.getWorld();
            }

            w.getR().renderFrame(worldToRender);
            HUD.showHUD(worldToRender);

            if (player1Won) {
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(World.getWIDTH() / 2.0, World.getHEIGHT() / 2.0, "Player AVATAR won!");
                StdDraw.show();
                StdDraw.pause(10000); // Pause for seconds to show message

                System.out.println("Player AVATAR wins!");
                System.exit(0);
            } else if (multi && time != 0L &&  System.currentTimeMillis() >= (time + 30*1000)) {
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(World.getWIDTH() / 2.0, World.getHEIGHT() / 2.0, "Player FLOWER won!");
                StdDraw.show();
                StdDraw.pause(10000); // Pause for seconds to show message

                System.out.println("Player FLOWER wins!");
                System.exit(0);
            }
        }
    }


}
