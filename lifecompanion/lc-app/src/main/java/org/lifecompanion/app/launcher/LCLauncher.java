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
package org.lifecompanion.app.launcher;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.api.component.definition.FramePosition;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.component.profile.LCProfileManager;
import org.lifecompanion.base.data.component.simple.LCConfigurationComponent;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.base.data.io.task.ConfigurationLoadingTask;
import org.lifecompanion.base.data.io.task.MultipleProfileDescriptionLoadingTask;
import org.lifecompanion.base.data.io.task.ProfileFullLoadingTask;
import org.lifecompanion.base.view.reusable.AnimatedBorderPane;
import org.lifecompanion.config.data.action.impl.GlobalActions;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.action.impl.LCProfileActions;
import org.lifecompanion.config.data.component.general.GeneralConfigurationController;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.config.tips.ConfigTipsController;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.config.data.mode.ConfigMode;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.dev.DevViewPane;
import org.lifecompanion.config.view.pane.general.GeneralConfigurationScene;
import org.lifecompanion.config.view.pane.general.GeneralConfigurationStage;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigSelectionScene;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigSelectionStage;
import org.lifecompanion.config.view.scene.ConfigurationScene;
import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.use.data.mode.UseMode;
import org.lifecompanion.use.view.scene.ConfigUseScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.lifecompanion.base.data.config.LCConstant.URL_PATH_CHANGELOG;
import static org.lifecompanion.base.data.config.LCConstant.URL_PATH_GET_STARTED;

