/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.controller.io;

import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.io.task.*;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Class that manage all the operation linked to saving/loading of configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum IOManager {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(IOManager.class);

    // FORMAT FOR FILE
    //========================================================================
    public static final SimpleDateFormat DATE_FORMAT_FILENAME_WITHOUT_TIME = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DATE_FORMAT_FILENAME_WITH_TIME = new SimpleDateFormat("yyyyMMdd_HH-mm");
    public static final SimpleDateFormat DATE_FORMAT_FILENAME_WITH_TIME_SECOND = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    //========================================================================


    // PATH GETTER
    //========================================================================
    private File getProfileRoot() {
        return new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getAbsolutePath() + File.separator + LCConstant.PROFILE_DIRECTORY);
    }

    public File getConfigurationPath(final String profileID, final String configurationID) {
        return new File(this.getConfigurationDirectoryPath(profileID, configurationID));
    }

    public String getProfileDirectoryPath(final String profileID) {
        return getProfileRoot().getPath() + File.separator + profileID + File.separator;
    }

    public File getUserCompPath(final String profileID, final String userCompId) {
        return new File(this.getUserCompDirectory(profileID) + userCompId);
    }

    public String getUserCompDirectory(final String profileID) {
        return this.getProfileDirectoryPath(profileID) + File.separator + LCConstant.USER_COMP_DIRECTORY + File.separator;
    }

    public String getConfigurationDirectoryPath(final String profileID, final String configurationID) {
        return this.getProfileDirectoryPath(profileID) + File.separator + LCConstant.CONFIGURATION_DIRECTORY + File.separator + configurationID
                + File.separator;
    }

    public File getBackupProfileDestinationPath(final LCProfileI profile) {
        return new File(getProfileRoot().getPath() + File.separator + LCConstant.BACKUP_DIR + File.separator + LCConstant.PROFILE_DIRECTORY + File.separator + profile.getID() + File.separator + DATE_FORMAT_FILENAME_WITH_TIME_SECOND.format(new Date()) + "_" + LCUtils.getValidFileName(profile.nameProperty().get()) + "." + LCConstant.PROFILE_FILE_EXTENSION);
    }

    public File getBackupConfigurationDestinationPath(final LCConfigurationDescriptionI configurationDescription) {
        return new File(getProfileRoot().getPath() + File.separator + LCConstant.BACKUP_DIR + File.separator + LCConstant.CONFIGURATION_DIRECTORY + File.separator + configurationDescription.getConfigurationId() + File.separator + DATE_FORMAT_FILENAME_WITH_TIME_SECOND.format(new Date()) + "_" + LCUtils.getValidFileName(configurationDescription.configurationNameProperty().get()) + "." + LCConstant.CONFIG_FILE_EXTENSION);
    }
    //========================================================================

    // PROFILES
    //========================================================================
    public ProfileSavingTask createSaveProfileTask(final LCProfileI profile) {
        return new ProfileSavingTask(new File(getProfileRoot().getPath() + File.separator + profile.getID() + File.separator), profile);
    }

    public ProfileFullLoadingTask createLoadFullProfileTask(final LCProfileI profileDescription, boolean runChangesOnFXThread) {
        return new ProfileFullLoadingTask(new File(getProfileRoot().getPath() + File.separator + profileDescription.getID() + File.separator), profileDescription, runChangesOnFXThread);
    }

    public MultipleProfileDescriptionLoadingTask createLoadAllProfileDescriptionTask() {
        return new MultipleProfileDescriptionLoadingTask(getProfileRoot());
    }

    public ProfileDuplicateTask createProfileDuplicateTask(LCProfileI profile) {
        String newProfileId = StringUtils.getNewID();
        File destDirectory = new File(IOManager.INSTANCE.getProfileDirectoryPath(newProfileId));
        File sourceDirectory = new File(IOManager.INSTANCE.getProfileDirectoryPath(profile.getID()));
        return new ProfileDuplicateTask(profile, sourceDirectory, newProfileId, destDirectory, true);
    }
    //========================================================================

    // CONFIGURATIONS
    //========================================================================
    //Note : profile must contains configuration description before call
    public ConfigurationSavingTask createSaveConfigurationTask(final LCConfigurationI configuration, final LCProfileI profile) {
        File configurationDirectory = this.getConfigurationPath(profile.getID(), configuration.getID());
        return new ConfigurationSavingTask(configurationDirectory, configuration, profile);
    }

    public ConfigurationLoadingTask createLoadConfigurationTask(final LCConfigurationDescriptionI configDescription, final LCProfileI profile) {
        File configurationDirectory = this.getConfigurationPath(profile.getID(), configDescription.getConfigurationId());
        return new ConfigurationLoadingTask(configurationDirectory, configDescription);
    }
    //========================================================================

    // Class part : "Configuration description"
    //========================================================================
    public ConfigurationDescriptionSavingTask createSaveConfigDescriptionTask(final LCConfigurationDescriptionI description, final LCProfileI profile) {
        File configDescriptionDirectory = new File(getProfileRoot().getPath() + File.separator + profile.getID() + File.separator
                + LCConstant.CONFIGURATION_DIRECTORY + File.separator + description.getConfigurationId() + File.separator);
        return new ConfigurationDescriptionSavingTask(configDescriptionDirectory, description);
    }

    public ConfigurationDescriptionSavingTask createSaveConfigDescriptionTask(final LCConfigurationDescriptionI description, File configurationDirectory) {
        return new ConfigurationDescriptionSavingTask(configurationDirectory, description);
    }
    //========================================================================

    // KEYLIST
    //========================================================================
    public KeyListSavingTask createSaveKeyListTask(LCConfigurationI configuration, File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_KEYLIST_DIRECTORY);
        return new KeyListSavingTask(keyListDir, configuration.rootKeyListNodeProperty().get());
    }

    public KeyListLoadingTask createLoadKeyListTask(File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_KEYLIST_DIRECTORY);
        return new KeyListLoadingTask(keyListDir);
    }

    public KeyListImportTask createImportKeyListTask(List<File> keylistFiles) {
        return new KeyListImportTask(keylistFiles);
    }

    public KeyListExportTask createExportKeyListTask(File keylistFile, final List<KeyListNodeI> keyListNodes) {
        return new KeyListExportTask(keylistFile, keyListNodes);
    }
    //========================================================================

    // SEQUENCES
    //========================================================================
    public UserActionSequenceSavingTask createSaveSequenceTask(LCConfigurationI configuration, File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_SEQUENCE_DIRECTORY);
        return new UserActionSequenceSavingTask(keyListDir, configuration.userActionSequencesProperty().get());
    }

    public UserActionSequenceLoadingTask createLoadSequenceTask(File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_SEQUENCE_DIRECTORY);
        return new UserActionSequenceLoadingTask(keyListDir);
    }
    //========================================================================

    // Class part : "Import/export profiles"
    //========================================================================
    public ProfileExportTask createProfileExportTask(final LCProfileI profile, final File exportFile) {
        File profileDirectory = new File(this.getProfileDirectoryPath(profile.getID()));
        return new ProfileExportTask(profile, profileDirectory, exportFile);
    }

    public ProfileBackupAndThenTask createProfileBackupTask(final LCProfileI profile, final File exportFile, Runnable postBackupAction) {
        File profileDirectory = new File(this.getProfileDirectoryPath(profile.getID()));
        return new ProfileBackupAndThenTask(profile, profileDirectory, exportFile, postBackupAction);
    }
    //========================================================================

    // Class part : "User comp."
    //========================================================================
    public UserCompSavingTask createUserCompSavingTask(final UserCompDescriptionI userComp, final LCProfileI profile) {
        return new UserCompSavingTask(this.getUserCompPath(profile.getID(), userComp.getSavedComponentId()), userComp);
    }

    public MultiUserCompDescriptionLoadingTask createMultiUserCompDescriptionLoadingTask(final LCProfileI profile) {
        return new MultiUserCompDescriptionLoadingTask(new File(this.getUserCompDirectory(profile.getID())));
    }

    public UserCompLoadingTask createUserCompLoadingTask(final UserCompDescriptionI userComp, final LCProfileI profile) {
        return new UserCompLoadingTask(this.getUserCompPath(profile.getID(), userComp.getSavedComponentId()), userComp);
    }
    //========================================================================

    // Class part : "Import/export configuration"
    //========================================================================
    public ConfigurationExportTask createConfigurationExportTask(final LCConfigurationDescriptionI configurationDescription, final LCProfileI profile, final File exportFile) {
        File configurationDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
        return new ConfigurationExportTask(configurationDescription, configurationDirectory, exportFile);
    }

    public ConfigurationBackupAndThenTask createConfigurationBackupTask(final LCConfigurationDescriptionI configurationDescription, final LCProfileI profile, final File exportFile, Runnable postBackupAction) {
        File configurationDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
        return new ConfigurationBackupAndThenTask(configurationDescription, configurationDirectory, exportFile, postBackupAction);
    }

    public ConfigurationDuplicateTask createConfigurationDuplicateTaskFromCurrentProfile(LCConfigurationDescriptionI configurationDescription, LCProfileI profile) {
        return getConfigurationDuplicateTask(configurationDescription, profile, ConfigurationDuplicateTask.DuplicateMode.IN_PROFILE);
    }

    public ConfigurationDuplicateTask createConfigurationDuplicateTaskFromCurrentProfileChangeIdOnly(LCConfigurationDescriptionI configurationDescription, LCProfileI profile) {
        return getConfigurationDuplicateTask(configurationDescription, profile, ConfigurationDuplicateTask.DuplicateMode.CHANGE_ID_ONLY);
    }

    private ConfigurationDuplicateTask getConfigurationDuplicateTask(LCConfigurationDescriptionI configurationDescription, LCProfileI profile, ConfigurationDuplicateTask.DuplicateMode mode) {
        String newConfigId = StringUtils.getNewID();
        File destDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profile.getID(), newConfigId));
        File currentConfigDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
        return new ConfigurationDuplicateTask(configurationDescription, newConfigId, destDirectory, currentConfigDirectory, mode);
    }

    public ConfigurationDuplicateTask createConfigurationDuplicateTaskFromDefaultConfigurationDir(LCConfigurationDescriptionI configurationDescription, File configPath, LCProfileI profile) {
        String newConfigId = StringUtils.getNewID();
        File destDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profile.getID(), newConfigId));
        return new ConfigurationDuplicateTask(configurationDescription, newConfigId, destDirectory, configPath, ConfigurationDuplicateTask.DuplicateMode.FROM_DEFAULT);
    }

    public ConfigurationImportTask createConfigurationImport(final LCProfileI profil, final File configFile) throws LCException {
        String configurationID = this.getFileID(configFile);
        File configurationDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profil.getID(), configurationID));
        final LCConfigurationDescriptionI currentDefaultConfiguration = profil.getCurrentDefaultConfiguration();
        return new ConfigurationImportTask(configurationDirectory, configFile, configurationID, false,
                currentDefaultConfiguration != null ? currentDefaultConfiguration.getConfigurationId() : null);
    }

    public ConfigurationImportTask createCustomConfigurationImport(final File configurationImportRootDirectory, final File configFile) throws LCException {
        String configurationID = this.getFileID(configFile);
        File configurationDirectory = new File(configurationImportRootDirectory.getPath() + File.separator + configurationID);
        return new ConfigurationImportTask(configurationDirectory, configFile, configurationID, false, null);
    }

    public ProfileImportTask createProfileImportTask(final File profileFile) throws LCException {
        String profileId = this.getFileID(profileFile);
        File profileDirectory = new File(IOManager.INSTANCE.getProfileDirectoryPath(profileId));
        return new ProfileImportTask(profileFile, profileDirectory, profileId);
    }

    public String getFileID(final File lcFile) throws LCException {
        try {
            return IOUtils.getZipComment(lcFile);
        } catch (IOException e) {
            throw LCException.newException().withMessageId("exception.invalid.config.profil.file").withCause(e).build();
        }
    }
    //========================================================================


    // USE INFORMATION
    //========================================================================

    /**
     * Try to save the configuration use informations.<br>
     * If there is a current profile, and the configuration directory exist, save in the configuration directory.<br>
     * If the configuration was imported, try to save in the configuration file.
     *
     * @param configuration the configuration with the use information to save.
     */
    public void saveUseInformation(final LCConfigurationI configuration) {
        File savingDirectory = this.getDirectoryForUseInformation(configuration);
        UseInformationSavingTask useInfoSavingTask = new UseInformationSavingTask(savingDirectory, configuration);
        try {
            LCUtils.executeInCurrentThread(useInfoSavingTask);
        } catch (Exception e) {
            LOGGER.warn("Couldn't save the configuration use information for configuration {}", configuration.getID(), e);
        }
    }

    public void loadUseInformation(final LCConfigurationI configuration) {
        File loadingDirectory = this.getDirectoryForUseInformation(configuration);
        UseInformationLoadingTask useInfoLoadingTask = new UseInformationLoadingTask(loadingDirectory, configuration);
        try {
            LCUtils.executeInCurrentThread(useInfoLoadingTask);
        } catch (Exception e) {
            LOGGER.warn("Couldn't load the configuration use information for configuration {}", configuration.getID(), e);
        }
    }

    private File getDirectoryForUseInformation(final LCConfigurationI configuration) {
        LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
        File directory;
        if (currentProfile != null) {
            directory = IOManager.INSTANCE.getConfigurationPath(currentProfile.getID(), configuration.getID());
        } else {
            directory = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + File.separator + LCConstant.CONFIGURATION_USE_INFO_DEFAULT_DIRECTORY + File.separator + configuration.getID());
            LOGGER.warn("There is no current profile, so use information will be saved/loaded into the default location : {}",
                    directory);
        }
        return directory;
    }
    //========================================================================
}
