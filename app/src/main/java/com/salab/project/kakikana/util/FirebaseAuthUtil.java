package com.salab.project.kakikana.util;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Utility class abstract FirebaseAuth related code.
 */
public class FirebaseAuthUtil {

    // constants
    private static final String TAG = FirebaseAuthUtil.class.getSimpleName();
    private static FirebaseAuth authInstance;

    public static FirebaseAuth getInstance() {
        if (authInstance == null) {
            authInstance = FirebaseAuth.getInstance();
        }
        return authInstance;
    }

    public static String getUid() {
        if (getInstance().getCurrentUser() != null) {
            return getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public static Task<FirebaseUser> linkWithGoogle(AuthCredential credential) {

        final TaskCompletionSource<FirebaseUser> userTask = new TaskCompletionSource<>();

        FirebaseUser currentUser = getInstance().getCurrentUser();
        if (currentUser != null) {
            getInstance().getCurrentUser().linkWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        userTask.setResult(currentUser);
                        Log.i(TAG, "linked with Google successfully");
                    })
                    .addOnFailureListener(e -> {
                        userTask.setException(e);
                        Log.d(TAG, "failed to link with Google. " + e.getMessage());
                    });
        } else {
            Log.wtf(TAG, "User needs to log in before link");
        }

        return userTask.getTask();
    }

    public static Task<FirebaseUser> signInAnonymously() {
        // create an anonymous account for users to keep their progress
        // can be link with Google (or other auth methods) latter
        final TaskCompletionSource<FirebaseUser> loginTask = new TaskCompletionSource<>();

        getInstance().signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful() && getInstance().getCurrentUser() != null) {

                FirebaseUser currentUser = getInstance().getCurrentUser();
                loginTask.setResult(currentUser);
                Log.i(TAG, "succeeded to sign in anonymously.");

            } else {
                Log.w(TAG, "failed to sign in anonymously. " + task.getException());
            }
        });
        return loginTask.getTask();
    }

    public static Task<FirebaseUser> signInWithCredential(AuthCredential credential) {
        // create an account and link with Google in one step

        final TaskCompletionSource<FirebaseUser> loginTask = new TaskCompletionSource<>();

        getInstance().signInWithCredential(credential)
                .addOnCompleteListener((OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful() && getInstance().getCurrentUser() != null) {

                        FirebaseUser currentUser = getInstance().getCurrentUser();
                        loginTask.setResult(currentUser);
                        Log.i(TAG, "succeeded to sign in with Google.");

                    } else {
                        Log.w(TAG, "failed to sign in with Google. " + task.getException());
                    }
                });

        return loginTask.getTask();
    }

    public static void userSignOut() {
        getInstance().signOut();
        Log.i(TAG, "Current user have been signed out");
    }
}
