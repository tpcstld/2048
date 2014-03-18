package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationCell extends Cell {
    private int direction;
    private long timeElapsed;
    private long totalTime;
    public int extra;
    public int extra2;

    public AnimationCell (int x, int y, int direction, long length, int extra, int extra2) {
        super(x, y);
        this.direction = direction;
        totalTime = length;
        this.extra = extra;
        this.extra2 = extra2;
    }

    public int getDirection() {
        return direction;
    }

    public long getTimeElapsed() { return timeElapsed; }

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
