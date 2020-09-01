package com.salab.project.kakikana.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.repository.Repository;

/**
 * ViewModel to cache scoreboard data, and limit the scope to ScoreboardFragment.
 */
public class ScoreboardViewModel extends ViewModel {
    // constants
    private static final String TAG = ScoreboardViewModel.class.getSimpleName();

    LiveData<DataSnapshot> scoreboardData;
    Repository repository;

    public ScoreboardViewModel(){
        repository = new Repository();
        scoreboardData = repository.getScoreboardUsers();
    }

    public LiveData<DataSnapshot> getScoreboardData() {
        return scoreboardData;
    }
}
