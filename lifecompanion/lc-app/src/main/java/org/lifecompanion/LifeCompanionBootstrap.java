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

package org.lifecompanion;

import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editaction.LCProfileActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.ConfigurationImportTask;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.lifecycle.LifeCompanionController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.translation.TranslationLoader;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.EditModeScene;
import org.lifecompanion.ui.LoadingStage;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStage;
import org.lifecompanion.ui.app.profileconfigselect.ProfileConfigSelectionStage;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.List;

import static org.lifecompanion.model.impl.constant.LCConstant.URL_PATH_GET_STARTED;

public class LifeCompanionBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCompanionBootstrap.class);

    private final long startAt;
    private final Stage stage;
    private final List<String> args;
    private LoadingStage loadingStage;

    public LifeCompanionBootstrap(Stage stage, List<String> args) {
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
        try {
            UserConfigurationController.INSTANCE.load();
        } catch (Exception e) {
            LOGGER.warn("The user configuration can't be loaded", e);
        }
        //Load text
        String language = UserConfigurationController.INSTANCE.userLanguageProperty().get();
        for (String languageFile : LCConstant.INT_PATH_TEXT_FILES) {
            TranslationLoader.loadLanguageResource(language, languageFile);
        }
        GlyphFontHelper.loadFont();
        LOGGER.info("Preload done in {}s", (System.currentTimeMillis() - startAt) / 1000.0);
    }
    //========================================================================

    // LOADING THREAD
    //========================================================================
    private void startBackgroundLoad() {
        LCNamedThreadFactory.threadFactory("LifeCompanionBootstrap").newThread(() -> {
            long start = System.currentTimeMillis();
            LifeCompanionController.INSTANCE.lcStart();

            final EditModeScene editModeScene = new EditModeScene(new StackPane());
            editModeScene.initAll();

            ProfileConfigSelectionController.INSTANCE.getStage().getProfileConfigSelectionScene().initAll();
            GeneralConfigurationController.INSTANCE.getStage().getGeneralConfigurationScene().initAll();

            LOGGER.info("UI background loading done in {} s, will define post load action", (System.currentTimeMillis() - start) / 1000.0);
            try {
                start = System.currentTimeMillis();
                final AfterLoad afterLoad = getAfterLoad();
                LOGGER.info("After load action definition done in {} s", (System.currentTimeMillis() - start) / 1000.0);
                FXThreadUtils.runOnFXThread(() -> handleAfterLoadAction(editModeScene, afterLoad));
            } catch (Exception e) {
                LOGGER.error("Unexpected issue while defining post load actions, will show profile selection step", e);
                FXThreadUtils.runOnFXThread(() -> handleAfterLoadAction(editModeScene, DEFAULT_AFTERLOAD));
            }
        }).start();
    }

    private void handleAfterLoadAction(EditModeScene editModeScene, AfterLoad afterLoad) {
        LOGGER.info("Loading done after {}s / post load action is {}", (System.currentTimeMillis() - startAt) / 1000.0, afterLoad.afterLoadAction);
        stage.setScene(editModeScene);
        if (afterLoad.afterLoadAction != AfterLoadAction.LAUNCH_USE) {
            stage.show();
        }

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

        if (loadingStage != null) {
            loadingStage.hide();
            loadingStage = null;
        }

        if (afterLoad.afterLoadAction != AfterLoadAction.LAUNCH_USE) {
            AppModeController.INSTANCE.startEditMode();
        } else {
            AppModeController.INSTANCE.startUseModeForConfiguration(afterLoad.configuration, afterLoad.configurationDescription);
        }
    }

    private void showProfileCreateOrList() {
        if (ProfileController.INSTANCE.getProfiles().isEmpty()) {
            ProfileConfigSelectionController.INSTANCE.setStep(ProfileConfigStep.PROFILE_CREATE, ProfileConfigStep.PROFILE_LIST);
            DesktopUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl() + URL_PATH_GET_STARTED);
        } else {
            ProfileConfigSelectionController.INSTANCE.setStep(ProfileConfigStep.PROFILE_LIST, null);
        }
    }

    private AfterLoad getAfterLoad() throws Exception {
        // First, load all profile
        final List<LCProfileI> profiles = ThreadUtils.executeInCurrentThread(IOHelper.createLoadAllProfileDescriptionTask());
        ProfileController.INSTANCE.getProfiles().setAll(profiles);// it is ok to set profile outside FXThread because there is no UI displayed at this time

        // Check if profile to import
        final File profileFile = IOHelper.getFirstProfileFile(args);
        if (profileFile != null) return new AfterLoad(AfterLoadAction.IMPORT_PROFILE, null, null, null, profileFile);

        // Check profile to select
        final String profileIDToSelect = getProfileIDToSelect();
        if (profileIDToSelect != null) {
            try {
                LCProfileI profileToSelect = ProfileController.INSTANCE.getByID(profileIDToSelect);
                profileToSelect = ThreadUtils.executeInCurrentThread(IOHelper.createLoadFullProfileTask(profileToSelect, false));

                // Check if a configuration is imported
                File configurationFile = IOHelper.getFirstConfigurationFile(args);
                if (configurationFile != null) {
                    return new AfterLoad(AfterLoadAction.IMPORT_CONFIG, null, null, profileToSelect, configurationFile);
                }

                // Try to launch a configuration in use mode
                final Pair<LCConfigurationDescriptionI, LCConfigurationI> configurationToLaunchFor = getConfigurationToLaunchFor(profileToSelect);
                if (configurationToLaunchFor != null) {
                    return new AfterLoad(AfterLoadAction.LAUNCH_USE, configurationToLaunchFor.getKey(), configurationToLaunchFor.getValue(), profileToSelect, null);
                }

                // Default : select a configuration
                return new AfterLoad(AfterLoadAction.SELECT_CONFIGURATION, null, null, profileToSelect, null);
            } catch (Exception ep) {
                LOGGER.warn("Couldn't select profile or detect launch args", ep);
            }
        }
        return DEFAULT_AFTERLOAD;
    }

    private final static AfterLoad DEFAULT_AFTERLOAD = new AfterLoad(AfterLoadAction.SELECT_PROFILE, null, null, null, null);
    //========================================================================

    // STAGES
    //========================================================================
    private void initLifeCompanionStage() {
        ProfileConfigSelectionController.INSTANCE.initStage(new ProfileConfigSelectionStage(stage));
        GeneralConfigurationController.INSTANCE.initStage(new GeneralConfigurationStage(stage));
    }

    private void loadAndShowStageAndLoadingScene() {
        // Main stage configuration
        this.stage.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.stage.initStyle(StageStyle.DECORATED);
        this.stage.setTitle(StageUtils.getStageDefaultTitle());
        this.stage.setWidth(UserConfigurationController.INSTANCE.mainFrameWidthProperty().get());
        this.stage.setHeight(UserConfigurationController.INSTANCE.mainFrameHeightProperty().get());
        this.stage.setMaximized(UserConfigurationController.INSTANCE.launchMaximizedProperty().get());
        this.stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.stage.setOnCloseRequest((we) -> {
            we.consume();
            GlobalActions.HANDLER_CANCEL.handle(we);
        });

        // Show loading stage
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_LOADING_WINDOW)) {
            loadingStage = new LoadingStage();
            loadingStage.show();
        }

        // Close the splashscreen (AWT splashscreen)
        final SplashScreen splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen != null) {
            splashScreen.close();
        }
        LOGGER.info("Loading scene initialized and shown after {}s", (System.currentTimeMillis() - startAt) / 1000.0);
    }
    //========================================================================

    // ANALYSE ARGS
    //========================================================================
    private Pair<LCConfigurationDescriptionI, LCConfigurationI> getConfigurationToLaunchFor(LCProfileI profile) throws Exception {
        // Configuration to load from arg (already in profile)
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION)) {
            final String configIdToSearch = GlobalRuntimeConfigurationController.INSTANCE.getParameters(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION).get(1);
            final LCConfigurationDescriptionI configurationFromId = profile.getConfiguration()
                    .stream()
                    .filter(configDesc -> StringUtils.isEquals(configIdToSearch, configDesc.getConfigurationId()))
                    .findAny()
                    .orElse(null);
            if (configurationFromId != null) {
                return loadConfigurationFromProfile(profile, configurationFromId);
            }
        }
        // Configuration to load (not in profile, should import then open)
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DIRECT_IMPORT_AND_LAUNCH_CONFIGURATION)) {
            final String configFilePath = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.DIRECT_IMPORT_AND_LAUNCH_CONFIGURATION);
            final ConfigurationImportTask customConfigurationImport = IOHelper.createCustomConfigurationImport(IOUtils.getTempDir("import-and-launch"), new File(configFilePath), true);
            return ThreadUtils.executeInCurrentThread(customConfigurationImport);
        }
        final LCConfigurationDescriptionI currentDefaultConfiguration = profile.getCurrentDefaultConfiguration();
        return currentDefaultConfiguration != null ? loadConfigurationFromProfile(profile, currentDefaultConfiguration) : null;
    }

    private Pair<LCConfigurationDescriptionI, LCConfigurationI> loadConfigurationFromProfile(LCProfileI profile, LCConfigurationDescriptionI configurationDescription) throws Exception {
        final LCConfigurationI configuration = ThreadUtils.executeInCurrentThread(IOHelper.createLoadConfigurationTask(configurationDescription, profile));
        return new Pair<>(configurationDescription, configuration);
    }

    private String getProfileIDToSelect() {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION)) {
            return GlobalRuntimeConfigurationController.INSTANCE.getParameters(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION).get(0);
        }
        return LCStateController.INSTANCE.getLastSelectedProfileID();
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
