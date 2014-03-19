package com.tpcstld.twozerogame;

import java.util.ArrayList;


public class AnimationGrid {
    public ArrayList<AnimationCell>[][] field;
    int activeAnimations = 0;
    boolean oneMoreFrame = false;
    public ArrayList<AnimationCell> globalAnimation = new ArrayList<AnimationCell>();
    public AnimationGrid(int x, int y) {
        field = new ArrayList[x][y];

        for (int xx = 0; xx < x; xx++) {
            for (int yy = 0; yy < y; yy++) {
                field[xx][yy] = new ArrayList<AnimationCell>();
            }
        }
    }

    public void startAnimation(int x, int y, int direction, long length, int e1, int e2) {
        field[x][y].add(new AnimationCell(x, y, direction, length, e1, e2));
        activeAnimations = activeAnimations + 1;
    }

    public void startGlobalAnimation(int direction, long length, int e1, int e2) {
        globalAnimation.add(new AnimationCell(-1, -1, direction, length, e1, e2));
        activeAnimations = activeAnimations + 1;
    }

    public void tickAll(long timeElapsed) {
        ArrayList<AnimationCell> cancelledAnimations = new ArrayList<AnimationCell>();
        for (AnimationCell animation : globalAnimation) {
            animation.tick(timeElapsed);
            if (animation.animationDone()) {
                cancelledAnimations.add(animation);
                activeAnimations = activeAnimations - 1;
            }
        }

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
        if (activeAnimations != 0) {
            oneMoreFrame = true;
            return true;
        } else if (oneMoreFrame) {
            oneMoreFrame = false;
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<AnimationCell> getAnimationCell(int x, int y) {
        return field[x][y];
    }

    public void cancelAnimation(AnimationCell animation) {
        if (animation.getX() == -1 && animation.getY() == -1) {
            globalAnimation.remove(animation);
        } else {
            field[animation.getX()][animation.getY()].remove(animation);
        }
    }

}
