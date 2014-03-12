package com.tpcstld.twozerogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by tpcstld on 3/12/14.
 */

public class MainGame extends View {

    Paint paint = new Paint();

    final int numSquaresX = 4;
    final int numSquaresY = 4;
    final int startTiles = 2;

    boolean getScreenSize = true;

    int[][] field = new int[numSquaresX][numSquaresY];

    int score = 0;
    int highscore = 0;

    @Override
    public void onDraw(Canvas canvas) {
        if (getScreenSize) {
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();
            getLayout(width, height);
        }
    }

    public void getLayout(int width, int height) {

    }

    public MainGame (Context context) {
        super(context);
    }

}
