package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.ProfileService;
import org.lifecompanion.util.model.LCTask;

import java.io.File;

public class LoadDirProfileTask extends LCTask<UserProfile> {
    private final File dataDirectory;

    public LoadDirProfileTask(final File dataDirectory) {
        super("ppp.plugin.task.profile.load.directory.title");
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected UserProfile call() throws Exception {
        return ProfileService.INSTANCE.loadProfile(this.dataDirectory.getAbsolutePath());
    }
}
