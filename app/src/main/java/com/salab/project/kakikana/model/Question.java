package com.salab.project.kakikana.model;

/**
 * Wrapper class extending from Kana, and provide more question-specific fields and methods
 */
public class Question extends Kana {


    public Question() {
    }

    public Question(int id, String type, String kana, String romaji) {
        super(id, type, kana, romaji);
    }

    public static Question getQuestionFromKana(Kana kana) {
        // currently no difference between Question and Kana
        return new Question(kana.getId(), kana.getType(), kana.getKana(), kana.getRomaji());
    }


}
