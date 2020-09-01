package com.salab.project.kakikana.viewmodel;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.repository.Repository;

/**
 * ViewModel to cache user-related data. Scope is tied to MainsActivity, so the ViewModel is shared
 * with all Fragments which would like to access user information.
 */
public class UserViewModel extends ViewModel {
    // constants
    private static final String TAG = UserViewModel.class.getSimpleName();

    LiveData<String> userId;
    LiveData<DataSnapshot> userData;
    Repository repository;

    public UserViewModel(){
        repository = new Repository();
        Pair<LiveData<String>, LiveData<DataSnapshot> > userDataPair = repository.getAllUserData();
        userId = userDataPair.first;
        userData = userDataPair.second;

        Log.d(TAG, "ViewModel is instantiated");
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public LiveData<DataSnapshot> getUserData() {
        return userData;
    }
}
