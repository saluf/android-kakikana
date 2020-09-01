package com.salab.project.kakikana.util;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.salab.project.kakikana.model.User;

import java.util.Map;


/**
 * The is a helper class hosting a bunch of methods and constants to help repository reduce
 * the loading of get data from or put data onto Firebase Realtime Database.
 */
public class FirebaseDatabaseUtil {

    // constants
    private static final String TAG = FirebaseDatabaseUtil.class.getSimpleName();
    public static final String DATABASE_REFERENCE_USER = "users";

    private static FirebaseDatabase dbInstance;

    private static FirebaseDatabase getInstance() {
        if (dbInstance == null) {
            dbInstance = FirebaseDatabase.getInstance();
            dbInstance.setPersistenceEnabled(true);
        }
        return dbInstance;
    }

    public static DatabaseReference getUserDatabaseReference(String uid) {
        return getInstance().getReference(DATABASE_REFERENCE_USER).child(uid);
    }

    public static Query getScoreboardUsersQuery() {
        // Firebase always return ascending order -> limitToLast == largest
        return getInstance().getReference(DATABASE_REFERENCE_USER).orderByChild("totalCorrect").limitToLast(100);
    }

    public static void createUserData(String uid) {
        // create initial user data right after users are registered
        Map<String, Object> newUser = new User().toMap(true);
        getInstance().getReference(DATABASE_REFERENCE_USER).child(uid).setValue(newUser);
        Log.d(TAG, "init user profile");
    }

}
