package com.tpcstld.twozerogame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by tpcstld on 3/12/14.
 */

public class MainGame extends View {

    Paint paint = new Paint();

    final int numSquaresX = 4;
    final int numSquaresY = 4;
    final int startTiles = 2;


    boolean getScreenSize = true;
    int cellSize = 0;
    int gridWidth = 0;
    int screenMiddleX = 0;
    int screenMiddleY = 0;

    Drawable backgroundRectangle;
    Drawable cellRectangle;

    int[][] field = new int[numSquaresX][numSquaresY];
    ArrayList<Point> availableCells = new ArrayList<Point>();


    int score = 0;
    int highscore = 0;

    @Override
    public void onDraw(Canvas canvas) {
        if (getScreenSize) {
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();
            getLayout(width, height);
        }

        //Draw the grid
        int halfNumSquaresX = numSquaresX / 2;
        int halfNumSquaresY = numSquaresY / 2;

        int startingX = screenMiddleX - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2;
        int endingX = screenMiddleX + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2;
        int startingY = screenMiddleY - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2;
        int endingY = screenMiddleY + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2;

        backgroundRectangle.setBounds(startingX, startingY, endingX, endingY);
        backgroundRectangle.draw(canvas);

        for (int xx = 0; xx < numSquaresX; xx++) {
            for (int yy = 0; yy < numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;
                cellRectangle.setBounds(sX, sY, eX, eY);
                cellRectangle.draw(canvas);
            }
        }
        invalidate();
    }

    public void getLayout(int width, int height) {
        cellSize = (int) Math.min(width / (numSquaresX + 1), height / (numSquaresY + 1));
        gridWidth = (int) cellSize / 10;
        paint.setStrokeWidth(gridWidth);
        screenMiddleX = (int) width / 2;
        screenMiddleY = (int) height / 2;
        getScreenSize = false;
    }

    public void newGame() {
        availableCells = new ArrayList<Point>();
        for (int xx = 0; xx < numSquaresX; xx++) {
            for (int yy = 0; yy < numSquaresY; yy++) {
                availableCells.add(new Point(xx, yy));
                field[xx][yy] = 0;
            }
        }
        score = 0;
    }

    public MainGame (Context context) {
        super(context);
        Resources resources = context.getResources();
        try {
            backgroundRectangle = (Drawable) resources.getDrawable(R.drawable.background_rectangle);
            cellRectangle = (Drawable) resources.getDrawable(R.drawable.cell_rectangle);
        } catch (Exception e) {
            System.out.println("Error getting rectangle?");
        }
    }

}