package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.salab.project.kakikana.databinding.FragmentScoreboardBinding;

/**
 * Fragment to show how good the user is among the others
 **/
public class ScoreboardFragment extends Fragment {
    // constants
    private static final String TAG = ScoreboardFragment.class.getSimpleName();

    // global variables
    private FragmentScoreboardBinding mBinding;

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
}