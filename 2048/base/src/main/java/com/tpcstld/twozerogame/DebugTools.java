package com.tpcstld.twozerogame;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools and hooks for debugging.
 *
 * Note to self: when making a custom build, set the versionCode in
 * installed/build.gradle to 1.
 */
class DebugTools {
    private static final boolean DEBUG_ENABLED = false;

    private static final int[][] PREMADE_MAP = {
        {128, 256, 32768, 131072},
        {8, 16, 0, 2},
        {0, 0, 0, 2},
        {0, 0, 0, 0},
    };
    private static final long STARTING_SCORE = 2529244L;

    @Nullable
    static List<Tile> generatePremadeMap() {
        if (!DEBUG_ENABLED) {
            return null;
        }

        if (PREMADE_MAP == null) {
            return null;
        }

        List<Tile> result = new ArrayList<>();
        for (int yy = 0; yy < PREMADE_MAP.length; yy++) {
            for (int xx = 0; xx < PREMADE_MAP[0].length; xx++) {
                if (PREMADE_MAP[yy][xx] == 0) {
                    continue;
                }

                result.add(new Tile(xx, yy, PREMADE_MAP[yy][xx]));
            }
        }

        return result;
    }

    static long getStartingScore() {
        if (!DEBUG_ENABLED) {
            return 0;
        }

        return STARTING_SCORE;
    }
}
