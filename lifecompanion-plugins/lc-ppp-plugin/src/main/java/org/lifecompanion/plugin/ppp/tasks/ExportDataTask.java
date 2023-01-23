package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.services.FilesService;
import org.lifecompanion.util.model.LCTask;

import java.io.File;

public class ExportDataTask extends LCTask<Void> {
    private final LCConfigurationI config;
    private final File destinationZipFile;

    public ExportDataTask(final LCConfigurationI config, final File destinationZipFile) {
        super("ppp.plugin.task.data.export.title");
        this.config = config;
        this.destinationZipFile = destinationZipFile;
    }

    @Override
    protected Void call() throws Exception {
        FilesService.INSTANCE.exportData(this.config, this.destinationZipFile);
        return null;
    }
}
