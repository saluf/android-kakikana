package com.salab.project.kakikana.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.salab.project.kakikana.model.Question;
import com.salab.project.kakikana.model.QuestionResult;
import com.salab.project.kakikana.model.QuizResult;
import com.salab.project.kakikana.model.UserKana;
import com.salab.project.kakikana.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizViewModel extends AndroidViewModel {

    // constants
    private static final String TAG = QuizViewModel.class.getSimpleName();
    private static final int numQuestions = 5;
    private static final int quizId = 0;

    // global variables
    private Repository repository;

    private MutableLiveData<Boolean> isQuizEnded = new MutableLiveData<>(false);
    private MutableLiveData<Integer> quizProgressInPercent = new MutableLiveData<>(0);

    private LiveData<List<Question>> questionList;
    private MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private int currentQuestionIndex = 0;
    private MutableLiveData<QuestionResult> questionResult = new MutableLiveData<>();

    // output
    private QuizResult quizResult = new QuizResult();
    private Map<String, UserKana> kanaQuizResult = new HashMap<>();


    public QuizViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application.getApplicationContext());

        questionList = repository.getQuestionList(quizId, numQuestions);
        questionList.observeForever(new Observer<List<Question>>() {
            @Override
            public void onChanged(List<Question> questions) {
                // set first question when data ready and it notifies UI to start
                if (questions != null && questions.size() != 0){
                    currentQuestion.setValue(questions.get(currentQuestionIndex));
                    questionList.removeObserver(this);
                }
            }
        });
    }

    // response to UI events

    public void submitAnswer(Bitmap bitmap, int timeTaken){
        // users confirm their answer and start judging their answers
        repository.getQuestionResult(currentQuestion, bitmap, timeTaken, questionResult);
    }

    public void nextQuestion() {
        // save previous question result for latter result displaying and uploading
        saveResultToOutputs();
        updateProgress();
        // get a new question or end the quiz
        List<Question> questions = questionList.getValue();
        if (questions != null){

            if (currentQuestionIndex < questions.size() - 1){
                currentQuestionIndex++;
                currentQuestion.setValue(questions.get(currentQuestionIndex));
            } else {
                // no more questions. Quiz ends
                isQuizEnded.setValue(true);
                uploadQuizResult();
            }
        }
    }

    public boolean onReportIssueClick() {
        QuestionResult questionResultValue = questionResult.getValue();
        if (questionResultValue != null){
            // every click will swap the flag
            questionResultValue.setIssued(!questionResultValue.isIssued());
            return questionResultValue.isIssued();
        }
        return false;
    }

    private void updateProgress() {
        // update current progress in percentage
        quizProgressInPercent.setValue(Math.round((float)(currentQuestionIndex+1)/numQuestions*100));
    }

    private void uploadQuizResult() {
        // sync data to server
        repository.addQuizResult(quizResult, kanaQuizResult);
    }

    private void saveResultToOutputs() {
        QuestionResult questionResultValue = questionResult.getValue();
        if (questionResultValue == null){
            return;
        }
        // save result to quizResult which will be upload to server
        quizResult.appendQuestionResult(questionResultValue);

        // using map to record by kana result
        // it will be used later to update users' kana-base stats
        if (!questionResultValue.isIssued()) {
            String kanaId = questionResultValue.getQuestionKanaIdString();

            UserKana userKana;
            if (kanaQuizResult.containsKey(kanaId) && kanaQuizResult.get(kanaId) != null){
                userKana = kanaQuizResult.get(kanaId);
            } else {
                userKana = new UserKana();
            }

            if (questionResultValue.isCorrect()) {
                userKana.addCorrect();
            } else {
                userKana.addIncorrect();
            }
            kanaQuizResult.put(kanaId, userKana);
        }
    }

    // getter and setter methods

    public LiveData<Integer> getQuizProgressInPercent() {
        return quizProgressInPercent;
    }

    public LiveData<Boolean> getIsQuizEnded() {
        return isQuizEnded;
    }

    public LiveData<List<Question>> getQuestionList() {
        return questionList;
    }

    public LiveData<Question> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<QuestionResult> getQuestionResult() {
        return questionResult;
    }

    public QuizResult getQuizResult() {
        return quizResult;
    }


}
