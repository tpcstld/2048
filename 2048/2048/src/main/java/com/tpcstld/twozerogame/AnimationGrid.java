package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationGrid {
    public AnimationCell[][] field;
    int activeAnimations = 0;

    public AnimationGrid(int x, int y) {
        field = new AnimationCell[x][y];
        for (int xx = 0; xx < x; xx++) {
            for (int yy = 0; yy < y; yy++) {
                field[xx][yy] = null;
            }
        }
    }

    public void startAnimation(int x, int y, int direction, int frame, int e1, int e2) {
        field[x][y] = new AnimationCell(x, y, direction, frame, e1, e2);
        activeAnimations = activeAnimations + 1;
    }

    public void tickAll(long timeElapsed) {
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] != null) {
                    field[xx][yy].tick(timeElapsed);
                    if (field[xx][yy].animationDone()) {
                        cancelAnimation(xx, yy);
                        activeAnimations = activeAnimations - 1;
                    }
                }
            }
        }
    }

    public boolean isAnimationActive() {
        return activeAnimations != 0;
    }

    public AnimationCell getAnimationCell(int x, int y) {
        return field[x][y];
    }

    public void cancelAnimation(int x, int y) {
        field[x][y] = null;
    }

}
