package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.ProfileService;
import org.lifecompanion.util.model.LCTask;

public class LoadConfigProfileTask extends LCTask<UserProfile> {
    private final LCConfigurationI config;

    public LoadConfigProfileTask(final LCConfigurationI config) {
        super("ppp.plugin.task.profile.load.config.title");
        this.config = config;
    }

    @Override
    protected UserProfile call() throws Exception {
        return ProfileService.INSTANCE.loadProfile(this.config);
    }
}
