package org.lifecompanion.plugin.aac4all.wp2.model.useaction;

public enum EvaScoreType {
    SCORE_0(0),SCORE_1(1), SCORE_2(2), SCORE_3(3), SCORE_4(4), SCORE_5(5),SCORE_6(6), SCORE_7(7), SCORE_8(8),SCORE_9(9), SCORE_10(10);

    private final int score;

    EvaScoreType(int score){
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
