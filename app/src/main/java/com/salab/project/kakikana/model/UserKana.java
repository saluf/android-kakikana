package com.salab.project.kakikana.model;

public class UserKana {

    int totalCorrect = 0;
    int totalTested = 0;

    public UserKana() {
    }

    public UserKana(int totalCorrect, int totalTested) {
        this.totalCorrect = totalCorrect;
        this.totalTested = totalTested;
    }

    public void addCorrect() {
        totalCorrect++;
        totalTested++;
    }

    public void addIncorrect() {
        totalTested++;
    }

    public void mergeUserKanas(UserKana userKana) {
        totalCorrect += userKana.totalCorrect;
        totalTested += userKana.totalTested;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public int getTotalTested() {
        return totalTested;
    }

    public void setTotalTested(int totalTested) {
        this.totalTested = totalTested;
    }
}
