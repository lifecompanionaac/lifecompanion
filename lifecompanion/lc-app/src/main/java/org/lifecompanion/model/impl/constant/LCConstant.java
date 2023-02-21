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
package org.lifecompanion.model.impl.constant;

import org.lifecompanion.framework.commons.ApplicationConstant;

import java.io.File;

/**
 * Keep all values that will be modified only in code.<br>
 * This values will not be modified by configuration.<br>
 * All values in this class must be "public final static"
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConstant {
    public final static String EXT_PATH_DATA = "." + File.separator + "data" + File.separator;
    public final static long UPDATE_CHECK_DELAY = 1000 * 60 * 60 * 24 * 3;//Every 3 days

    // Class part : "Software constant"
    // ========================================================================
    public final static String LC_ICON_PATH = "lifecompanion_icon_64px.png";
    public final static String LC_BIG_ICON_PATH = "lifecompanion_title_icon_400px.png";
    public final static String LC_BIG_TITLE_ONLY_ICON_PATH = "lifecompanion_title_only_icon_600px.png";
    public final static String LC_BIG_ICON_ONLY_PATH = "lifecompanion_icon_128px.png";
    public final static String LC_COPYRIGHT_ICON_PATH = "lifecompanion_copyright_300.png";
    public final static String CONFIG_FILE_EXTENSION = "lcc";
    public final static String PROFILE_FILE_EXTENSION = "lcp";
    public final static String KEYLIST_FILE_EXTENSION = "lckl";

    public static final double SETTINGS_STAGE_WIDTH = 700.0, SETTINGS_STAGE_HEIGHT = 500.0;
    public static final double TIPS_STAGE_WIDTH = 1100.0, TIPS_STAGE_HEIGHT = 700.0;
    public static final double DEV_STAGE_WIDTH = 1200.0, DEV_STAGE_HEIGHT = 250.0;
    public static final int DEFAULT_COLOR_REPLACE_THRESHOLD = 10;

    public static final String ARG_LAUNCH_CONFIG = "-directLaunchOn";
    public static final String ARG_IMPORT_LAUNCH_CONFIG = "-directImportAndLaunch";

    // ========================================================================

    // Class part : "Files constant"
    // ========================================================================
    public final static String EXT_PATH_DATA_TIPS_IMAGES = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "tips-medias" + File.separator;
    public final static String EXT_PATH_FUS_CHAR_PREDICTOR = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "fus-char-predictor" + File.separator;
    public final static String EXT_PATH_DEFAULT_CONFIGURATIONS = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "default-configurations" + File.separator;
    public final static String EXT_PATH_DEFAULT_CONFIGURATIONS_EXTRACTED = EXT_PATH_DEFAULT_CONFIGURATIONS + "extracted" + File.separator;
    public final static String EXT_PATH_LC_STATE_FILENAME = "lifecompanion.xml";
    public final static String EXT_PATH_LAST_UPDATE_CHECK = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "last-update";
    // ========================================================================

    // Class part : "Internal path"
    // ========================================================================
    public final static String INT_PATH_ICONS = "/icons/";
    public final static String INT_PATH_USE_ACTION_ICON_PATH = "use-actions/categories/";
    public final static String INT_PATH_USE_USE_EVENT_ICON_PATH = "use-events/categories/";
    public final static String[] INT_PATH_TEXT_FILES = {"_translations.xml"};
    // ========================================================================

    // Class part : "CSS Style"
    // ========================================================================
    public final static String[] CSS_USE_MODE = {"style/lifecompanion_use-mode.css"};
    public final static String[] CSS_STYLE_PATH = {"style/lifecompanion.css", "style/custom_modena.css", CSS_USE_MODE[0]};
    public final static String[] CSS_NOTIFICATION_STYLE_PATH = {"style/lifecompanion_notification.css"};
    // ========================================================================

    // THREADING
    // ========================================================================
    public final static int CONFIGURATION_ACTION_POOL_SIZE = 4;
    public final static int USE_ACTION_POOL_SIZE = 4;
    public final static int NOTIFICATION_HIDE_POOL_SIZE = 4;
    // ========================================================================

    // CONFIG FILES
    //========================================================================
    public final static String CONFIG_FILE_NAME = "configuration.cfg";
    //========================================================================

    // CONFIGURATION LOAD/SAVE
    // ========================================================================
    public final static String PROFILE_DIRECTORY = "profiles";
    public final static String CONFIGURATION_DIRECTORY = "configurations";
    public final static String USER_COMP_DIRECTORY = "user-components";
    public final static String CONFIGURATION_RESOURCE_DIRECTORY = "resources";
    public final static String CONFIGURATION_IMAGE_DIRECTORY = "images";
    public final static String CONFIGURATION_KEYLIST_DIRECTORY = "keylist";
    public final static String CONFIGURATION_SEQUENCE_DIRECTORY = "sequence";

    public final static String CONFIGURATION_USE_INFO_DEFAULT_DIRECTORY = "use-info-default";
    public static final String BACKUP_DIR = "backup";

    public final static String CONFIGURATION_XML_NAME = "lifecompanion-configuration.xml";
    public final static String KEYLIST_XML_NAME = "lifecompanion-keylist.xml";
    public final static String SEQUENCE_XML_NAME = "lifecompanion-sequence.xml";
    public final static String USER_COMP_XML_NAME = "component.xml";
    public final static String USER_COMP_DESCRIPTION_XML_NAME = "component-description.xml";
    public final static String CONFIGURATION_USE_INFO_XML_NAME = "lifecompanion-use-information.xml";
    public final static String PROFILE_XML_NAME = "profile.xml";
    public final static String CONFIGURATION_DESCRIPTION_XML_NAME = "lifecompanion-configuration-description.xml";
    public final static String CONFIGURATION_SCREENSHOT_NAME = "preview.png";
    public final static String USERCOMP_SCREENSHOT_NAME = "preview.png";
    public final static String CONFIGURATION_RESOURCE_XML = "resources.xml";
    // ========================================================================

    // Class part : "Images extension"
    // ========================================================================
    public static final String[] IMAGE_EXTENSIONS = {"png", "gif", "jpg", "jpeg"};
    // ========================================================================

    // Class part : "Base UI"
    // ========================================================================
    /**
     * The space around each component in configuration (to avoid component that are directly on configuration border)
     */
    public final static double CONFIG_ROOT_COMPONENT_GAP = 2.0;

    /**
     * The minimum size for each configuration root component
     */
    public final static double MIN_SIZE_COMPONENT = 20.0;

    public final static long TOOLTIP_SHOW_DELAY = 1500;
    public final static long TOOLTIP_DURATION = 25000;
    public final static long TOOLTIP_CLOSE_DELAY = 200;

    public final static int GO_TO_CONFIG_MODE_DELAY = 30;
    public final static int DOUBLE_LAUNCH_DISPLAY_DELAY = 10;
    public final static int WARNING_EXIT_DISABLED_DELAY = 10;

    public final static long TRAINING_DIALOG_SHOW_INTERVAL = 1000 * 60 * 60 * 24 * 20; // 20 DAYS
    public final static long TRAINING_DIALOG_SHOW_DELAY = 20_000; // 20 SECONDS
    // ========================================================================

    // EXTERNAL LIB/PROJECTS
    // ========================================================================
    public final static String SAPI_VOICE_SYNTHESIZER2_EXE = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "voice-synthesizer" + File.separator + "win-sapi-voicesynthesizer-gap.exe";
    public final static String FUS_CHAR_PREDICTION_FILE = LCConstant.EXT_PATH_FUS_CHAR_PREDICTOR + File.separator + "char-predictions.bin";

    public static final String WIN_INPUT_LISTENER_EXE = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "win-input-gap" + File.separator + "win-input-listener.exe";
    public static final String WIN_INPUT_SENDER_EXE = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "win-input-gap" + File.separator + "win-input-sender.exe";
    // ========================================================================


    // CHECKED
    //========================================================================
    public final static String NAME = "LifeCompanion";
    public final static String URL_PATH_CHANGELOG = "/categories/documentations/lifecompanion-changelog";
    public final static String URL_PATH_GET_STARTED = "/categories/documentations/get-started-guide";
    public final static String URL_TRAININGS = "/formations";
    //========================================================================

    // IMAGE DICTIONARIES
    //========================================================================
    public static final String RESOURCES_DIR_NAME = File.separator + "resources" + File.separator;
    public static final String IMAGE_RESOURCES_DIR_NAME = RESOURCES_DIR_NAME + "images" + File.separator;
    public static final String WEBCAM_CAPTURE_DIR_NAME = RESOURCES_DIR_NAME + "webcam-capture" + File.separator;
    public static final String CLIPBOARD_CAPTURE_DIR_NAME = RESOURCES_DIR_NAME + "clipboard-capture" + File.separator;
    public static final String IMPORTED_IMAGE_DIR_NAME = RESOURCES_DIR_NAME + "imported-images" + File.separator;
    public static final String DEFAULT_IMAGE_DICTIONARIES = ApplicationConstant.DIR_NAME_APPLICATION_DATA + IMAGE_RESOURCES_DIR_NAME;
    public static final String THUMBNAIL_DIR_NAME = "thumbnails";
    public static final String DICTIONARY_NAME_USER_IMAGES = "user-images.json";
    public static final String DICTIONARY_NAME_CONFIGURATION_IMAGES = "configuration-images.json";
    //========================================================================

    // PLUGINS
    //========================================================================
    public static final String PATH_PLUGIN_ROOT_DIR = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "plugins" + File.separator;
    public static final String PATH_PLUGIN_CP_FILE = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "plugins" + File.separator + "plugin-classpath";
    public static final String PATH_PLUGIN_JAR_DIR = PATH_PLUGIN_ROOT_DIR + "jars" + File.separator;
    public static final String PATH_PLUGIN_UPDATE_DIR = PATH_PLUGIN_ROOT_DIR + "updates" + File.separator;
    public static final String PATH_PLUGIN_DATA_DIR = PATH_PLUGIN_ROOT_DIR + "data" + File.separator;
    public static final String PATH_PLUGIN_NEXT_UPDATE_DIR = PATH_PLUGIN_ROOT_DIR + "next-update" + File.separator;
    public final static String EXT_PATH_LAST_PLUGIN_UPDATE_CHECK = PATH_PLUGIN_UPDATE_DIR + File.separator + "last-update";
    //========================================================================

    // UPDATE
    //========================================================================
    public static final String PATH_UPDATE_STAT_CACHE = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "update-stats" + File.separator;
    //========================================================================

    // STATS
    //========================================================================
    public static final String PATH_SESSION_STATS_CACHE = ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "session-stats" + File.separator;
    public static final String SESSION_DATA_FILENAME = "data.json";
    //========================================================================

}
