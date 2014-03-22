package com.tpcstld.twozerogame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.ArrayList;

public class MainView extends View {

    Paint paint = new Paint();
    public MainGame game;

    boolean getScreenSize = true;
    int cellSize = 0;
    float textSize = 0;
    int gridWidth = 0;
    int screenMiddleX = 0;
    int screenMiddleY = 0;
    int boardMiddleX = 0;
    int boardMiddleY = 0;
    Drawable backgroundRectangle;
    Drawable[] cellRectangle = new Drawable[12];
    Drawable settingsIcon;
    Drawable lightUpRectangle;
    Drawable fadeRectangle;
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

    static int sYIcons;
    static int sXNewGame;

    static int iconSize;
    long lastFPSTime = System.nanoTime();
    long currentTime = System.nanoTime();

    float titleTextSize;
    float bodyTextSize;
    float headerTextSize;
    float instructionsTextSize;
    float gameOverTextSize;

    boolean refreshLastTime = true;

    static final int BASE_ANIMATION_TIME = 100000000;
    static int textPaddingSize = 0;
    static int iconPaddingSize = 0;

    static final float MERGING_ACCELERATION = (float) -0.5;
    static final float INITIAL_VELOCITY = (1 - MERGING_ACCELERATION) / 4;

    @Override
    public void onDraw(Canvas canvas) {
        //Reset the transparency of the screen
        paint.setAlpha(255);

        drawScoreText(canvas);

        drawNewGameButton(canvas);

        drawHeader(canvas);
        //Drawing the instructions
        paint.setTextSize(instructionsTextSize);

        int textShiftY = centerText() * 2;
        canvas.drawText("Swipe to move. 2+2 = 4. Reach 2048.",
                startingX, endingY - textShiftY + textPaddingSize, paint);


        //DRAWING MAIN GAME SCREEN

        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        //Drawing the background
        drawDrawable(canvas, backgroundRectangle, startingX, startingY, endingX, endingY);

        // Outputting the game grid
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                drawDrawable(canvas, cellRectangle[0], sX, sY, eX, eY);
            }
        }

        // Outputting the individual cells
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                Tile currentTile = game.grid.getCellContent(xx,yy);
                if (currentTile != null) {
                    //Get and represent the value of the tile
                    int value = currentTile.getValue();
                    int index = log2(value);

                    //Check for any active animations
                    ArrayList<AnimationCell> aArray = game.aGrid.getAnimationCell(xx, yy);
                    boolean animated = false;
                    for (int i = aArray.size() - 1; i >= 0; i--) {
                        AnimationCell aCell = aArray.get(i);
                        //If this animation is not active, skip it
                        if (aCell.getAnimationType() == MainGame.SPAWN_ANIMATION) {
                            animated = true;
                        }
                        if (!aCell.isActive()) {
                            continue;
                        }

                        if (aCell.getAnimationType() == MainGame.SPAWN_ANIMATION) { // Spawning animation
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) (percentDone);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            drawDrawable(canvas, cellRectangle[index], (int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            drawCellText(canvas, value, sX, sY);
                        } else if (aCell.getAnimationType() == MainGame.MERGE_ANIMATION) { // Merging Animation
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) (1 + INITIAL_VELOCITY * percentDone
                                    + MERGING_ACCELERATION * percentDone * percentDone / 2);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            drawDrawable(canvas, cellRectangle[index], (int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            drawCellText(canvas, value, sX, sY);
                        } else if (aCell.getAnimationType() == MainGame.MOVE_ANIMATION) {  // Moving animation
                            double percentDone = aCell.getPercentageDone();
                            int tempIndex = index;
                            if (aArray.size() >= 2) {
                                tempIndex = tempIndex - 1;
                            }
                            paint.setTextSize(textSize);
                            int previousX = aCell.extras[0];
                            int previousY = aCell.extras[1];
                            int currentX = currentTile.getX();
                            int currentY = currentTile.getY();
                            int dX = (int) ((currentX - previousX) * (cellSize + gridWidth) * (percentDone - 1) * 1.0);
                            int dY = (int) ((currentY - previousY) * (cellSize + gridWidth) * (percentDone - 1) * 1.0);
                            drawDrawable(canvas, cellRectangle[tempIndex], sX + dX, sY + dY, eX + dX, eY + dY);
                            if (index != tempIndex) {
                                drawCellText(canvas, value / 2, sX + dX, sY + dY);
                            } else {
                                drawCellText(canvas, value, sX + dX, sY + dY);
                            }
                        }
                        animated = true;
                    }

                    //No active animations? Just draw the cell
                    if (!animated) {
                        paint.setTextSize(textSize);

                        drawDrawable(canvas, cellRectangle[index], sX, sY, eX, eY);
                        drawCellText(canvas, value , sX, sY);
                    }
                }
            }
        }
        double alphaChange = 1;
        //Animation: Dynamically change the alpha
        for (AnimationCell animation : game.aGrid.globalAnimation) {
            if (animation.getAnimationType() == MainGame.FADE_GLOBAL_ANIMATION) {
                alphaChange = animation.getPercentageDone();
            }

        }
        // Displaying game over
        if (game.won) {
            lightUpRectangle.setAlpha((int) (127 * alphaChange));
            drawDrawable(canvas, lightUpRectangle ,startingX, startingY, endingX, endingY);
            lightUpRectangle.setAlpha(255);
            paint.setColor(TEXT_WHITE);
            paint.setAlpha((int) (255 * alphaChange));
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("You Win!", boardMiddleX, boardMiddleY - centerText(), paint);
        } else if (game.lose) {
            fadeRectangle.setAlpha((int) (127 * alphaChange));
            drawDrawable(canvas, fadeRectangle, startingX, startingY, endingX, endingY);
            fadeRectangle.setAlpha(255);
            paint.setColor(TEXT_BLACK);
            paint.setAlpha((int) (255 * alphaChange));
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Game Over!", boardMiddleX, boardMiddleY - centerText(), paint);
        }

        //Refresh the screen if there is still an animation running
        if (game.aGrid.isAnimationActive()) {
            invalidate(startingX, startingY, endingX, endingY);
            tick();
        //Refresh one last time on game end.
        } else if ((game.won || game.lose) && refreshLastTime) {
            invalidate();
            refreshLastTime = false;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        getLayout(width, height);
    }

    public void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    public void drawCellText(Canvas canvas, int value, int sX, int sY) {
        int textShiftY = centerText();
        if (value >= 8) {
            paint.setColor(TEXT_WHITE);
        } else {
            paint.setColor(TEXT_BLACK);
        }
        canvas.drawText("" + value, sX + cellSize / 2, sY + cellSize / 2 - textShiftY, paint);
    }

    public void drawScoreText(Canvas canvas) {
        //Drawing the score text: Ver 2
        paint.setTextSize(bodyTextSize);

        int bodyWidthHighScore = (int) (paint.measureText("" + game.highScore));
        int bodyWidthScore = (int) (paint.measureText("" + game.score));

        int textWidthHighScore = Math.max(titleWidthHighScore, bodyWidthHighScore) + textPaddingSize * 2;
        int textWidthScore = Math.max(titleWidthScore, bodyWidthScore) + textPaddingSize * 2;

        int textMiddleHighScore = textWidthHighScore / 2;
        int textMiddleScore = textWidthScore / 2;

        int eXHighScore = endingX;
        int sXHighScore = eXHighScore - textWidthHighScore;

        int eXScore = sXHighScore - textPaddingSize;
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

    public void drawNewGameButton(Canvas canvas) {
        if ((game.won || game.lose) && !game.aGrid.isAnimationActive()) {
            drawDrawable(canvas, lightUpRectangle, sXNewGame, sYIcons, sXNewGame + iconSize, sYIcons + iconSize);
        } else {
            drawDrawable(canvas, backgroundRectangle, sXNewGame, sYIcons, sXNewGame + iconSize, sYIcons + iconSize);
        }
        drawDrawable(canvas, settingsIcon, sXNewGame + iconPaddingSize, sYIcons + iconPaddingSize,
                sXNewGame + iconSize - iconPaddingSize, sYIcons + iconSize - iconPaddingSize);
    }

    public void drawHeader(Canvas canvas) {
        //Drawing the header
        paint.setTextSize(headerTextSize);
        paint.setColor(TEXT_BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        int textShiftY = centerText() * 2;
        int headerStartY = sYAll - textShiftY;
        canvas.drawText("2048", startingX, headerStartY, paint);
    }

    public void tick() {
        currentTime = System.nanoTime();
        game.aGrid.tickAll(currentTime - lastFPSTime);
        lastFPSTime = currentTime;
    }

    public void resyncTime() {
        lastFPSTime = System.nanoTime();
    }

    public static int log2(int n){
        if(n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    public void getLayout(int width, int height) {
        cellSize = Math.min(width / (game.numSquaresX + 1), height / (game.numSquaresY + 3));
        gridWidth = cellSize / 7;
        screenMiddleX = width / 2;
        screenMiddleY = height / 2;
        boardMiddleX = screenMiddleX;
        boardMiddleY = screenMiddleY  + cellSize / 2;
        iconSize = cellSize / 2;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize);
        textSize = cellSize * cellSize / Math.max(cellSize, paint.measureText("0000"));
        titleTextSize = textSize / 3;
        bodyTextSize = (int) (textSize / 1.5);
        instructionsTextSize = (int) (textSize / 1.5);
        headerTextSize = textSize * 2;
        gameOverTextSize = textSize * 2;
        textPaddingSize = (int) (textSize / 3);
        iconPaddingSize = (int) (textSize / 5);

        //Grid Dimensions
        halfNumSquaresX = game.numSquaresX / 2;
        halfNumSquaresY = game.numSquaresY / 2;

        startingX = boardMiddleX - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2;
        endingX = boardMiddleX + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2;
        startingY = boardMiddleY - (cellSize + gridWidth) * halfNumSquaresY - gridWidth / 2;
        endingY = boardMiddleY + (cellSize + gridWidth) * halfNumSquaresY + gridWidth / 2;

        paint.setTextSize(titleTextSize);

        int textShiftYAll = centerText();
        //static variables
        sYAll = (int) (startingY - cellSize * 1.5);
        titleStartYAll = (int) (sYAll + textPaddingSize + titleTextSize / 2 - textShiftYAll);
        bodyStartYAll = (int) (titleStartYAll + textPaddingSize + titleTextSize / 2 + bodyTextSize / 2);

        titleWidthHighScore = (int) (paint.measureText("HIGH SCORE"));
        titleWidthScore = (int) (paint.measureText("SCORE"));
        paint.setTextSize(bodyTextSize);
        textShiftYAll = centerText();
        eYAll = (int) (bodyStartYAll + textShiftYAll + bodyTextSize / 2 + textPaddingSize);

        sYIcons = (startingY + eYAll) / 2 - iconSize / 2;
        sXNewGame = (endingX - iconSize);
        resyncTime();
        getScreenSize = false;
    }

    public int centerText() {
        return (int) ((paint.descent() + paint.ascent()) / 2);
    }

    public MainView(Context context) {
        super(context);
        Resources resources = context.getResources();
        //Loading resources
        game = new MainGame(context, this);
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
            settingsIcon = resources.getDrawable(R.drawable.ic_action_refresh);
            lightUpRectangle = resources.getDrawable(R.drawable.light_up_rectangle);
            fadeRectangle = resources.getDrawable(R.drawable.fade_rectangle);
            TEXT_WHITE = resources.getColor(R.color.text_white);
            TEXT_BLACK = resources.getColor(R.color.text_black);
            TEXT_BROWN = resources.getColor(R.color.text_brown);
            this.setBackgroundColor(resources.getColor(R.color.background));
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);
        } catch (Exception e) {
            System.out.println("Error getting assets?");
        }
        setOnTouchListener(new InputListener(this));
        game.newGame();
    }

}