package com.salab.project.kakikana.repository;

import android.content.Context;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.util.FirebaseAuthUtil;
import com.salab.project.kakikana.viewmodel.FirebaseQueryLiveData;

import java.util.List;

import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.getScoreboardUsersQuery;
import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.getUserDatabaseReference;
import static com.salab.project.kakikana.util.KanaUtil.processJsonIntoKana;

/**
 * A repository coordinate data requirement from ViewModels and data providers, eg. Firebase database,
 * FirebaseAuth, and local storage.
 */
public class Repository {

    // constants
    private static final String TAG = Repository.class.getSimpleName();
    public static final int KANA_JSON_TABLE_RESOURCE = R.raw.kana_table;

    private Context context;

    public Repository() {
    }

    public Repository(Context context) {
        // Context is needed to read Resources
        this.context = context;
    }

    public Pair<LiveData<String>, LiveData<DataSnapshot>> getAllUserData() {
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

    public LiveData<DataSnapshot> getScoreboardUsers() {
        return new FirebaseQueryLiveData(getScoreboardUsersQuery());
    }

    public LiveData<List<Kana>> getKanaList() {
        List<Kana> kanaList = processJsonIntoKana(context, KANA_JSON_TABLE_RESOURCE);
        return new MutableLiveData<>(kanaList);
    }


}
