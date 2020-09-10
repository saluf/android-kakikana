package com.salab.project.kakikana.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.salab.project.kakikana.R;
import com.salab.project.kakikana.databinding.FragmentQuizBinding;
import com.salab.project.kakikana.model.Question;
import com.salab.project.kakikana.viewmodel.QuizViewModel;

/**
 * Fragment to host quiz
 **/

public class QuizFragment extends Fragment {

    // constants
    private static final String TAG = QuizFragment.class.getSimpleName();
    public static final int TIME_PER_TICK = 100;

    // global variables
    private FragmentQuizBinding mBinding;
    private QuizViewModel mQuizViewModel;
    private QuizCountDownTime mQuestionTimer;

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable options menu
        setHasOptionsMenu(true);

        // lock rotation during quiz (smaller phone only allows portrait)
        if (getResources().getBoolean(R.bool.isTablet)){
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
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

        // setup UI, data and responses
        setupQuizViewModel();
        setupLiveDataObservers();
        setupButtonOnClickResponse();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getResources().getBoolean(R.bool.isTablet)){
            // release rotation lock
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Add cancel action button to app bar
        inflater.inflate(R.menu.menu_quiz, menu);
    }

    private void setupLiveDataObservers() {
        mQuizViewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), newQuestion -> {
            // new question comes
            populateNewQuestionUI(newQuestion);
            // start countdown timer
            setQuestionCountdownTimer(10 * 1000);
            // TODO loading time from SharedPreference
        });

        mQuizViewModel.getQuizProgressInPercent().observe(getViewLifecycleOwner(), progress -> {
            // progress is updated
            mBinding.contentQuiz.pbQuizProgress.setProgress(progress);
        });

        mQuizViewModel.getIsQuizEnded().observe(getViewLifecycleOwner(), isQuizEnded -> {
            if (isQuizEnded) {
                // redirect to result page when ViewModel notifies the quiz is ended.
                NavDirections action = QuizFragmentDirections.actionQuizFragmentToQuizResultFragment();
                NavHostFragment.findNavController(this).navigate(action);
            }
        });

        mQuizViewModel.getQuestionResult().observe(getViewLifecycleOwner(), questionResult -> {
            // here comes the result of the previous question
            showResultUI(questionResult.isCorrect());
        });
    }

    private void setupButtonOnClickResponse() {
        mBinding.contentQuiz.btnConfirmAnswer.setOnClickListener(v -> {
            // submit answer
            mQuizViewModel.submitAnswer(
                    mBinding.contentQuiz.drawingView.getCachedBitmap(),
                    mQuestionTimer.getTimePassed());
        });

        mBinding.contentQuiz.btnClearDrawing.setOnClickListener(v -> {
            // request clear drawing canvas
            mBinding.contentQuiz.drawingView.clear();
        });

        mBinding.contentQuiz.btnNextQuestion.setOnClickListener(v -> {
            // request next question
            mQuizViewModel.nextQuestion();
        });

        mBinding.contentQuiz.btnReportIssue.setOnClickListener(v -> {
            // users report the recognition could be wrong
            boolean isIssued = mQuizViewModel.onReportIssueClick();
            setReportIssueBtnFlagState(isIssued);
        });
    }

    private void setupQuizViewModel() {
        NavBackStackEntry quizNavBackStackEntry =
                NavHostFragment.findNavController(this).getBackStackEntry(R.id.quiz_dest);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        mQuizViewModel = new ViewModelProvider(quizNavBackStackEntry, factory).get(QuizViewModel.class);
    }

    private void setReportIssueBtnFlagState(boolean isIssued) {
        mBinding.contentQuiz.btnReportIssue.setImageResource(isIssued ?
                R.drawable.ic_flag : R.drawable.ic_outlined_flag);
    }

    private void showResultUI(boolean correct) {
        // show result UI including an indication to tell user if this correct
        // a next button to next question and a flag button to report recognition issue

        mBinding.contentQuiz.ivCorrectnessIndicator.setImageResource(correct ? R.drawable.ic_correct : R.drawable.ic_incorrect);
        setReportIssueBtnFlagState(false); // clear up for new question
        mQuestionTimer.cancel(); // stop countdown timer

        mBinding.contentQuiz.groupQuestionQuiz.setVisibility(View.INVISIBLE);
        mBinding.contentQuiz.groupQuestionResult.setVisibility(View.VISIBLE);
    }

    private void showQuestionUI() {
        mBinding.contentQuiz.drawingView.clear();
        mBinding.contentQuiz.groupQuestionQuiz.setVisibility(View.VISIBLE);
        mBinding.contentQuiz.groupQuestionResult.setVisibility(View.GONE);
    }

    private void populateNewQuestionUI(Question newQuestion) {
        showQuestionUI();
        // set question title
        mBinding.contentQuiz.tvQuestion.setText(getString(
                R.string.format_quiz_question,
                newQuestion.getRomaji(),
                newQuestion.getType()));
    }

    private void setQuestionCountdownTimer(long quizTimeLimitMilli) {
        // ref:https://stackoverflow.com/questions/10241633/android-progressbar-countdown

        mBinding.contentQuiz.pbQuizCountdown.setProgress(100); // reset progress bar

        if (mQuestionTimer != null) {
            // cancel last question timer and start new one
            mQuestionTimer.cancel();
        }
        mQuestionTimer = new QuizCountDownTime(quizTimeLimitMilli, TIME_PER_TICK);
        mQuestionTimer.start();
    }

    class QuizCountDownTime extends CountDownTimer{
        private float progressInPercentPerTick;
        private int tickCounter = 0;
        private float progressInPercent = 100;
        private ProgressBar quizCountDownProgressBar;


        public QuizCountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            progressInPercentPerTick = (float) TIME_PER_TICK / millisInFuture * 100;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tickCounter++;
            progressInPercent -= progressInPercentPerTick;
            mBinding.contentQuiz.pbQuizCountdown.setProgress(Math.round(progressInPercent));
        }

        @Override
        public void onFinish() {
            mBinding.contentQuiz.pbQuizCountdown.setProgress(0);
            // countdown to zero and force submitting the answer
            mBinding.contentQuiz.btnConfirmAnswer.performClick();
        }

        public int getTimePassed(){
            return (int) (tickCounter * progressInPercentPerTick);
        }
    }
}