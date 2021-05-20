package com.tpcstld.twozerogame.snapshot;

import androidx.annotation.Nullable;

import com.google.android.gms.games.snapshot.Snapshot;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * The data in a snapshot.
 */
public class SnapshotData {
    private static long VERSION_NUMBER = 1;

    private long highScore;

    public SnapshotData(long highScore) {
        this.highScore = highScore;
    }

    public long getHighScore() {
        return highScore;
    }

    public byte[] serialize() {
        return (VERSION_NUMBER + "," + highScore).getBytes();
    }

    @Nullable
    public static SnapshotData deserialize(byte[] bytes) {
        List<String> data = Arrays.asList(new String(bytes).split(","));
        if (!data.get(0).equals(Long.toString(VERSION_NUMBER))) {
            return null;
        }
        return new SnapshotData(Long.parseLong(data.get(1)));
    }

    @Nullable
    public static SnapshotData deserialize(Snapshot snapshot) {
        try {
            byte[] bytes = snapshot.getSnapshotContents().readFully();
            return SnapshotData.deserialize(bytes);
        } catch (IOException ignored) {
            return null;
        }
    }
}
