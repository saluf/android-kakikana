package com.salab.project.kakikana.model;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class User {

    // TODO: consider separate private User data from public one
    private String name = "Anonymous";
    private long registerTime;
    private long lastLoginTime;
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

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
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

    public Map<String, Object> toMap(boolean isServerTime){
        // map format is easier to manipulate before update or add to Firebase database
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("registerTime", registerTime);
        result.put("lastLoginTime", lastLoginTime);
        if (isServerTime){
            // replace with reserved keyword to fill it with server time
            result.put("totalCorrect", ServerValue.TIMESTAMP);
            result.put("totalTested", ServerValue.TIMESTAMP);
        } else {
            result.put("totalCorrect", totalCorrect);
            result.put("totalTested", totalTested);
        }
        return result;
    }
}
