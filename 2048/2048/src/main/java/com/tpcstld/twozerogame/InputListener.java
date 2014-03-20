package com.tpcstld.twozerogame;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class InputListener implements View.OnTouchListener {

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 85;
    private static final int SWIPE_THRESHOLD_VELOCITY = 30;
    private static final int MOVE_THRESHOLD = 170;
    private static final int RESET_STARTING = 10;

    private float x;
    private float y;
    private float lastdx;
    private float lastdy;
    private float previousX;
    private float previousY;
    private float startingX;
    private float startingY;
    private int previousDirection = 1;
    private int veryLastDirection = 1;

    MainView mView;

    public InputListener(MainView view) {
        super();
        this.mView = view;
    }

    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                startingX = x;
                startingY = y;
                previousX = x;
                previousY = y;
                lastdx = 0;
                lastdy = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                if (!mView.game.won && !mView.game.lose) {
                    float dx = x - previousX;
                    if (Math.abs(lastdx + dx) < Math.abs(lastdx) + Math.abs(dx) && Math.abs(dx) > RESET_STARTING
                            &&  Math.abs(x - startingX) > SWIPE_MIN_DISTANCE) {
                        startingX = x;
                        startingY = y;
                        lastdx = dx;
                        previousDirection = veryLastDirection;
                        Log.d("Debug","ResetX");
                    }
                    if (lastdx == 0) {
                        lastdx = dx;
                    }
                    float dy = y - previousY;
                    if (Math.abs(lastdy + dy) < Math.abs(lastdy) + Math.abs(dy) && Math.abs(dy) > RESET_STARTING
                            && Math.abs(y - startingY) > SWIPE_MIN_DISTANCE) {
                        startingX = x;
                        startingY = y;
                        lastdy = dy;
                        previousDirection = veryLastDirection;
                        Log.d("Debug","ResetY");
                    }
                    if (lastdy == 0) {
                        lastdy = dy;
                    }
                    if (pathMoved() > SWIPE_MIN_DISTANCE * SWIPE_MIN_DISTANCE && startingY > 50) {
                        boolean moved = false;
                        if ((dy >= SWIPE_THRESHOLD_VELOCITY || y - startingY >= MOVE_THRESHOLD) && previousDirection % 2 != 0) {
                            moved = true;
                            previousDirection = previousDirection * 2;
                            veryLastDirection = 2;
                            mView.game.move(2);
                            Log.d("debug", "Moved. Down");
                        } else if ((dy <= -SWIPE_THRESHOLD_VELOCITY || y - startingY <= -MOVE_THRESHOLD ) && previousDirection % 3 != 0) {
                            moved = true;
                            previousDirection = previousDirection * 3;
                            veryLastDirection = 3;
                            mView.game.move(0);
                            Log.d("debug", "Moved. Up");
                        } else if ((dx >= SWIPE_THRESHOLD_VELOCITY || x - startingX >= MOVE_THRESHOLD) && previousDirection % 5 != 0) {
                            moved = true;
                            previousDirection = previousDirection * 5;
                            veryLastDirection = 5;
                            mView.game.move(1);
                            Log.d("debug", "Moved. Right");
                        } else if ((dx <= -SWIPE_THRESHOLD_VELOCITY || x - startingX <= -MOVE_THRESHOLD) && previousDirection % 7 != 0) {
                            moved = true;
                            previousDirection = previousDirection * 7;
                            veryLastDirection = 7;
                            mView.game.move(3);
                            Log.d("debug", "Moved. Left");
                        }
                        if (moved) {
                            startingX = x;
                            startingY = y;
                        }
                    }
                }
                previousX = x;
                previousY = y;
                return true;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                y = event.getY();
                previousDirection = 1;
                veryLastDirection = 1;
                if (pathMoved() <= MainView.iconSize
                        && inRange(MainView.sXNewGame, x, MainView.sXNewGame + MainView.iconSize)
                        && inRange(MainView.sYIcons, y, MainView.sYIcons + MainView.iconSize)) {
                    mView.game.newGame();
                }
        }
        return true;
    }

    public boolean checkOnPath(float current, float starting) {
        return (Math.abs(current - starting) <= SWIPE_MAX_OFF_PATH);
    }

    public float pathMoved() {
        return (float) ((x - startingX) * (x - startingX) + (y - startingY) * (y - startingY));
    }

    public boolean inRange(float left, float check, float right) {
        return (left <= check && check <= right);
    }

}
