package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.UserDatabase;
import org.lifecompanion.plugin.ppp.services.UserDatabaseService;
import org.lifecompanion.util.model.LCTask;

public class SaveUserDatabaseTask extends LCTask<Void> {
    private final LCConfigurationI config;
    private final UserDatabase userDatabase;

    public SaveUserDatabaseTask(LCConfigurationI config, UserDatabase userDatabase) {
        super("ppp.plugin.task.save.user.database.title");
        this.config = config;
        this.userDatabase = userDatabase;
    }

    @Override
    protected Void call() throws Exception {
        UserDatabaseService.INSTANCE.saveDatabase(this.config, this.userDatabase);
        return null;
    }
}
