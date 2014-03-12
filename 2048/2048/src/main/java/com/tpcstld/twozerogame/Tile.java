package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/12/14.
 */
public class Tile extends Point{
    private int value;
    private Point previousPosition = null;
    private Tile mergedFrom = null;

    public Tile(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public Tile(Point point, int value) {
       super(point.getX(), point.getY());
        this.value = value;
    }

    public void savePosition() {
        previousPosition = new Point(this.getX(), this.getY());
    }

    public void updatePosition(Point point) {
        this.setX(point.getX());
        this.setY(point.getY());
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }


}
