package com.tpcstld.twozerogame;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;

public class Grid {

    public final Tile[][] field;
    private final Tile[][] bufferField;
    private final Deque<Tile[][]> undoStack = new ArrayDeque<>();

    public Grid(int sizeX, int sizeY) {
        field = new Tile[sizeX][sizeY];
        bufferField = new Tile[sizeX][sizeY];
        clearGrid();
    }

    public Cell randomAvailableCell() {
        ArrayList<Cell> availableCells = getAvailableCells();
        if (availableCells.size() >= 1) {
            return availableCells.get((int) Math.floor(Math.random() * availableCells.size()));
        }
        return null;
    }

    private ArrayList<Cell> getAvailableCells() {
        ArrayList<Cell> availableCells = new ArrayList<>();
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] == null) {
                    availableCells.add(new Cell(xx, yy));
                }
            }
        }
        return availableCells;
    }

    public boolean isCellsAvailable() {
        return (getAvailableCells().size() >= 1);
    }

    public boolean isCellAvailable(Cell cell) {
        return !isCellOccupied(cell);
    }

    public boolean isCellOccupied(Cell cell) {
        return (getCellContent(cell) != null);
    }

    public Tile getCellContent(Cell cell) {
        if (cell != null && isCellWithinBounds(cell)) {
            return field[cell.getX()][cell.getY()];
        } else {
            return null;
        }
    }

    public Tile getCellContent(int x, int y) {
        if (isCellWithinBounds(x, y)) {
            return field[x][y];
        } else {
            return null;
        }
    }

    public boolean isCellWithinBounds(Cell cell) {
        return 0 <= cell.getX() && cell.getX() < field.length
                && 0 <= cell.getY() && cell.getY() < field[0].length;
    }

    private boolean isCellWithinBounds(int x, int y) {
        return 0 <= x && x < field.length
                && 0 <= y && y < field[0].length;
    }

    public void insertTile(Tile tile) {
        field[tile.getX()][tile.getY()] = tile;
    }

    public void removeTile(Tile tile) {
        field[tile.getX()][tile.getY()] = null;
    }

    public void saveTiles() {
        Tile[][] snapshot = new Tile[bufferField.length][bufferField[0].length];
        for (int xx = 0; xx < bufferField.length; xx++) {
            for (int yy = 0; yy < bufferField[0].length; yy++) {
                if (bufferField[xx][yy] == null) {
                    snapshot[xx][yy] = null;
                } else {
                    snapshot[xx][yy] = new Tile(xx, yy, bufferField[xx][yy].getValue());
                }
            }
        }
        undoStack.push(snapshot);
    }

    public void prepareSaveTiles() {
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] == null) {
                    bufferField[xx][yy] = null;
                } else {
                    bufferField[xx][yy] = new Tile(xx, yy, field[xx][yy].getValue());
                }
            }
        }
    }

    public void revertTiles() {
        if (!undoStack.isEmpty()) {
            Tile[][] snapshot = undoStack.pop();
            for (int xx = 0; xx < snapshot.length; xx++) {
                for (int yy = 0; yy < snapshot[0].length; yy++) {
                    if (snapshot[xx][yy] == null) {
                        field[xx][yy] = null;
                    } else {
                        field[xx][yy] = new Tile(xx, yy, snapshot[xx][yy].getValue());
                    }
                }
            }
        }
    }

    public boolean hasUndo() {
        return !undoStack.isEmpty();
    }

    public int undoDepth() {
        return undoStack.size();
    }

    public Tile[][][] getUndoSnapshots() {
        Tile[][][] snapshots = new Tile[undoStack.size()][][];
        int i = 0;
        for (Tile[][] snapshot : undoStack) {
            snapshots[i++] = snapshot;
        }
        return snapshots;
    }

    public void pushUndoSnapshot(Tile[][] snapshot) {
        undoStack.push(snapshot);
    }

    public void clearUndoStack() {
        undoStack.clear();
    }

    public void clearGrid() {
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                field[xx][yy] = null;
            }
        }
    }
}
