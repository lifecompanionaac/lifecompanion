package org.lifecompanion.plugin.flirc.model.steps;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.flirc.controller.FlircController;

import java.util.List;

public class PrepareLearningStep extends AbstractIRLearningStep {

    public PrepareLearningStep() {
        super("flirc.plugin.ir.learning.step.prepare.first.learning", "prepare_learning.png");
    }

    @Override
    public IRLearningStepTask getTask() {
        return new IRLearningStepTask() {
            @Override
            protected List<String> call() throws Exception {
                FlircController.INSTANCE.clearLogsAndDisableRecording();
                return null;
            }
        };
    }

    @Override
    public String getManualStepButtonName() {
        return Translation.getText(translation + ".button");
    }

}
