package com.salab.project.kakikana.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salab.project.kakikana.model.QuizResult;
import com.salab.project.kakikana.model.User;
import com.salab.project.kakikana.model.UserKana;

import java.util.HashMap;
import java.util.Map;


/**
 * The is a helper class hosting a bunch of methods and constants to help repository reduce
 * the loading of get data from or put data onto Firebase Realtime Database.
 */
public class FirebaseDatabaseUtil {

    // constants
    private static final String TAG = FirebaseDatabaseUtil.class.getSimpleName();
    public static final String DATABASE_REFERENCE_USER = "users";
    public static final String DATABASE_REFERENCE_USER_NAME = "name";
    public static final String DATABASE_REFERENCE_QUIZ_RESULTS = "quizResults";
    public static final String DATABASE_REFERENCE_USER_KANA_STAT = "userKana";

    private static FirebaseDatabase dbInstance;

    private static FirebaseDatabase getInstance() {
        if (dbInstance == null) {
            dbInstance = FirebaseDatabase.getInstance();
            dbInstance.setPersistenceEnabled(true);
        }
        return dbInstance;
    }

    public static DatabaseReference getUserDatabaseReference(String uid) {
        // user profile reference
        return getInstance().getReference(DATABASE_REFERENCE_USER).child(uid);
    }

    public static DatabaseReference getQuizResultDatabaseReference(String uid) {
        // QuizResult reference. A place to hold every single quiz results from all users
        return getInstance().getReference(DATABASE_REFERENCE_QUIZ_RESULTS).child(uid).push();
    }


    public static Query getScoreboardUsersQuery() {
        // Firebase always return ascending order -> limitToLast == largest
        return getInstance().getReference(DATABASE_REFERENCE_USER).orderByChild("totalCorrect").limitToLast(100);
    }

    public static void createUserDataIfNotExist(String uid) {
        createUserDataIfNotExist(uid, null);
    }

    public static void createUserDataIfNotExist(String uid, String userName) {
        // check if user data has existed or create one for new user

        getInstance().getReference(DATABASE_REFERENCE_USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    // completely new user -> create a profile
                    createNewUserData(uid, userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "check user data existence was cancelled by the server.");
            }
        });
    }

    private static void createNewUserData(String uid, String userName) {
        User newUser = new User();
        if (userName != null) {
            // default name is "anonymous". If user sign in with Google, then take their profile name.
            newUser.setName(userName);
        }

        Map<String, Object> newUserMap = newUser.toMap(true);
        getInstance().getReference(DATABASE_REFERENCE_USER).child(uid).setValue(newUserMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "successfully create user profile ");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "failed to create user profile. " + e.getMessage());
                });
    }

    public static void updateUserName(String uid, String displayName) {
        getInstance().getReference(DATABASE_REFERENCE_USER).child(uid).child(DATABASE_REFERENCE_USER_NAME)
                .setValue(displayName)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "successfully update user user name as " + displayName);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "failed  update user user name. " + e.getMessage());
                });
    }

    public static void uploadQuizResult(String uid, QuizResult quizResult) {
        getQuizResultDatabaseReference(uid).setValue(quizResult.toMap());
    }

    public static void updatedUserQuizStat(String uid, QuizResult quizResult) {

        getUserDatabaseReference(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {

                    // avoid overwriting all user data, so use map to update stats only
                    Map<String, Object> statMap = new HashMap<>();
                    statMap.put("totalCorrect", user.getTotalCorrect() + quizResult.getTotalCorrected());
                    statMap.put("totalTested", user.getTotalTested() + quizResult.getTotalTested());

                    getUserDatabaseReference(uid)
                            .updateChildren(statMap)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "user quiz statistics update successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "user quiz statistics failed to update: " + e.getMessage());
                            });
                } else {
                    // empty user profile create one
                    createUserDataIfNotExist(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "user quiz statistics update was cancelled by the server.");
            }
        });
    }

    public static void updatedUserKanaQuizStat(String uid, Map<String, UserKana> kanaQuizResult) {

        for (Map.Entry<String, UserKana> result : kanaQuizResult.entrySet()) {
            String kanaId = result.getKey();
            UserKana newUserKana = result.getValue();
            getUserKanaDatabaseReference(uid, kanaId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserKana existedUserKana = snapshot.getValue(UserKana.class);
                            if (existedUserKana == null) {
                                existedUserKana = new UserKana();
                            }
                            existedUserKana.mergeUserKanas(newUserKana);
                            getUserKanaDatabaseReference(uid, kanaId)
                                    .setValue(existedUserKana)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "user kana quiz " + kanaId + " statistics update successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d(TAG, "user kana  quiz statistics failed to update: " + e.getMessage());
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "user quiz statistics update was cancelled by the server.");
                        }
                    }
            );
        }
    }

    private static DatabaseReference getUserKanaDatabaseReference(String userId, String kanaId) {
        return getInstance().getReference(DATABASE_REFERENCE_USER_KANA_STAT).child(userId).child(kanaId);
    }

}
