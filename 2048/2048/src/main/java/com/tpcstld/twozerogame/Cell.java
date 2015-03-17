package com.tpcstld.twozerogame;

public class Cell {
    private int x;
    private int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    private void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    private void setY(int y) {
        this.y = y;
    }
}
