package org.lifecompanion.plugin.aac4all.wp2.model.logs;

import java.util.ArrayList;
import java.util.List;

public class WP2KeyboardEvaluation {
    private KeyboardType type;
    private int fatigueInitScore;
    private int fatiguePostScore;
    private int satisfactionScore;
    private List<WP2SentenceEvaluation> sentences = new ArrayList<>();

    public WP2KeyboardEvaluation(KeyboardType type) {
        this.type = type;
    }

    public int getFatigueScore() { return fatiguePostScore;}

    public void setFatigueScore(int fatigueScore) {
        this.fatiguePostScore = fatigueScore;
    }

    public int getFatigueInitScore(){return fatigueInitScore;}

    public void setFatigueInitScore(int fatigueInitScore){this.fatigueInitScore=fatigueInitScore;}

    public int getSatisfactionScore() {
        return satisfactionScore;
    }

    public void setSatisfactionScore(int satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }

    public List<WP2SentenceEvaluation> getSentenceLogs() {
        return sentences;
    }

    public void resetEva(){
        this.fatigueInitScore =-1;
        this.fatiguePostScore=-1;
        this.satisfactionScore=-1;
    }

}
