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
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Class that manage all the operation linked to saving/loading of configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IOHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOHelper.class);

    // FORMAT FOR FILE
    //========================================================================
    public static final SimpleDateFormat DATE_FORMAT_FILENAME_WITHOUT_TIME = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DATE_FORMAT_FILENAME_WITH_TIME = new SimpleDateFormat("yyyyMMdd_HH-mm");
    public static final SimpleDateFormat DATE_FORMAT_FILENAME_WITH_TIME_SECOND = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    //========================================================================


    // PATH GETTER
    //========================================================================
    private static File getProfileRoot() {
        return new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getAbsolutePath() + File.separator + LCConstant.PROFILE_DIRECTORY);
    }

    public static File getConfigurationPath(final String profileID, final String configurationID) {
        return new File(getConfigurationDirectoryPath(profileID, configurationID));
    }

    public static String getProfileDirectoryPath(final String profileID) {
        return getProfileRoot().getPath() + File.separator + profileID + File.separator;
    }

    public static File getUserCompPath(final String profileID, final String userCompId) {
        return new File(getUserCompDirectory(profileID) + userCompId);
    }

    public static String getUserCompDirectory(final String profileID) {
        return getProfileDirectoryPath(profileID) + File.separator + LCConstant.USER_COMP_DIRECTORY + File.separator;
    }

    public static String getConfigurationDirectoryPath(final String profileID, final String configurationID) {
        return getProfileDirectoryPath(profileID) + File.separator + LCConstant.CONFIGURATION_DIRECTORY + File.separator + configurationID
                + File.separator;
    }

    public static File getBackupProfileDestinationPath(final LCProfileI profile) {
        return new File(getBackupRootDirectory() + LCConstant.PROFILE_DIRECTORY + File.separator + profile.getID() + File.separator + DATE_FORMAT_FILENAME_WITH_TIME_SECOND.format(
                new Date()) + "_" + org.lifecompanion.util.IOUtils.getValidFileName(profile.nameProperty().get()) + "." + LCConstant.PROFILE_FILE_EXTENSION);
    }

    public static File getBackupConfigurationDestinationPath(final LCConfigurationDescriptionI configurationDescription) {
        return new File( getBackupRootDirectory()+ LCConstant.CONFIGURATION_DIRECTORY + File.separator + configurationDescription.getConfigurationId() + File.separator + DATE_FORMAT_FILENAME_WITH_TIME_SECOND.format(
                new Date()) + "_" + org.lifecompanion.util.IOUtils.getValidFileName(configurationDescription.configurationNameProperty().get()) + "." + LCConstant.CONFIG_FILE_EXTENSION);
    }

    public static String getBackupRootDirectory(){
        return getProfileRoot().getPath() + File.separator + LCConstant.BACKUP_DIR + File.separator;
    }
    //========================================================================

    // PROFILES
    //========================================================================
    public static ProfileSavingTask createSaveProfileTask(final LCProfileI profile) {
        return new ProfileSavingTask(new File(getProfileRoot().getPath() + File.separator + profile.getID() + File.separator), profile);
    }

    public static ProfileFullLoadingTask createLoadFullProfileTask(final LCProfileI profileDescription, boolean runChangesOnFXThread) {
        return new ProfileFullLoadingTask(new File(getProfileRoot().getPath() + File.separator + profileDescription.getID() + File.separator), profileDescription, runChangesOnFXThread);
    }

    public static MultipleProfileDescriptionLoadingTask createLoadAllProfileDescriptionTask() {
        return new MultipleProfileDescriptionLoadingTask(getProfileRoot());
    }

    public static ProfileDuplicateTask createProfileDuplicateTask(LCProfileI profile) {
        String newProfileId = StringUtils.getNewID();
        File destDirectory = new File(getProfileDirectoryPath(newProfileId));
        File sourceDirectory = new File(getProfileDirectoryPath(profile.getID()));
        return new ProfileDuplicateTask(profile, sourceDirectory, newProfileId, destDirectory, true);
    }
    //========================================================================

    // CONFIGURATIONS
    //========================================================================
    //Note : profile must contains configuration description before call
    public static ConfigurationSavingTask createSaveConfigurationTask(final LCConfigurationI configuration, final LCProfileI profile) {
        File configurationDirectory = getConfigurationPath(profile.getID(), configuration.getID());
        return new ConfigurationSavingTask(configurationDirectory, configuration, profile);
    }

    public static ConfigurationLoadingTask createLoadConfigurationTask(final LCConfigurationDescriptionI configDescription, final LCProfileI profile) {
        File configurationDirectory = getConfigurationPath(profile.getID(), configDescription.getConfigurationId());
        return new ConfigurationLoadingTask(configurationDirectory, configDescription);
    }
    //========================================================================

    // Class part : "Configuration description"
    //========================================================================
    public static ConfigurationDescriptionSavingTask createSaveConfigDescriptionTask(final LCConfigurationDescriptionI description, final LCProfileI profile) {
        File configDescriptionDirectory = new File(getProfileRoot().getPath() + File.separator + profile.getID() + File.separator
                + LCConstant.CONFIGURATION_DIRECTORY + File.separator + description.getConfigurationId() + File.separator);
        return new ConfigurationDescriptionSavingTask(configDescriptionDirectory, description);
    }

    public static ConfigurationDescriptionSavingTask createSaveConfigDescriptionTask(final LCConfigurationDescriptionI description, File configurationDirectory) {
        return new ConfigurationDescriptionSavingTask(configurationDirectory, description);
    }
    //========================================================================

    // KEYLIST
    //========================================================================
    public static KeyListSavingTask createSaveKeyListTask(LCConfigurationI configuration, File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_KEYLIST_DIRECTORY);
        return new KeyListSavingTask(keyListDir, configuration.rootKeyListNodeProperty().get());
    }

    public static KeyListLoadingTask createLoadKeyListTask(File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_KEYLIST_DIRECTORY);
        return new KeyListLoadingTask(keyListDir);
    }

    public static KeyListImportTask createImportKeyListTask(List<File> keylistFiles) {
        return new KeyListImportTask(keylistFiles);
    }

    public static KeyListExportTask createExportKeyListTask(File keylistFile, final List<KeyListNodeI> keyListNodes) {
        return new KeyListExportTask(keylistFile, keyListNodes);
    }
    //========================================================================

    // SEQUENCES
    //========================================================================
    public static UserActionSequenceSavingTask createSaveSequenceTask(LCConfigurationI configuration, File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_SEQUENCE_DIRECTORY);
        return new UserActionSequenceSavingTask(keyListDir, configuration.userActionSequencesProperty().get());
    }

    public static UserActionSequenceLoadingTask createLoadSequenceTask(File configurationDirectory) {
        File keyListDir = new File(configurationDirectory + File.separator + LCConstant.CONFIGURATION_SEQUENCE_DIRECTORY);
        return new UserActionSequenceLoadingTask(keyListDir);
    }
    //========================================================================

    // Class part : "Import/export profiles"
    //========================================================================
    public static ProfileExportTask createProfileExportTask(final LCProfileI profile, final File exportFile) {
        File profileDirectory = new File(getProfileDirectoryPath(profile.getID()));
        return new ProfileExportTask(profile, profileDirectory, exportFile);
    }

    public static ProfileBackupAndThenTask createProfileBackupTask(final LCProfileI profile, final File exportFile, Runnable postBackupAction) {
        File profileDirectory = new File(getProfileDirectoryPath(profile.getID()));
        return new ProfileBackupAndThenTask(profile, profileDirectory, exportFile, postBackupAction);
    }
    //========================================================================

    // Class part : "User comp."
    //========================================================================
    public static UserCompSavingTask createUserCompSavingTask(final UserCompDescriptionI userComp, final LCProfileI profile) {
        return new UserCompSavingTask(getUserCompPath(profile.getID(), userComp.getSavedComponentId()), userComp);
    }

    public static MultiUserCompDescriptionLoadingTask createMultiUserCompDescriptionLoadingTask(final LCProfileI profile) {
        return new MultiUserCompDescriptionLoadingTask(new File(getUserCompDirectory(profile.getID())));
    }

    public static UserCompLoadingTask createUserCompLoadingTask(final UserCompDescriptionI userComp, final LCProfileI profile) {
        return new UserCompLoadingTask(getUserCompPath(profile.getID(), userComp.getSavedComponentId()), userComp);
    }
    //========================================================================

    // Class part : "Import/export configuration"
    //========================================================================
    public static ConfigurationExportTask createConfigurationExportTask(final LCConfigurationDescriptionI configurationDescription, final LCProfileI profile, final File exportFile) {
        File configurationDirectory = new File(getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
        return new ConfigurationExportTask(configurationDescription, configurationDirectory, exportFile);
    }

    public static ConfigurationBackupAndThenTask createConfigurationBackupTask(final LCConfigurationDescriptionI configurationDescription,
                                                                               final LCProfileI profile,
                                                                               final File exportFile,
                                                                               Runnable postBackupAction) {
        File configurationDirectory = new File(getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
        return new ConfigurationBackupAndThenTask(configurationDescription, configurationDirectory, exportFile, postBackupAction);
    }

    public static ConfigurationDuplicateTask createConfigurationDuplicateTaskFromCurrentProfile(LCConfigurationDescriptionI configurationDescription, LCProfileI profile) {
        return getConfigurationDuplicateTask(configurationDescription, profile, ConfigurationDuplicateTask.DuplicateMode.IN_PROFILE);
    }

    public static ConfigurationDuplicateTask createConfigurationDuplicateTaskFromCurrentProfileChangeIdOnly(LCConfigurationDescriptionI configurationDescription, LCProfileI profile) {
        return getConfigurationDuplicateTask(configurationDescription, profile, ConfigurationDuplicateTask.DuplicateMode.CHANGE_ID_ONLY);
    }

    private static ConfigurationDuplicateTask getConfigurationDuplicateTask(LCConfigurationDescriptionI configurationDescription, LCProfileI profile, ConfigurationDuplicateTask.DuplicateMode mode) {
        String newConfigId = StringUtils.getNewID();
        File destDirectory = new File(getConfigurationDirectoryPath(profile.getID(), newConfigId));
        File currentConfigDirectory = new File(getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
        return new ConfigurationDuplicateTask(configurationDescription, newConfigId, destDirectory, currentConfigDirectory, mode);
    }

    public static ConfigurationDuplicateTask createConfigurationDuplicateTaskFromDefaultConfigurationDir(LCConfigurationDescriptionI configurationDescription, File configPath, LCProfileI profile) {
        String newConfigId = StringUtils.getNewID();
        File destDirectory = new File(getConfigurationDirectoryPath(profile.getID(), newConfigId));
        return new ConfigurationDuplicateTask(configurationDescription, newConfigId, destDirectory, configPath, ConfigurationDuplicateTask.DuplicateMode.FROM_DEFAULT);
    }

    public static ConfigurationImportTask createConfigurationImport(final LCProfileI profil, final File configFile) throws LCException {
        String configurationID = getFileID(configFile);
        File configurationDirectory = new File(getConfigurationDirectoryPath(profil.getID(), configurationID));
        final LCConfigurationDescriptionI currentDefaultConfiguration = profil.getCurrentDefaultConfiguration();
        return new ConfigurationImportTask(configurationDirectory, configFile, configurationID, false,
                currentDefaultConfiguration != null ? currentDefaultConfiguration.getConfigurationId() : null);
    }

    public static ConfigurationImportTask createCustomConfigurationImport(final File configurationImportRootDirectory, final File configFile, boolean loadConfiguration) throws LCException {
        String configurationID = getFileID(configFile);
        File configurationDirectory = new File(configurationImportRootDirectory.getPath() + File.separator + configurationID);
        return new ConfigurationImportTask(configurationDirectory, configFile, configurationID, loadConfiguration, null);
    }

    public static ProfileImportTask createProfileImportTask(final File profileFile) throws LCException {
        String profileId = getFileID(profileFile);
        File profileDirectory = new File(getProfileDirectoryPath(profileId));
        return new ProfileImportTask(profileFile, profileDirectory, profileId);
    }

    public static String getFileID(final File lcFile) throws LCException {
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
    public static void saveUseInformation(final LCConfigurationI configuration) {
        File savingDirectory = getDirectoryForUseInformation(configuration);
        UseInformationSavingTask useInfoSavingTask = new UseInformationSavingTask(savingDirectory, configuration);
        try {
            ThreadUtils.executeInCurrentThread(useInfoSavingTask);
        } catch (Exception e) {
            LOGGER.warn("Couldn't save the configuration use information for configuration {}", configuration.getID(), e);
        }
    }

    public static void loadUseInformation(final LCConfigurationI configuration) {
        File loadingDirectory = getDirectoryForUseInformation(configuration);
        UseInformationLoadingTask useInfoLoadingTask = new UseInformationLoadingTask(loadingDirectory, configuration);
        try {
            ThreadUtils.executeInCurrentThread(useInfoLoadingTask);
        } catch (Exception e) {
            LOGGER.warn("Couldn't load the configuration use information for configuration {}", configuration.getID(), e);
        }
    }

    private static File getDirectoryForUseInformation(final LCConfigurationI configuration) {
        LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
        File directory;
        if (currentProfile != null) {
            directory = getConfigurationPath(currentProfile.getID(), configuration.getID());
        } else {
            directory = new File(InstallationConfigurationController.INSTANCE.getUserDirectory()
                    .getPath() + File.separator + LCConstant.CONFIGURATION_USE_INFO_DEFAULT_DIRECTORY + File.separator + configuration.getID());
            LOGGER.warn("There is no current profile, so use information will be saved/loaded into the default location : {}",
                    directory);
        }
        return directory;
    }
    //========================================================================

    public static File getFirstConfigurationFile(Collection<String> args) {
        return getFirstValidConfigurationOrProfile(args, LCConstant.CONFIG_FILE_EXTENSION);
    }

    public static File getFirstProfileFile(Collection<String> args) {
        return getFirstValidConfigurationOrProfile(args, LCConstant.PROFILE_FILE_EXTENSION);
    }

    public static File getFirstValidConfigurationOrProfile(Collection<String> args, String extensionToSearch) {
        if (!CollectionUtils.isEmpty(args) && !args.contains(LCConstant.ARG_IMPORT_LAUNCH_CONFIG)) {
            for (String arg : args) {
                String ext = FileNameUtils.getExtension(arg);
                if (extensionToSearch.equalsIgnoreCase(ext)) {
                    try {
                        File path = new File(arg);
                        IOHelper.getFileID(path);// to check if the file is an profile or configuration
                        return path;
                    } catch (LCException e) {
                        //Will return null
                    }
                }
            }
        }
        return null;
    }
}
