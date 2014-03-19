package com.tpcstld.twozerogame;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tpcstld on 3/12/14.
 */
public class InputListener implements View.OnTouchListener {

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 30;

    private float x;
    private float y;
    private float previousX;
    private float previousY;
    private float startingX;
    private float startingY;
    private boolean xOnPath;
    private boolean yOnPath;
    private boolean ignoreInputs;

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
                xOnPath = true;
                yOnPath = true;
                ignoreInputs = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                float dx = x - previousX;
                float dy = y - previousY;
                xOnPath = (xOnPath && checkOnPath(x, startingX));
                yOnPath = (yOnPath && checkOnPath(y, startingY));
                if (!mView.game.won && !mView.game.lose) {
                    if (pathMoved() > SWIPE_MIN_DISTANCE * SWIPE_MIN_DISTANCE && !ignoreInputs  && startingY > 50) {
                        boolean moved = false;
                        if (dy >= SWIPE_THRESHOLD_VELOCITY && yOnPath) {
                            moved = true;
                            mView.game.move(2);
                        } else if (dy <= -SWIPE_THRESHOLD_VELOCITY && yOnPath) {
                            moved = true;
                            mView.game.move(0);
                        } else if (dx >= SWIPE_THRESHOLD_VELOCITY && xOnPath) {
                            moved = true;
                            mView.game.move(1);
                        } else if (dx <= -SWIPE_THRESHOLD_VELOCITY && xOnPath) {
                            moved = true;
                            mView.game.move(3);
                        }
                        if (moved) {
                            ignoreInputs = true;
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
                if (pathMoved() <= MainView.iconSize
                        && inRange(MainView.sXNewGame, x, MainView.sXNewGame + MainView.iconSize)
                        && inRange(MainView.sYNewGame, y, MainView.sYNewGame + MainView.iconSize)) {
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
