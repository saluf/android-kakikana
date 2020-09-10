package com.salab.project.kakikana.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.repository.Repository;

/**
 * ViewModel to cache user-related data. Scope is tied to MainsActivity, so the ViewModel is shared
 * with all Fragments which would like to access user information.
 */
public class UserViewModel extends ViewModel {
    // constants
    private static final String TAG = UserViewModel.class.getSimpleName();

    Repository repository;

    LiveData<FirebaseUser> user;
    LiveData<DataSnapshot> userData;

    public UserViewModel() {
        repository = new Repository();
        user = repository.getUser();

    }

    public void loadUserData() {
        if (userData != null) {
            userData = null;
        }

        if (user.getValue() != null) {
            String uid = user.getValue().getUid();
            userData = repository.getUserData(uid);
        }
    }

    public void signIn() {
        repository.signInAnonymously();
    }

    public void signIn(AuthCredential credential) {
        repository.signInWithCredential(credential);
    }

    public void signOut() {
        repository.userSignOut();
    }

    public void linkWithGoogle(AuthCredential credential) {
        repository.linkOrSignInWithGoogle(credential);
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public LiveData<DataSnapshot> getUserData() {
        return userData;
    }


}
