package com.tpcstld.twozerogame;

import java.util.ArrayList;

/**
 * Created by tpcstld on 3/12/14.
 */
public class Grid {

    Tile[][] field;

    public Grid(int sizeX, int sizeY) {
        field = new Tile[sizeX][sizeY];
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                field[xx][yy] = null;
            }
        }
    }

    public Cell randomAvailableCell() {
       ArrayList<Cell> availableCells = getAvailableCells();
       if (availableCells.size() >= 1) {
           return availableCells.get((int) Math.floor(Math.random() * availableCells.size()));
       };
       return null;
    }

    public ArrayList<Cell> getAvailableCells() {
        ArrayList<Cell> availableCells = new ArrayList<Cell>();
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (isCellAvailable(field[xx][yy])) {
                    availableCells.add(field[xx][yy]);
                }
            }
        }
        return availableCells;
    }

    public boolean isCellsAvailable() {
        return (getAvailableCells().size() >= 1);
    }

    public boolean isCellAvailable(Tile cell) {
        return !isCellOccupied(cell);
    }

    public boolean isCellOccupied(Tile cell) {
        return (getCellContent(cell) > 0);
    }

    public int getCellContent(Tile cell) {
        if (cell != null && isCellWithinBounds(cell)) {
            return cell.getValue();
        } else {
            return -1;
        }
    }

    public boolean isCellWithinBounds(Tile cell) {
        return 0 <= cell.getX() && cell.getX() < field.length
            && 0 <= cell.getY() && cell.getY() < field[0].length;
    }

    public void insertTile(Tile tile) {
        field[tile.getX()][tile.getY()] = tile;
    }

    public void removeTile(Tile tile) {
        field[tile.getX()][tile.getY()] = null;
    }
}
