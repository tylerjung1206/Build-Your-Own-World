package core;

import java.util.Objects;

public class roomNode {
    public int x, y, w, h; // stands for x y coordinates and width and height

    public roomNode(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    // Floor coordinates
    public int getLeftX() { return x; }
    public int getRightX() { return x + w - 1; }
    public int getBottomY() { return y; }
    public int getTopY() { return y + h - 1; }

    // Center coordinates (integer division useful for hallways)
    public int getCenterX() { return x + w / 2; }
    public int getCenterY() { return y + h / 2; }

    // Bounding box including a 1-tile WALL border
    public int getWallLeftX() { return x - 1; }
    public int getWallRightX() { return x + w; }
    public int getWallBottomY() { return y - 1; }
    public int getWallTopY() { return y + h; }

    public boolean overlaps(roomNode other) {
        return this.getWallLeftX() <= other.getWallRightX() &&
                this.getWallRightX() >= other.getWallLeftX() &&
                this.getWallBottomY() <= other.getWallTopY() &&
                this.getWallTopY() >= other.getWallBottomY();
    }
    public double distanceTo(roomNode other) {
        double dx = this.getCenterX() - other.getCenterX();
        double dy = this.getCenterY() - other.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, w, h);
    }

    @Override
    public String toString() {
        return String.format("room@(%d,%d) size[%dx%d]", x, y, w, h);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof roomNode)) {
            return false;
        }
        roomNode r = (roomNode) o;
        return x == r.x && y == r.y && w == r.w && h == r.h;
    }
}
