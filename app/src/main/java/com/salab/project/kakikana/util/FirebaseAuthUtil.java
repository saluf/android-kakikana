package com.salab.project.kakikana.util;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.createUserData;

/**
 * Utility class abstract FirebaseAuth related code.
 */
public class FirebaseAuthUtil {

    // constants
    private static final String TAG = FirebaseAuthUtil.class.getSimpleName();
    private static final FirebaseAuth authInstance = FirebaseAuth.getInstance();

    public static Task<FirebaseUser> getFirebaseUser(){
        // return current user or create one
        final TaskCompletionSource<FirebaseUser> userTask = new TaskCompletionSource<>();

        if (isSignedIn()){
            userTask.setResult(authInstance.getCurrentUser());
        } else {
            // Create a anonymous account for every user
            authInstance.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = authInstance.getCurrentUser();
                    if (currentUser != null){
                        createUserData(currentUser.getUid());
                        userTask.setResult(currentUser);
                    }
                } else {
                    // TODO: handle sign-in failure, user should not proceed under this circumstance
//                    userTask.setException(task.getException());
                    Log.w(TAG, "User failed to sign in");
                }
            });
        }
        return userTask.getTask();
    }

    private static boolean isSignedIn() {
        // return null user data -> not signed in yet (either new or signed out)
        return authInstance.getCurrentUser() != null;
    }
}
