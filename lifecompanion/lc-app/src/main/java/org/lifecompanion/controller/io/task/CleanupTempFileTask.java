package org.lifecompanion.controller.io.task;

import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CleanupTempFileTask extends LCTask<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanupTempFileTask.class);

    public CleanupTempFileTask() {
        super("task.cleanup.temp.file");
    }

    @Override
    protected Long call() throws Exception {
        long removedSize = 0;
        File[] directoriesToCleanUp = {
                // Temp directory dedicated to LifeCompanion
                new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion"),
                // Config/profile backups
                new File(IOHelper.getBackupRootDirectory()),
                // Clipboard temp dir
                new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.CLIPBOARD_CAPTURE_DIR_NAME),
                // Thumbnails
                new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + File.separator + LCConstant.THUMBNAIL_DIR_NAME + File.separator),
        };
        for (File dir : directoriesToCleanUp) {
            removedSize += clean(dir);
        }
        return removedSize;
    }

    private long clean(File target) {
        if (target.isDirectory()) {
            File[] children = target.listFiles();
            long removed = 0;
            if (children != null) {
                for (File child : children) {
                    removed += clean(child);
                }
            }
            target.delete();
            return removed;
        } else if (target.isFile()) {
            long length = target.length();
            boolean delete = target.delete();
            if (delete) {
                return length;
            } else {
                LOGGER.warn("Could not delete temp file : {}", target);
            }
        }
        return 0;
    }
}
