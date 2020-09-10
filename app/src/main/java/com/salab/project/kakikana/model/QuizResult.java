package com.salab.project.kakikana.model;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizResult {

    private Long timestamp;
    private int testId = 0;
    private int totalCorrected = 0;
    private int totalTested = 0;
    private List<QuestionResult> questionResultList = new ArrayList<>();

    public QuizResult() {
    }

    public void appendQuestionResult(QuestionResult questionResult) {
        questionResultList.add(questionResult);

        if (!questionResult.isIssued()) {
            // if users flag the question as issued, then it will not be counted into score
            totalTested++;
            if (questionResult.isCorrect()) {
                totalCorrected++;
            }
        }
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalCorrected() {
        return totalCorrected;
    }

    public void setTotalCorrected(int totalCorrected) {
        this.totalCorrected = totalCorrected;
    }

    public int getTotalTested() {
        return totalTested;
    }

    public void setTotalTested(int totalTested) {
        this.totalTested = totalTested;
    }

    public List<QuestionResult> getQuestionResultList() {
        return questionResultList;
    }

    public void setQuestionResultList(List<QuestionResult> questionResultList) {
        this.questionResultList = questionResultList;
    }

    public Map<String, Object> toMap() {
        // map format is easier to manipulate before update or add to Firebase database
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("testId", testId);
        result.put("totalCorrected", totalCorrected);
        result.put("totalTested", totalTested);
        result.put("questionResultList", questionResultList);

        return result;
    }

}
