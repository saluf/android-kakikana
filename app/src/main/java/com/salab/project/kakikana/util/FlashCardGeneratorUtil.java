package com.salab.project.kakikana.util;

import com.salab.project.kakikana.model.Kana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generate all kinds of flash cards on demand.
 */
public class FlashCardGeneratorUtil {

    // constants
    private static final String TAG = FlashCardGeneratorUtil.class.getSimpleName();

    public static List<Kana> simpleRandomCardGenerator(int numQuestions, List<Kana> kanaList) {

        List<Kana> generatedCardList = new ArrayList<>();
        Collections.shuffle(kanaList);

        Kana kanaAtIndex;
        int subListSize = Math.min(numQuestions, kanaList.size());
        for (int i = 0; i < subListSize; i++) {
            kanaAtIndex = kanaList.get(i);
            if (!kanaAtIndex.getKana().isEmpty()) {
                generatedCardList.add(kanaAtIndex);
            } else {
                // some ids are placeholder in order to arrange standard table format
                // skip those by extends the limit
                subListSize++;
            }
        }
        return generatedCardList;
    }

}
