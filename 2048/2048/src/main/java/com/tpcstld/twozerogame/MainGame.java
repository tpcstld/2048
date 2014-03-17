package com.tpcstld.twozerogame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tpcstld on 3/12/14.
 */
public class MainGame {

    public Grid grid;
    public AnimationGrid aGrid;
    final int numSquaresX = 4;
    final int numSquaresY = 4;
    final int startTiles = 2;

    int score = 0;
    int highscore = 0;
    boolean won = false;
    boolean lose = false;

    public MainGame() {
    }

    public void newGame() {
        grid = new Grid(numSquaresX, numSquaresY);
        aGrid = new AnimationGrid(numSquaresX, numSquaresY);
        score = 0;
        won = false;
        lose = false;
        addStartTiles();

        record();
    }

    public void addStartTiles() {
        for (int xx = 0; xx < startTiles; xx++) {
            this.addRandomTile();
        }
    }

    public void addRandomTile() {
        if (grid.isCellsAvailable()) {
            int value = Math.random() < 0.9 ? 2 : 4;
            Tile tile = new Tile(grid.randomAvailableCell(), value);
            grid.insertTile(tile);
            aGrid.startAnimation(tile.getX(), tile.getY(), -1, 20);
        }
    }

    public void record() {

    }

    public void prepareTiles() {
        for (Tile[] array : grid.field) {
            for (Tile tile : array) {
                if (grid.isCellOccupied(tile)) {
                    tile.setMergedFrom(null);
                    tile.savePosition();
                }
            }
        }
    }

    public void moveTile(Tile tile, Cell cell) {
        grid.field[tile.getX()][tile.getY()] = null;
        grid.field[cell.getX()][cell.getY()] = tile;
        tile.updatePosition(cell);
    }

    public void move (int direction) {
        // 0: up, 1: right, 2: down, 3: left
        if (lose || won) {
            return;
        }
        Cell vector = getVector(direction);
        List<Integer> traversalsX = buildTraversalsX(vector);
        List<Integer> traversalsY = buildTraversalsY(vector);
        boolean moved = false;

        prepareTiles();

        for (int xx: traversalsX) {
            for (int yy: traversalsY) {
                Cell cell = new Cell(xx, yy);
                Tile tile = grid.getCellContent(cell);

                if (tile != null) {
                    Cell[] positions = findFarthestPosition(cell, vector);
                    Tile next = grid.getCellContent(positions[1]);

                    if (next != null && next.getValue() == tile.getValue() && next.getMergedFrom() == null) {
                        Tile merged = new Tile(positions[1], tile.getValue() * 2);
                        Tile[] temp = {tile, next};
                        merged.setMergedFrom(temp);

                        grid.insertTile(merged);
                        grid.removeTile(tile);

                        // Converge the two tiles' positions
                        tile.updatePosition(positions[1]);

                        // Update the score
                        score = score + merged.getValue();

                        // The mighty 2048 tile
                        if (merged.getValue() == 2048) {
                            won = true;
                        }
                    } else {
                        moveTile(tile, positions[0]);
                    }

                    if (!positionsEqual(cell, tile)) {
                        moved = true;
                    }
                }
            }
        }

        if (moved) {
            addRandomTile();

            if (!movesAvailable()) {
                lose = true;
            }

            this.record();
        }
    }

    public Cell getVector(int direction) {
        Cell[] map = {
                new Cell(0, -1), // up
                new Cell(1, 0),  // right
                new Cell(0, 1),  // down
                new Cell(-1, 0)  // left
        };
        return map[direction];
    }

    public List<Integer> buildTraversalsX(Cell vector) {
        List<Integer> traversals = new ArrayList<Integer>();

        for (int xx = 0; xx < numSquaresX; xx++) {
            traversals.add(xx);
        }
        if (vector.getX() == 1) {
            Collections.reverse(traversals);
        }

       return traversals;
    }

    public List<Integer> buildTraversalsY(Cell vector) {
        List<Integer> traversals = new ArrayList<Integer>();

        for (int xx = 0; xx <numSquaresY; xx++) {
            traversals.add(xx);
        }
        if (vector.getY() == 1) {
            Collections.reverse(traversals);
        }

        return traversals;
    }

    public Cell[] findFarthestPosition(Cell cell, Cell vector) {
        Cell previous;
        Cell nextCell = new Cell(cell.getX(), cell.getY());
        do {
            previous = nextCell;
            nextCell = new Cell(previous.getX() + vector.getX(),
                    previous.getY() + vector.getY());
        } while (grid.isCellWithinBounds(nextCell) && grid.isCellAvailable(nextCell));

        Cell[] answer = {previous, nextCell};
        return answer;
    }
    public boolean movesAvailable() {
        return grid.isCellsAvailable() || tileMatchesAvailable();
    }
    public boolean tileMatchesAvailable() {
        Tile tile;

        for (int xx = 0; xx < numSquaresX; xx++) {
            for (int yy = 0; yy < numSquaresY; yy++) {
                tile = grid.getCellContent(new Cell(xx, yy));

                if (tile != null) {
                    for (int direction = 0; direction < 4; direction++) {
                        Cell vector = getVector(direction);
                        Cell cell = new Cell(xx + vector.getX(), yy + vector.getY());

                        Tile other = grid.getCellContent(cell);

                        if (other != null && other.getValue() == tile.getValue()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean positionsEqual(Cell first, Cell second) {
        return first.getX() == second.getX() && first.getY() == second.getY();
    }
}
