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
package org.lifecompanion.controller.userconfiguration;

import javafx.beans.property.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.ui.app.userconfiguration.UserConfigStage;
import org.lifecompanion.ui.app.userconfiguration.UserConfigurationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * This class represent the software configuration that can be changed by a user.<br>
 * Each configuration is a property because it could exist a UI to change this values.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UserConfigurationController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(UserConfigurationController.class);

    //Properties name
    private static final String PROP_LANGUAGE = "language", PROP_FRAME_WIDTH = "frame-width", PROP_FRAME_HEIGHT = "frame-height",
            PROP_LAUNCH_MAXIMIZED = "start-maximized", PROP_SELECTION_STROKE_SIZE = "selection-stroke-size",
            PROP_SELECTION_DASH_SIZE = "selection-dash-size", PROP_TIPS_STARTUP = "show-tips-on-startup",
            PROP_UNSAVED_CHANGE_THRESHOLD = "unsaved-changes-in-config-warning-threshold",
            PROP_SCREEN_INDEX = "screen-index",
            PROP_RECORD_SEND_SESSION_STATS = "record-and-send-session-stats", PROP_ENABLE_AUTO_VK_SHOW = "auto-virtual-keyboard-show", PROP_ENABLE_JPD_EASTER_EGG = "enable-jpd-easter-egg",
            PROP_DISABLE_EXIT_USE_MODE = "disable-exit-use-mode", PROP_SECURE_GO_EDIT_MODE = "secure-go-edit-mode", PROP_AUTO_CONFIG_PROFILE_BACKUP = "auto-config-profile-backup",
            PROP_AUTO_SELECT_IMAGES = "auto-select-images", PROP_ENABLE_SPEECH_OPTIMIZATION = "enable-speech-optimization", PROP_DEFAULT_TEXT_POSITION_ON_IMAGE_SELECTION = "default-text-position-on-image-selection",
            PROP_DISABLE_FULLSCREEN_SHORTCUT = "disable-fullscreen-shortcut", PROP_ENABLE_PREVIOUS_CONFIGURATION_SHORTCUT = "enable-previous-configuration-shortcut", PROP_ENABLE_EXPORT_MOBILE_CONFIGURATION = "enable-export-mobile-configuration";


    //Properties
    private final StringProperty userLanguage;
    private final IntegerProperty mainFrameWidth, mainFrameHeight;
    private final BooleanProperty launchMaximized;
    private final DoubleProperty selectionStrokeSize, selectionDashSize;
    private final BooleanProperty showTipsOnStartup;
    private final IntegerProperty unsavedChangeInConfigurationThreshold;
    private final transient BooleanProperty launchLCSystemStartup;
    private final BooleanProperty recordAndSendSessionStats;
    private final BooleanProperty autoVirtualKeyboardShow;
    private final BooleanProperty enableJPDRetirementEasterEgg;
    private final BooleanProperty disableExitInUseMode;
    private final BooleanProperty secureGoToEditMode;
    private final BooleanProperty autoConfigurationProfileBackup;
    private final BooleanProperty autoSelectImages;
    private final BooleanProperty enableSpeechOptimization;
    private final BooleanProperty disableFullscreenShortcut;
    private final BooleanProperty enablePreviousConfigurationShortcut;
    private final BooleanProperty enableExportMobileConfiguration;
    private final ObjectProperty<TextPosition> defaultTextPositionOnImageSelection;
    private IntegerProperty screenIndex;
    private UserConfigurationView userConfigurationView;

    UserConfigurationController() {
        this.screenIndex = new SimpleIntegerProperty(0);
        this.userLanguage = new SimpleStringProperty("fr");
        this.mainFrameWidth = new SimpleIntegerProperty(1200);
        this.mainFrameHeight = new SimpleIntegerProperty(800);
        this.launchMaximized = new SimpleBooleanProperty(true);
        this.showTipsOnStartup = new SimpleBooleanProperty(true);
        this.selectionStrokeSize = new SimpleDoubleProperty(3.0);
        this.selectionDashSize = new SimpleDoubleProperty(5.0);
        this.launchLCSystemStartup = new SimpleBooleanProperty(false);
        this.recordAndSendSessionStats = new SimpleBooleanProperty(false);
        this.unsavedChangeInConfigurationThreshold = new SimpleIntegerProperty(80);
        this.autoVirtualKeyboardShow = new SimpleBooleanProperty(true);
        this.enableJPDRetirementEasterEgg = new SimpleBooleanProperty(true);
        this.disableExitInUseMode = new SimpleBooleanProperty(false);
        this.secureGoToEditMode = new SimpleBooleanProperty(false);
        this.autoSelectImages = new SimpleBooleanProperty(false);
        this.enableSpeechOptimization = new SimpleBooleanProperty(true);
        this.autoConfigurationProfileBackup = new SimpleBooleanProperty(true);
        this.disableFullscreenShortcut = new SimpleBooleanProperty(false);
        this.enablePreviousConfigurationShortcut = new SimpleBooleanProperty(true);
        this.enableExportMobileConfiguration = new SimpleBooleanProperty(false);
        this.defaultTextPositionOnImageSelection = new SimpleObjectProperty<>(TextPosition.BOTTOM);
    }

    private File getConfigFile() {
        return new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + File.separator + LCConstant.CONFIG_FILE_NAME);
    }

    public UserConfigurationView getUserConfigurationView() {
        if (this.userConfigurationView == null) {
            Stage userConfigStage = new UserConfigStage(AppModeController.INSTANCE.getEditModeContext().getStage());
            this.userConfigurationView = new UserConfigurationView(userConfigStage);
            Scene settingScene = new Scene(this.userConfigurationView);
            SystemVirtualKeyboardController.INSTANCE.registerScene(settingScene);
            SessionStatsController.INSTANCE.registerScene(settingScene);
            userConfigStage.setScene(settingScene);
        }
        return userConfigurationView;
    }

    /**
     * Load the configuration values from configuration file
     *
     * @throws IOException when reading problem happen
     */
    public void load() throws IOException {
        Properties prop = new Properties();
        File configFile = getConfigFile();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            prop.load(fis);
            if (prop.containsKey(UserConfigurationController.PROP_LANGUAGE)) {
                this.userLanguage.set(prop.getProperty(UserConfigurationController.PROP_LANGUAGE));
            }
            if (prop.containsKey(UserConfigurationController.PROP_FRAME_WIDTH)) {
                this.mainFrameWidth.set(Integer.parseInt(prop.getProperty(UserConfigurationController.PROP_FRAME_WIDTH)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_FRAME_HEIGHT)) {
                this.mainFrameHeight.set(Integer.parseInt(prop.getProperty(UserConfigurationController.PROP_FRAME_HEIGHT)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_SCREEN_INDEX)) {
                this.screenIndex.set(Integer.parseInt(prop.getProperty(UserConfigurationController.PROP_SCREEN_INDEX)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_LAUNCH_MAXIMIZED)) {
                this.launchMaximized.set(Boolean.parseBoolean(prop.getProperty(UserConfigurationController.PROP_LAUNCH_MAXIMIZED)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_TIPS_STARTUP)) {
                this.showTipsOnStartup.set(Boolean.parseBoolean(prop.getProperty(UserConfigurationController.PROP_TIPS_STARTUP)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_SELECTION_STROKE_SIZE)) {
                this.selectionStrokeSize.set(Double.parseDouble(prop.getProperty(UserConfigurationController.PROP_SELECTION_STROKE_SIZE)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_SELECTION_DASH_SIZE)) {
                this.selectionDashSize.set(Double.parseDouble(prop.getProperty(UserConfigurationController.PROP_SELECTION_DASH_SIZE)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_UNSAVED_CHANGE_THRESHOLD)) {
                this.unsavedChangeInConfigurationThreshold.set(Integer.parseInt(prop.getProperty(PROP_UNSAVED_CHANGE_THRESHOLD)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_RECORD_SEND_SESSION_STATS)) {
                this.recordAndSendSessionStats.set(Boolean.parseBoolean(prop.getProperty(PROP_RECORD_SEND_SESSION_STATS)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_ENABLE_AUTO_VK_SHOW)) {
                this.autoVirtualKeyboardShow.set(Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_AUTO_VK_SHOW)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_ENABLE_JPD_EASTER_EGG)) {
                this.enableJPDRetirementEasterEgg.set(Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_JPD_EASTER_EGG)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_DISABLE_EXIT_USE_MODE)) {
                this.disableExitInUseMode.set(Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_EXIT_USE_MODE)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_SECURE_GO_EDIT_MODE)) {
                this.secureGoToEditMode.set(Boolean.parseBoolean(prop.getProperty(PROP_SECURE_GO_EDIT_MODE)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_AUTO_CONFIG_PROFILE_BACKUP)) {
                this.autoConfigurationProfileBackup.set(Boolean.parseBoolean(prop.getProperty(PROP_AUTO_CONFIG_PROFILE_BACKUP)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_AUTO_SELECT_IMAGES)) {
                this.autoSelectImages.set(Boolean.parseBoolean(prop.getProperty(PROP_AUTO_SELECT_IMAGES)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_ENABLE_SPEECH_OPTIMIZATION)) {
                this.enableSpeechOptimization.set(Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_SPEECH_OPTIMIZATION)));
            }
            if (prop.containsKey(UserConfigurationController.PROP_DEFAULT_TEXT_POSITION_ON_IMAGE_SELECTION)) {
                this.defaultTextPositionOnImageSelection.set(TextPosition.valueOf(prop.getProperty(PROP_DEFAULT_TEXT_POSITION_ON_IMAGE_SELECTION)));
            }
            if (prop.containsKey(PROP_DISABLE_FULLSCREEN_SHORTCUT)) {
                this.disableFullscreenShortcut.set(Boolean.parseBoolean(prop.getProperty(PROP_DISABLE_FULLSCREEN_SHORTCUT)));
            }
            if (prop.containsKey(PROP_ENABLE_PREVIOUS_CONFIGURATION_SHORTCUT)) {
                this.enablePreviousConfigurationShortcut.set(Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_PREVIOUS_CONFIGURATION_SHORTCUT)));
            }
            if (prop.containsKey(PROP_ENABLE_EXPORT_MOBILE_CONFIGURATION)) {
                this.enableExportMobileConfiguration.set(Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_EXPORT_MOBILE_CONFIGURATION)));
            }
        } catch (FileNotFoundException e) {
            this.LOGGER.warn("Configuration file {} not found", configFile, e);
        }
    }


    /**
     * Save the configuration values to configuration file
     *
     * @throws IOException if values can't be saved
     */
    public void save() throws IOException {
        File configFile = getConfigFile();
        Properties prop = new Properties();
        prop.setProperty(UserConfigurationController.PROP_LANGUAGE, "" + this.userLanguage.get());
        prop.setProperty(UserConfigurationController.PROP_FRAME_WIDTH, "" + this.mainFrameWidth.get());
        prop.setProperty(UserConfigurationController.PROP_FRAME_HEIGHT, "" + this.mainFrameHeight.get());
        prop.setProperty(UserConfigurationController.PROP_SCREEN_INDEX, "" + this.screenIndex.get());
        prop.setProperty(UserConfigurationController.PROP_LAUNCH_MAXIMIZED, "" + this.launchMaximized.get());
        prop.setProperty(UserConfigurationController.PROP_RECORD_SEND_SESSION_STATS, "" + this.recordAndSendSessionStats.get());
        prop.setProperty(UserConfigurationController.PROP_TIPS_STARTUP, "" + this.showTipsOnStartup.get());
        prop.setProperty(UserConfigurationController.PROP_SELECTION_STROKE_SIZE, "" + this.selectionStrokeSize.get());
        prop.setProperty(UserConfigurationController.PROP_SELECTION_DASH_SIZE, "" + this.selectionDashSize.get());
        prop.setProperty(PROP_UNSAVED_CHANGE_THRESHOLD, "" + this.unsavedChangeInConfigurationThreshold.get());
        prop.setProperty(PROP_ENABLE_AUTO_VK_SHOW, "" + this.autoVirtualKeyboardShow.get());
        prop.setProperty(PROP_ENABLE_JPD_EASTER_EGG, "" + this.enableJPDRetirementEasterEgg.get());
        prop.setProperty(PROP_SECURE_GO_EDIT_MODE, "" + this.secureGoToEditMode.get());
        prop.setProperty(PROP_DISABLE_EXIT_USE_MODE, "" + this.disableExitInUseMode.get());
        prop.setProperty(PROP_AUTO_CONFIG_PROFILE_BACKUP, "" + this.autoConfigurationProfileBackup.get());
        prop.setProperty(PROP_AUTO_SELECT_IMAGES, "" + this.autoSelectImages.get());
        prop.setProperty(PROP_ENABLE_SPEECH_OPTIMIZATION, "" + this.enableSpeechOptimization.get());
        prop.setProperty(PROP_DEFAULT_TEXT_POSITION_ON_IMAGE_SELECTION, "" + this.defaultTextPositionOnImageSelection.get());
        prop.setProperty(PROP_DISABLE_FULLSCREEN_SHORTCUT, "" + this.disableFullscreenShortcut.get());
        prop.setProperty(PROP_ENABLE_PREVIOUS_CONFIGURATION_SHORTCUT, "" + this.enablePreviousConfigurationShortcut.get());
        prop.setProperty(PROP_ENABLE_EXPORT_MOBILE_CONFIGURATION, "" + this.enableExportMobileConfiguration.get());
        IOUtils.createParentDirectoryIfNeeded(configFile);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            prop.store(fos, LCConstant.NAME + " user configuration file");
        }
        LOGGER.info("User configuration file saved to {}", configFile);
    }

    public StringProperty userLanguageProperty() {
        return this.userLanguage;
    }

    public IntegerProperty mainFrameWidthProperty() {
        return this.mainFrameWidth;
    }

    public IntegerProperty mainFrameHeightProperty() {
        return this.mainFrameHeight;
    }

    public IntegerProperty screenIndexProperty() {
        return screenIndex;
    }

    public BooleanProperty launchMaximizedProperty() {
        return this.launchMaximized;
    }

    public DoubleProperty selectionStrokeSizeProperty() {
        return this.selectionStrokeSize;
    }

    public DoubleProperty selectionDashSizeProperty() {
        return this.selectionDashSize;
    }

    public BooleanProperty showTipsOnStartupProperty() {
        return this.showTipsOnStartup;
    }

    public IntegerProperty unsavedChangeInConfigurationThresholdProperty() {
        return unsavedChangeInConfigurationThreshold;
    }

    public BooleanProperty launchLCSystemStartupProperty() {
        return launchLCSystemStartup;
    }

    public BooleanProperty recordAndSendSessionStatsProperty() {
        return recordAndSendSessionStats;
    }

    public BooleanProperty autoVirtualKeyboardShowProperty() {
        return autoVirtualKeyboardShow;
    }

    public BooleanProperty enableJPDRetirementEasterEggProperty() {
        return enableJPDRetirementEasterEgg;
    }

    public BooleanProperty disableExitInUseModeProperty() {
        return disableExitInUseMode;
    }

    public BooleanProperty secureGoToEditModeProperty() {
        return secureGoToEditMode;
    }

    public BooleanProperty autoConfigurationProfileBackupProperty() {
        return autoConfigurationProfileBackup;
    }

    public BooleanProperty autoSelectImagesProperty() {
        return autoSelectImages;
    }

    public BooleanProperty enableSpeechOptimizationProperty() {
        return enableSpeechOptimization;
    }

    public ObjectProperty<TextPosition> defaultTextPositionOnImageSelectionProperty() {
        return defaultTextPositionOnImageSelection;
    }

    public BooleanProperty disableFullscreenShortcutProperty() {
        return disableFullscreenShortcut;
    }

    public BooleanProperty enablePreviousConfigurationShortcutProperty() {
        return enablePreviousConfigurationShortcut;
    }

    public BooleanProperty enableExportMobileConfigurationProperty() {
        return enableExportMobileConfiguration;
    }
}
