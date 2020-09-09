package com.salab.project.kakikana.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.adpater.QuestionHistoryAdapter;
import com.salab.project.kakikana.databinding.FragmentQuizResultBinding;
import com.salab.project.kakikana.model.QuestionResult;
import com.salab.project.kakikana.model.QuizResult;
import com.salab.project.kakikana.viewmodel.QuizViewModel;

import java.util.List;

/**
 * Fragment to display the result of the finished quiz. Users can start again, return to home, or
 * review the history of this quiz.
 **/
public class QuizResultFragment extends Fragment {

    // constants
    private static final String TAG = QuizFragment.class.getSimpleName();

    // global variables
    private FragmentQuizResultBinding mBinding;
    private QuestionHistoryAdapter mAdapter;
    private QuizViewModel mQuizViewModel;

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

        setupButtonOnClickResponse();
        SetupViewModel();

        // get quiz result and update UI
        QuizResult quizResult = mQuizViewModel.getQuizResult();
        List<QuestionResult> questionResultList = quizResult.getQuestionResultList();
        float correctnessRateInPercent = getCorrectnessRateInPercent(quizResult);

        SetupRecyclerView(questionResultList);
        PopulateUI(correctnessRateInPercent);
    }

    private void setupButtonOnClickResponse() {
        // start again
        mBinding.btnQuizAgain.setOnClickListener(v -> {
            NavDirections action = QuizResultFragmentDirections.actionQuizResultDestToQuizDest();
            Navigation.findNavController(v).navigate(action);
        });

        // end quiz and return
        mBinding.btnQuizEnd.setOnClickListener(v -> popBackToQuizList());
    }

    private void SetupViewModel() {
        NavBackStackEntry quizNavBackStackEntry =
                NavHostFragment.findNavController(this).getBackStackEntry(R.id.quiz_dest);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        mQuizViewModel = new ViewModelProvider(quizNavBackStackEntry, factory).get(QuizViewModel.class);
    }


    private float getCorrectnessRateInPercent(QuizResult quizResult) {
        float correctnessRateInPercent = 0f;
        if (quizResult.getTotalTested() != 0f) {
            correctnessRateInPercent = Math.round((float) quizResult.getTotalCorrected() / quizResult.getTotalTested() * 100);
        }
        return correctnessRateInPercent;
    }

    private void SetupRecyclerView(List<QuestionResult> questionResultList) {
        mAdapter = new QuestionHistoryAdapter(questionResultList, requireContext());
        mBinding.rvQuizHistoryBody.setAdapter(mAdapter);
        mBinding.rvQuizHistoryBody.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.rvQuizHistoryBody.setHasFixedSize(true);
    }

    private void PopulateUI(float correctnessRateInPercent) {
        mBinding.tvCorrectnessRate.setText(getString(R.string.format_corr_rate_in_percent_rounded, correctnessRateInPercent));

        String encouragingWords;
        if (correctnessRateInPercent >= 80f) {
            encouragingWords = getString(R.string.text_encouraging_words_best);
        } else if (correctnessRateInPercent >= 50f) {
            encouragingWords = getString(R.string.text_encouraging_words_medium);
        } else {
            encouragingWords = getString(R.string.text_encouraging_words_low);
        }
        mBinding.tvEncouragingWords.setText(encouragingWords);
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