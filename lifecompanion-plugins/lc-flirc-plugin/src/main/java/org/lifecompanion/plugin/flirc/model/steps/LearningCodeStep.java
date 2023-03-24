package org.lifecompanion.plugin.flirc.model.steps;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.plugin.flirc.controller.FlircController;
import org.lifecompanion.util.ThreadUtils;

import java.util.List;

public class LearningCodeStep extends AbstractIRLearningStep {

    private final int index, count;

    public LearningCodeStep(int index, int count) {
        super("flirc.plugin.ir.learning.step.learning.code", "learning_code.png");
        this.count = count;
        this.index = index;
    }

    @Override
    public String getName() {
        return Translation.getText(translation + ".name", index, count);
    }

    @Override
    public IRLearningStepTask getTask() {
        return new IRLearningStepTask() {
            @Override
            protected List<String> call() throws Exception {
                try {
                    FlircController.INSTANCE.clearLogsAndEnableRecording();
                    while (!isCancelled()) {
                        List<String> recordedCodes = FlircController.INSTANCE.getRecordedCodes();
                        if (!CollectionUtils.isEmpty(recordedCodes)) {
                            return recordedCodes;
                        }
                        ThreadUtils.safeSleep(50);
                    }
                } finally {
                    if (index == count || isCancelled()) {
                        FlircController.INSTANCE.clearLogsAndDisableRecording();
                    }
                }
                return null;
            }
        };
    }

    @Override
    public boolean generateCodes() {
        return true;
    }
}