/**
 * Class to launch all the main application and load needed things.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCLauncher {
    private final static Logger LOGGER = LoggerFactory.getLogger(LCLauncher.class);
    private long startTime;
    private Stage mainStage;

    private ProfileConfigSelectionScene profileScene;
    private ConfigurationScene configurationScene;
    private GeneralConfigurationScene generalConfigurationScene;

    private ConfigUseScene useScene;
    private UseMode useMode;
    private final List<String> args;
    private LoadingScene loadingScene;

    /**
     * Create the launcher that will launch full LifeCompanion application
     *
     * @param mainFrameP the main stage for LifeCompanion
     */
    public LCLauncher(final Stage mainFrameP, List<String> args) {
        this.mainStage = mainFrameP;
        this.args = args;
    }

    /**
     * This method will trigger all the loading of the application.<br>
     * Will load the mode and the UI.
     */
    public void startLoading() {
        this.startTime = System.currentTimeMillis();
        this.preload();
        this.loadUI();
    }

    // Class part : "Preloading"
    //========================================================================

    /**
     * Create the needed UI base and initialize application modes.
     */
    private void preload() {
        AppController.INSTANCE.setMainFrame(this.mainStage);
        this.configurationScene = new ConfigurationScene(new StackPane());
        this.useScene = new ConfigUseScene(new Group());
        //Create mode
        ConfigMode configMode = new ConfigMode(this.configurationScene);
        AppController.INSTANCE.registerMode(configMode);
        this.useMode = new UseMode(this.useScene);
        AppController.INSTANCE.registerMode(this.useMode);
        // Pre start config mode
        configMode.preLcStart();
    }
    //========================================================================

    /**
     * Load the UI stuff
     */
    public void loadUI() {
        LCLauncher.LOGGER.info("Prelaunch task ended in " + (System.currentTimeMillis() - this.startTime) / 1000.0 + "s");
        long startUI = System.currentTimeMillis();
        LCGlyphFont.loadFont();
        this.initMainFrame();
        this.initAppStages();
        LCLauncher.LOGGER.info("Base UI initialization ended in " + (System.currentTimeMillis() - startUI) / 1000.0 + "s");
        if (InstallationController.INSTANCE.isUpdateDownloadFinished()) {
            LOGGER.info("Will not load main UI because update download is finished, should finalize");
            InstallationController.INSTANCE.launchInstallAppUpdate();
        } else {
            this.launchMainFrameUIInit();
        }
    }

    /**
     * Initialize the main frame with a loading progress content
     */
    private void initMainFrame() {
        //Stage style
        this.mainStage.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.mainStage.initStyle(StageStyle.DECORATED);
        this.mainStage.setTitle(AppController.INSTANCE.getMainStageDefaultTitle());
        this.mainStage.setWidth(UserBaseConfiguration.INSTANCE.mainFrameWidthProperty().get());
        this.mainStage.setHeight(UserBaseConfiguration.INSTANCE.mainFrameHeightProperty().get());
        this.mainStage.setMaximized(UserBaseConfiguration.INSTANCE.launchMaximizedProperty().get());
        this.mainStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.mainStage.centerOnScreen();
        this.mainStage.getIcons().add(IconManager.get(LCConstant.LC_ICON_PATH));
        this.mainStage.setOnCloseRequest((we) -> {
            we.consume();
            GlobalActions.HANDLER_CANCEL.handle(null);
        });
        this.mainStage.setScene(loadingScene = new LoadingScene(new VBox()));

        // Show once scene is set
        this.mainStage.show();

        // Close the splashscreen (AWT splashscreen)
        final SplashScreen splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen != null) {
            splashScreen.close();
        }

        LOGGER.info("Main frame size : {}x{}", this.mainStage.getWidth(), this.mainStage.getHeight());
        LCLauncher.LOGGER.info("Loading scene initialized");
    }

    /**
     * Initialize other application stages that are needed globally (and quite heavy).<br>
     * These stages could have been lazy loaded later, but initialization here allow a background loading of their scene.
     */
    private void initAppStages() {
        this.profileScene = new ProfileConfigSelectionScene(new AnimatedBorderPane());
        ProfileConfigSelectionStage profileConfigSelectionStage = new ProfileConfigSelectionStage(this.mainStage, this.profileScene);
        ProfileConfigSelectionController.INSTANCE.setStage(profileConfigSelectionStage);

        this.generalConfigurationScene = new GeneralConfigurationScene(new BorderPane());
        GeneralConfigurationStage generalConfigurationStage = new GeneralConfigurationStage(this.mainStage, generalConfigurationScene);
        GeneralConfigurationController.INSTANCE.setStage(generalConfigurationStage);

        LCLauncher.LOGGER.info("Other app stages initialized");
    }

    /**
     * Initialize the main frame real content (in a background Thread)
     */
    private void launchMainFrameUIInit() {
        LCNamedThreadFactory.threadFactory("UI-Loading").newThread(() -> {
            long start = System.currentTimeMillis();
            AppController.INSTANCE.lcStart();

            this.configurationScene.initAll();
            this.useScene.initAll();
            this.profileScene.initAll();
            this.generalConfigurationScene.initAll();

            LCLauncher.LOGGER.info("Main UI initialization ended in " + (System.currentTimeMillis() - start) / 1000.0 + "s");
            Platform.runLater(this::loadingEndedCallback);
        }).start();
    }

    /**
     * Method called when the main frame UI was fully loaded.<br>
     * This will load the profile in background (and the gallery too).<br>
     * Once profile are loaded the profile/config selection view will be display
     */
    @SuppressWarnings("unchecked")
    private void loadingEndedCallback() {
        //Time of full loading
        LCLauncher.LOGGER.info("Total loading time {} s", (System.currentTimeMillis() - this.startTime) / 1000.0);
        //Profile loading
        MultipleProfileDescriptionLoadingTask profilesLoadingTask = IOManager.INSTANCE.createLoadAllProfileDescriptionTask();
        profilesLoadingTask.setOnSucceeded((wse) -> {
            LCProfileManager.INSTANCE.getProfiles().addAll((List<LCProfileI>) wse.getSource().getValue());
            this.afterProfileLoading();
        });
        profilesLoadingTask.setOnFailed((wse) -> {
            LCLauncher.LOGGER.warn("Couldn't load the profiles", wse.getSource().getException());
            //Profile loading fail, user should select another one
            this.showStepAndUpdateFrame(ProfileConfigStep.PROFILE_LIST, null);
        });
        AsyncExecutorController.INSTANCE.addAndExecute(false, false, profilesLoadingTask);
    }


    private void showStepAndUpdateFrame(final ProfileConfigStep step, final ProfileConfigStep previousStep) {
        AppController.INSTANCE.setNextModeChangeAfterLoadingCallback(() -> {
            ProfileConfigSelectionController.INSTANCE.setStep(step, previousStep);
            this.showDevFrameIfNeeded();
            this.showConfigTipsFrame();

            // Clear loading scene (stop animation and free for GC)
            loadingScene.stopAndClear();
            loadingScene = null;
        });
        AppController.INSTANCE.currentModeProperty().set(AppMode.CONFIG);
    }

    private void afterProfileLoading() {
        //If a profile is imported
        File validProfilePath = this.getFirstValidLCFilePath(args, LCConstant.PROFILE_FILE_EXTENSION);
        if (validProfilePath != null) {
            LCLauncher.LOGGER.info("Found a valid profile file to load in launch args : {}", validProfilePath);
            this.importProfile(validProfilePath);
        } else {
            String profileId = getProfileToLoad();
            if (profileId != null) {
                LCProfileI profileSelectedById = LCProfileManager.INSTANCE.getByID(profileId);
                if (profileSelectedById != null) {
                    LCLauncher.LOGGER.info("Found a profile to load ({}), will load and select it and directly show the configuration selection", profileId);
                    ProfileFullLoadingTask profileFullLoadingTask = IOManager.INSTANCE.createLoadFullProfileTask(profileSelectedById);
                    profileFullLoadingTask.setOnSucceeded(event -> {
                        AppController.INSTANCE.currentProfileProperty().set(profileSelectedById);
                        // Import configuration ?
                        File validConfigPath = this.getFirstValidLCFilePath(args, LCConstant.CONFIG_FILE_EXTENSION);
                        if (validConfigPath == null) {
                            //Default configuration on profile ?
                            if (!this.checkLaunchConfigurationUseMode(profileSelectedById)) {
                                this.showStepAndUpdateFrame(ProfileConfigStep.CONFIGURATION_LIST, ProfileConfigStep.PROFILE_LIST);
                            }
                        } else {
                            AppController.INSTANCE.currentModeProperty().set(AppMode.CONFIG);
                            LCLauncher.LOGGER.info("Found a valid configuration to load in launch args : {}", validConfigPath);
                            this.importConfiguration(validConfigPath);
                        }
                    });
                    profileFullLoadingTask.setOnFailed(event -> {
                        LOGGER.error("Couldn't fully load the profile {}", profileId, event.getSource().getException());
                        showNoSelectedProfileStep();
                    });
                    AsyncExecutorController.INSTANCE.addAndExecute(false, false, profileFullLoadingTask);
                    return;// -- PROFILE FOUND, RETURN
                } else {
                    LCLauncher.LOGGER.info("The last selected profile {} doesn't exist anymore, will show profile selection", profileId);
                }
            } else {
                LCLauncher.LOGGER.info("No last selected profile, will show profile selection");
            }
        }
        showNoSelectedProfileStep();
    }

    private String getProfileToLoad() {
        final int indexOfLaunchConfigArg = args.indexOf(LCConstant.ARG_LAUNCH_CONFIG);
        if (indexOfLaunchConfigArg >= 0) {
            // Check that there is two next args (profile and configuration ids)
            if (indexOfLaunchConfigArg + 2 < args.size()) {
                return args.get(indexOfLaunchConfigArg + 1);
            }
        }
        return LCStateController.INSTANCE.getLastSelectedProfileID();
    }

    private void showNoSelectedProfileStep() {
        //When profiles are loaded and there is no valid previous profile, show the profile selection/creation frame check if there is an existing profile : first start !
        if (LCProfileManager.INSTANCE.getProfiles().isEmpty()) {
            this.showStepAndUpdateFrame(ProfileConfigStep.PROFILE_CREATE, ProfileConfigStep.PROFILE_LIST);
            UIUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl() + URL_PATH_GET_STARTED);
        } else {
            this.showStepAndUpdateFrame(ProfileConfigStep.PROFILE_LIST, null);
        }
    }

    /**
     * Initialize and show the dev frame (if application is launched in dev mode)
     */
    private void showDevFrameIfNeeded() {
        if (this.args.contains(ApplicationConstant.ARG_DEV)) {
            AppController.INSTANCE.devModeProperty().set(true);
            Scene devScene = new Scene(new DevViewPane());
            devScene.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
            Stage devStage = ConfigUIUtils.createApplicationModalStage(mainStage, LCConstant.DEV_STAGE_WIDTH, LCConstant.DEV_STAGE_HEIGHT);
            devStage.initModality(Modality.NONE);
            devStage.initOwner(null);
            devStage.setScene(devScene);
            devStage.setAlwaysOnTop(true);
            devStage.setOpacity(0.8);
            AppController.moveStageTo(devStage, FramePosition.BOTTOM_RIGHT);
            devStage.show();
        } else {
            AppController.INSTANCE.disableLog();
        }
    }

    /**
     * Initialize and show the config tips frame (if user configuration enable this feature)
     */
    private void showConfigTipsFrame() {
        if (UserBaseConfiguration.INSTANCE.showTipsOnStartupProperty().get()) {
            ConfigTipsController.INSTANCE.showConfigTipsStage();
        }
    }

    // CONFIGURATION USE/LOAD
    //========================================================================

    /**
     * Check if the given profile contains a configuration to launch in use mode.<br>
     * This will also launch it if needed.
     *
     * @param profile the profile
     * @return true if the use mode will be launched
     */
    private boolean checkLaunchConfigurationUseMode(final LCProfileI profile) {
        final LCConfigurationDescriptionI configurationToLaunch = getConfigurationToLaunchFor(profile);
        if (configurationToLaunch != null) {
            LCLauncher.LOGGER.info("Found a configuration {} to launch in use mode for the profile {}, will load and launch it", configurationToLaunch.getConfigurationId(), profile.nameProperty().get());
            //Try to load the configuration and launch configuration
            ConfigurationLoadingTask loadTask = IOManager.INSTANCE.createLoadConfigurationTask(configurationToLaunch, AppController.INSTANCE.currentProfileProperty().get());
            loadTask.setOnSucceeded((ea) -> {
                //Change mode without firing a mode start (cause it must be fired once configuration is loaded)
                this.useMode.enableSkipNextModeStart();
                AppController.INSTANCE.currentModeProperty().set(AppMode.USE);
                //Change configuration in use mode without firing a mode stop (cause there was no mode currently running)
                LCConfigurationComponent value = (LCConfigurationComponent) ea.getSource().getValue();
                AppController.INSTANCE.changeUseModeConfiguration(value, configurationToLaunch, true);
            });
            loadTask.setOnFailed((ea) -> this.showStepAndUpdateFrame(ProfileConfigStep.CONFIGURATION_LIST, ProfileConfigStep.PROFILE_LIST));
            AsyncExecutorController.INSTANCE.addAndExecute(false, false, loadTask);
            return true;
        }
        return false;
    }

    private LCConfigurationDescriptionI getConfigurationToLaunchFor(LCProfileI profile) {
        // Default configuration
        LCConfigurationDescriptionI configurationToLaunch = profile.getCurrentDefaultConfiguration();

        // Configuration to load from arg
        final int indexOfLaunchConfigArg = args.indexOf(LCConstant.ARG_LAUNCH_CONFIG);
        if (indexOfLaunchConfigArg >= 0) {
            if (indexOfLaunchConfigArg + 2 < args.size()) {
                final String configIdToSearch = args.get(indexOfLaunchConfigArg + 2);
                final LCConfigurationDescriptionI configurationFromId = profile.getConfiguration().stream().filter(configDesc -> StringUtils.isEquals(configIdToSearch, configDesc.getConfigurationId())).findAny().orElse(null);
                if (configurationFromId != null) {
                    return configurationFromId;
                }
            }
        }

        return configurationToLaunch;
    }

    /**
     * Check if the first parameter is a specific file with valid format and ID (for configuration and profile files)
     *
     * @param args              the parameters list
     * @param extensionToSearch searched extension
     * @return the first valid file found
     */
    private File getFirstValidLCFilePath(final Collection<String> args, String extensionToSearch) {
        if (!CollectionUtils.isEmpty(args)) {
            for (String arg : args) {
                String ext = FileNameUtils.getExtension(arg);
                if (extensionToSearch.equalsIgnoreCase(ext)) {
                    try {
                        File path = new File(arg);
                        IOManager.INSTANCE.getFileID(path);
                        return path;
                    } catch (LCException e) {
                        //Will return false
                    }
                }
            }
        }
        return null;
    }

    /**
     * Load the configuration from the configuration path contains in parameters
     *
     * @param configurationPath path to configuration
     */
    private void importConfiguration(final File configurationPath) {
        LCConfigurationActions.ImportOpenConfigAction importOpenConfig = new LCConfigurationActions.ImportOpenConfigAction(configurationPath);
        ConfigActionController.INSTANCE.executeAction(importOpenConfig);
    }

    /**
     * Load the profile from the profile path contained in parameters
     *
     * @param profilePath path to configuration
     */
    private void importProfile(final File profilePath) {
        LCProfileActions.ProfileImportAction profileImportAction = new LCProfileActions.ProfileImportAction(profilePath);
        ConfigActionController.INSTANCE.executeAction(profileImportAction);
    }
    //========================================================================


    // GC CHECK
    //========================================================================
    //    private String lastConfigId;
    //
    //    private void createConfigChangeListener(AppMode mode, String configID) {
    //        AppController.INSTANCE.currentConfigurationProperty(mode).addListener((obs, ov, nv) -> {
    //            if (nv != null) {
    //                configurations.get(mode).add(new WeakReference<>(nv));
    //                lastConfigId = nv.getID();
    //                // System.gc();
    //                // LOGGER.info("[{}] DIRECT - {} = {}", mode, configID, configurations.get(mode).stream().map(Reference::get).filter(Objects::nonNull).filter(c -> StringUtils.isEquals(c.getID(), configID)).count());
    //            }
    //            new Thread(() -> {
    //                LCUtils.safeSleep(5_000);
    //                System.gc();
    //                LOGGER.info("[{}] DELAYED - {} = {}", mode, configID, configurations.get(mode).stream().map(Reference::get).filter(Objects::nonNull).filter(
    //                        c -> c.getAllComponent().size() > 0 && !StringUtils.isEquals(c.getID(), lastConfigId) && !StringUtils.isEquals("empty-configuration", c.getID()))
    //                        .count());
    //            }).start();
    //        });
    //    }
    //
    //    static final Map<AppMode, Set<WeakReference<LCConfigurationI>>> configurations = new HashMap<>();
    //
    //    static {
    //        configurations.put(AppMode.CONFIG, new HashSet<>());
    //        configurations.put(AppMode.USE, new HashSet<>());
    //    }
    //========================================================================
}
