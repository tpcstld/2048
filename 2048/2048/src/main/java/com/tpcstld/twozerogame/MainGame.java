package com.tpcstld.twozerogame;

import android.content.Context;
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

    int[][] field = new int[numSquaresX][numSquaresY];

    int score = 0;
    int highscore = 0;

    public MainGame (Context context) {
        super(context);
        System.out.println("Alright, started!");
    }

}
