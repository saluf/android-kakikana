package com.salab.project.kakikana.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.salab.project.kakikana.ExecutorStore;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.model.CommonUse;
import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.model.Question;
import com.salab.project.kakikana.model.QuestionResult;
import com.salab.project.kakikana.model.QuizResult;
import com.salab.project.kakikana.model.UserKana;
import com.salab.project.kakikana.util.FirebaseAuthUtil;
import com.salab.project.kakikana.util.FirebaseDatabaseUtil;
import com.salab.project.kakikana.util.JishoApiUtil;
import com.salab.project.kakikana.util.QuizGeneratorUtil;
import com.salab.project.kakikana.classifier.ClassifierHandler;
import com.salab.project.kakikana.viewmodel.FirebaseQueryLiveData;
import com.salab.project.kakikana.viewmodel.FirebaseUserLiveData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.salab.project.kakikana.util.FirebaseDatabaseUtil.createUserDataIfNotExist;
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

    public LiveData<FirebaseUser> getUser() {
        return new FirebaseUserLiveData(FirebaseAuthUtil.getInstance());
    }

    public LiveData<DataSnapshot> getUserData(String uid) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData();
        userData.setDatabaseReference(getUserDatabaseReference(uid));
        return userData;
    }

    public void signInAnonymously() {
        // sign in successfully, create user progress data in Firebase realtime database
        FirebaseAuthUtil.signInAnonymously().addOnSuccessListener(currentUser -> {
            FirebaseDatabaseUtil.createUserDataIfNotExist(currentUser.getUid());
        });
    }

    public void signInWithCredential(AuthCredential credential) {
        FirebaseAuthUtil.signInWithCredential(credential).addOnSuccessListener(currentUser -> {
            createUserDataIfNotExist(currentUser.getUid(), currentUser.getDisplayName());
        });
    }

    public void linkOrSignInWithGoogle(AuthCredential credential) {
        FirebaseAuthUtil.linkWithGoogle(credential)
                .addOnSuccessListener(currentUser -> {
                    // update firebase database User name
                    FirebaseDatabaseUtil.updateUserName(currentUser.getUid(), currentUser.getDisplayName());
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        // The credential has already been linked with other account.
                        // Sign user in and the current anonymous user data will be lost
                        FirebaseAuthUtil.signInWithCredential(credential);
                    }
                });
    }

    public void userSignOut() {
        FirebaseAuthUtil.userSignOut();
    }

    public LiveData<DataSnapshot> getScoreboardUsers() {
        return new FirebaseQueryLiveData(getScoreboardUsersQuery());
    }

    public LiveData<List<Kana>> getKanaList() {
        List<Kana> kanaList = processJsonIntoKana(context, KANA_JSON_TABLE_RESOURCE);
        return new MutableLiveData<>(kanaList);
    }

    public LiveData<List<CommonUse>> getCommonUse(String kana, int numCommonUse) {
        final MutableLiveData<List<CommonUse>> CommonWordList = new MutableLiveData<>();

        // load common words from network API using AsyncTask to run it asynchronously
        new AsyncTask<String, Void, List<CommonUse>>() {

            @Override
            protected List<CommonUse> doInBackground(String... keywords) {
                return JishoApiUtil.fetchWordsFromApi(keywords[0], numCommonUse);
            }

            @Override
            protected void onPostExecute(List<CommonUse> commonUses) {
                CommonWordList.setValue(commonUses);
            }
        }.execute(kana);

        return CommonWordList;
    }

    public LiveData<DataSnapshot> getUserKana(int kanaId) {
        FirebaseQueryLiveData userKana = new FirebaseQueryLiveData();
        String uid = FirebaseAuthUtil.getUid();
        userKana.setDatabaseReference(FirebaseDatabaseUtil.getUserKanaDatabaseReference(uid, String.valueOf(kanaId)));
        return userKana;
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

        String uid = FirebaseAuthUtil.getUid();

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
