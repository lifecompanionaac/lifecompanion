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

import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.data.config.*;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.lifecycle.LifeCompanionController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.util.StageUtils;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.config.data.action.impl.GlobalActions;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.action.impl.LCProfileActions;
import org.lifecompanion.config.data.component.general.GeneralConfigurationController;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.config.view.pane.general.GeneralConfigurationScene;
import org.lifecompanion.config.view.pane.general.GeneralConfigurationStage;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigSelectionScene;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigSelectionStage;
import org.lifecompanion.config.view.scene.ConfigurationScene;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.List;

import static org.lifecompanion.base.data.config.LCConstant.URL_PATH_GET_STARTED;

public class LCLauncherV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(LCLauncherV2.class);

    private final long startAt;
    private final Stage stage;
    private final List<String> args;

    private LoadingScene loadingScene;

    public LCLauncherV2(Stage stage, List<String> args) {
        this.stage = stage;
        this.args = args;
        this.startAt = System.currentTimeMillis();
    }

    public void startLifeCompanion() {
        AppModeController.INSTANCE.initEditModeStage(stage);

        preload();
        loadAndShowStageAndLoadingScene();
        if (InstallationController.INSTANCE.isUpdateDownloadFinished()) {
            LOGGER.info("Will not load main UI because update download is finished, should finalize");
            InstallationController.INSTANCE.launchInstallAppUpdate();
        } else {
            initLifeCompanionStage();
            startBackgroundLoad();
        }

    }

    // LOADING RES
    //========================================================================
    public void preload() {
        long start = System.currentTimeMillis();
        try {
            UserBaseConfiguration.INSTANCE.load();
        } catch (Exception e) {
            LOGGER.warn("The user configuration can't be loaded", e);
        }
        //Load text
        String language = UserBaseConfiguration.INSTANCE.userLanguageProperty().get();
        for (String languageFile : LCConstant.INT_PATH_TEXT_FILES) {
            TranslationManager.INSTANCE.loadLanguageResource(language, languageFile);
        }
        LCGlyphFont.loadFont();
        LOGGER.info("Preload done in {} ms", System.currentTimeMillis() - start);
    }
    //========================================================================

    // LOADING THREAD
    //========================================================================
    private void startBackgroundLoad() {
        LCNamedThreadFactory.threadFactory("LCLauncher").newThread(() -> {
            long start = System.currentTimeMillis();
            LifeCompanionController.INSTANCE.lcStart();

            final ConfigurationScene configurationScene = new ConfigurationScene(new StackPane());
            configurationScene.initAll();

            ProfileConfigSelectionController.INSTANCE.getStage().getProfileConfigSelectionScene().initAll();
            GeneralConfigurationController.INSTANCE.getStage().getGeneralConfigurationScene().initAll();

            LOGGER.info("UI background loading done in {} s, will define post load action", (System.currentTimeMillis() - start) / 1000.0);
            try {
                final AfterLoad afterLoad = getAfterLoad();
                LCUtils.runOnFXThread(() -> handleAfterLoadAction(configurationScene, afterLoad));
            } catch (Exception e) {
                LOGGER.error("Unexpected issue while defining post load actions, will show profile selection step", e);
                //FIXME
            }
        }).start();
    }

    private void handleAfterLoadAction(ConfigurationScene configurationScene, AfterLoad afterLoad) {
        LOGGER.info("After load action will be {}", afterLoad.afterLoadAction);

        if (afterLoad.profile != null) {
            ProfileController.INSTANCE.selectProfile(afterLoad.profile);
        }

        if (afterLoad.afterLoadAction == AfterLoadAction.SELECT_PROFILE) {
            showProfileCreateOrList();
        }

        if (afterLoad.afterLoadAction == AfterLoadAction.SELECT_CONFIGURATION) {
            ProfileConfigSelectionController.INSTANCE.setStep(ProfileConfigStep.CONFIGURATION_LIST, ProfileConfigStep.PROFILE_LIST);
        }

        if (afterLoad.afterLoadAction == AfterLoadAction.IMPORT_PROFILE) {
            LCProfileActions.ProfileImportAction profileImportAction = new LCProfileActions.ProfileImportAction(afterLoad.file);
            ConfigActionController.INSTANCE.executeAction(profileImportAction);
            showProfileCreateOrList();
        }

        if (afterLoad.afterLoadAction == AfterLoadAction.IMPORT_CONFIG) {
            LCConfigurationActions.ImportOpenEditAction importOpenConfig = new LCConfigurationActions.ImportOpenEditAction(afterLoad.file);
            ConfigActionController.INSTANCE.executeAction(importOpenConfig);
        }

        loadingScene.stopAndClear();
        stage.setScene(configurationScene);

        if (afterLoad.afterLoadAction != AfterLoadAction.LAUNCH_USE) {
            AppModeController.INSTANCE.startEditMode();
        } else {
            AppModeController.INSTANCE.startUseModeForConfiguration(afterLoad.configuration, afterLoad.configurationDescription);
        }


    }

    private void showProfileCreateOrList() {
        if (ProfileController.INSTANCE.getProfiles().isEmpty()) {
            ProfileConfigSelectionController.INSTANCE.setStep(ProfileConfigStep.PROFILE_CREATE, ProfileConfigStep.PROFILE_LIST);
            UIUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl() + URL_PATH_GET_STARTED);
        } else {
            ProfileConfigSelectionController.INSTANCE.setStep(ProfileConfigStep.PROFILE_LIST, null);
        }
    }

    private AfterLoad getAfterLoad() throws Exception {
        // First, load all profile
        final List<LCProfileI> profiles = LCUtils.executeInCurrentThread(IOManager.INSTANCE.createLoadAllProfileDescriptionTask());
        ProfileController.INSTANCE.getProfiles().setAll(profiles); // FIXME FX Thread ?

        // Check if profile to import
        final File profileFile = getFirstLifeCompanionFile(LCConstant.PROFILE_FILE_EXTENSION);
        if (profileFile != null) return new AfterLoad(AfterLoadAction.IMPORT_PROFILE, null, null, null, profileFile);

        // Check profile to select
        final String profileIDToSelect = getProfileIDToSelect();
        if (profileIDToSelect != null) {
            try {
                LCProfileI profileToSelect = ProfileController.INSTANCE.getByID(profileIDToSelect);
                profileToSelect = LCUtils.executeInCurrentThread(IOManager.INSTANCE.createLoadFullProfileTask(profileToSelect));

                // Check if a configuration is imported
                File configurationFile = this.getFirstLifeCompanionFile(LCConstant.CONFIG_FILE_EXTENSION);
                if (configurationFile != null) {
                    return new AfterLoad(AfterLoadAction.IMPORT_CONFIG, null, null, profileToSelect, configurationFile);
                }

                // Try to launch a configuration in use mode
                final LCConfigurationDescriptionI configurationToLaunchFor = getConfigurationToLaunchFor(profileToSelect);
                if (configurationToLaunchFor != null) {
                    final LCConfigurationI configuration = LCUtils.executeInCurrentThread(IOManager.INSTANCE.createLoadConfigurationTask(configurationToLaunchFor, profileToSelect));
                    return new AfterLoad(AfterLoadAction.LAUNCH_USE, configurationToLaunchFor, configuration, profileToSelect, null);
                }

                // Default : select a configuration
                return new AfterLoad(AfterLoadAction.SELECT_CONFIGURATION, null, null, profileToSelect, null);
            } catch (Exception ep) {
                LOGGER.warn("Couldn't select profile with ID {}", profileIDToSelect, ep);
            }
        }

        // Default : select a profile
        return new AfterLoad(AfterLoadAction.SELECT_PROFILE, null, null, null, null);
    }
    //========================================================================

    // STAGES
    //========================================================================
    private void initLifeCompanionStage() {
        ProfileConfigSelectionStage profileConfigSelectionStage = new ProfileConfigSelectionStage(stage, new ProfileConfigSelectionScene());
        ProfileConfigSelectionController.INSTANCE.initStage(profileConfigSelectionStage);
        GeneralConfigurationStage generalConfigurationStage = new GeneralConfigurationStage(stage, new GeneralConfigurationScene());
        GeneralConfigurationController.INSTANCE.initStage(generalConfigurationStage);
    }

    private void loadAndShowStageAndLoadingScene() {
        // Main stage configuration
        this.stage.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.stage.initStyle(StageStyle.DECORATED);
        this.stage.setTitle(StageUtils.getStageDefaultTitle());
        this.stage.setWidth(UserBaseConfiguration.INSTANCE.mainFrameWidthProperty().get());
        this.stage.setHeight(UserBaseConfiguration.INSTANCE.mainFrameHeightProperty().get());
        this.stage.setMaximized(UserBaseConfiguration.INSTANCE.launchMaximizedProperty().get());
        this.stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(IconManager.get(LCConstant.LC_ICON_PATH));
        this.stage.setOnCloseRequest((we) -> {
            we.consume();
            GlobalActions.HANDLER_CANCEL.handle(null);
        });
        this.stage.setScene(loadingScene = new LoadingScene(new VBox()));

        // Show once scene is set
        this.stage.show();

        // Close the splashscreen (AWT splashscreen)
        final SplashScreen splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen != null) {
            splashScreen.close();
        }
        LOGGER.info("Loading scene initialized and shown");
    }
    //========================================================================

    // ANALYSE ARGS
    //========================================================================
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


    private String getProfileIDToSelect() {
        final int indexOfLaunchConfigArg = args.indexOf(LCConstant.ARG_LAUNCH_CONFIG);
        if (indexOfLaunchConfigArg >= 0) {
            // Check that there is two next args (profile and configuration ids)
            if (indexOfLaunchConfigArg + 2 < args.size()) {
                return args.get(indexOfLaunchConfigArg + 1);
            }
        }
        return LCStateController.INSTANCE.getLastSelectedProfileID();
    }

    private File getFirstLifeCompanionFile(String extensionToSearch) {
        if (!CollectionUtils.isEmpty(args)) {
            for (String arg : args) {
                String ext = FileNameUtils.getExtension(arg);
                if (extensionToSearch.equalsIgnoreCase(ext)) {
                    try {
                        File path = new File(arg);
                        IOManager.INSTANCE.getFileID(path);// to check if the file is an profile or configuration
                        return path;
                    } catch (LCException e) {
                        //Will return null
                    }
                }
            }
        }
        return null;
    }
    //========================================================================

    // MODEL
    //========================================================================
    private static class AfterLoad {
        private final AfterLoadAction afterLoadAction;
        private final LCConfigurationDescriptionI configurationDescription;
        private final LCConfigurationI configuration;
        private final LCProfileI profile;
        private final File file;

        public AfterLoad(AfterLoadAction afterLoadAction, LCConfigurationDescriptionI configurationDescription, LCConfigurationI configuration, LCProfileI profile, File file) {
            this.afterLoadAction = afterLoadAction;
            this.configurationDescription = configurationDescription;
            this.configuration = configuration;
            this.profile = profile;
            this.file = file;
        }
    }

    private enum AfterLoadAction {
        SELECT_PROFILE,
        SELECT_CONFIGURATION,
        LAUNCH_USE,
        IMPORT_CONFIG,
        IMPORT_PROFILE
    }
    //========================================================================
}
