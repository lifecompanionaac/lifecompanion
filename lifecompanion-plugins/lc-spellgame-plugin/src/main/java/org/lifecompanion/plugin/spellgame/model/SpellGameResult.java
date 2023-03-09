package org.lifecompanion.plugin.spellgame.model;

import java.util.Date;
import java.util.List;

public class SpellGameResult {
    private String listName;
    private int listSize;
    private Date createAt;
    private long duration;
    private int score;
    private int doneCount;
    private boolean ignoreAccents;
    private transient List<SpellGameStepResult> answers;

    public SpellGameResult(String listName, int listSize,boolean ignoreAccents, Date createAt, long duration, int score, int doneCount, List<SpellGameStepResult> answers) {
        this.listName = listName;
        this.listSize = listSize;
        this.ignoreAccents = ignoreAccents;
        this.createAt = createAt;
        this.duration = duration;
        this.score = score;
        this.doneCount = doneCount;
        this.answers = answers;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public List<SpellGameStepResult> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SpellGameStepResult> answers) {
        this.answers = answers;
    }

    public boolean isIgnoreAccents() {
        return ignoreAccents;
    }

    public void setIgnoreAccents(boolean ignoreAccents) {
        this.ignoreAccents = ignoreAccents;
    }

    @Override
    public String toString() {
        return "SpellGameResult{" +
                "listName='" + listName + '\'' +
                ", listSize=" + listSize +
                ", createAt=" + createAt +
                ", duration=" + duration +
                ", score=" + score +
                ", doneCount=" + doneCount +
                ", ignoreAccents=" + ignoreAccents +
                ", answers=" + answers +
                '}';
    }
}
