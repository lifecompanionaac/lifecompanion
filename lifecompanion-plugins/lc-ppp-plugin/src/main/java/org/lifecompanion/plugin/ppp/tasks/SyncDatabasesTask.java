package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.dto.ApplicationUpdateFileDto;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.ppp.model.UserDatabase;
import org.lifecompanion.plugin.ppp.services.FilesService;
import org.lifecompanion.plugin.ppp.services.UserDatabaseService;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SyncDatabasesTask extends LCTask<Boolean> {
    private final String currentDatabasePath;
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncDatabasesTask.class);

    public SyncDatabasesTask(String currentDatabasePath) {
        super("ppp.plugin.task.sync.databases.title");
        this.currentDatabasePath = currentDatabasePath;
    }

    @Override
    protected Boolean call() throws Exception {
        boolean currentDatabaseShouldBeReload = false;

        updateMessage("ppp.plugin.task.sync.databases.checking");
        UserDatabase currentDatabase = UserDatabaseService.INSTANCE.loadDatabase(this.currentDatabasePath);
        File[] roots = File.listRoots();
        File extPath = null;
        for (File root : roots) {
            File syncDir = new File(root.getPath() + File.separator + "ppp-sync-database");
            if (syncDir.exists()) {
                extPath = syncDir;
                break;
            }
        }
        if (extPath == null) {
            LCException.newException().withMessageId("ppp.plugin.task.sync.cant.find.database.key").buildAndThrow();
        }

        UserDatabase extDatabase = UserDatabaseService.INSTANCE.loadDatabase(extPath.getAbsolutePath());

        LOGGER.info("Database modification date comparison, current {}, external {}", currentDatabase.getModifiedAt(), extDatabase.getModifiedAt());
        File databaseFileCurrent = new File(UserDatabaseService.INSTANCE.getDatabaseFilePath(currentDatabasePath));
        File databaseFileExt = new File(UserDatabaseService.INSTANCE.getDatabaseFilePath(extPath.getAbsolutePath()));
        if (extDatabase.getModifiedAt() != null && (currentDatabase.getModifiedAt() == null || extDatabase.getModifiedAt().isAfter(currentDatabase.getModifiedAt()))) {
            LOGGER.info("External user database was modified last, current database will be replaced with the external");
            IOUtils.copyFiles(databaseFileExt, databaseFileCurrent);
            currentDatabaseShouldBeReload = true;
        } else {
            LOGGER.info("Current user database is the most recent, external database will be replaced with");
            IOUtils.copyFiles(databaseFileCurrent, databaseFileExt);
        }

        updateMessage("ppp.plugin.task.sync.check.changes");
        // Count and detect files in both database
        long start = System.currentTimeMillis();
        HashMap<String, Long> currentFiles = new HashMap<>(1500);
        File currentUsersDir = new File(currentDatabasePath + File.separator + "users");
        listFilesIn(currentUsersDir, currentUsersDir, currentFiles);
        LOGGER.info("Found {} files in current database in {} ms", currentFiles.size(), (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        HashMap<String, Long> extFiles = new HashMap<>(1500);
        File extUsersDir = new File(extPath + File.separator + "users");
        listFilesIn(extUsersDir, extUsersDir, extFiles);
        LOGGER.info("Found {} files in external database in {} ms", extFiles.size(), (System.currentTimeMillis() - start));

        Set<String> fromCurrentToExternal = detectMissing(currentFiles, extFiles);
        LOGGER.info("Detect {} changes from current to external database", fromCurrentToExternal.size());

        Set<String> fromExternalToCurrent = detectMissing(extFiles, currentFiles);
        LOGGER.info("Detect {} changes from external to current database", fromExternalToCurrent.size());

        updateMessage("ppp.plugin.task.sync.sync.changes");
        int p = 0, total = fromCurrentToExternal.size() + fromExternalToCurrent.size();
        p = copyFiles(extUsersDir, currentUsersDir, fromCurrentToExternal, p, total);
        p = copyFiles(currentUsersDir, extUsersDir, fromExternalToCurrent, p, total);
        updateProgress(1, 1);

        return currentDatabaseShouldBeReload;
    }

    private int copyFiles(File fromDir, File toDir, Set<String> pathList, int progress, int total) throws IOException {
        for (String toCopy : pathList) {
            File srcFile = new File(toDir.getPath() + File.separator + toCopy);
            File destFile = new File(fromDir.getPath() + File.separator + toCopy);
            IOUtils.copyFiles(srcFile, destFile);
            updateProgress(progress++, total);
        }
        return progress;
    }

    private Set<String> detectMissing(Map<String, Long> from, Map<String, Long> to) {
        Set<String> diff = new HashSet<>(500);
        from.forEach((path, size) -> {
            if (!to.containsKey(path)) {
                diff.add(path);
            }
        });
        return diff;
    }

    private void listFilesIn(File root, File file, Map<String, Long> result) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    listFilesIn(root, f, result);
                }
            }
        } else if (root != file) {
            String relativePath = IOUtils.getRelativePath(file.getAbsolutePath(), root.getAbsolutePath());
            result.put(relativePath, file.length());
        }
    }

}
