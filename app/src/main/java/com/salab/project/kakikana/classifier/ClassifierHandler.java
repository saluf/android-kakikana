package com.salab.project.kakikana.classifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.salab.project.kakikana.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to control, setup the classifier, and interpret the results. There are two models.
 * One for Hiragana, and one for Katakana classification. The class needs to give corresponding
 * options.
 */
public class ClassifierHandler {

    public static final String TAG = ClassifierHandler.class.getSimpleName();

    // Const
    public static final String KEY_CLASSIFIER_MODEL_FILE_NAME = "key_classifier_model_file_name";
    public static final String KEY_CLASSIFIER_LABEL_RESOURCE_ID = "key_classifier_label_resource_name";
    public static final String KEY_CLASSIFIER_OUTPUT_CLASSES_COUNT = "key_classifier_output_class_count";

    public static final float REJECT_CONFIDENCE_CRITERIA = 0.7f;
    public static final int LOW_CONFIDENCE_REJECTED_ID = -1;

    public static int recognizeKana(Context context, Bitmap answerBitmap, boolean isHiragana) throws IOException {

        KanaClassifier classifier = new KanaClassifier(context);

        // init classifier
        Map<String, Object> classifierOptions = getClassifierOptions(isHiragana);
        classifier.initializeInterpreter(classifierOptions);

        // classify
        Pair<Integer, Float> classifiedResult = classifier.classify(answerBitmap);
        int classifiedIndex = classifiedResult.first;
        float confidence = classifiedResult.second;

        // interpret
        int labelResourceId = (int) classifierOptions.get(KEY_CLASSIFIER_LABEL_RESOURCE_ID);
        int classifiedKanaId = context.getResources().getIntArray(labelResourceId)[classifiedIndex];

        // close
        classifier.close();

        // return low confidence result (id = -1) to prevent accidentally giving right answer with wrong input
        return confidence > REJECT_CONFIDENCE_CRITERIA ? classifiedKanaId : LOW_CONFIDENCE_REJECTED_ID;
    }

    private static Map<String, Object> getClassifierOptions(boolean isHiragana) {

        Map<String, Object> options = new HashMap<>();

        if (isHiragana) {
            options.put(KEY_CLASSIFIER_MODEL_FILE_NAME, "hiragana_v3.4.tflite");
            options.put(KEY_CLASSIFIER_LABEL_RESOURCE_ID, R.array.tf_labels_hiragana);
            options.put(KEY_CLASSIFIER_OUTPUT_CLASSES_COUNT, 46);
        } else {
            options.put(KEY_CLASSIFIER_MODEL_FILE_NAME, "katakana_v1.0.tflite");
            options.put(KEY_CLASSIFIER_LABEL_RESOURCE_ID, R.array.tf_labels_katakana);
            options.put(KEY_CLASSIFIER_OUTPUT_CLASSES_COUNT, 46);
        }

        Log.d(TAG, "model " + options.get(KEY_CLASSIFIER_MODEL_FILE_NAME) + " was chosen to run");

        return options;
    }
}
