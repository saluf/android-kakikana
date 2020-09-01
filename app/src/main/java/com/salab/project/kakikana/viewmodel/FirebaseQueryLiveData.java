package com.salab.project.kakikana.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Customized LiveData to hold the data from Firebase Realtime database. App read data from the
 * database by setting DataReference and Query (sorting and filtering), and listen to the
 * them for updates.
 * With LiveData as wrapper, allows it to be lifecycle-aware and compatible with modern
 * android architecture. This is a official recommended way.
 * ref: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 */
public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {

    private static final String TAG = FirebaseQueryLiveData.class.getSimpleName();

    private Query query; //DatabaseReference is a subclass of Query
    private final CustomValueEventListener listener = new CustomValueEventListener();

    public FirebaseQueryLiveData() {
        // no-args constructor which allows to get LiveData instance first before getting a
        // correct DataReference, eg. observer user data before sign in complete
        Log.d(TAG, "instantiated");
    }

    public FirebaseQueryLiveData(Query query) {
        setQuery(query);
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        setDatabaseReference(ref);
    }

    public void setQuery(Query query) {
        this.query = query;
        if (hasActiveObservers()) {
            // if any observer in active state, trigger listener setup
            onActive();
        }
    }

    public void setDatabaseReference(DatabaseReference ref) {
        setQuery(ref);
    }

    @Override
    protected void onActive() {
        if (query != null) {
            query.addValueEventListener(listener);
        }
    }

    @Override
    protected void onInactive() {
        if (query != null) {
            query.removeEventListener(listener);
        }
    }

    private class CustomValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot); // LiveData.setValue() to notify all observers

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
