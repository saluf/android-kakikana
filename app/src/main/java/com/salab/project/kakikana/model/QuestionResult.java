package com.salab.project.kakikana.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

public class QuestionResult {

    private int questionKanaId;
    private int answerKanaId;
    private int timeTaken;
    private boolean isCorrect;
    private boolean isIssued = false;

    // These members are marked @Exclude on their getter and setter methods.
    // This will tell Firebase not upload these members
    // ref: https://stackoverflow.com/questions/51774621/when-why-should-i-use-firebase-exclude-annotation-on-methods
    private Bitmap drawnAnswer;
    private String questionKanaType;
    private String questionKana;
    private String questionKanaRomaji;

    public void judgeAnswer() {
        // int cannot be null, default is 0
        if (answerKanaId != 0 && questionKanaId != 0) {
            isCorrect = (answerKanaId == questionKanaId);
        }
    }

    public void loadQuestionKana(Kana questionKana) {
        questionKanaId = questionKana.getId();
        questionKanaRomaji = questionKana.getRomaji();
        questionKanaType = questionKana.getType();
        this.questionKana = questionKana.getKana();
    }


    public void setQuestionKanaId(int questionKanaId) {
        this.questionKanaId = questionKanaId;
    }

    public int getAnswerKanaId() {
        return answerKanaId;
    }

    public void setAnswerKanaId(int answerKanaId) {
        this.answerKanaId = answerKanaId;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void setIssued(boolean issued) {
        isIssued = issued;
    }

    @Exclude
    public Bitmap getDrawnAnswer() {
        return drawnAnswer;
    }

    @Exclude
    public void setDrawnAnswer(Bitmap drawnAnswer) {
        this.drawnAnswer = drawnAnswer;
    }

    @Exclude
    public String getQuestionKanaType() {
        return questionKanaType;
    }

    @Exclude
    public void setQuestionKanaType(String questionKanaType) {
        this.questionKanaType = questionKanaType;
    }

    @Exclude
    public String getQuestionKana() {
        return questionKana;
    }

    @Exclude
    public void setQuestionKana(String questionKana) {
        this.questionKana = questionKana;
    }

    @Exclude
    public String getQuestionKanaRomaji() {
        return questionKanaRomaji;
    }

    @Exclude
    public void setQuestionKanaRomaji(String questionKanaRomaji) {
        this.questionKanaRomaji = questionKanaRomaji;
    }

    @Exclude
    public int getQuestionKanaId() {
        return questionKanaId;
    }

    @Exclude
    public String getQuestionKanaIdString() {
        return String.valueOf(questionKanaId);
    }

}
