package com.tpcstld.twozerogame;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by tpcstld on 3/12/14.
 */

public class MainView extends View {

    Paint paint = new Paint();
    MainGame game;

    boolean getScreenSize = true;
    int cellSize = 0;
    int gridWidth = 0;
    int screenMiddleX = 0;
    int screenMiddleY = 0;
    int orientation = Configuration.ORIENTATION_UNDEFINED;
    Drawable backgroundRectangle;
    Drawable cellRectangle;

    @Override
    public void onDraw(Canvas canvas) {
        int newOrientation = getOrientation();
        if (newOrientation != orientation) {
            getScreenSize = true;
            orientation = newOrientation;
        }
        if (getScreenSize) {
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();
            getLayout(width, height);
        }

        //Draw the grid
        int halfNumSquaresX = game.numSquaresX / 2;
        int halfNumSquaresY = game.numSquaresY / 2;

        int startingX = screenMiddleX - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2;
        int endingX = screenMiddleX + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2;
        int startingY = screenMiddleY - (cellSize + gridWidth) * halfNumSquaresY - gridWidth / 2;
        int endingY = screenMiddleY + (cellSize + gridWidth) * halfNumSquaresY + gridWidth / 2;

        //Drawing the background
        backgroundRectangle.setBounds(startingX, startingY, endingX, endingY);
        backgroundRectangle.draw(canvas);

        //Drawing the score text
        paint.setTextAlign(Paint.Align.LEFT);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            int textSize = (int) cellSize / 4;
            paint.setTextSize(textSize);
            int textShiftY = (int) ((paint.descent() + paint.ascent()) / 2);
            int y = startingY - textSize / 2 - textShiftY;
            String text = "";
            if (game.lose) {
                text = " GAME OVER";
            } else if (game.won) {
                text = " WINNER!";
            }
            canvas.drawText("Score: " + game.score + text, startingX, y, paint);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int textSize = (int) cellSize / 3;
            paint.setTextSize(textSize);
            String text = "";
            if (game.lose) {
                text = " GAME OVER";
            } else if (game.won) {
                text = " WINNER!";
            }
            canvas.drawText("Score: " + text, endingX, startingY + textSize, paint);
            canvas.drawText("" + game.score, endingX, startingY + textSize + textSize, paint);
        }

        paint.setTextAlign(Paint.Align.CENTER);
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;
                cellRectangle.setBounds(sX, sY, eX, eY);
                cellRectangle.draw(canvas);

                if (game.grid.getCellContent(new Cell(xx, yy)) != null) {
                    paint.setTextSize(cellSize);
                    float tempSize = cellSize * cellSize / Math.max(cellSize, paint.measureText(String.valueOf(game.grid.getCellContent(new Cell(xx, yy)).getValue())));
                    paint.setTextSize(tempSize);
                    int textShiftY = (int) ((paint.descent() + paint.ascent()) / 2);
                    canvas.drawText("" + game.grid.field[xx][yy].getValue(), sX + cellSize / 2, sY + cellSize / 2 - textShiftY, paint);
                }
            }
        }

        invalidate();
    }

    public int getOrientation() {
        if (this.getMeasuredWidth() > this.getMeasuredHeight()) {
            return Configuration.ORIENTATION_LANDSCAPE;
        } else {
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }

    public void getLayout(int width, int height) {
        cellSize = (int) Math.min(width / (game.numSquaresX + 1), height / (game.numSquaresY + 1));
        gridWidth = (int) cellSize / 10;
        paint.setTextAlign(Paint.Align.CENTER);
        screenMiddleX = (int) width / 2;
        screenMiddleY = (int) height / 2;
        getScreenSize = false;
    }

    public MainView(Context context) {
        super(context);
        Resources resources = context.getResources();
        try {
            backgroundRectangle = (Drawable) resources.getDrawable(R.drawable.background_rectangle);
            cellRectangle = (Drawable) resources.getDrawable(R.drawable.cell_rectangle);
        } catch (Exception e) {
            System.out.println("Error getting rectangle?");
        }
        game = new MainGame();
        setOnTouchListener(new InputListener(game));
        game.newGame();
    }

}