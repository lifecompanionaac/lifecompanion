package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.plugin.ppp.services.FilesService;
import org.lifecompanion.util.model.LCTask;

import java.io.File;

public class ImportDataTask extends LCTask<File> {
    private final File dataZipFile;

    public ImportDataTask(final File dataZipFile) {
        super("ppp.plugin.task.data.import.title");
        this.dataZipFile = dataZipFile;
    }

    @Override
    protected File call() throws Exception {
        return FilesService.INSTANCE.importData(this.dataZipFile);
    }
}
