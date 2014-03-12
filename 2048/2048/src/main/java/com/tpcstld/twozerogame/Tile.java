package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/12/14.
 */
public class Tile extends Cell {
    private int value;
    private Cell previousPosition = null;
    private Tile mergedFrom = null;

    public Tile(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public Tile(Cell cell, int value) {
       super(cell.getX(), cell.getY());
        this.value = value;
    }

    public void savePosition() {
        previousPosition = new Cell(this.getX(), this.getY());
    }

    public void updatePosition(Cell cell) {
        this.setX(cell.getX());
        this.setY(cell.getY());
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Tile getMergedFrom() {
       return mergedFrom;
    }

    public void setMergedFrom(Tile tile) {
        mergedFrom = tile;
    }

    public Cell getPreviousPosition() {
        return previousPosition;
    }
}
