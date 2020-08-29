package com.salab.project.kakikana.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salab.project.kakikana.databinding.FragmentQuizResultBinding;

/**
 * Fragment to display the result of the finished quiz. Users can start again, return to home, or
 * review the history of this quiz.
 **/
public class QuizResultFragment extends Fragment {

    // constants
    private static final String TAG = QuizFragment.class.getSimpleName();

    // global variables
    private FragmentQuizResultBinding mBinding;

    public QuizResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overrideOnBackPressedBehavior();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentQuizResultBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // start again
        mBinding.btnQuizAgain.setOnClickListener(v -> {
            NavDirections action = QuizResultFragmentDirections.actionQuizResultDestToQuizDest();
            Navigation.findNavController(v).navigate(action);
        });

        // end quiz and return
        mBinding.btnQuizEnd.setOnClickListener(v -> popBackToQuizList());
    }

    private void overrideOnBackPressedBehavior() {
        // on "Back" button pressed, it pops up back to quiz list instead of quiz
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                popBackToQuizList();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void popBackToQuizList() {
        NavDirections action = QuizResultFragmentDirections.actionQuizResultFragmentToQuizListFragment();
        NavHostFragment.findNavController(QuizResultFragment.this).navigate(action);
    }
}