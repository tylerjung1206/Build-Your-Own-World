package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

public class HUD {
    public static void showHUD(TETile[][] world) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(1, World.getHEIGHT() - 1, "Tile: " + hoverIdentify(world));
        StdDraw.show();
        StdDraw.pause(10);
    }

    public static String hoverIdentify(TETile[][] world) {
        int mouseTileX = (int) StdDraw.mouseX();
        int mouseTileY = (int) StdDraw.mouseY();
        //avoids out of bounds error
        if (mouseTileX < 0 || mouseTileX >= World.getWIDTH() || mouseTileY < 0 || mouseTileY >= World.getHEIGHT()) {
            return "";
        }
        return world[mouseTileX][mouseTileY].description();
    }

}
