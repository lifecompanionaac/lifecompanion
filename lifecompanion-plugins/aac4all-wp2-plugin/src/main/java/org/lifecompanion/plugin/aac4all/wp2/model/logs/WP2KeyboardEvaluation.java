package org.lifecompanion.plugin.aac4all.wp2.model.logs;

import java.util.ArrayList;
import java.util.List;

public class WP2KeyboardEvaluation {
    private KeyboardType type;
    private int fatigueScore;
    private int satisfactionScore;
    private List<WP2SentenceEvaluation> sentences = new ArrayList<>();

    public WP2KeyboardEvaluation(KeyboardType type) {
        this.type = type;
    }

    public int getFatigueScore() {
        return fatigueScore;
    }

    public void setFatigueScore(int fatigueScore) {
        this.fatigueScore = fatigueScore;
    }

    public int getSatisfactionScore() {
        return satisfactionScore;
    }

    public void setSatisfactionScore(int satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }

    public List<WP2SentenceEvaluation> getSentenceLogs() {
        return sentences;
    }

}
