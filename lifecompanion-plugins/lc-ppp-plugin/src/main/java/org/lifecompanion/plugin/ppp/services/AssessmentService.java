package org.lifecompanion.plugin.ppp.services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.ppp.model.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum AssessmentService implements ModeListenerI {
    INSTANCE;

    private LCConfigurationI useConfiguration;
    private final ObjectProperty<AssessmentRecord> assessment;
    private final ObjectProperty<Question> question;

    private final Set<Consumer<AssessmentRecord>> assessmentStopCallbacks;
    private Consumer<AssessmentRecord> internalAssessmentStopCallback;
    private BiConsumer<AssessmentRecord, Runnable> internalAssessmentBeforeStopCallback;
    private Consumer<String> internalPainLocalizationCallback;

    private Integer latestPPPScore;

    AssessmentService() {
        this.assessmentStopCallbacks = new HashSet<>();
        this.assessment = new SimpleObjectProperty<>();
        this.question = new SimpleObjectProperty<>();
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.useConfiguration = configuration;
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.useConfiguration = null;
        this.assessment.set(null);
        this.question.set(null);
        this.internalAssessmentStopCallback = null;
        this.internalAssessmentBeforeStopCallback = null;
        this.latestPPPScore = null;
    }

    public void startPPPAssessment(boolean keepPrevEvaluator) {
        EvaluatorService.INSTANCE.startEvaluatorSelection(keepPrevEvaluator, evaluator -> {
            this.startAssessment(AssessmentType.PPP, evaluator, (assessment, callback) -> {
                KeyboardInputService.INSTANCE.startInput(
                        comment -> {
                            assessment.setComment(comment);

                            this.latestPPPScore = assessment.getScore();

                            callback.run();
                        },
                        Translation.getText("ppp.plugin.variables.current_keyboard_input.value.assessments.comment"),
                        NavigationService.INSTANCE::moveToPPPAssessmentGrid);
            }, this::saveAssessment);

            NavigationService.INSTANCE.moveToPPPAssessmentGrid();
        });
    }

    public void startEvsAssessment(boolean keepPrevEvaluator, boolean askPainLocalization) {
        EvaluatorService.INSTANCE.startEvaluatorSelection(keepPrevEvaluator, evaluator -> {
            this.startAssessment(AssessmentType.EVS, evaluator, this.askPainLocalization(askPainLocalization), assessment -> {
                ActionService.INSTANCE.startAction(true, action -> {
                    String comment = action.getComment();
                    if (comment != null) {
                        if (assessment.getComment() != null) {
                            comment = assessment.getComment() + "\n" + comment;
                        }

                        assessment.setComment(comment);
                    }

                    this.saveAssessment(assessment);
                });
            });

            NavigationService.INSTANCE.moveToEvsAssessmentGrid();
        });
    }

    public void startAutoEvsAssessment(boolean askPainLocalization) {
        this.startAssessment(AssessmentType.AUTO_EVS, null, this.askPainLocalization(askPainLocalization), this::saveAssessment);

        NavigationService.INSTANCE.moveToAutoEvsAssessmentGrid();
    }

    private BiConsumer<AssessmentRecord, Runnable> askPainLocalization(boolean askPainLocalization) {
        return askPainLocalization ? (assessment, stopCallback) -> {
            this.internalPainLocalizationCallback = (painLocalization) -> {
                assessment.setComment(Translation.getText(
                        "ppp.plugin.actions.assessment.select_pain_localization.comment", painLocalization));

                stopCallback.run();
            };

            NavigationService.INSTANCE.moveToPainLocalizationGrid();
        } : null;
    }

    private void saveAssessment(AssessmentRecord assessment) {
        RecordsService.INSTANCE.save(this.useConfiguration, assessment);
        SessionStatsController.INSTANCE.pushEvent("ppp.assessment.done", FluentHashMap
                .mapStrObj("id", assessment.getId())
                .with("startedAt", SessionStatsController.DATE_FORMAT_FOR_DATA_MAP.format(Date.from(assessment.getRecordedAt().toInstant())))
                .with("endedAt", SessionStatsController.DATE_FORMAT_FOR_DATA_MAP.format(new Date()))
                .with("assessmentType", assessment.getAssessmentType().name())
                .with("evaluatorType", assessment.getEvaluator().getType().name())
                .with("evaluatorName", assessment.getEvaluator().getName())
        );
    }

    private void startAssessment(AssessmentType assessmentType, Evaluator evaluator,
                                 BiConsumer<AssessmentRecord, Runnable> beforeStopCallback,
                                 Consumer<AssessmentRecord> afterStopCallback) {
        this.assessment.set(new AssessmentRecord(assessmentType, evaluator));
        this.question.set(assessmentType.getQuestions().get(0));
        this.internalPainLocalizationCallback = null;
        this.internalAssessmentBeforeStopCallback = beforeStopCallback;
        this.internalAssessmentStopCallback = afterStopCallback;
    }

    public void selectChoice(Choice choice) {
        AssessmentRecord assessment = this.assessment.get();
        Question question = this.question.get();
        if (choice == null || assessment == null || question == null) {
            return;
        }

        assessment.addAnswer(question, choice);

        List<Question> questions = assessment.getAssessmentType().getQuestions();
        int nextIndex = questions.indexOf(question) + 1;
        if (nextIndex < questions.size()) {
            this.setQuestionAndUpdate(questions.get(nextIndex));
        } else {
            this.stopAssessment();
        }
    }

    public void previousQuestion() {
        AssessmentRecord assessment = this.assessment.get();
        Question question = this.question.get();
        if (assessment == null || question == null) {
            return;
        }

        List<Question> questions = assessment.getAssessmentType().getQuestions();
        int currentIndex = questions.indexOf(question);
        if (currentIndex > 0) {
            Question previousQuestion = questions.get(currentIndex - 1);

            assessment.removeAnswer(previousQuestion);

            this.setQuestionAndUpdate(previousQuestion);
        }
    }

    public void stopAssessment() {
        AssessmentRecord assessmentRecord = this.assessment.get();
        if (assessmentRecord == null || !assessmentRecord.isCompleted()) {
            return;
        }

        Runnable stopAssessment = () -> {
            this.question.set(null);
            this.assessment.set(null);

            assessmentRecord.markCompleted();

            Consumer<AssessmentRecord> stopCallback = this.internalAssessmentStopCallback;
            if (stopCallback != null && assessmentRecord.isCompleted()) {
                this.internalAssessmentStopCallback = null;
                stopCallback.accept(assessmentRecord);
            }

            this.assessmentStopCallbacks.forEach(l -> l.accept(assessmentRecord));
        };

        if (this.internalAssessmentBeforeStopCallback != null) {
            this.internalAssessmentBeforeStopCallback.accept(assessmentRecord, stopAssessment);
        } else {
            stopAssessment.run();
        }
    }

    public void selectPainLocalization(String localization) {
        if (this.internalPainLocalizationCallback != null) {
            this.internalPainLocalizationCallback.accept(localization);
            this.internalPainLocalizationCallback = null;
        }
    }

    public void addAssessmentEndListener(Consumer<AssessmentRecord> stopCallback) {
        this.assessmentStopCallbacks.add(stopCallback);
    }

    public void removeAssessmentEndListener(Consumer<AssessmentRecord> stopCallback) {
        this.assessmentStopCallbacks.remove(stopCallback);
    }

    public ObjectProperty<Question> questionProperty() {
        return this.question;
    }

    public ObjectProperty<AssessmentRecord> assessmentProperty() {
        return this.assessment;
    }

    public Integer getLatestPPPScore() {
        return this.latestPPPScore;
    }

    private void setQuestionAndUpdate(Question question) {
        this.question.set(question);
        UseVariableController.INSTANCE.requestVariablesUpdate();
    }
}
