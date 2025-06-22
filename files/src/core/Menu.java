package core;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

import static core.SL.loadWorld;


public class Menu {
    private TETile[][] menu;
    private int width;
    private int height;
    char c;
    private static int x;
    private static Long seed = 0L;

    public Menu() {
        width = World.getWIDTH();
        height = World.getHEIGHT();
    }

    public void menuStart() {
        StdDraw.setCanvasSize(width * 15, height * 15);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear();

        StdDraw.text(width / 2, height / 2 + 4, "CS61B - BYOW");
        StdDraw.text(width / 2.0, height / 2.0 - 2, "(N) New Game");
        StdDraw.text(width / 2.0, height / 2.0 - 4, "(L) Load Game");
        StdDraw.text(width / 2.0, height / 2.0 - 6, "(Q) Quit Game");

        StdDraw.show();
        while(true) {
            if (StdDraw.hasNextKeyTyped()) {
                char curr = StdDraw.nextKeyTyped();
                if (curr == 'n' || curr == 'N') {
                    StdDraw.clear();
                    StdDraw.text(width / 2.0, height / 2.0 - 2, "(1) Single player");
                    StdDraw.text(width / 2.0, height / 2.0 - 4, "(2) Multi player");
                    StdDraw.show();
                    while(true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char curr2 = StdDraw.nextKeyTyped();
                            if (curr2 == '1') {
                                newGame(false);
                                return;
                            } else if (curr2 == '2') {
                                newGame(true);
                                return;
                            }
                        }
                    }
                }
                //loading the old world
                if (curr == 'l' || curr == 'L') {
                    TETile[][] tiles = loadWorld("save.txt");
                    World loadedWorld = new World(tiles);
                    Player.startLoadedGame(loadedWorld);
                }
                if (curr == 'q' || curr == 'Q') {
                    System.exit(0);
                }
            }
        }

    }

    public void newGame(boolean n) {
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear();
        StdDraw.text(width / 2, height / 2 + 4, "CS61B - BYOW");
        StdDraw.text(width / 2, height / 2, "Enter seed followed by S");
        x = width / 2 - 10;
        while(true) {
            while (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                int y = height / 2 - 4;
                switch (c) {
                    case '0':
                        x = appear(x, y, 0);
                        break;
                    case '1':
                        x = appear(x, y, 1);
                        break;
                    case '2':
                        x =appear(x, y, 2);
                        break;
                    case '3':
                        x = appear(x, y, 3);
                        break;
                    case '4':
                        x = appear(x, y, 4);
                    break;
                    case '5':
                        x = appear(x, y, 5);
                        break;
                    case '6':
                        x = appear(x, y, 6);
                        break;
                    case '7':
                        x = appear(x, y, 7);
                        break;
                    case '8':
                        x =appear(x, y, 8);
                        break;
                    case '9':
                        x = appear(x, y, 9);
                        break;
                    default:
                        break;
                }
                if (c == 's') {
                    Player.startGame(new Random(seed), n);
                    return;
                }

            }
        }

    }

    public static int appear(int x, int y, int number) {
        StdDraw.text(x, y, String.valueOf(number));
        seed = seed * 10 + number;
        //System.out.println(seed);
        return x + 1;
    }
}
