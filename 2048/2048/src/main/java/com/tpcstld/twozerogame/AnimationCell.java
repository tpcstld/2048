package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationCell extends Cell {
    private int maxFrame = 5;
    private int direction;
    private int currentFrame;

    public AnimationCell (int x, int y, int direction, int frame) {
        super(x, y);
        this.direction = direction;
        currentFrame = frame;
        maxFrame = frame;
    }

    public int getDirection() {
        return direction;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getMaxFrame() { return maxFrame; }

    public int getFramesElapsed() {
        return (getMaxFrame() - getCurrentFrame() + 1);
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void tick() {
        currentFrame = currentFrame - 1;
    }

    public boolean atMaxFrame() {
        return currentFrame == 0;
    }
}
