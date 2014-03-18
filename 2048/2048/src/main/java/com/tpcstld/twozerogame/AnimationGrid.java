package com.tpcstld.twozerogame;

import java.util.ArrayList;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationGrid {
    public ArrayList<AnimationCell>[][] field;
    int activeAnimations = 0;

    public AnimationGrid(int x, int y) {
        field = new ArrayList[x][y];

        for (int xx = 0; xx < x; xx++) {
            for (int yy = 0; yy < y; yy++) {
                field[xx][yy] = new ArrayList<AnimationCell>();
            }
        }
    }

    public void startAnimation(int x, int y, int direction, int frame, int e1, int e2) {
        field[x][y].add(new AnimationCell(x, y, direction, frame, e1, e2));
        activeAnimations = activeAnimations + 1;
    }

    public void tickAll(long timeElapsed) {
        ArrayList<AnimationCell> cancelledAnimations = new ArrayList<AnimationCell>();
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                for (AnimationCell animation : field[xx][yy]) {
                    animation.tick(timeElapsed);
                    if (animation.animationDone()) {
                        cancelledAnimations.add(animation);
                        activeAnimations = activeAnimations - 1;
                    }
                }
            }
        }
        for (AnimationCell animation : cancelledAnimations) {
            cancelAnimation(animation);
        }
    }

    public boolean isAnimationActive() {
        return activeAnimations != 0;
    }

    public ArrayList<AnimationCell> getAnimationCell(int x, int y) {
        return field[x][y];
    }

    public void cancelAnimation(AnimationCell animation) {
        field[animation.getX()][animation.getY()].remove(animation);
    }

}
