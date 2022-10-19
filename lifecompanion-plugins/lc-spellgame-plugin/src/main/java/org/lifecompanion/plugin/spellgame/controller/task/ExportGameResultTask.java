package org.lifecompanion.plugin.spellgame.controller.task;

import org.lifecompanion.util.model.LCTask;

import java.io.File;

public class ExportGameResultTask extends LCTask<Void> {
    private final File resultFile;

    public ExportGameResultTask(File resultFile) {
        super("");
        this.resultFile = resultFile;
    }

    @Override
    protected Void call() throws Exception {
        return null;
    }
}
