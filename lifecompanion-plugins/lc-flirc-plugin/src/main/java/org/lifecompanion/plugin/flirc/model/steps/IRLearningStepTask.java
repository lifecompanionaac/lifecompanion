package org.lifecompanion.plugin.flirc.model.steps;

import org.lifecompanion.util.model.LCTask;

import java.util.List;

public abstract class IRLearningStepTask extends LCTask<List<String>> {
    protected IRLearningStepTask() {
        super("flirc.plugin.ir.learning.task.title");
    }
}
