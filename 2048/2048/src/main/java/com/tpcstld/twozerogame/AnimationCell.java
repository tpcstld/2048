package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationCell extends Cell {
    private int maxFrame = 5;
    private int direction;
    private long timeElapsed;
    private long totalTime;

    public AnimationCell (int x, int y, int direction, long length) {
        super(x, y);
        this.direction = direction;
        totalTime = length;
    }

    public int getDirection() {
        return direction;
    }

    public long getTimeElapsed() { return timeElapsed; }

    public int getMaxFrame() { return maxFrame; }

    public void tick(long timeElapsed) {
        this.timeElapsed = this.timeElapsed + timeElapsed;
    }

    public boolean animationDone() {
        return totalTime < timeElapsed;
    }

    public double getPercentageDone() {
        return 1.0 * timeElapsed / totalTime;
    }
}
