package org.lifecompanion.plugin.ppp.model;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class AssessmentRecord extends AbstractRecord {
    private final transient Map<Question, Choice> answers;

    private transient AssessmentType assessmentType;

    private int score;

    private ZonedDateTime endedAt;

    public AssessmentRecord(AssessmentType assessmentType, Evaluator evaluator) {
        super(evaluator);
        this.assessmentType = assessmentType;
        this.answers = new HashMap<>();
        this.score = 0;
    }

    @Override
    public String getRecordsDirectory() {
        return this.getAssessmentType().getDirectory();
    }

    public AssessmentType getAssessmentType() {
        return this.assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public int getScore() {
        return this.score;
    }

    public void addAnswer(Question question, Choice choice) {
        if (!question.getChoicesScoresMap().containsKey(choice)) {
            return;
        }

        this.answers.put(question, choice);
        this.score += question.getChoicesScoresMap().get(choice);
    }

    public void removeAnswer(Question question) {
        this.score -= question.getChoicesScoresMap().get(this.answers.get(question));
        this.answers.remove(question);
    }

    public boolean isCompleted() {
        return this.answers.size() == this.assessmentType.getQuestions().size();
    }

    public void markCompleted() {
        this.endedAt = ZonedDateTime.now();
    }
}
