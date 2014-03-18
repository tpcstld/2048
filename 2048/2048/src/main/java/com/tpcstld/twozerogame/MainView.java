package com.tpcstld.twozerogame;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
    float textSize = 0;
    int gridWidth = 0;
    int screenMiddleX = 0;
    int screenMiddleY = 0;
    int orientation = Configuration.ORIENTATION_UNDEFINED;
    Drawable backgroundRectangle;
    Drawable[] cellRectangle = new Drawable[12];
    int TEXT_BLACK;
    int TEXT_WHITE;

    long lastFPSTime = System.nanoTime();
    long currentTime = System.nanoTime();

    static final int BASE_ANIMATION_TIME = 100000000;
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
        paint.setTextSize(textSize / 2);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            int textShiftY = (int) ((paint.descent() + paint.ascent()) / 2);
            float y = endingY + textSize / 2 - textShiftY;
            String text = "";
            if (game.lose) {
                text = " GAME OVER";
            } else if (game.won) {
                text = " WINNER!";
            }
            if (!text.equals(""))  {
                canvas.drawText("Score: " + game.score + text, startingX, y, paint);
                canvas.drawText("High Score: " + game.highScore, startingX, y + textSize, paint);
            }
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            String text = "";
            if (game.lose) {
                text = "GAME OVER";
            } else if (game.won) {
                text = "WINNER!";
            }
            if (!text.equals("")) {
                canvas.drawText("Score: ", endingX, startingY + textSize, paint);
                canvas.drawText("" + game.score, endingX, startingY + textSize * 2, paint);
                canvas.drawText("High Score: ", endingX, startingY + textSize * 3, paint);
                canvas.drawText("" + game.highScore, endingX, startingY + textSize * 4, paint);
                canvas.drawText(text, endingX, startingY + textSize * 5, paint);
            }
        }

        paint.setTextAlign(Paint.Align.CENTER);

        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                drawRectangle(canvas, cellRectangle[0], sX, sY, eX, eY);
            }
        }

        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                Tile currentTile = game.grid.getCellContent(new Cell(xx, yy));
                if (currentTile != null) {
                    ArrayList<AnimationCell> aArray = game.aGrid.getAnimationCell(xx, yy);
                    int value = currentTile.getValue();
                    int index = log2(value);
                    for (AnimationCell aCell : aArray) {

                        if (aCell.getDirection() == -1 && aCell.getPercentageDone() >= 0.5) { //Spawning animation
                            double percentDone = (aCell.getPercentageDone() - 0.5) * 2;
                            float textScaleSize = (float) (percentDone);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = (float) (cellSize / 2 * (1 - textScaleSize));
                            drawRectangle(canvas, cellRectangle[index], (int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            drawCellText(canvas, value, sX, sY);
                        } else if (aCell.getDirection() == -1) {
                            //Draw Nothing
                        } else if (aCell.getPercentageDone() >= 0.5 && (aCell.getDirection() == 0 || aArray.size() >= 2)) {
                            double percentDone = (aCell.getPercentageDone() - 0.5) * 2;
                            float textScaleSize = (float) (1.125 - Math.abs(percentDone - 0.5) / 4);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = (float) (cellSize / 2 * (1 - textScaleSize));
                            drawRectangle(canvas, cellRectangle[index], (int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            drawCellText(canvas, value, sX, sY);
                        } else if (aCell.getDirection() == 0 || aArray.size() == 2) {  //Merging animation
                            paint.setTextSize(textSize);
                            if (aArray.size() <= 1) {
                                drawRectangle(canvas, cellRectangle[index-1], sX, sY, eX, eY);
                                drawCellText(canvas, value / 2, sX, sY);
                            }
                            int previousX = aCell.extra;
                            int previousY = aCell.extra2;
                            int currentX = currentTile.getX();
                            int currentY = currentTile.getY();
                            int dX = (int) ((currentX - previousX) * (cellSize + gridWidth) * (aCell.getPercentageDone()*2 - 1) * 1.0);
                            int dY = (int) ((currentY - previousY) * (cellSize + gridWidth) * (aCell.getPercentageDone()*2 - 1) * 1.0);
                            drawRectangle(canvas, cellRectangle[index-1], sX + dX, sY + dY, eX + dX, eY + dY);

                            drawCellText(canvas, value / 2, sX + dX, sY + dY);
                        } else if (aCell.getDirection() == 1 && aCell.getPercentageDone() <= 0.5) { //Moving, no merge animation

                            paint.setTextSize(textSize);
                            int previousX = aCell.extra;
                            int previousY = aCell.extra2;
                            int currentX = currentTile.getX();
                            int currentY = currentTile.getY();
                            int dX = (int) ((currentX - previousX) * (cellSize + gridWidth) * (aCell.getPercentageDone()*2 - 1) * 1.0);
                            int dY = (int) ((currentY - previousY) * (cellSize + gridWidth) * (aCell.getPercentageDone()*2 - 1) * 1.0);
                            drawRectangle(canvas, cellRectangle[index], sX + dX, sY + dY, eX + dX, eY + dY);
                            drawCellText(canvas, value, sX + dX, sY + dY);
                        } else {
                            paint.setTextSize(textSize);

                            drawRectangle(canvas, cellRectangle[index], sX, sY, eX, eY);
                            drawCellText(canvas, value , sX, sY);
                        }
                    }
                    if (aArray.size() == 0) {
                        paint.setTextSize(textSize);

                        drawRectangle(canvas, cellRectangle[index], sX, sY, eX, eY);
                        drawCellText(canvas, value , sX, sY);
                    }
                }
            }
        }
        tick();
        invalidate();
    }

    public void drawRectangle(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    public void drawCellText(Canvas canvas, int value, int sX, int sY) {
        int textShiftY = (int) ((paint.descent() + paint.ascent()) / 2);
        if (value >= 8) {
            paint.setColor(TEXT_WHITE);
        } else {
            paint.setColor(TEXT_BLACK);
        }
        canvas.drawText("" + value, sX + cellSize / 2, sY + cellSize / 2 - textShiftY, paint);
    }

    public void tick() {
        currentTime = System.nanoTime();
        game.aGrid.tickAll(currentTime - lastFPSTime);
        lastFPSTime = currentTime;
        if (game.spawnTile && !game.aGrid.isAnimationActive()) {
            game.addTile();
        }
    }

    public static int log2(int n){
        if(n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
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
        gridWidth = (int) cellSize / 7;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize);
        textSize = cellSize * cellSize / Math.max(cellSize, paint.measureText("0000"));
        screenMiddleX = (int) width / 2;
        screenMiddleY = (int) height / 2;
        getScreenSize = false;
    }

    public MainView(Context context) {
        super(context);
        Resources resources = context.getResources();
        //Loading resources
        try {
            backgroundRectangle =  resources.getDrawable(R.drawable.background_rectangle);
            cellRectangle[0] =  resources.getDrawable(R.drawable.cell_rectangle);
            cellRectangle[1] =  resources.getDrawable(R.drawable.cell_rectangle_2);
            cellRectangle[2] =  resources.getDrawable(R.drawable.cell_rectangle_4);
            cellRectangle[3] =  resources.getDrawable(R.drawable.cell_rectangle_8);
            cellRectangle[4] =  resources.getDrawable(R.drawable.cell_rectangle_16);
            cellRectangle[5] =  resources.getDrawable(R.drawable.cell_rectangle_32);
            cellRectangle[6] =  resources.getDrawable(R.drawable.cell_rectangle_64);
            cellRectangle[7] =  resources.getDrawable(R.drawable.cell_rectangle_128);
            cellRectangle[8] =  resources.getDrawable(R.drawable.cell_rectangle_256);
            cellRectangle[9] =  resources.getDrawable(R.drawable.cell_rectangle_512);
            cellRectangle[10] = resources.getDrawable(R.drawable.cell_rectangle_1024);
            cellRectangle[11] = resources.getDrawable(R.drawable.cell_rectangle_2048);
            TEXT_WHITE = resources.getColor(R.color.text_white);
            TEXT_BLACK = resources.getColor(R.color.text_black);
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);
        } catch (Exception e) {
            System.out.println("Error getting rectangle?");
        }
        game = new MainGame(context);
        setOnTouchListener(new InputListener(game));
        game.newGame();
    }

}