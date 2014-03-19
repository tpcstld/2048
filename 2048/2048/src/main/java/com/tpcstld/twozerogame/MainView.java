package com.tpcstld.twozerogame;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

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
    int TEXT_BROWN;

    int halfNumSquaresX;
    int halfNumSquaresY;

    int startingX;
    int startingY;
    int endingX;
    int endingY;

    int sYAll;
    int titleStartYAll;
    int bodyStartYAll;
    int eYAll;
    int titleWidthHighScore;
    int titleWidthScore;
    long lastFPSTime = System.nanoTime();
    long currentTime = System.nanoTime();

    float titleTextSize;
    float bodyTextSize;

    static final int BASE_ANIMATION_TIME = 100000000;
    static int PADDING_SIZE = 50;
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

        //Drawing the background
        backgroundRectangle.setBounds(startingX, startingY, endingX, endingY);
        backgroundRectangle.draw(canvas);

        //Drawing the score text: Part 2

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            paint.setTextSize(bodyTextSize);

            int bodyWidthHighScore = (int) (paint.measureText("" + game.highScore));
            int bodyWidthScore = (int) (paint.measureText("" + game.score));

            int textWidthHighScore = Math.max(titleWidthHighScore, bodyWidthHighScore) + PADDING_SIZE * 2;
            int textWidthScore = Math.max(titleWidthScore, bodyWidthScore) + PADDING_SIZE * 2;

            int textMiddleHighScore = textWidthHighScore / 2;
            int textMiddleScore = textWidthScore / 2;

            int eXHighScore = endingX;
            int sXHighScore = eXHighScore - textWidthHighScore;

            int eXScore = sXHighScore - PADDING_SIZE;
            int sXScore = eXScore - textWidthScore;

            //Outputting high-scores box
            backgroundRectangle.setBounds(sXHighScore, sYAll, eXHighScore, eYAll);
            backgroundRectangle.draw(canvas);
            paint.setTextSize(titleTextSize);
            paint.setColor(TEXT_BROWN);
            canvas.drawText("HIGH SCORE", sXHighScore + textMiddleHighScore, titleStartYAll, paint);
            paint.setTextSize(bodyTextSize);
            paint.setColor(TEXT_WHITE);
            canvas.drawText("" + game.highScore, sXHighScore + textMiddleHighScore, bodyStartYAll, paint);


            //Outputting scores box
            backgroundRectangle.setBounds(sXScore, sYAll, eXScore, eYAll);
            backgroundRectangle.draw(canvas);
            paint.setTextSize(titleTextSize);
            paint.setColor(TEXT_BROWN);
            canvas.drawText("SCORE", sXScore + textMiddleScore, titleStartYAll, paint);
            paint.setTextSize(bodyTextSize);
            paint.setColor(TEXT_WHITE);
            canvas.drawText("" + game.score, sXScore + textMiddleScore, bodyStartYAll, paint);
        }

        paint.setTextSize(textSize);

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

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            drawRectangle(canvas, cellRectangle[index], (int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            drawCellText(canvas, value, sX, sY);
                        } else if (aCell.getPercentageDone() >= 0.5 && (aCell.getDirection() == 0 || aArray.size() >= 2)) {
                            double percentDone = (aCell.getPercentageDone() - 0.5) * 2;
                            float textScaleSize = (float) (1.125 - Math.abs(percentDone - 0.5) / 4);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
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
                        } else if (aCell.getDirection() != -1) {
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
        cellSize = Math.min(width / (game.numSquaresX + 1), height / (game.numSquaresY + 1));
        gridWidth = cellSize / 7;
        screenMiddleX = width / 2;
        screenMiddleY = height / 2;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize);
        textSize = cellSize * cellSize / Math.max(cellSize, paint.measureText("0000"));
        titleTextSize = textSize / 3;
        bodyTextSize = (int) (textSize / 1.5);
        PADDING_SIZE = (int) (textSize / 3);

        //Grid Dimensions
        halfNumSquaresX = game.numSquaresX / 2;
        halfNumSquaresY = game.numSquaresY / 2;

        startingX = screenMiddleX - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2;
        endingX = screenMiddleX + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2;
        startingY = screenMiddleY - (cellSize + gridWidth) * halfNumSquaresY - gridWidth / 2;
        endingY = screenMiddleY + (cellSize + gridWidth) * halfNumSquaresY + gridWidth / 2;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            startingY = startingY + cellSize / 2;
            endingY = endingY + cellSize / 2;
        }

        paint.setTextSize(titleTextSize);

        int textShiftYAll = (int) ((paint.descent() + paint.ascent()) / 2);
        //static variables
        sYAll = (int) (startingY - cellSize * 1.5);
        titleStartYAll = (int) (sYAll + PADDING_SIZE + titleTextSize / 2 - textShiftYAll);
        bodyStartYAll = (int) (titleStartYAll + PADDING_SIZE + titleTextSize / 2 + bodyTextSize / 2);

        titleWidthHighScore = (int) (paint.measureText("HIGH SCORE"));
        paint.setTextSize(bodyTextSize);
        titleWidthScore = (int) (paint.measureText("SCORE"));
        textShiftYAll = (int) ((paint.descent() + paint.ascent()) / 2);
        eYAll = (int) (bodyStartYAll + textShiftYAll + bodyTextSize / 2 + PADDING_SIZE);

        getScreenSize = false;
    }

    public MainView(Context context) {
        super(context);
        Resources resources = context.getResources();
        //Loading resources
        game = new MainGame(context);
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
            TEXT_BROWN = resources.getColor(R.color.text_brown);
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);

        } catch (Exception e) {
            System.out.println("Error getting rectangle?");
        }
        setOnTouchListener(new InputListener(game));
        game.newGame();
    }

}