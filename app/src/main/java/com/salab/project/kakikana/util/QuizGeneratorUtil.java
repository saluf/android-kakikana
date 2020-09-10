package com.salab.project.kakikana.util;

import com.salab.project.kakikana.model.Kana;
import com.salab.project.kakikana.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collection of helper methods, settings, and constants related to generate different types
 * of questions.
 */
public class QuizGeneratorUtil {

    public static List<Question> generateQuestionList(int quizId, int numQuestions, List<Kana> kanaList) {

        switch (quizId) {
            case 0:
                // simplest random questions
                return simpleRandomGenerator(numQuestions, kanaList);
            case 1:
                // placeholder
                break;
        }
        return null;
    }

    private static List<Question> simpleRandomGenerator(int numQuestions, List<Kana> kanaList) {

        List<Question> generatedQuestionList = new ArrayList<>();
        Collections.shuffle(kanaList);

        Kana kanaAtIndex;
        int subListSize = Math.min(numQuestions, kanaList.size());
        for (int i = 0; i < subListSize; i++) {
            kanaAtIndex = kanaList.get(i);
            if (!kanaAtIndex.getKana().isEmpty()) {
                generatedQuestionList.add(Question.getQuestionFromKana(kanaAtIndex));
            } else {
                // some ids are placeholder in order to arrange standard table format
                // skip those by extends the limit
                subListSize++;
            }

        }

        return generatedQuestionList;
    }
}
