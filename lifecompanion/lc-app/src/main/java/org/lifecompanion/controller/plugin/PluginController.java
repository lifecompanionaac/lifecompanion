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
package org.lifecompanion.controller.plugin;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.ConfigurationChildComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.textprediction.CharPredictorI;
import org.lifecompanion.model.api.textprediction.WordPredictorI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerI;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.model.impl.plugin.PluginInfoState;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that load the plugin classes, and that instantiate found plugins.</br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum PluginController implements LCStateListener, ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginController.class);

    /**
     * Plugin infos list
     */
    private final ObservableList<PluginInfo> pluginInfoList;

    /**
     * Contains loaded plugin instance
     */
    private final Map<String, PluginI> loadedPlugins;

    PluginController() {
        this.loadedPlugins = new HashMap<>();
        this.pluginInfoList = FXCollections.observableArrayList();
    }

    public ObservableList<PluginInfo> getPluginInfoList() {
        return pluginInfoList;
    }

    public InputStream getResourceFromPlugin(String resourcePath) {
        for (PluginI plugin : loadedPlugins.values()) {
            InputStream is = plugin.getClass().getResourceAsStream(resourcePath);
            if (is != null) return is;
        }
        return null;
    }

    public boolean isPluginLoaded(String pluginDependencyId) {
        return loadedPlugins.containsKey(pluginDependencyId);
    }

    public List<Pair<String, Function<UseVariableDefinitionI, UseVariableI<?>>>> generatePluginUseVariable() {
        // TODO : should be cached in use mode ?
        List<Pair<String, Function<UseVariableDefinitionI, UseVariableI<?>>>> vars = new ArrayList<>();
        for (PluginI plugin : this.loadedPlugins.values()) {
            try {
                List<UseVariableDefinitionI> defVar = plugin.getDefinedVariables();
                if (!CollectionUtils.isEmpty(defVar)) {
                    Map<String, UseVariableDefinitionI> varForPlugin = defVar.stream()
                            .collect(Collectors.toMap(UseVariableDefinitionI::getId, v -> v));
                    for (Map.Entry<String, UseVariableDefinitionI> idAndDef : varForPlugin.entrySet()) {
                        vars.add(Pair.of(idAndDef.getKey(), plugin.getSupplierForUseVariable(idAndDef.getKey())));
                    }
                }
            } catch (Throwable t) {
                PluginController.LOGGER.warn("Couldn't generate plugin use variable for plugin {}", plugin.getClass(), t);
            }
        }
        return vars;
    }

    @SuppressWarnings("deprecation")
    public Map<String, UseVariableI<?>> generatePluginsUseVariableBackwardCompatibility() {
        Map<String, UseVariableI<?>> vars = new HashMap<>();
        // For each plugin, generate the variables
        for (PluginI plugin : this.loadedPlugins.values()) {
            try {
                List<UseVariableDefinitionI> defVar = plugin.getDefinedVariables();
                if (!CollectionUtils.isEmpty(defVar)) {
                    Map<String, UseVariableDefinitionI> varForPlugin = defVar.stream()
                            .collect(Collectors.toMap(UseVariableDefinitionI::getId, v -> v));
                    Map<String, UseVariableI<?>> generateVariables = plugin.generateVariables(varForPlugin);
                    if (generateVariables != null) {
                        vars.putAll(generateVariables);// Plugin can override variables
                    }
                }
            } catch (Throwable t) {
                PluginController.LOGGER.warn("Couldn't generate plugin use variable for plugin {}", plugin.getClass(), t);
            }
        }
        return vars;
    }

    public List<Pair<String, InputStream>> getDefaultConfigurationsFor(String pluginId) {
        PluginI pluginInstance = this.loadedPlugins.get(pluginId);
        String[] defaultConfigurationPaths = pluginInstance.getDefaultConfigurations(UserConfigurationController.INSTANCE.userLanguageProperty().get());
        List<Pair<String, InputStream>> defaultConfigurations = new ArrayList<>();
        if (defaultConfigurationPaths != null) {
            for (String defaultConfigurationPath : defaultConfigurationPaths) {
                InputStream resourceAsStream = pluginInstance.getClass().getResourceAsStream(defaultConfigurationPath);
                if (resourceAsStream != null) {
                    defaultConfigurations.add(Pair.of(defaultConfigurationPath, resourceAsStream));
                } else {
                    LOGGER.warn("Given default configuration {} from plugin {} is invalid, check the given path", defaultConfigurationPath, pluginId);
                }
            }
        }
        return defaultConfigurations;
    }


    // PLUGIN LOADING
    //========================================================================
    private final PluginImplementationLoadingHandler<Class<? extends BaseUseActionI>> useActions = new PluginImplementationLoadingHandler<>(BaseUseActionI.class, this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends UseActionConfigurationViewI>> useActionConfigViews = new PluginImplementationLoadingHandler<>(UseActionConfigurationViewI.class,
            this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends CharPredictorI>> charPredictors = new PluginImplementationLoadingHandler<>(CharPredictorI.class, this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends WordPredictorI>> wordPredictors = new PluginImplementationLoadingHandler<>(WordPredictorI.class, this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends VoiceSynthesizerI>> voiceSynthesizers = new PluginImplementationLoadingHandler<>(VoiceSynthesizerI.class, this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends UseEventGeneratorI>> useEventGenerators = new PluginImplementationLoadingHandler<>(UseEventGeneratorI.class,
            this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends UseEventGeneratorConfigurationViewI>> useEventGeneratorConfigViews = new PluginImplementationLoadingHandler<>(
            UseEventGeneratorConfigurationViewI.class,
            this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends KeyOptionI>> keyOptions = new PluginImplementationLoadingHandler<>(KeyOptionI.class, this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends KeyOptionConfigurationViewI>> keyOptionConfigViews = new PluginImplementationLoadingHandler<>(KeyOptionConfigurationViewI.class,
            this::handlePluginError);
    private final PluginImplementationLoadingHandler<Class<? extends GeneralConfigurationStepViewI>> generalConfigurationSteps = new PluginImplementationLoadingHandler<>(GeneralConfigurationStepViewI.class,
            this::handlePluginError);
    private final PluginImplementationLoadingHandler<UseVariableDefinitionI> useVariableDefinitions = new PluginImplementationLoadingHandler<>(null, this::handlePluginError);
    private final PluginImplementationLoadingHandler<String[]> stylesheets = new PluginImplementationLoadingHandler<>(null, this::handlePluginError);

    public PluginImplementationLoadingHandler<Class<? extends BaseUseActionI>> getUseActions() {
        return useActions;
    }

    public PluginImplementationLoadingHandler<Class<? extends UseActionConfigurationViewI>> getUseActionConfigViews() {
        return useActionConfigViews;
    }

    public PluginImplementationLoadingHandler<Class<? extends CharPredictorI>> getCharPredictors() {
        return charPredictors;
    }

    public PluginImplementationLoadingHandler<Class<? extends WordPredictorI>> getWordPredictors() {
        return wordPredictors;
    }

    public PluginImplementationLoadingHandler<Class<? extends VoiceSynthesizerI>> getVoiceSynthesizers() {
        return voiceSynthesizers;
    }

    public PluginImplementationLoadingHandler<Class<? extends UseEventGeneratorI>> getUseEventGenerators() {
        return useEventGenerators;
    }

    public PluginImplementationLoadingHandler<Class<? extends UseEventGeneratorConfigurationViewI>> getUseEventGeneratorConfigViews() {
        return useEventGeneratorConfigViews;
    }

    public PluginImplementationLoadingHandler<Class<? extends KeyOptionI>> getKeyOptions() {
        return keyOptions;
    }

    public PluginImplementationLoadingHandler<Class<? extends KeyOptionConfigurationViewI>> getKeyOptionConfigViews() {
        return keyOptionConfigViews;
    }

    public PluginImplementationLoadingHandler<UseVariableDefinitionI> getUseVariableDefinitions() {
        return useVariableDefinitions;
    }

    public PluginImplementationLoadingHandler<String[]> getStylesheets() {
        return stylesheets;
    }

    public PluginImplementationLoadingHandler<Class<? extends GeneralConfigurationStepViewI>> getGeneralConfigurationSteps() {
        return generalConfigurationSteps;
    }

    private final List<PluginImplementationLoadingHandler<? extends Class<?>>> pluginImplementationLoadingHandlers = Arrays.asList(
            useActions, useActionConfigViews, charPredictors, wordPredictors, voiceSynthesizers, useEventGenerators,
            useEventGeneratorConfigViews, keyOptions, keyOptionConfigViews, generalConfigurationSteps
    );

    private List<PluginInfo> getPluginById(String id) {
        return getPluginById(id, p -> true);
    }

    private List<PluginInfo> getPluginById(String id, Predicate<PluginInfo> pluginInfoPredicate) {
        return pluginInfoList.stream()
                .filter(p -> StringUtils.isEquals(id, p.getPluginId()))
                .filter(pluginInfoPredicate)
                .collect(Collectors.toList());
    }

    private PluginInfo loadPluginInfo(File pluginJar) {
        try {
            return PluginInfo.createFromJarManifest(pluginJar);
        } catch (Exception e) {
            LOGGER.error("Couldn't load plugin info from file {}", pluginJar, e);
            return null;
        }
    }

    private void loadPlugin(PluginInfo pluginInfo, File pluginJar) {
        if (pluginInfo != null) {
            LOGGER.info("Will try to load plugin {}", pluginInfo);
            try {
                // Try to load plugin class
                Class<? extends PluginI> pluginClassType = (Class<? extends PluginI>) Class.forName(pluginInfo.getPluginClass());
                Constructor<? extends PluginI> constructor = pluginClassType.getConstructor();
                if (constructor == null) {
                    throw new Exception("Couldn't find default public constructor on plugin type " + pluginInfo.getPluginClass() + ", you should provide a public no arg constructor");
                }

                PluginI pluginInstance = constructor.newInstance();
                startPlugin(pluginInfo, pluginInstance);

                // Add to plugin list
                loadedPlugins.put(pluginInfo.getPluginId(), pluginInstance);

                // Will now look for implementations
                try (ScanResult scanResult = new ClassGraph()
                        .whitelistJars(pluginJar.getName())
                        .whitelistPackages(pluginInfo.getPluginPackageScanningBase().split(","))
                        .enableClassInfo()
                        .scan()
                ) {
                    // Register every serializable types to IOManager
                    List<Class<? extends XMLSerializable>> serializableClassesInPlugin = getClassesInPlugin(scanResult, XMLSerializable.class);
                    ConfigurationComponentIOHelper.addSerializableTypes(serializableClassesInPlugin, pluginInfo);

                    // Find and register plugin custom implementations
                    for (PluginImplementationLoadingHandler pluginImplementationLoadingHandler : pluginImplementationLoadingHandlers) {
                        scanForImplementationInPlugin(pluginInfo, scanResult, pluginImplementationLoadingHandler);
                    }
                }
                pluginInfo.stateProperty().set(PluginInfoState.LOADED);
            } catch (Throwable t) {
                LOGGER.error("Failed to load plugin : {}", pluginInfo, t);
                pluginInfo.stateProperty().set(PluginInfoState.ERROR);
            } finally {
                pluginInfoList.add(pluginInfo);
            }
        }
    }

    private <T extends Class<T>> void scanForImplementationInPlugin(PluginInfo pluginInfo, ScanResult scanResult, PluginImplementationLoadingHandler<T> pluginImplementationLoadingHandler) {
        List<Class<? extends T>> classes = getClassesInPlugin(scanResult, pluginImplementationLoadingHandler.getType());
        pluginImplementationLoadingHandler.elementAdded(pluginInfo.getPluginId(), (Collection<T>) classes);
        LOGGER.info("Found {} {} implementations in plugin {}", classes.size(), pluginImplementationLoadingHandler.getType().getName(), pluginInfo.getPluginId());
    }

    private <T> List<Class<? extends T>> getClassesInPlugin(ScanResult scanResult, Class<T> type) {
        ClassInfoList actionImplementation = scanResult.getClassesImplementing(type.getName()).filter(classInfo -> !classInfo.isAbstract() && !classInfo.isInterface());
        return actionImplementation.loadClasses().stream().map(c -> (Class<? extends T>) c).collect(Collectors.toList());
    }

    private void startPlugin(PluginInfo pluginInfo, PluginI plugin) {
        plugin.start(getPluginDataFolder(pluginInfo.getPluginId()));
        // Load language
        String[] languageFiles = plugin.getLanguageFiles(UserConfigurationController.INSTANCE.userLanguageProperty().get());
        if (languageFiles != null) {
            for (String languageFilePath : languageFiles) {
                try (InputStream fis = plugin.getClass().getResourceAsStream(languageFilePath)) {
                    Translation.INSTANCE.load(languageFilePath, fis);
                    PluginController.LOGGER.info("Plugin language file {} loaded for {}", languageFilePath, pluginInfo.getPluginName());
                } catch (Exception e) {
                    PluginController.LOGGER.error("Couldn't load the {} plugin language file {}", pluginInfo.getPluginId(), languageFilePath, e);
                }
            }
        }

        // Load JavaFX stylesheets
        String[] javaFXStylesheets = plugin.getJavaFXStylesheets();
        if (javaFXStylesheets != null) {
            stylesheets.elementAdded(pluginInfo.getPluginId(), Collections.singleton(javaFXStylesheets));
        }

        // Load use variable definitions
        List<UseVariableDefinitionI> definedVariables = plugin.getDefinedVariables();
        if (LangUtils.isNotEmpty(definedVariables)) {
            LOGGER.info("Found {} use variable definition in plugin {}", definedVariables.size(), pluginInfo.getPluginId());
            useVariableDefinitions.elementAdded(pluginInfo.getPluginId(), definedVariables);
        }
    }

    private File getPluginDataFolder(String pluginId) {
        File pluginDataFolder = new File(LCConstant.PATH_PLUGIN_DATA_DIR + File.separator + pluginId + File.separator);
        pluginDataFolder.mkdirs();
        return pluginDataFolder;
    }

    private void handlePluginError(String pluginId, Throwable throwable) {
        LOGGER.error("Detected error for plugin \"{}\", it will be deleted", pluginId, throwable);
        List<PluginInfo> pluginInfo = getPluginById(pluginId);
        if (!CollectionUtils.isEmpty(pluginInfo)) {
            FXThreadUtils.runOnFXThread(() -> {
                for (PluginInfo info : pluginInfo) {
                    removePlugin(info);
                    info.stateProperty().set(PluginInfoState.ERROR);
                }
            });
            LCNotificationController.INSTANCE.showNotification(LCNotification.createError(Translation.getText("plugin.error.startup.notification.will.be.deleted", pluginId),
                    "plugin.error.startup.notification.will.be.deleted.button",
                    () -> InstallationController.INSTANCE.restart("")));
        }
    }
    //========================================================================

    // PLUGIN ADD/REMOVE
    //========================================================================
    public enum PluginAddResult {
        NOT_ADDED_ALREADY_SAME_OR_NEWER,
        ADDED_TO_NEXT_RESTART;
    }

    public Pair<PluginAddResult, PluginInfo> tryToAddPluginFrom(File pluginFile) throws Exception {
        PluginInfo addedPluginInfo = loadPluginInfo(pluginFile);
        if (addedPluginInfo == null)
            LCException.newException().withMessageId("plugin.error.load.info.from.jar").buildAndThrow();

        // Check min app version
        // Note : no plugin minAppVersion means old plugin so it is not compatible with new plugin API
        if (addedPluginInfo.getPluginMinAppVersion() == null || VersionUtils.compare(InstallationController.INSTANCE.getBuildProperties().getVersionLabel(),
                addedPluginInfo.getPluginMinAppVersion()) < 0) {
            LCException.newException()
                    .withMessage(addedPluginInfo.getPluginMinAppVersion() != null ? "plugin.error.min.app.version.with.number" : "plugin.error.min.app.version.without.number",
                            addedPluginInfo.getPluginMinAppVersion())
                    .buildAndThrow();
        }

        // TODO : handle removed then added plugin

        // If the plugin is already in app with the same or > version
        List<PluginInfo> pluginsWithSameId = getPluginById(addedPluginInfo.getPluginId(), pluginInfo -> pluginInfo.stateProperty().get() != PluginInfoState.REMOVED);
        boolean newerVersionFound = false;
        if (!CollectionUtils.isEmpty(pluginsWithSameId)) {
            for (PluginInfo pluginWithSameId : pluginsWithSameId) {
                // Found a newer version loaded (or same)
                if (VersionUtils.compare(pluginWithSameId.getPluginVersion(), addedPluginInfo.getPluginVersion()) >= 0) {
                    newerVersionFound = true;
                }
                // Found an older version loaded : remove previous version and add new one
                else {
                    removePlugin(pluginWithSameId);
                }
            }
        }
        if (newerVersionFound) {
            return Pair.of(PluginAddResult.NOT_ADDED_ALREADY_SAME_OR_NEWER, addedPluginInfo);
        }

        // Add to plugin jar directory (if the same file is not already there)
        File destinationPluginFile = new File(LCConstant.PATH_PLUGIN_JAR_DIR + addedPluginInfo.getFileName());
        IOUtils.createParentDirectoryIfNeeded(destinationPluginFile);
        if (!destinationPluginFile.exists()) {
            IOUtils.copyFiles(pluginFile, destinationPluginFile);
        } else {
            LOGGER.warn("Didn't copy the new plugin file {} because the file already exists in plugin directory", pluginFile);
        }

        // Modify and save classpath config
        Set<String> pluginCpConfig = readClasspathConfigurationOrGetDefault();
        pluginCpConfig.add(LCConstant.PATH_PLUGIN_JAR_DIR + addedPluginInfo.getFileName());
        writeClasspathConfiguration(pluginCpConfig);

        // Add to info list
        pluginInfoList.add(addedPluginInfo);

        return Pair.of(PluginAddResult.ADDED_TO_NEXT_RESTART, addedPluginInfo);
    }

    public void removePlugin(PluginInfo pluginInfo) {
        pluginInfo.stateProperty().set(PluginInfoState.REMOVED);
        Set<String> pluginCpConfig = readClasspathConfigurationOrGetDefault();
        boolean remove = pluginCpConfig.remove(LCConstant.PATH_PLUGIN_JAR_DIR + pluginInfo.getFileName());
        LOGGER.info("Remove plugin {} = {}", pluginInfo, remove);
        writeClasspathConfiguration(pluginCpConfig);
    }

    private Set<String> readClasspathConfigurationOrGetDefault() {
        Set<String> pluginInCp = new HashSet<>();
        File cpConfigFile = new File(LCConstant.PATH_PLUGIN_CP_FILE);
        if (cpConfigFile.exists()) {
            try (Scanner scan = new Scanner(cpConfigFile, StandardCharsets.UTF_8)) {
                pluginInCp.addAll(Arrays.asList(scan.nextLine().split(File.pathSeparator)));
            } catch (Exception e) {
                LOGGER.error("Couldn't read the classpath configuration for plugins", e);
            }
        }
        return pluginInCp;
    }

    public static void writeClasspathConfiguration(Set<String> pluginsInCp) {
        File cpConfigFile = new File(LCConstant.PATH_PLUGIN_CP_FILE);
        IOUtils.createParentDirectoryIfNeeded(cpConfigFile);
        if (CollectionUtils.isEmpty(pluginsInCp)) {
            cpConfigFile.delete();
        } else {
            try (PrintWriter pw = new PrintWriter(cpConfigFile, StandardCharsets.UTF_8)) {
                pw.println(pluginsInCp.stream().collect(Collectors.joining(File.pathSeparator)));
            } catch (Exception e) {
                LOGGER.error("Couldn't write the classpath configuration for plugins", e);
            }
        }
    }
    //========================================================================

    // Class part : "Init/stop"
    // ========================================================================
    @Override
    public void lcStart() {
        if (!InstallationController.INSTANCE.isUpdateDownloadFinished()) {
            try {
                // Detect plugin in classpath configuration and try to load them
                Set<String> classPathPlugins = readClasspathConfigurationOrGetDefault();
                for (String classPathPlugin : classPathPlugins) {
                    LOGGER.info("Will try to load plugin from classpath configuration : {}", classPathPlugin);
                    File pluginJarFile = new File(classPathPlugin);
                    loadPlugin(loadPluginInfo(pluginJarFile), pluginJarFile);
                }

                // This part is for dev only
                if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_LOAD_PLUGIN_FROM_CP)) {
                    List<File> jarFiles = new ClassGraph().getClasspathFiles();
                    for (File jarFile : jarFiles) {
                        if (jarFile.getName().contains("plugin")) {
                            PluginInfo pluginInfo = loadPluginInfo(jarFile);
                            if (pluginInfo != null && !loadedPlugins.containsKey(pluginInfo.getPluginId())) {
                                loadPlugin(pluginInfo, jarFile);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Global problem when trying to load plugins...", t);
            }

            // Launch plugin update check (once plugin are loaded)
            InstallationController.INSTANCE.launchPluginUpdateCheckTask(false);
        }
    }

    @Override
    public void lcExit() {
        for (Map.Entry<String, PluginI> pluginE : this.loadedPlugins.entrySet()) {
            pluginE.getValue().stop(getPluginDataFolder(pluginE.getKey()));
        }
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        for (PluginI plugin : this.loadedPlugins.values()) {
            try {
                plugin.modeStart(configuration);
            } catch (Throwable t) {
                LOGGER.error("Fire modeStart(...) on plugin {} failed", plugin.getClass(), t);
            }
        }
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        for (PluginI plugin : this.loadedPlugins.values()) {
            try {
                plugin.modeStop(configuration);
            } catch (Throwable t) {
                LOGGER.error("Fire modeStop(...) on plugin {} failed", plugin.getClass(), t);
            }
        }
    }
    // ========================================================================


    // PLUGIN IO
    // ========================================================================
    private static final String NODE_PLUGINS = "Plugins";
    private static final String NODE_PLUGIN_DEPENDENCIES = "PluginDependencies";
    private static final String NODE_PLUGIN_CUSTOM_INFORMATIONS = "PluginCustomInformations";
    private static final String NODE_PLUGIN_CUSTOM_INFORMATION = "PluginCustomInformation";
    private static final String ATB_PLUGIN_ID = "pluginId";

    public void serializePluginInformation(final ConfigurationChildComponentI pluginUser, final IOContextI context, final Element configurationElement) {
        // Detected used plugin (auto and manual)
        HashSet<String> usedPluginIds = new HashSet<>();
        usedPluginIds.addAll(context.getAutomaticPluginDependencyIds());
        LCConfigurationI parentConfiguration = pluginUser.configurationParentProperty().get();
        if (parentConfiguration == null) {
            LOGGER.warn("Serialized plugin user haven't any configuration parent : {}", pluginUser);
        } else {
            usedPluginIds.addAll(parentConfiguration.getManualPluginDependencyIds());
        }

        // Save dependencies
        Element pluginDependenciesElement = new Element(PluginController.NODE_PLUGIN_DEPENDENCIES);
        usedPluginIds.stream()
                .flatMap(id -> getPluginById(id, pi -> pi.stateProperty().get() == PluginInfoState.LOADED).stream())
                .map(pi -> pi.serialize(context))
                .forEach(pluginDependenciesElement::addContent);

        // Save information
        Element pluginInformations = new Element(PluginController.NODE_PLUGIN_CUSTOM_INFORMATIONS);
        for (String pluginId : usedPluginIds) {

            PluginConfigPropertiesI configurationProperties = parentConfiguration != null ? parentConfiguration.getPluginConfigProperties(pluginId, PluginConfigPropertiesI.class) : null;
            if (configurationProperties != null) {
                Element pluginXmlNode = configurationProperties.serialize(context);
                if (pluginXmlNode != null) {
                    Element customInfo = new Element(NODE_PLUGIN_CUSTOM_INFORMATION);
                    customInfo.addContent(pluginXmlNode);
                    customInfo.setAttribute(ATB_PLUGIN_ID, pluginId);
                    pluginInformations.addContent(customInfo);
                }
            } else {
                LOGGER.warn("Didn't find any configuration properties for plugin {}", pluginId);
            }
        }

        Element elementPlugins = new Element(NODE_PLUGINS);
        configurationElement.addContent(elementPlugins);
        elementPlugins.addContent(pluginDependenciesElement);
        elementPlugins.addContent(pluginInformations);
    }

    public void deserializePluginInformation(final ConfigurationChildComponentI pluginUser, final IOContextI context, final Element configurationElement) throws LCException {
        if (pluginUser == null || pluginUser.configurationParentProperty().get() == null) {
            LOGGER.warn("Plugin user {} haven't any configuration parent, plugin information can't be loaded", pluginUser);
            return;
        }
        LCConfigurationI parentConfiguration = pluginUser.configurationParentProperty().get();
        Element pluginsElement = configurationElement.getChild(PluginController.NODE_PLUGINS);
        if (pluginsElement != null) {
            // Load each plugin custom information
            Element pluginInformations = pluginsElement.getChild(PluginController.NODE_PLUGIN_CUSTOM_INFORMATIONS);
            for (Element pluginInformation : pluginInformations.getChildren()) {
                String pluginId = pluginInformation.getAttributeValue(ATB_PLUGIN_ID);
                PluginI pluginI = this.loadedPlugins.get(pluginId);
                if (pluginI == null || pluginInformation.getChildren().size() < 1) {
                    LOGGER.warn("Couldn't load plugin information from XML because plugin {} is not loaded or there is no information", pluginId);
                } else {
                    final PluginConfigPropertiesI pluginConfigProperties = parentConfiguration.getPluginConfigProperties(pluginId, PluginConfigPropertiesI.class);
                    pluginConfigProperties.deserialize(pluginInformation.getChildren().get(0), context);
                }
            }
        }
    }

    // FIXME user dependencies
    public Pair<String, Set<String>> checkPluginDependencies(final Element xmlRoot) throws LCException {
        Element pluginsElement = xmlRoot.getChild(PluginController.NODE_PLUGINS);
        if (pluginsElement != null) {
            Set<String> ids = new HashSet<>();
            StringBuilder warningMessage = new StringBuilder();

            Element pluginDependenciesElement = pluginsElement.getChild(PluginController.NODE_PLUGIN_DEPENDENCIES);
            for (Element pluginDependencyElement : pluginDependenciesElement.getChildren()) {
                PluginInfo usedPluginInfo = new PluginInfo();
                usedPluginInfo.deserialize(pluginDependencyElement, null);
                PluginInfo loadedPluginInfo = getPluginById(usedPluginInfo.getPluginId(), pi -> pi.stateProperty().get() == PluginInfoState.LOADED).stream().findFirst().orElseGet(() -> null);
                if (loadedPluginInfo == null) {
                    warningMessage.append("\n - ").append(Translation.getText("configuration.loading.plugin.not.loaded", usedPluginInfo.getPluginName(), usedPluginInfo.getPluginVersion()));
                    ids.add(usedPluginInfo.getPluginId());
                } else if (VersionUtils.compare(loadedPluginInfo.getPluginVersion(), usedPluginInfo.getPluginVersion()) < 0) {
                    warningMessage.append("\n - ").append(Translation.getText("configuration.loading.plugin.older.version", loadedPluginInfo.getPluginName(), loadedPluginInfo.getPluginVersion(),
                            usedPluginInfo.getPluginVersion()));
                    ids.add(usedPluginInfo.getPluginId());
                }
            }
            return warningMessage.length() > 0 ? Pair.of(warningMessage.toString(), ids) : null;
        }
        return null;
    }

    public Map<String, PluginConfigPropertiesI> getPluginConfigurationPropertiesMap(ObjectProperty<LCConfigurationI> parentConfiguration) {
        Map<String, PluginConfigPropertiesI> pluginConfigurationPropertiesMap = new HashMap<>();
        pluginInfoList.stream()
                .filter(p -> p.stateProperty().get() == PluginInfoState.LOADED)
                .forEach(p -> pluginConfigurationPropertiesMap.put(p.getPluginId(), loadedPlugins.get(p.getPluginId()).newPluginConfigProperties(parentConfiguration)));
        return Collections.unmodifiableMap(pluginConfigurationPropertiesMap);
    }
    // ========================================================================


}
