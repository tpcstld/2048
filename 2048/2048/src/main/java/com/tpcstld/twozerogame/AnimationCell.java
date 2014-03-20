package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/16/14.
 */
public class AnimationCell extends Cell {
    private int animationType;
    private long timeElapsed;
    private long totalTime;
    private long delayTime;
    public int extra;
    public int extra2;

    public AnimationCell (int x, int y, int animationType, long length, int extra, int extra2) {
        super(x, y);
        this.animationType = animationType;
        totalTime = length;
        this.extra = extra;
        this.extra2 = extra2;
    }

    public int getAnimationType() {
        return animationType;
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
