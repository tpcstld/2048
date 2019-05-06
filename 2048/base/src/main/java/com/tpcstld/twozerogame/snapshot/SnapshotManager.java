package com.tpcstld.twozerogame.snapshot;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Manages the Google Cloud Save snapshot.
 */
public class SnapshotManager {

    private static boolean saveInProgress = false;
    private static SnapshotData lastSnapshot = null;

    public interface Callback {
        void run(@NonNull SnapshotData data);
    }

    private static final String FILE_NAME = "2048";
    // TODO: Move this to a resource.
    private static final String FILE_DESCRIPTION = "2048 highscore save.";

    public static void loadSnapshot(Context context, final Callback callback) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            SnapshotsClient snapshotsClient = Games.getSnapshotsClient(context, account);
            snapshotsClient.open(FILE_NAME, true, SnapshotsClient.RESOLUTION_POLICY_HIGHEST_PROGRESS)
                    .addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
                        @Override
                        public void onComplete(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) {
                            Snapshot snapshot = getSnapshotFromTask(task);
                            if (snapshot == null) {
                                return;
                            }

                            SnapshotData data = SnapshotData.deserialize(snapshot);
                            if (data != null) {
                                callback.run(data);
                            }
                        }
                    });
        }
    }

    public static void saveSnapshot(@NonNull Context context, @NonNull final SnapshotData data) {
        lastSnapshot = data;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            if (saveInProgress) {
                return;
            }

            saveInProgress = true;
            final SnapshotsClient snapshotsClient = Games.getSnapshotsClient(context, account);
            snapshotsClient.open(FILE_NAME, true, SnapshotsClient.RESOLUTION_POLICY_HIGHEST_PROGRESS)
                    .addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
                        @Override
                        public void onComplete(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) {
                            saveInProgress = false;

                            Snapshot snapshot = getSnapshotFromTask(task);
                            if (snapshot == null) {
                                return;
                            }

                            SnapshotData dataToSave = lastSnapshot;

                            // Do not overwrite a higher high-score.
                            SnapshotData originalData = SnapshotData.deserialize(snapshot);
                            if (originalData != null && originalData.getHighScore() > dataToSave.getHighScore()) {
                                return;
                            }

                            snapshot.getSnapshotContents().writeBytes(dataToSave.serialize());

                            SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                                    .setDescription(FILE_DESCRIPTION)
                                    .setProgressValue(dataToSave.getHighScore())
                                    .build();

                            snapshotsClient.commitAndClose(snapshot, metadataChange);
                        }
                    });
        }
    }

    private static Snapshot getSnapshotFromTask(Task<SnapshotsClient.DataOrConflict<Snapshot>> task) {
        if (!task.isSuccessful()) {
            return null;
        }
        if (task.getResult() == null) {
            return null;
        }
        return task.getResult().getData();
    }
}
