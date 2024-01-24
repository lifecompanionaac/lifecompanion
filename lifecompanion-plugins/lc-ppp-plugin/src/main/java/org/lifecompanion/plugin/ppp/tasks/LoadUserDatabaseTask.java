package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.UserDatabase;
import org.lifecompanion.plugin.ppp.services.UserDatabaseService;
import org.lifecompanion.util.model.LCTask;

public class LoadUserDatabaseTask extends LCTask<UserDatabase> {
    private final LCConfigurationI config;

    public LoadUserDatabaseTask(final LCConfigurationI config) {
        super("ppp.plugin.task.profile.load.user.database.title");
        this.config = config;
    }

    @Override
    protected UserDatabase call() throws Exception {
        return UserDatabaseService.INSTANCE.loadDatabase(this.config);
    }
}
