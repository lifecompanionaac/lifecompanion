package org.lifecompanion.plugin.flirc.model.steps;

public interface IRLearningStep {
    String getImage();

    String getName();

    String getDescription();

    IRLearningStepTask getTask();

    boolean generateCodes();

    String getManualStepButtonName();

    boolean isManualStep();

    String getNotificationOnShown();
}
