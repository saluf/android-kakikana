package com.salab.project.kakikana.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.ExecutorStore;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.model.Question;
import com.salab.project.kakikana.model.QuestionResult;
import com.salab.project.kakikana.model.QuizResult;
import com.salab.project.kakikana.model.UserKana;
import com.salab.project.kakikana.util.FirebaseAuthUtil;
import com.salab.project.kakikana.util.QuizGeneratorUtil;
import com.salab.project.kakikana.classifier.ClassifierHandler;
import com.salab.project.kakikana.viewmodel.FirebaseQueryLiveData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.getScoreboardUsersQuery;
import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.getUserDatabaseReference;
import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.updatedUserKanaQuizStat;
import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.updatedUserQuizStat;
import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.uploadQuizResult;
import static com.salab.project.kakikana.util.FlashCardGeneratorUtil.simpleRandomCardGenerator;
import static com.salab.project.kakikana.util.KanaUtil.processJsonIntoKana;

/**
 * A repository coordinate data requirement from ViewModels and data providers, eg. Firebase database,
 * FirebaseAuth, and local storage.
 */
public class Repository {

    // constants
    private static final String TAG = Repository.class.getSimpleName();
    public static final int KANA_JSON_TABLE_RESOURCE = R.raw.kana_table;

    private Context context;

    public Repository() {
    }

    public Repository(Context context) {
        // Context is needed to read Resources
        this.context = context;
    }

    public Pair<LiveData<String>, LiveData<DataSnapshot>> getAllUserData() {
        // user id is saved in FirebaseAuth, additional data is saved in Firebase Database

        final MutableLiveData<String> userId = new MutableLiveData<>();
        final FirebaseQueryLiveData userData = new FirebaseQueryLiveData();

        FirebaseAuthUtil.getFirebaseUser().addOnSuccessListener(firebaseUser -> {
            // cannot get Firebase user data unless there is user id
            String uid = firebaseUser.getUid();
            userId.setValue(uid);
            userData.setDatabaseReference(getUserDatabaseReference(uid));
        }).addOnFailureListener(e -> {
            // TODO: handle cannot get user exceptions
        });
        return new Pair<>(userId, userData);

        // TODO: cannot prevent user data delete from server side
    }

    public LiveData<DataSnapshot> getScoreboardUsers() {
        return new FirebaseQueryLiveData(getScoreboardUsersQuery());
    }

    public LiveData<List<Kana>> getKanaList() {
        List<Kana> kanaList = processJsonIntoKana(context, KANA_JSON_TABLE_RESOURCE);
        return new MutableLiveData<>(kanaList);
    }

    public LiveData<List<Question>> getQuestionList(int quizId, int numQuestions) {

        final MutableLiveData<List<Question>> questionList = new MutableLiveData<>();

        ExecutorStore.getInstance().getDiskIO().execute(() -> {
            List<Kana> kanaList = processJsonIntoKana(context, KANA_JSON_TABLE_RESOURCE);
            List<Question> questions = QuizGeneratorUtil.generateQuestionList(quizId, numQuestions, kanaList);
            questionList.postValue(questions);
        });

        return questionList;
    }


    public void getQuestionResult(LiveData<Question> question, Bitmap answerBitmap,
                                  int timeTaken, MutableLiveData<QuestionResult> questionResultWrapper) {

        if (question.getValue() == null || answerBitmap == null) {
            Log.w(TAG, "Input Question or Bitmap is empty");
            return;
        }

        ExecutorStore.getInstance().getDiskIO().execute(() -> {
            int recognizedKanaId = 0;

            try {
                // TODO: instead of hard coding judge, get result from model it self
                boolean isHiragana = question.getValue().getType().equals("hiragana");
                recognizedKanaId = ClassifierHandler.recognizeKana(context, answerBitmap, isHiragana);
            } catch (IOException e) {
                e.printStackTrace();
            }

            QuestionResult questionResult = new QuestionResult();
            questionResult.loadQuestionKana(question.getValue());
            questionResult.setTimeTaken(timeTaken);
            questionResult.setAnswerKanaId(recognizedKanaId);
            questionResult.judgeAnswer();
            // save a compressed copy of the user drawn bitmap
            int savedBitmapSize = (int) context.getResources().getDimension(R.dimen.size_saved_quiz_result_bitmap_size);
            questionResult.setDrawnAnswer(Bitmap.createScaledBitmap(answerBitmap, savedBitmapSize, savedBitmapSize, true));

            // the LiveData wrapper is created by ViewModel and observed by Fragment,
            // so needs to put the data into the same holder every time
            questionResultWrapper.postValue(questionResult);
        });

    }

    public void addQuizResult(QuizResult quizResult, Map<String, UserKana> kanaQuizResult) {

        String uid = FirebaseAuthUtil.getFirebaseAuthUid();

        if (uid != null && quizResult != null) {
            // for every quiz result, three related numbers need to be updated
            // 1. quizResults itself, 2. user-level quiz statistics, 3. user-based per kana quiz statistics
            ExecutorStore.getInstance().getDiskIO().execute(() -> {
                // run on background thread
                uploadQuizResult(uid, quizResult);
                updatedUserQuizStat(uid, quizResult);
                updatedUserKanaQuizStat(uid, kanaQuizResult);
            });

        } else {
            Log.d(TAG, "User is not logged in, or empty quizResult.");
        }
    }

    public List<Kana> getRandomFlashCards(int numCards) {
        List<Kana> kanaList = processJsonIntoKana(context, KANA_JSON_TABLE_RESOURCE);
        return simpleRandomCardGenerator(numCards, kanaList);
    }
}
