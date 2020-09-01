package com.salab.project.kakikana.repository;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.util.FirebaseAuthUtil;
import com.salab.project.kakikana.viewmodel.FirebaseQueryLiveData;

import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.getScoreboardUsersQuery;
import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.getUserDatabaseReference;

/**
 * A repository coordinate data requirement from ViewModels and data providers, eg. Firebase database,
 * FirebaseAuth, and local storage.
 */
public class Repository {

    // constants
    private static final String TAG = Repository.class.getSimpleName();

    public Pair<LiveData<String>, LiveData<DataSnapshot>> getAllUserData(){
        // user id is saved in FirebaseAuth, additional data is saved in Firebase Database

        final MutableLiveData<String> userId = new MutableLiveData<>();
        final FirebaseQueryLiveData userData = new FirebaseQueryLiveData();

        FirebaseAuthUtil.getFirebaseUser().addOnSuccessListener(firebaseUser -> {
            // cannot get Firebase user data unless there is user id
            String uid = firebaseUser.getUid();
            userId.setValue(uid);
            userData.setDatabaseReference(getUserDatabaseReference(uid));
        }).addOnFailureListener(e -> {
            // TODO: handle cannot get user exceptions
        });
        return new Pair<>(userId, userData);
    }

    public LiveData<DataSnapshot> getScoreboardUsers(){
        return new FirebaseQueryLiveData(getScoreboardUsersQuery());
    }
}
