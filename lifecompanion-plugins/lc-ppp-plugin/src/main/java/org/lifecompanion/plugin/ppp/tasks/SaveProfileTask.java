package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.ProfileService;
import org.lifecompanion.util.model.LCTask;

public class SaveProfileTask extends LCTask<Void> {
    private final LCConfigurationI config;
    private final UserProfile profile;

    public SaveProfileTask(LCConfigurationI config, UserProfile profile) {
        super("ppp.plugin.task.profile.save.title");
        this.config = config;
        this.profile = profile;
    }

    @Override
    protected Void call() throws Exception {
        ProfileService.INSTANCE.saveProfile(this.config, this.profile);

        return null;
    }
}
