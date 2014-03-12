package com.tpcstld.twozerogame;

import java.util.ArrayList;

/**
 * Created by tpcstld on 3/12/14.
 */
public class MainGame {

    Grid grid;

    final int numSquaresX = 4;
    final int numSquaresY = 4;
    final int startTiles = 2;

    int score = 0;
    int highscore = 0;
    boolean won = false;
    boolean lose = false;

    public MainGame() {
        newGame();
    }

    public void newGame() {
        ArrayList<Tile> availableCells = new ArrayList<Tile>();
        grid = new Grid(numSquaresX, numSquaresY);
        score = 0;
        won = false;
        lose = false;
        addStartTiles();
    }

    public void addStartTiles() {
        for (int xx = 0; xx < startTiles; xx++) {
            this.addRandomTile();
        }
    }

    public void addRandomTile() {
        if (grid.isCellsAvailable()) {
            int value = Math.random() < 0.9 ? 2 : 4;
            Tile tile = new Tile(this.grid.randomAvailableCell(), value);
            grid.insertTile(tile);
        }
    }

    public void prepareTiles() {
        for (int xx = 0; xx < )
    }
}
