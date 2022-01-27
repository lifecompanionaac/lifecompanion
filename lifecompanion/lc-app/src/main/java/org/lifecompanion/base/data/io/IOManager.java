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
package org.lifecompanion.base.data.io;

import org.jdom2.Element;
import org.lifecompanion.api.component.definition.ConfigurationChildComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.api.component.definition.usercomp.UserCompDescriptionI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.InstallationConfigurationController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.base.data.io.task.*;
import org.lifecompanion.base.data.plugins.PluginInfo;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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

    /**
     * Contains a map that convert type from loaded element (e.g. <strong>nodeType</strong> attribute)
     * to real Java types.<br>
     * This method is new, so the previously saved type are converted with {@link LCBackwardCompatibility}.<br>
     * This map is meant to be initialized just once on startup.
     */
    private static Map<String, Pair<Class<? extends XMLSerializable>, PluginInfo>> typeAlias;

    /**
     * Boolean that becomes true once default {@link XMLSerializable} from default modules are discovered (typeAlias will be filled with them)
     */
    private static final AtomicBoolean defaultTypeInitialized = new AtomicBoolean(false);


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

    public ProfileFullLoadingTask createLoadFullProfileTask(final LCProfileI profileDescription) {
        return new ProfileFullLoadingTask(new File(getProfileRoot().getPath() + File.separator + profileDescription.getID() + File.separator), profileDescription);
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

    // Class part : "Use informations"
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
    //========================================================================

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

    // TYPES
    //========================================================================
    public static final String ATB_TYPE = "nodeType";
    public static final String ATB_PLUGIN_ID = "dependencyPluginId";

    /**
     * Create the base serialize object from a xml serialized component.<br>
     * This will not call {@link XMLSerializable#deserialize(Element, Object)} on the create object.
     *
     * @param element the element that contains the object serialized
     * @return the created component
     * @throws LCException if the component can't be created
     */
    @SuppressWarnings("unchecked")
    public static Pair<Boolean, XMLSerializable<IOContextI>> create(final Element element, IOContextI ioContext, Supplier<XMLSerializable<IOContextI>> fallbackSupplier) throws LCException {
        String className = element.getAttributeValue(ATB_TYPE);
        try {
            // Check if the plugin dependency is loaded, if not use fallback when enable
            String pluginDependencyId = element.getAttributeValue(ATB_PLUGIN_ID);
            if (pluginDependencyId != null && !PluginManager.INSTANCE.isPluginLoaded(pluginDependencyId)) {
                if (ioContext.isFallbackOnDefaultInstanceOnFail()) {
                    return Pair.of(true, fallbackSupplier != null ? fallbackSupplier.get() : null);
                } else {
                    throw LCException.newException().withMessage("error.io.manager.xml.element.read", element.getName(), element.getAttributes()).build();
                }
            }
            // Normal situation, no plugin or plugin is loaded
            else {
                Class<XMLSerializable<IOContextI>> loadedClass = getClassForName(className);
                XMLSerializable<IOContextI> createdObject = loadedClass.getDeclaredConstructor().newInstance();
                return Pair.of(false, createdObject);
            }
        } catch (Throwable t) {
            // Unknown error on loading
            IOManager.LOGGER.warn("Problem while creating the object from a serialized object in XML", t);
            if (ioContext.isFallbackOnDefaultInstanceOnFail()) {
                return Pair.of(true, fallbackSupplier != null ? fallbackSupplier.get() : null);
            } else {
                throw LCException.newException().withMessage("error.io.manager.xml.element.read", element.getName(), element.getAttributes()).withCause(t).build();
            }
        }
    }


    /**
     * Set the base element on a XML serializable object to be deserialized
     *
     * @param caller the calling class
     * @param node   the node where we must put the base
     */
    public static Element addTypeAlias(final XMLSerializable<?> caller, final Element node, IOContextI ioContext) {
        node.setAttribute(ATB_TYPE, caller.getClass().getSimpleName());
        Pair<Class<? extends XMLSerializable>, PluginInfo> pluginInfoForType = getTypeAlias().get(caller.getClass().getSimpleName());
        // When the saved element is from a plugin : "flag" the XML element to be dependent on the plugin and add the plugin id to dependencies list
        if (pluginInfoForType != null && pluginInfoForType.getRight() != null) {
            node.setAttribute(ATB_PLUGIN_ID, pluginInfoForType.getRight().getPluginId());
            ioContext.getAutomaticPluginDependencyIds().add(pluginInfoForType.getRight().getPluginId());
        }
        return node;
    }

    public static <T> Class<T> getClassForName(final String className) throws ClassNotFoundException {
        if (getTypeAlias().containsKey(className)) {
            return (Class<T>) getTypeAlias().get(className).getLeft();
        } else {
            // Backward compatibility : type were directly written in XML
            return (Class<T>) Class.forName(LCBackwardCompatibility.getBackwardCompatibleType(className));
        }
    }

    private static void initializeTypeMap() {
        if (!defaultTypeInitialized.getAndSet(true)) {
            addSerializableTypes(ReflectionHelper.findImplementationsInModules(XMLSerializable.class), null);
        }
    }

    private static Map<String, Pair<Class<? extends XMLSerializable>, PluginInfo>> getTypeAlias() {
        initializeTypeMap();
        return typeAlias;
    }

    public static void addSerializableTypes(List<Class<? extends XMLSerializable>> types, PluginInfo pluginInfo) {
        if (typeAlias == null) {
            typeAlias = new HashMap<>(150);
        }
        for (Class<? extends XMLSerializable> type : types) {
            String typeName = type.getSimpleName();
            Pair<Class<? extends XMLSerializable>, PluginInfo> previous = typeAlias.put(typeName, Pair.of(type, pluginInfo));
            if (previous != null) {
                LOGGER.error("Found two types with the same name : {} / {} and {}", typeName, previous.getLeft().getName(), type.getName());
            }
        }
    }
    //========================================================================

    // STYLE
    //========================================================================
    // FIXME : change method names
    public static <T extends ConfigurationChildComponentI> void serializeComponentDependencies(final IOContextI context, final T element, final Element node) {
        PluginManager.INSTANCE.serializePluginInformation(element, context, node);
    }

    public static <T extends ConfigurationChildComponentI> void deserializeComponentDependencies(final IOContextI context, final T element, final Element node) throws LCException {
        //Plugins
        PluginManager.INSTANCE.deserializePluginInformation(element, context, node);
    }
    //========================================================================
}
