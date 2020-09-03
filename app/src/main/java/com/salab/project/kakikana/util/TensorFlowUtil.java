package com.salab.project.kakikana.util;

import android.graphics.Bitmap;

import java.util.Random;

public class TensorFlowUtil {
    public static int recognizeKana(Bitmap answerBitmap, int questionId) {
        // question id is for early build up
        // TODO implement TensorFlow to actual recognize it.

        boolean isCorrect;
        isCorrect = new Random().nextBoolean();

        return isCorrect ? questionId : questionId + 1;

    }
}
