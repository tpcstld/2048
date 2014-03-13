package com.tpcstld.twozerogame;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    MainView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(getBaseContext());
        if (savedInstanceState != null) {
            Tile[][] field = view.game.grid.field;
            int[][] saveState = new int[field.length][field[0].length];
            for (int xx = 0; xx < saveState.length; xx++) {
                saveState[xx] = savedInstanceState.getIntArray("" + xx);
            }
            for (int xx = 0; xx < saveState.length; xx++) {
                for (int yy = 0; yy < saveState[0].length; yy++) {
                    if (saveState[xx][yy] != 0) {
                        view.game.grid.field[xx][yy] = new Tile(xx, yy, saveState[xx][yy]);
                    } else {
                        view.game.grid.field[xx][yy] = null;
                    }
                }
            }
            view.game.score = savedInstanceState.getInt("score");
            view.game.won = savedInstanceState.getBoolean("won");
            view.game.lose = savedInstanceState.getBoolean("lose");
        }
        setContentView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                view.game.newGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Tile[][] field = view.game.grid.field;
        int[][] saveState = new int[field.length][field[0].length];
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] != null) {
                    saveState[xx][yy] = field[xx][yy].getValue();
                } else {
                    saveState[xx][yy] = 0;
                }
            }
        }
        for (int xx = 0; xx < saveState.length; xx++) {
            savedInstanceState.putIntArray("" + xx, saveState[xx]);
        }
        savedInstanceState.putInt("score", view.game.score);
        savedInstanceState.putBoolean("won", view.game.won);
        savedInstanceState.putBoolean("lose", view.game.lose);
    }
}
