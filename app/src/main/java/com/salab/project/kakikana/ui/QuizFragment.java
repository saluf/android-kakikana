package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.databinding.FragmentQuizBinding;

/**
 * Fragment to host quiz
 **/

public class QuizFragment extends Fragment {

    // constants
    private static final String TAG = QuizFragment.class.getSimpleName();

    // global variables
    private FragmentQuizBinding mBinding;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable options menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentQuizBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // navigate to quiz result
        mBinding.btnConfirmAnswer.setOnClickListener(v -> {
            NavDirections action = QuizFragmentDirections.actionQuizFragmentToQuizResultFragment();
            Navigation.findNavController(v).navigate(action);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Add cancel action button to app bar
        inflater.inflate(R.menu.menu_quiz, menu);
    }
}