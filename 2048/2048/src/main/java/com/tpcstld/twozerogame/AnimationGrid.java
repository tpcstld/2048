package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationGrid {
    public AnimationCell[][] field;

    public AnimationGrid(int x, int y) {
        field = new AnimationCell[x][y];
        for (int xx = 0; xx < x; xx++) {
            for (int yy = 0; yy < y; yy++) {
                field[xx][yy] = null;
            }
        }
    }

    public void startAnimation(int x, int y, int direction, int frame) {
        field[x][y] = new AnimationCell(x, y, direction, frame);
    }

    public void tickAll() {
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] != null) {
                    field[xx][yy].tick();
                    if (field[xx][yy].atMaxFrame()) {
                        cancelAnimation(xx, yy);
                    }
                }
            }
        }
    }

    public AnimationCell getAnimationCell(int x, int y) {
        return field[x][y];
    }

    public void cancelAnimation(int x, int y) {
        field[x][y] = null;
    }

}
