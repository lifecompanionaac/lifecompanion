package org.lifecompanion.plugin.flirc.model.steps;

import org.lifecompanion.plugin.flirc.controller.FlircController;

import java.util.List;

public class WaitingDeviceStep extends AbstractIRLearningStep {

    public WaitingDeviceStep() {
        super("flirc.plugin.ir.learning.step.waiting.for.device", "waiting_step.jpg");
    }

    @Override
    public IRLearningStepTask getTask() {
        return new IRLearningStepTask() {
            @Override
            protected List<String> call() throws Exception {
                FlircController.INSTANCE.waitForDevice();
                return null;
            }
        };
    }
}
