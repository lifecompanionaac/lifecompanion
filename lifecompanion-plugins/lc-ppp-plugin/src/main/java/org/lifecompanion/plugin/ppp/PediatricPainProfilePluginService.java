package org.lifecompanion.plugin.ppp;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.model.Question;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.*;
import org.lifecompanion.plugin.ppp.view.records.periods.DateFormats;

import java.time.LocalDate;
import java.util.function.Function;

public enum PediatricPainProfilePluginService implements ModeListenerI {
    INSTANCE;

    public static final String VAR_CURRENT_QUESTION_TEXT = "PDPCurrentQuestionText";
    public static final String VAR_CURRENT_QUESTION_INDEX = "PDPCurrentQuestionIndex";
    public static final String VAR_LATEST_PPP_SCORE = "PDPLatestPDPScore";
    public static final String VAR_PROFILE_BASE_SCORE = "PDPProfileBaseScore";
    public static final String VAR_PROFILE_BASE_SCORE_AT = "PDPProfileBaseScoreAt";
    public static final String VAR_PROFILE_NAME = "PDPProfileName";
    public static final String VAR_CURRENT_KEYBOARD_INPUT = "PDPCurrentKeyboardInput";

    private static final String EMPTY_VAR_VALUE = "-";

    private final ModeListenerI[] SERVICES = {
            KeyboardInputService.INSTANCE,
            NavigationService.INSTANCE,
            EvaluatorService.INSTANCE,
            AssessmentService.INSTANCE,
            UserDatabaseService.INSTANCE,
            ActionService.INSTANCE
    };

    PediatricPainProfilePluginService() {
    }

    // Class part : "Start/stop"
    //========================================================================

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        for (ModeListenerI service : SERVICES) {
            service.modeStart(configuration);
        }
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        for (ModeListenerI service : SERVICES) {
            service.modeStop(configuration);
        }
    }

    //========================================================================

    // Class part : "Use variables"
    //========================================================================
    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return switch (id) {
            case VAR_CURRENT_QUESTION_TEXT -> def -> new StringUseVariable(def, this.generateCurrentQuestionText());
            case VAR_CURRENT_QUESTION_INDEX -> def -> new StringUseVariable(def, generateCurrentQuestionIndex());
            case VAR_LATEST_PPP_SCORE -> def -> new StringUseVariable(def, generateLatestPPPScore());
            case VAR_PROFILE_BASE_SCORE -> def -> new StringUseVariable(def, generateProfileBaseScore());
            case VAR_PROFILE_BASE_SCORE_AT -> def -> new StringUseVariable(def, generateProfileBaseScoreAt());
            case VAR_PROFILE_NAME -> def -> new StringUseVariable(def, generateProfileName());
            case VAR_CURRENT_KEYBOARD_INPUT -> def -> new StringUseVariable(def, generateCurrentKeyboardInput());
            default -> null;
        };
    }

    private String generateCurrentQuestionText() {
        Question currentQuestion = AssessmentService.INSTANCE.questionProperty().get();

        return currentQuestion != null ? currentQuestion.getText() : "";
    }

    private String generateCurrentQuestionIndex() {
        AssessmentRecord currentAssessment = AssessmentService.INSTANCE.assessmentProperty().get();
        Question currentQuestion = AssessmentService.INSTANCE.questionProperty().get();
        if (currentAssessment == null || currentQuestion == null) {
            return Translation.getText("ppp.plugin.variables.current_question_index.value",
                    EMPTY_VAR_VALUE, EMPTY_VAR_VALUE);
        }

        return Translation.getText("ppp.plugin.variables.current_question_index.value",
                currentAssessment.getAssessmentType().getQuestions().indexOf(currentQuestion) + 1,
                currentAssessment.getAssessmentType().getQuestions().size());
    }

    private String generateLatestPPPScore() {
        Integer latestScore = AssessmentService.INSTANCE.getLatestPPPScore();
        if (latestScore != null) {
            return Integer.toString(latestScore);
        }

        return EMPTY_VAR_VALUE;
    }

    private String generateProfileBaseScore() {
        UserProfile selectedProfile = UserDatabaseService.INSTANCE.getSelectedProfile();
        return selectedProfile != null ? Integer.toString(selectedProfile.getBaseScore()) : "?";
    }

    private String generateProfileBaseScoreAt() {
        UserProfile selectedProfile = UserDatabaseService.INSTANCE.getSelectedProfile();
        LocalDate date = selectedProfile != null ? selectedProfile.getBaseScoreAt() : null;
        return date == null ? "" : date.format(DateFormats.SHORT_DATE);
    }

    private String generateProfileName() {
        UserProfile selectedProfile = UserDatabaseService.INSTANCE.getSelectedProfile();
        return selectedProfile != null ? selectedProfile.getUserName() : "";
    }

    private String generateCurrentKeyboardInput() {
        String keyboardInputRequest = KeyboardInputService.INSTANCE.keyboardInputRequestProperty().get();
        return keyboardInputRequest != null ? keyboardInputRequest : "";
    }
    //========================================================================
}
