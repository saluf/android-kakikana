package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.salab.project.kakikana.databinding.FragmentQuizListBinding;

/**
 * Fragment to display list of quizzes
 **/
public class QuizListFragment extends Fragment {
    //constants
    private static final String TAG = QuizListFragment.class.getSimpleName();

    // global variables
    private FragmentQuizListBinding mBinding;

    public QuizListFragment() {
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
        mBinding = FragmentQuizListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Navigate to quiz destination
        mBinding.btnStartQuiz.setOnClickListener(v -> {
            NavDirections action = QuizListFragmentDirections.actionQuizListDestToQuizDest();
            Navigation.findNavController(v).navigate(action);
        });
    }
}