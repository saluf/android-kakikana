package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.adpater.ScoreboardAdapter;
import com.salab.project.kakikana.databinding.FragmentScoreboardBinding;
import com.salab.project.kakikana.model.User;
import com.salab.project.kakikana.viewmodel.ScoreboardViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to show how good the user is among the others
 **/
public class ScoreboardFragment extends Fragment {
    // constants
    private static final String TAG = ScoreboardFragment.class.getSimpleName();

    // global variables
    private FragmentScoreboardBinding mBinding;
    private ScoreboardAdapter mAdapter;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentScoreboardBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup RecyclerView adapter
        mAdapter = new ScoreboardAdapter(requireContext());
        mBinding.contentScoreboard.rvScoreboard.setAdapter(mAdapter);
        mBinding.contentScoreboard.rvScoreboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.contentScoreboard.rvScoreboard.setHasFixedSize(true);

        // reference to ViewHolder
        ScoreboardViewModel scoreboardViewModel = new ViewModelProvider(this).get(ScoreboardViewModel.class);
        scoreboardViewModel.getScoreboardData().observe(getViewLifecycleOwner(), dataSnapshot -> {
            if (dataSnapshot != null) {
                List<User> scoreboardUsers = new ArrayList<>();
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    scoreboardUsers.add(s.getValue(User.class));
                }

                mAdapter.setmScoreboardUserList(scoreboardUsers);
                Log.d(TAG, scoreboardUsers.toString());
            }
        });
    }
}