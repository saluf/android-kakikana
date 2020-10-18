package com.salab.project.kakikana.model;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String name = "Anonymous";
    private long registerTime;
    private int totalCorrect = 0;
    private int totalTested = 0;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
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

    public Map<String, Object> toMap(boolean isServerTime) {
        // map format is easier to manipulate before update or add to Firebase database
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("totalCorrect", totalCorrect);
        result.put("totalTested", totalTested);
        if (isServerTime) {
            // replace with reserved keyword to fill it with server time
            result.put("registerTime", ServerValue.TIMESTAMP);
        } else {
            result.put("registerTime", registerTime);
        }
        return result;
    }
}
