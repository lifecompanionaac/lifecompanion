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
package org.lifecompanion.base.data.control;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.api.mode.LCModeI;
import org.lifecompanion.api.mode.LCStateListener;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.simple.LCConfigurationComponent;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.prediction.AutoCharPredictionController;
import org.lifecompanion.base.data.control.prediction.CustomCharPredictionController;
import org.lifecompanion.base.data.control.prediction.WordPredictionController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseController;
import org.lifecompanion.base.data.dev.LogEntry;
import org.lifecompanion.base.data.image2.ImageDictionaries;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The application main controller.<br>
 * This handles current configuration and mode, and also application stages.<br>
 * This is also this controller that control LifeCompanion life cycle (start, stop...)
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum AppController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    /**
     * Software main frame
     */
    private Stage mainFrame;

    /**
     * The current application running mode
     */
    private final ObjectProperty<AppMode> currentMode;

    /**
     * Map that keep every current application mode.<br>
     * Each mode have its own LCModeI to describe the view provider, the current configuration, etc...
     */
    private final Map<AppMode, LCModeI> appModes;

    /**
     * List of all state listener
     */
    private final List<LCStateListener> stateListeners;

    /**
     * Contains a list of callback to call once a mode is started
     */
    private final List<Consumer<LCModeI>> modeChangedCallback;

    /**
     * Callback called if configuration in config mode is not the same than the current configuration description
     */
    private Consumer<LCConfigurationDescriptionI> callbackIfDescriptionIsDifferent;

    private Runnable nextModeChangeAfterLoadingCallback;

    /**
     * The current profile in use, can be null
     */
    private final ObjectProperty<LCProfileI> currentProfile;

    /**
     * The current configuration description (can be null when configuration is not saved yet)
     */
    private final ObjectProperty<LCConfigurationDescriptionI> currentConfigDescription;

    /**
     * This property should be set on true if the current application that use this will only run in use mode.<br>
     * This could be the case on embedded devices.<br>
     * This is use to avoid useless loading.
     */
    private boolean useModeOnly = false;

    /**
     * Function to confirm that user can go to configuration mode
     */
    private BiFunction<Node, LCConfigurationI, Boolean> confirmConfigurationModeFunction;

    /**
     * List of all log entries (only enabled if dev mode is enabled)
     */
    private final ObservableList<LogEntry> logEntries;

    /**
     * To disable log (disable append to {@link #logEntries} and clear the entry list)
     */
    private boolean disableLog;

    /**
     * Configuration scaling
     */
    private final DoubleProperty configurationScale;

    /**
     * If LC is launched in dev mode
     */
    private final BooleanProperty devMode;

    /**
     * To keep a track that {@link #lcStart()} was ran
     */
    private boolean lcStartRan;

    AppController() {
        this.configurationScale = new SimpleDoubleProperty(this, "configurationScale", 1.0);
        this.currentMode = new SimpleObjectProperty<>(this, "currentMode");
        this.devMode = new SimpleBooleanProperty(this, "devMode", false);
        this.appModes = new HashMap<>();
        this.currentProfile = new SimpleObjectProperty<>(this, "currentProfile");
        this.currentConfigDescription = new SimpleObjectProperty<>(this, "currentConfigDescription");
        this.stateListeners = new ArrayList<>();
        this.modeChangedCallback = new ArrayList<>();
        this.logEntries = FXCollections.observableArrayList();
        this.initBinding();
        this.initCommonStateListener();
        this.setUseModeOnly(this.isOnEmbeddedDevice());
        this.LOGGER.info("Singleton {} initialized", this.getClass().getSimpleName());
    }

    private LCConfigurationComponent getEmptyConfiguration() {
        LCConfigurationComponent emptyConfiguration = new LCConfigurationComponent();
        emptyConfiguration.setId("empty-configuration");
        return emptyConfiguration;
    }

    /**
     * Init needed binding
     */
    private void initBinding() {
        //Mode
        this.currentMode.addListener((obs, previousMode, newMode) -> {
            LCConfigurationI configuration = null;
            this.LOGGER.info("Change the current mode from {} to {}", previousMode, newMode);
            //Clear previous configuration
            boolean copyNeeded = false;
            if (previousMode != null) {
                LCModeI previousAppMode = this.appModes.get(previousMode);
                configuration = previousAppMode.currentConfigurationProperty().get();
                previousAppMode.modeStop(configuration);
                previousAppMode.currentConfigurationProperty().set(getEmptyConfiguration());// FIXME : check if ok
                copyNeeded = previousMode.isCopyNeeded();

                configuration.dispatchDisplayedProperty(false);
                //Save configuration if needed
                if (previousMode.isRestoreConfigurationNeeded()) {
                    previousAppMode.configurationBeforeChangeProperty().set(configuration);
                } else {
                    configuration.dispatchRemovedPropertyValue(true);
                }
            }
            //Set the new
            if (newMode != null) {
                LCModeI newAppMode = this.appModes.get(newMode);
                LCConfigurationI startedConfiguration = null;
                //We need a copy of the previous configuration
                if (configuration != null && copyNeeded) {
                    this.LOGGER.debug("Change mode from {} to {} and copy the previous configuration to the new mode", previousMode, newMode);
                    startedConfiguration = (LCConfigurationI) configuration.duplicate(false);
                }
                //We need the raw previous configuration, but without restore
                else if (configuration != null && !newMode.isRestoreConfigurationNeeded()) {
                    this.LOGGER.debug("Change mode from {} to {} and set the raw previous configuration to the new mode", previousMode, newMode);
                    startedConfiguration = configuration;
                }
                //We need to restore previous configuration
                else if (newMode.isRestoreConfigurationNeeded() && newAppMode.configurationBeforeChangeProperty().get() != null) {
                    this.LOGGER.debug("Change mode from {} to {} and restore the configuration before change", previousMode, newMode);
                    startedConfiguration = newAppMode.configurationBeforeChangeProperty().get();
                    //Only set the use information from current configuration
                }
                //No match : create a new configuration
                else {
                    this.LOGGER.info("Change mode from {} to {} and create a new configuration", previousMode, newMode);
                    startedConfiguration = new LCConfigurationComponent();
                }
                this.modeChanged(startedConfiguration);
            }
        });
    }

    /**
     * Create the task used to change the current configuration
     *
     * @param mode          the mode that will be used
     * @param configuration the configuration that will be displayed and used by the mode
     * @return the task that change the current mode
     */
    private Task<Void> createModeStartTask(final LCModeI mode, final LCConfigurationI configuration) {
        //Change task
        return new LCTask<>("change.mode.task.title") {
            @Override
            protected Void call() {
                mode.modeStart(configuration);
                return null;
            }
        };
    }

    /**
     * Change the current mode configuration and fire a mode start to the current mode with a given configuration.<br>
     * Mode start is done asynchronously
     *
     * @param configuration the configuration to use with the mode
     */
    private void modeChanged(final LCConfigurationI configuration) {
        //Get the mode, and the configuration in it
        LCModeI mode = this.appModes.get(this.currentMode.get());
        mode.currentConfigurationProperty().set(configuration);
        //Execute the change task
        Task<Void> changeModeTask = this.createModeStartTask(mode, configuration);
        //Display current mode scene if change worked (and execute callback)
        changeModeTask.setOnSucceeded((e) -> {
            this.mainFrame.setScene(mode.initializeAndGetScene());
            for (Consumer<LCModeI> callback : this.modeChangedCallback) {
                callback.accept(mode);
            }
            //When the configuration description is not the same than configuration : call the action
            if (mode.getMode() == AppMode.CONFIG && configuration != null) {
                LCConfigurationDescriptionI desc = this.currentConfigDescription.get();
                if (desc != null && this.callbackIfDescriptionIsDifferent != null
                        && StringUtils.isDifferent(desc.getConfigurationId(), configuration.getID())) {
                    this.callbackIfDescriptionIsDifferent.accept(desc);
                }
            }
            configuration.dispatchDisplayedProperty(true);
            // If a callback is set
            if (nextModeChangeAfterLoadingCallback != null) {
                nextModeChangeAfterLoadingCallback.run();
                nextModeChangeAfterLoadingCallback = null;
            }
        });
        if (mode.isSkipNextModeStartAndReset()) {
            changeModeTask.getOnSucceeded().handle(null);
        } else {
            AsyncExecutorController.INSTANCE.addAndExecute(true, this.currentMode.get() == AppMode.CONFIG, changeModeTask);
        }
    }

    /**
     * Add the common state listener for all modes to listener list.<br>
     */
    private void initCommonStateListener() {
        this.stateListeners.add(VoiceSynthesizerController.INSTANCE);
        this.stateListeners.add(UserActionController.INSTANCE);
        this.stateListeners.add(WordPredictionController.INSTANCE);
        this.stateListeners.add(AutoCharPredictionController.INSTANCE);
        this.stateListeners.add(CustomCharPredictionController.INSTANCE);
        this.stateListeners.add(ImageDictionaries.INSTANCE);
        this.stateListeners.add(InstallationController.INSTANCE);
        this.stateListeners.add(SessionStatsController.INSTANCE);
    }

    /**
     * Close all the resources relative to the application.
     */
    private void close() {
        AsyncExecutorController.INSTANCE.close();
        this.LOGGER.info("Configuration action Thread pool is stopped");
    }


    // Class part : "Frame"
    //========================================================================

    /**
     * @return the application main {@link Stage}
     */
    public Stage getMainStage() {
        return this.mainFrame;
    }

    public Node getMainStageRoot() {
        return this.mainFrame.getScene().getRoot();
    }

    public String getMainStageDefaultTitle() {
        return LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel();
    }

    public void setMainFrame(final Stage mainFrameP) {
        this.mainFrame = mainFrameP;
    }

    public Map<AppMode, LCModeI> getAppModes() {
        return this.appModes;
    }

    public void setCallbackIfDescriptionIsDifferent(final Consumer<LCConfigurationDescriptionI> callbackIfDescriptionIsDifferent) {
        this.callbackIfDescriptionIsDifferent = callbackIfDescriptionIsDifferent;
    }

    /**
     * @return the current configuration description in both mode.<br>
     * This couldn't not be filled in use mode, and can be null if configuration has never been saved.
     */
    // FIXME : accept null ?
    public ObjectProperty<LCConfigurationDescriptionI> currentConfigDescriptionProperty() {
        return this.currentConfigDescription;
    }

    public DoubleProperty configurationScaleProperty() {
        return this.configurationScale;
    }

    public void zoomIn() {
        double newValue = this.configurationScale.get() + LCGraphicStyle.ZOOM_MODIFIER;
        if (newValue < LCGraphicStyle.MAX_ZOOM_VALUE) {
            this.configurationScale.set(newValue);
        }
    }

    public void zoomOut() {
        double newValue = this.configurationScale.get() - LCGraphicStyle.ZOOM_MODIFIER;
        if (newValue > LCGraphicStyle.MIN_ZOOM_VALUE) {
            this.configurationScale.set(newValue);
        }
    }

    public void resetZoom() {
        this.configurationScale.set(1.0);
    }

    /**
     * Move the stage to a given position.<br>
     * <strong>Should be called from Java FX Thread</strong>
     *
     * @param framePosition the frame position
     */
    public void moveFrameTo(final FramePosition framePosition) {
        if (!this.isOnEmbeddedDevice()) {
            AppController.moveStageTo(this.mainFrame, framePosition);
            VirtualMouseController.INSTANCE.centerMouseOnMainFrame();
        }
    }

    public static void moveStageTo(final Stage stage, final FramePosition framePosition) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();//Issue #169 : windows shouldn't be on window task bar
        double centerX = screenBounds.getWidth() / 2.0 - stage.getWidth() / 2.0;
        double centerY = screenBounds.getHeight() / 2.0 - stage.getHeight() / 2.0;
        switch (framePosition) {
            case CENTER:
                stage.centerOnScreen();
                break;
            case BOTTOM_RIGHT:
                stage.setX(screenBounds.getWidth() - stage.getWidth());
                stage.setY(screenBounds.getHeight() - stage.getHeight());
                break;
            case BOTTOM_LEFT:
                stage.setX(0.0);
                stage.setY(screenBounds.getHeight() - stage.getHeight());
                break;
            case TOP_RIGHT:
                stage.setX(screenBounds.getWidth() - stage.getWidth());
                stage.setY(0.0);
                break;
            case TOP_LEFT:
                stage.setX(0.0);
                stage.setY(0.0);
                break;
            case TOP:
                stage.setX(centerX);
                stage.setY(0.0);
                break;
            case LEFT:
                stage.setX(0.0);
                stage.setY(centerY);
                break;
            case RIGHT:
                stage.setX(screenBounds.getWidth() - stage.getWidth());
                stage.setY(centerY);
                break;
            case BOTTOM:
                stage.setX(centerX);
                stage.setY(screenBounds.getHeight() - stage.getHeight());
                break;
            default:
                stage.centerOnScreen();
                break;
        }
    }
    //========================================================================

    // Class part : "Mode"
    //========================================================================

    /**
     * @param mode the mode to register to the controller
     */
    public void registerMode(final LCModeI mode) {
        this.LOGGER.info("Register a new app mode {}", mode.getMode());
        this.appModes.put(mode.getMode(), mode);
        this.stateListeners.add(mode);
    }

    /**
     * Inform every modes that the application is starting
     */
    public void lcStart() {
        lcStartRan = true;
        this.LOGGER.info("Fire the LifeCompanion start to every state listeners");
        for (LCStateListener stateListener : this.stateListeners) {
            stateListener.lcStart();
        }
    }

    /**
     * Inform every modes that the application is stopping<br>
     * Also exit resources.
     */
    public void lcExit() {
        //Stop current mode
        AppMode mode = this.currentMode.get();
        if (mode != null && this.appModes.containsKey(mode)) {
            this.appModes.get(mode).modeStop(this.currentConfigurationProperty(mode).get());
        }
        //Stop LC
        this.close();
        if (lcStartRan) {
            this.LOGGER.info("Fire the LifeCompanion exit to every state listeners");
            for (LCStateListener stateListener : this.stateListeners) {
                stateListener.lcExit();
            }
        } else {
            LOGGER.info("Didn't go thought lcExit() on state listener as lcStart() wasn't run before");
        }
    }

    /**
     * @return the state listener (to listen for LifeCompanion start/stop)
     */
    public List<LCStateListener> getStateListeners() {
        return this.stateListeners;
    }

    /**
     * @return a list where callback after a mode start can be added.<br>
     * Note that the callback in this list will be called on the JavaFX Thread.
     */
    public List<Consumer<LCModeI>> getModeChangedCallback() {
        return this.modeChangedCallback;
    }

    public BiFunction<Node, LCConfigurationI, Boolean> getConfirmConfigurationModeFunction() {
        return this.confirmConfigurationModeFunction;
    }

    public void setConfirmConfigurationModeFunction(final BiFunction<Node, LCConfigurationI, Boolean> confirmConfigurationModeFunction) {
        this.confirmConfigurationModeFunction = confirmConfigurationModeFunction;
    }
    //========================================================================

    // Class part : "Configuration changes"
    //========================================================================

    /**
     * Tool method to create a new configuration is the current configuration mode.<br>
     * This will create a new configuration and set the current description to null
     */
    public void newConfigModeConfiguration() {
        this.changeConfigModeConfiguration(new LCConfigurationComponent(), null);
    }

    /**
     * Useful method to change the configuration and the configuration description in the same time.<br>
     * This method will always fire a change, even if the configuration are the same, or the description are the same.<br>
     * If given configuration is null, will not set to the current configuration property.
     *
     * @param config the new configuration to set
     * @param desc   the new configuration description to set
     */
    public void changeConfigModeConfiguration(final LCConfigurationI config, final LCConfigurationDescriptionI desc) {
        //Fire config change when needed
        String previousConfigId = null;
        if (config != null) {
            LCConfigurationI previous = this.currentConfigConfigurationProperty().get();
            if (previous != null) {
                previousConfigId = previous.getID();
                // When configuration changes, the loaded configuration will never be used again (it will be reloaded if needed)
                // memory should be freed : configuration manually delete some references to help GC.
                if (this.currentConfigDescription.get() != null) {
                    this.currentConfigDescription.get().loadedConfigurationProperty().set(null);
                }
                //Clear mode
                // this.appModes.get(AppMode.CONFIG).getViewProvider().clearAllViewCaches(); //VIEWCACHE
                //previous.clearViewCache();
                this.appModes.get(AppMode.CONFIG).configurationBeforeChangeProperty().set(null);
                //Fire a delete on previous component of configuration
                fireConfigurationRemoved(previous);
            }
            this.currentConfigurationProperty(AppMode.CONFIG).set(config);
        }
        //Fire configuration description when needed
        this.currentConfigDescriptionProperty().set(desc);
        this.resetZoom();

        // Fix #416 : should not clear if same configuration
        if (previousConfigId != null && StringUtils.isDifferent(previousConfigId, config.getID())) {
            //this.appModes.get(AppMode.CONFIG).getViewProvider().clearViewCacheForConfiguration(previousConfigId);//VIEWCACHE
        }
    }

    /**
     * Useful method to change the configuration in use mode.<br>
     * This will stop the current use mode and start it again with the new configuration.<br>
     * This will not change the configuration stored in config mode, because the launching configuration should remain the same.<br>
     * <strong>This method should be called only if the current mode is {@link AppMode#USE}
     *
     * @param configuration            the configuration to launch.
     * @param configurationDescription the new configuration description
     */
    public void changeUseModeConfiguration(final LCConfigurationI configuration, final LCConfigurationDescriptionI configurationDescription) {
        this.changeUseModeConfiguration(configuration, configurationDescription, false);
    }

    public void changeUseModeConfiguration(final LCConfigurationI configuration, final LCConfigurationDescriptionI configurationDescription,
                                           final boolean skipStop) {
        if (this.currentMode.get() == AppMode.USE) {
            LCUtils.runOnFXThread(() -> {
                // Issue #191 : clean previous configuration from memory
                String previousConfigId = null;
                final LCConfigurationDescriptionI previousConfigDesc = this.currentConfigDescription.get();
                if (previousConfigDesc != null) {
                    final LCConfigurationI previouslyLoadedConfiguration = previousConfigDesc.loadedConfigurationProperty().get();
                    if (previouslyLoadedConfiguration != null) {
                        previousConfigId = previouslyLoadedConfiguration.getID();
                        fireConfigurationRemoved(previouslyLoadedConfiguration);
                    }
                    previousConfigDesc.loadedConfigurationProperty().set(null);
                    //this.appModes.get(AppMode.USE).getViewProvider().clearAllViewCaches();//VIEWCACHE
                }
                final LCModeI configMode = this.appModes.get(AppMode.CONFIG);
                if (configMode != null) {
                    //configMode.getViewProvider().clearAllViewCaches();//VIEWCACHE
                    LCConfigurationI configurationBeforeChange = configMode.configurationBeforeChangeProperty().get();
                    fireConfigurationRemoved(configurationBeforeChange);
                    configMode.configurationBeforeChangeProperty().set(null);
                }
                //Stop current mode
                LCModeI currentUseMode = this.appModes.get(this.currentMode.get());
                if (!skipStop) {
                    currentUseMode.modeStop(currentUseMode.currentConfigurationProperty().get());
                }

                fireConfigurationRemoved(currentUseMode.currentConfigurationProperty().get());

                //Start with the new configuration
                currentUseMode.currentConfigurationProperty().set(configuration);
                this.currentConfigDescription.set(configurationDescription);
                currentUseMode.modeStart(configuration);
                //Set previous configuration
                SelectionModeController.INSTANCE.setPreviousConfigurationInUseMode(previousConfigDesc);

                // Clear view cache for previous config
                if (previousConfigId != null) {
                    //this.appModes.get(AppMode.USE).getViewProvider().clearViewCacheForConfiguration(previousConfigId);//VIEWCACHE
                }
            });
        }
    }

    private void fireConfigurationRemoved(LCConfigurationI configuration) {
        if (configuration != null) {
            configuration.dispatchDisplayedProperty(false);
            configuration.dispatchRemovedPropertyValue(true);
        }
    }
    //========================================================================

    // Class part : "Properties"
    //========================================================================

    /**
     * @return the current config mode configuration
     */
    public ReadOnlyObjectProperty<LCConfigurationI> currentConfigConfigurationProperty() {
        return this.currentConfigurationProperty(AppMode.CONFIG);
    }

    /**
     * @return the current use mode configuration
     */
    public ReadOnlyObjectProperty<LCConfigurationI> currentUseConfigurationProperty() {
        return this.currentConfigurationProperty(AppMode.USE);
    }

    /**
     * @param mode the wanted mode
     * @return the configuration for a given mode
     */
    private ObjectProperty<LCConfigurationI> currentConfigurationProperty(final AppMode mode) {
        return this.appModes.get(mode).currentConfigurationProperty();
    }

    /**
     * @return the current mode.<br>
     * This property can be changed to switch between modes.
     */
    public ObjectProperty<AppMode> currentModeProperty() {
        return this.currentMode;
    }

    public void setNextModeChangeAfterLoadingCallback(Runnable callback) {
        nextModeChangeAfterLoadingCallback = callback;
    }

    public ViewProviderI getViewProvider(AppMode mode) {
        return appModes.get(mode).getViewProvider();
    }

    public ComponentViewI<?> getViewForCurrentMode(DisplayableComponentI comp) {
        return comp.getDisplay(appModes.get(this.currentMode.get()).getViewProvider(), true);
    }

    /**
     * @return true if LifeCompanion is running on a embedded device (Android, etc...)<br>
     * This mean that the device doesn't have a real frame.
     */
    public boolean isOnEmbeddedDevice() {
        return SystemType.current() == SystemType.ANDROID || SystemType.current() == SystemType.IOS;
    }

    /**
     * @return true if this LifeCompanion instance will never be able to go in configuration mode.<br>
     * This will be true on mobile devices or on use only instances.
     */
    public boolean isUseModeOnly() {
        return this.useModeOnly;
    }

    public void setUseModeOnly(final boolean useModeOnly) {
        this.useModeOnly = useModeOnly;
    }

    /**
     * @return the currently selected profile
     */
    public ObjectProperty<LCProfileI> currentProfileProperty() {
        return this.currentProfile;
    }

    /**
     * @return true if the current mode is the use mode
     */
    public boolean isUseMode() {
        return this.currentMode.get() == AppMode.USE;
    }

    public BooleanProperty devModeProperty() {
        return this.devMode;
    }

    /**
     * @return true if LifeCompanion is running with the dev mode (dev flag given to arg array on startup).<br>
     * Can be useful to disable some checking mechanism to improve dev.
     */
    public boolean isDevMode() {
        return devMode.get();
    }
    //========================================================================

    // Class part : "Log"
    //========================================================================
    public void appendLog(final LogEntry logEntry) {
        if (!this.disableLog) {
            this.logEntries.add(logEntry);
        }
    }

    public ObservableList<LogEntry> getLogEntries() {
        return this.logEntries;
    }

    public boolean isDisableLog() {
        return this.disableLog;
    }

    public void disableLog() {
        this.logEntries.clear();
        this.disableLog = true;
    }

    /**
     * Increase by one the unsaved action on the current config configuration.<br>
     * Can be called manually if an action need to flag that the configuration was modified.
     */
    public void increaseUnsavedActionOnCurrentConfiguration() {
        LCConfigurationI currentConfiguration = AppController.INSTANCE.currentConfigConfigurationProperty().get();
        currentConfiguration.unsavedActionProperty().set(currentConfiguration.unsavedActionProperty().get() + 1);
    }
    //========================================================================


}
