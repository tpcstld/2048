package com.tpcstld.twozerogame;

import android.content.SharedPreferences;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

public class MainActivity extends AppCompatActivity {

    private static final String WIDTH          = "width";
    private static final String HEIGHT         = "height";
    private static final String SCORE          = "score";
    private static final String HIGH_SCORE     = "high score temp";
    private static final String GAME_STATE     = "game state";
    private static final String UNDO_DEPTH     = "undo_depth";
    // Per-level keys: "undo_score_N", "undo_game_state_N", "undo_grid_N_xx_yy"

    private static final String NO_LOGIN_PROMPT = "no_login_prompt";

    private static final int RC_SIGN_IN = 9001;

    private boolean firstLoginAttempt = false;

    private MainView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        setContentView(view);

        // Set status bar color
        setStatusBarColor(getWindow(), getResources().getColor(R.color.status_background));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            view.game.move(2);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            view.game.move(0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            view.game.move(3);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            view.game.move(1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("hasState", true);
        save();
    }

    protected void onPause() {
        super.onPause();
        save();
    }

    private void save() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        // --- Save live grid ---
        Tile[][] field = view.game.grid.field;
        editor.putInt(WIDTH, field.length);
        editor.putInt(HEIGHT, field.length);
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                editor.putInt(xx + " " + yy,
                        field[xx][yy] != null ? field[xx][yy].getValue() : 0);
            }
        }

        // --- Save score and game state ---
        editor.putLong(SCORE, view.game.score);
        editor.putLong(HIGH_SCORE, view.game.highScore);
        editor.putInt(GAME_STATE, view.game.gameState);

        // --- Serialize undo stack ---
        // getUndoSnapshots() returns snapshots with index 0 = top (most recent).
        Tile[][][] snapshots = view.game.grid.getUndoSnapshots();
        int depth = snapshots.length;
        editor.putInt(UNDO_DEPTH, depth);

        Long[]    scores     = view.game.scoreStack.toArray(new Long[0]);
        Integer[] gameStates = view.game.gameStateStack.toArray(new Integer[0]);

        for (int i = 0; i < depth; i++) {
            editor.putLong("undo_score_" + i,      scores[i]);
            editor.putInt("undo_game_state_" + i,  gameStates[i]);
            Tile[][] snap = snapshots[i];
            for (int xx = 0; xx < snap.length; xx++) {
                for (int yy = 0; yy < snap[0].length; yy++) {
                    editor.putInt("undo_grid_" + i + "_" + xx + "_" + yy,
                            snap[xx][yy] != null ? snap[xx][yy].getValue() : 0);
                }
            }
        }

        editor.apply();
    }

    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        // Stopping all animations
        view.game.aGrid.cancelAnimations();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int w = view.game.grid.field.length;
        int h = view.game.grid.field[0].length;

        // --- Load live grid ---
        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int value = settings.getInt(xx + " " + yy, -1);
                if (value > 0) {
                    view.game.grid.field[xx][yy] = new Tile(xx, yy, value);
                } else if (value == 0) {
                    view.game.grid.field[xx][yy] = null;
                }
            }
        }

        // --- Load score and game state ---
        view.game.score     = settings.getLong(SCORE,      view.game.score);
        view.game.highScore = settings.getLong(HIGH_SCORE, view.game.highScore);
        view.game.gameState = settings.getInt(GAME_STATE,  view.game.gameState);

        // --- Rebuild undo stack ---
        // Stacks must be cleared before rebuilding to avoid stale data on resume.
        view.game.grid.clearUndoStack();
        view.game.scoreStack.clear();
        view.game.gameStateStack.clear();

        int depth = settings.getInt(UNDO_DEPTH, 0);
        // Push oldest first so that index 0 ends up on top (most recent).
        for (int i = depth - 1; i >= 0; i--) {
            // Rebuild tile snapshot for level i
            Tile[][] snap = new Tile[w][h];
            for (int xx = 0; xx < w; xx++) {
                for (int yy = 0; yy < h; yy++) {
                    int v = settings.getInt("undo_grid_" + i + "_" + xx + "_" + yy, -1);
                    snap[xx][yy] = (v > 0) ? new Tile(xx, yy, v) : null;
                }
            }
            view.game.grid.pushUndoSnapshot(snap);
            view.game.scoreStack.push(settings.getLong("undo_score_" + i, 0));
            view.game.gameStateStack.push(settings.getInt("undo_game_state_" + i, 0));
        }
    }

    private void setStatusBarColor(Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            window.getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsets onApplyWindowInsets(@NonNull View view, @NonNull WindowInsets windowInsets) {
                    Insets statusBarInsets = windowInsets.getInsets(WindowInsets.Type.statusBars());
                    view.setBackgroundColor(color);

                    // Adjust padding to avoid overlap
                    view.setPadding(0, statusBarInsets.top, 0, 0);
                    return windowInsets;
                }
            });

        } else {
            // For Android 14 and below
            window.setStatusBarColor(color);
        }
    }
}
