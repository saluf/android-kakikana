package com.salab.project.kakikana.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Wrapper a FirebaseAuth current user in LiveData allowing to automatically update its state
 * whenever user state change
 * ref: https://code.luasoftware.com/tutorials/android/android-firebase-auth-authstatelistener-as-livedata/
 */
public class FirebaseUserLiveData extends LiveData<FirebaseUser> implements FirebaseAuth.AuthStateListener {

    // constants
    private static final String TAG = FirebaseUserLiveData.class.getSimpleName();

    // global variables
    private FirebaseAuth auth;
    private String previousUid;

    public FirebaseUserLiveData(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    protected void onActive() {
        super.onActive();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        auth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        // FirebaseAuth calls when register a listener, a user signed in, the user signed out, current user changed
        String uid = firebaseAuth.getUid();
        if (uid == null || !uid.equals(previousUid)){
            // exclude callback triggered by registering a listener (null needs to notify)
            setValue(firebaseAuth.getCurrentUser());
            Log.d(TAG, "Auth state changed.");
        }

    }
}
