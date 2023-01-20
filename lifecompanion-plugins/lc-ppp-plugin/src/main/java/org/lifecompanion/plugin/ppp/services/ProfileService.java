package org.lifecompanion.plugin.ppp.services;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.ppp.model.UserProfile;

import java.io.File;

public enum ProfileService implements ModeListenerI {
    INSTANCE;

    private UserProfile currentProfile;

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.currentProfile = ProfileService.INSTANCE.loadProfile(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.currentProfile = null;
    }

    public UserProfile getCurrentProfile() {
        return this.currentProfile;
    }

    public void saveProfile(LCConfigurationI config, UserProfile profile) {
        FilesService.INSTANCE.jsonSave(profile,
                this.getProfileFilePath(FilesService.INSTANCE.getPluginDirectoryPath(config)));
    }

    public UserProfile loadProfile(String dataDirectory) {
        UserProfile profile = FilesService.INSTANCE.jsonLoadOne(UserProfile.class,
                this.getProfileFilePath(dataDirectory));
        return profile == null ? new UserProfile() : profile;
    }

    public UserProfile loadProfile(LCConfigurationI config) {
        return this.loadProfile(FilesService.INSTANCE.getPluginDirectoryPath(config));
    }

    private String getProfileFilePath(String dataDirectory) {
        return dataDirectory + File.separator + "profile.json";
    }
}
