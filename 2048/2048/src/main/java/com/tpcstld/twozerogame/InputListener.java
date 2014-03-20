package com.tpcstld.twozerogame;

import android.view.MotionEvent;
import android.view.View;

public class InputListener implements View.OnTouchListener {

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 100;
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
    private int previousDirection = -1;

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
                /*xOnPath = true;
                yOnPath = true;*/
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                float dx = x - previousX;
                if (Math.abs(lastdx + dx) < Math.abs(lastdx) + Math.abs(dx) && Math.abs(dx) > RESET_STARTING) {
                    startingX = x;
                    lastdx = dx;
                }
                float dy = y - previousY;
                if (Math.abs(lastdy + dy) < Math.abs(lastdy) + Math.abs(dy) && Math.abs(dy) > RESET_STARTING) {
                    startingY = y;
                    lastdy = dy;
                }
                if (!mView.game.won && !mView.game.lose) {
                    if (pathMoved() > SWIPE_MIN_DISTANCE * SWIPE_MIN_DISTANCE /*&& !ignoreInputs*/  && startingY > 50) {
                        boolean moved = false;
                        if ((dy >= SWIPE_THRESHOLD_VELOCITY || y - startingY >= MOVE_THRESHOLD) /*&& yOnPath*/ && previousDirection != 2) {
                            moved = true;
                            previousDirection = 2;
                            mView.game.move(2);
                        } else if ((dy <= -SWIPE_THRESHOLD_VELOCITY || y - startingY <= -MOVE_THRESHOLD ) /*&& yOnPath*/ && previousDirection != 0) {
                            moved = true;
                            previousDirection = 0;
                            mView.game.move(0);
                        } else if ((dx >= SWIPE_THRESHOLD_VELOCITY || x - startingX >= MOVE_THRESHOLD) /*&& xOnPath*/ && previousDirection != 1) {
                            moved = true;
                            previousDirection = 1;
                            mView.game.move(1);
                        } else if ((dx <= -SWIPE_THRESHOLD_VELOCITY || x - startingX <= -MOVE_THRESHOLD)/* && xOnPath*/ && previousDirection != 3) {
                            moved = true;
                            previousDirection = 3;
                            mView.game.move(3);
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
                previousDirection = -1;
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
