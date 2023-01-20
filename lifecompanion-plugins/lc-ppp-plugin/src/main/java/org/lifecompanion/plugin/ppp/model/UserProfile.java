package org.lifecompanion.plugin.ppp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String userId;

    private int baseScore;

    private LocalDate baseScoreAt;

    private List<Action> actions;

    public UserProfile() {
        this.userId = "";
        this.baseScore = 0;
        this.actions = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    public LocalDate getBaseScoreAt() {
        return this.baseScoreAt;
    }

    public void setBaseScoreAt(LocalDate baseScoreAt) {
        this.baseScoreAt = baseScoreAt;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
