package com.salab.project.kakikana.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.salab.project.kakikana.BuildConfig;

import java.util.concurrent.TimeUnit;

public class InAppReviewUtil {

    public final static Long TIME_GAP_BETWEEN_REVIEW = TimeUnit.DAYS.toMillis(30);
    public final static String KEY_LAST_IN_APP_REVIEW_TIME = "key_last_in_app_review_time";
    public final static String KEY_LAST_IN_APP_REVIEW_VERSION = "key_last_in_app_review_version";

    private static Long loadReviewTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(KEY_LAST_IN_APP_REVIEW_TIME, 0L);
    }

    private static int loadReviewVersion(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(KEY_LAST_IN_APP_REVIEW_VERSION, 0);
    }

    private static void saveReviewInfo(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(KEY_LAST_IN_APP_REVIEW_TIME, System.currentTimeMillis());
        editor.putInt(KEY_LAST_IN_APP_REVIEW_VERSION, BuildConfig.VERSION_CODE);
        editor.apply();
    }

    public static void reviewPromptTriggered(Context context) {
        saveReviewInfo(context);
    }

    public static boolean isReviewCriteriaMet(Context context) {
        Long lastReviewTime = loadReviewTime(context);
        boolean hasEnoughTimeGap = (System.currentTimeMillis() - lastReviewTime) >= TIME_GAP_BETWEEN_REVIEW;
        boolean hasNewerAppVersion = BuildConfig.VERSION_CODE >= loadReviewVersion(context);

        return hasEnoughTimeGap && hasNewerAppVersion;
    }
}
