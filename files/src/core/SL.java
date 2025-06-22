package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class SL {

    public static void saveWorld(World instance, String file) {
        try {
            FileWriter saved = new FileWriter(file);
            int w = World.getWIDTH();
            int h = World.getHEIGHT();
            //System.out.println(w);
            //System.out.println(h);
            TETile[][] world = instance.getWorld();
            //i saved so the file is organized like the actual world map, buts its upside down since
            //were iterating from x, y = 0, this doesn't matter tho since we also iterate from x, y = 0
            //in the loading method
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (world[x][y] != null) {
                        saved.write(world[x][y].character());
                    }
                }
                saved.write("\n");
            }

            saved.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TETile[][] loadWorld(String file) {
        try {
            int w = World.getWIDTH();
            int h = World.getHEIGHT();
            FileReader loaded = new FileReader(file);
            StringBuilder loadedString = new StringBuilder();
            int curr = 0;
            while (curr != -1) {
                curr = loaded.read();
                loadedString.append((char) curr);
            }
            //prev while loop adds extra character for -1, i didnt know how to get rid of it so
            //i just did this
            loadedString.deleteCharAt(loadedString.length() - 1);

            String[] allLines = loadedString.toString().split("\n");

            TETile[][] world = new TETile[w][h];
            for (int y = 0; y < h; y++) {
                if (y < allLines.length) {
                    String currLine = allLines[y];
                    for (int x = 0; x < w; x++) {
                        if (x < currLine.length()) {
                            world[x][y] = getTile(currLine.charAt(x));
                        } else {
                            world[x][y] = Tileset.NOTHING;
                        }
                    }
                } else {
                    // FileReader only reads characters that are not empty spaces (as spaces) in the text file
                    // (i think this is because I only specified saving characters that aren't nothing tiles in saving)
                    //so the save method doesn't fill up all the rows that are completely empty. (allLines
                    //is usually smaller than the height, not the same)
                    //so this is to cover the rest of them (usually only the top few and bottom few rows)
                    for (int x = 0; x < World.getWIDTH(); x++) {
                        world[x][y] = Tileset.NOTHING;
                    }
                }
            }

            return world;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TETile getTile(char c) {
        if (c == '#') {
            return Tileset.WALL;
        } else if (c == '·') {
            return Tileset.FLOOR;
        } else if (c == '@') {
            return Tileset.AVATAR;
        } else if (c == '❀') {
            return Tileset.FLOWER;
        } else {
            return Tileset.NOTHING;
        }
    }
}
