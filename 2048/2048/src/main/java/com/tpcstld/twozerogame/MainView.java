package com.tpcstld.twozerogame;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;

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

    static final double[] EXPAND_ANIMATION_FACTOR = {
            0.250,
            0.500,
            0.750,
            1.000,
    };

    final long SPF = 1000000000 / 30;
    long lastFPSTime = System.nanoTime();
    long currentTime = System.nanoTime();
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
                canvas.drawText(text, endingX, startingY + textSize * 3, paint);
            }
        }

        paint.setTextAlign(Paint.Align.CENTER);
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                float sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                float eX = sX + cellSize;
                float sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                float eY = sY + cellSize;

                if (game.grid.getCellContent(new Cell(xx, yy)) != null) {
                    float textScaleSize = 1;
                    float cellScaleSize = 0;
                    AnimationCell aCell = game.aGrid.getAnimationCell(xx, yy);
                    if (aCell != null) {
                        if (aCell.getDirection() == -1) {
                            textScaleSize = (float) (aCell.getPercentageDone());
                            cellScaleSize = (float) (cellSize / 2 * (1 - aCell.getPercentageDone()));
                        }
                    }
                    paint.setTextSize(textSize * textScaleSize);
                    int value = game.grid.getCellContent(new Cell(xx, yy)).getValue();
                    int index = log2(value);

                    cellRectangle[index].setBounds((int) (sX  + cellScaleSize),(int) (sY  + cellScaleSize),(int) (eX  - cellScaleSize),(int) (eY  - cellScaleSize));
                    cellRectangle[index].draw(canvas);

                    int textShiftY = (int) ((paint.descent() + paint.ascent()) / 2);
                    if (value >= 8) {
                        paint.setColor(TEXT_WHITE);
                    } else {
                        paint.setColor(TEXT_BLACK);
                    }
                    canvas.drawText("" + game.grid.field[xx][yy].getValue(), sX + cellSize / 2, sY + cellSize / 2 - textShiftY, paint);
                } else {
                    cellRectangle[0].setBounds((int)sX, (int)sY, (int)eX, (int)eY);
                    cellRectangle[0].draw(canvas);
                }
            }
        }
        tick();
        invalidate();
    }

    public void tick() {
        currentTime = System.nanoTime();
        game.aGrid.tickAll(currentTime - lastFPSTime);
        lastFPSTime = currentTime;
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
            backgroundRectangle = (Drawable) resources.getDrawable(R.drawable.background_rectangle);
            cellRectangle[0] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle);
            cellRectangle[1] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_2);
            cellRectangle[2] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_4);
            cellRectangle[3] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_8);
            cellRectangle[4] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_16);
            cellRectangle[5] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_32);
            cellRectangle[6] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_64);
            cellRectangle[7] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_128);
            cellRectangle[8] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_256);
            cellRectangle[9] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_512);
            cellRectangle[10] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_1024);
            cellRectangle[11] = (Drawable) resources.getDrawable(R.drawable.cell_rectangle_2048);
            TEXT_WHITE = resources.getColor(R.color.text_white);
            TEXT_BLACK = resources.getColor(R.color.text_black);
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);
        } catch (Exception e) {
            System.out.println("Error getting rectangle?");
        }
        game = new MainGame();
        setOnTouchListener(new InputListener(game));
        game.newGame();
    }

}