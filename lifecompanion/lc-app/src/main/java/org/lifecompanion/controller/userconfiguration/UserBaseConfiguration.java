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
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
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
public enum UserBaseConfiguration {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(UserBaseConfiguration.class);

    //Properties name
    private static final String PROP_LANGUAGE = "language", PROP_FRAME_WIDTH = "frame-width", PROP_FRAME_HEIGHT = "frame-height",
            PROP_LAUNCH_MAXIMIZED = "start-maximized", PROP_SELECTION_STROKE_SIZE = "selection-stroke-size",
            PROP_SELECTION_DASH_SIZE = "selection-dash-size", PROP_TIPS_STARTUP = "show-tips-on-startup",
            PROP_UNSAVED_CHANGE_THRESHOLD = "unsaved-changes-in-config-warning-threshold",
            PROP_RECORD_SEND_SESSION_STATS = "record-and-send-session-stats", PROP_ENABLE_AUTO_VK_SHOW = "auto-virtual-keyboard-show";


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

    UserBaseConfiguration() {
        this.userLanguage = new SimpleStringProperty(this, "userLanguage", "fr");
        this.mainFrameWidth = new SimpleIntegerProperty(this, "mainFrameWidth", 1200);
        this.mainFrameHeight = new SimpleIntegerProperty(this, "mainFrameHeight", 800);
        this.launchMaximized = new SimpleBooleanProperty(this, "launchMaximized", true);
        this.showTipsOnStartup = new SimpleBooleanProperty(this, "showTipsOnStartup", true);
        this.selectionStrokeSize = new SimpleDoubleProperty(this, "selectionStrokeSize", 2.0);
        this.selectionDashSize = new SimpleDoubleProperty(this, "selectionDashSize", 5.0);
        this.launchLCSystemStartup = new SimpleBooleanProperty(this, "launchLCSystemStartup", false);
        this.recordAndSendSessionStats = new SimpleBooleanProperty(this, "recordAndSendSessionStats", false);
        this.unsavedChangeInConfigurationThreshold = new SimpleIntegerProperty(this, "unsavedChangeInConfigurationThreshold", 80);
        this.autoVirtualKeyboardShow = new SimpleBooleanProperty(this, "autoVirtualKeyboardShow", true);
    }

    private File getConfigFile() {
        return new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + File.separator + LCConstant.CONFIG_FILE_NAME);
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
            if (prop.containsKey(UserBaseConfiguration.PROP_LANGUAGE)) {
                this.userLanguage.set(prop.getProperty(UserBaseConfiguration.PROP_LANGUAGE));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_FRAME_WIDTH)) {
                this.mainFrameWidth.set(Integer.parseInt(prop.getProperty(UserBaseConfiguration.PROP_FRAME_WIDTH)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_FRAME_HEIGHT)) {
                this.mainFrameHeight.set(Integer.parseInt(prop.getProperty(UserBaseConfiguration.PROP_FRAME_HEIGHT)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_LAUNCH_MAXIMIZED)) {
                this.launchMaximized.set(Boolean.parseBoolean(prop.getProperty(UserBaseConfiguration.PROP_LAUNCH_MAXIMIZED)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_TIPS_STARTUP)) {
                this.showTipsOnStartup.set(Boolean.parseBoolean(prop.getProperty(UserBaseConfiguration.PROP_TIPS_STARTUP)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_SELECTION_STROKE_SIZE)) {
                this.selectionStrokeSize.set(Double.parseDouble(prop.getProperty(UserBaseConfiguration.PROP_SELECTION_STROKE_SIZE)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_SELECTION_DASH_SIZE)) {
                this.selectionDashSize.set(Double.parseDouble(prop.getProperty(UserBaseConfiguration.PROP_SELECTION_DASH_SIZE)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_UNSAVED_CHANGE_THRESHOLD)) {
                this.unsavedChangeInConfigurationThreshold.set(Integer.parseInt(prop.getProperty(PROP_UNSAVED_CHANGE_THRESHOLD)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_RECORD_SEND_SESSION_STATS)) {
                this.recordAndSendSessionStats.set(Boolean.parseBoolean(prop.getProperty(PROP_RECORD_SEND_SESSION_STATS)));
            }
            if (prop.containsKey(UserBaseConfiguration.PROP_ENABLE_AUTO_VK_SHOW)) {
                this.autoVirtualKeyboardShow.set(Boolean.parseBoolean(prop.getProperty(PROP_ENABLE_AUTO_VK_SHOW)));
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
        prop.setProperty(UserBaseConfiguration.PROP_LANGUAGE, "" + this.userLanguage.get());
        prop.setProperty(UserBaseConfiguration.PROP_FRAME_WIDTH, "" + this.mainFrameWidth.get());
        prop.setProperty(UserBaseConfiguration.PROP_FRAME_HEIGHT, "" + this.mainFrameHeight.get());
        prop.setProperty(UserBaseConfiguration.PROP_LAUNCH_MAXIMIZED, "" + this.launchMaximized.get());
        prop.setProperty(UserBaseConfiguration.PROP_RECORD_SEND_SESSION_STATS, "" + this.recordAndSendSessionStats.get());
        prop.setProperty(UserBaseConfiguration.PROP_TIPS_STARTUP, "" + this.showTipsOnStartup.get());
        prop.setProperty(UserBaseConfiguration.PROP_SELECTION_STROKE_SIZE, "" + this.selectionStrokeSize.get());
        prop.setProperty(UserBaseConfiguration.PROP_SELECTION_DASH_SIZE, "" + this.selectionDashSize.get());
        prop.setProperty(PROP_UNSAVED_CHANGE_THRESHOLD, "" + this.unsavedChangeInConfigurationThreshold.get());
        prop.setProperty(PROP_ENABLE_AUTO_VK_SHOW, "" + this.autoVirtualKeyboardShow.get());
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
}
