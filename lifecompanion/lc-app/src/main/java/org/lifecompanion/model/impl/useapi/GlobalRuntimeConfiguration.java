/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2024 CMRRF KERPAPE (Lorient, France)
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
package org.lifecompanion.model.impl.useapi;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.useapi.GlobalRuntimeConfigurationI;
import org.lifecompanion.model.api.useapi.GlobalRuntimeConfigurationType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum GlobalRuntimeConfiguration implements GlobalRuntimeConfigurationI {
    // Launch modes
    DIRECT_LAUNCH_CONFIGURATION(
            "directLaunchOn",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "profileId configurationId",
            "Try to launch LifeCompanion directly in use mode on a given profile and configuration combination." +
                    " Profile and configuration should have already been loaded in LifeCompanion on a previous launch.",
            "4aab2626-6b72-4e5e-8318-777c3684e8a3 9e94f3c0-e2de-4afb-8b65-8b07a994b3d4",
            2
    ),
    DIRECT_IMPORT_AND_LAUNCH_CONFIGURATION(
            "directImportAndLaunch",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "configurationFilePath",
            "Try to import a configuration file and launch it directly in use mode. " +
                    "The given configuration will not be added to profile. This can be useful to run LifeCompanion as a \"configuration reader only\"",
            "C:\\lifecompanion\\my-configuration.lcc",
            1
    ),

    // Disable features/config/etc
    DISABLE_SWITCH_TO_EDIT_MODE(
            "disableSwitchToEditMode",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Disable the switch to edit mode when the use mode is launched." +
                    " This will hide the edit mode button and disable keyboard shortcuts or any action that could cause a switch to edit mode. " +
                    "Note that this doesn't disable the edit mode itself : on the first launch, LifeCompanion can be used in edit mode."
    ),
    DISABLE_UPDATES(
            "disableUpdates",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Will disable all the update checking process (for both app and plugins). " +
                    "Will not try to reach the update server at all."
    ),
    DISABLE_VIRTUAL_KEYBOARD(
            "disableVirtualKeyboard",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Disable every virtual keyboard mechanism, " +
                    "if enabled, will consider any configuration as a classic configuration even if the virtual keyboard parameter is enabled on it."
    ),
    DISABLE_VIRTUAL_MOUSE(
            "disableVirtualMouse",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Disable every virtual mouse mechanism, " +
                    "if enabled, will ignore any actions that could enable/show the virtual mouse."
    ),
    DISABLE_EXTERNAL_ACTIONS(
            "disableExternalActions",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Disable any actions that could interact with the system : printing, opening web pages, opening files, running commands, etc."
    ),
    DISABLE_EXIT(
            "disableExit",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Disable the ability for an user to exit LifeCompanion from use mode by itself. LifeCompanion will still be able to be closed by external events."
    ),
    DISABLE_LOADING_WINDOW(
            "disableLoadingWindow",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Don't show any loading window on LifeCompanion startup. Even if this parameter is enabled, the first splashscreen on launch will still be displayed"
    ),
    DISABLE_SELECTION_AUTOSTART(
            "disableSelectionAutostart",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "If enabled, the selection mode will not automatically start on each started configuration. It will be able to be activated only when the control server is enabled and `selection/start` is called."
    ),
    // TODO : force sound to 100% on voice, sound, etc.

    // Window configuration
    DISABLE_WINDOW_FULLSCREEN(
            "disableFullscreen",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Disable the user ability to switch from decorated/fullscreen mode on the use mode window. " +
                    "Will disable the fullscreen button, but also the keyboard shortcut"
    ),
    FORCE_WINDOW_UNDECORATED(
            "forceWindowUndecorated",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Will force the use mode window to be \"undecorated\" " +
                    "as stated in [JavaFX documentation](https://openjfx.io/javadoc/18/javafx.graphics/javafx/stage/Stage.html) on stage style. "
    ),
    FORCE_WINDOW_SIZE(
            "forceWindowSize",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "width height", "Will force the use mode window to be as the specified size (in pixel). " +
            "The given size will respect the screen scaling. The user will then not be able to resize the use mode window.",
            "1200 800",
            2
    ),
    FORCE_WINDOW_LOCATION(
            "forceWindowLocation",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "x y",
            "Will force the use mode window to be at a specific location on the screen (in pixel, from top left corner). The given location will respect the screen scaling.",
            "0 0",
            2
    ),
    FORCE_WINDOW_OPACITY(
            "forceWindowOpacity",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "opacity",
            "Will force the use mode window to keep a specific opacity regardless the configuration set on it for its opacity. Opacity should range between 0.0 (transparent) to 1.0 (opaque).",
            "0.8",
            1
    ),
    DISABLE_WINDOW_ALWAYS_ON_TOP(
            "disableWindowAlwaysOnTop",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Will force the use mode window to not always be on top of the other window. This change the default LifeCompanion behavior that set the use mode window always on top."
    ),
    FORCE_WINDOW_MINIMIZED(
            "forceWindowMinimized",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Will start use mode with the LifeCompanion window iconified. Useful if you don't want the LifeCompanion window to pop on start."
    ),

    // Api server configuration
    ENABLE_CONTROL_SERVER(
            "enableControlServer",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Will enable the API server to control LifeCompanion while running. To get details on control feature, check the \"LifeCompanion control server API\" part of documentation." +
                    "API server will run on its default port (8648) if enable expect if the port is specific with its own parameter."
    ),
    CONTROL_SERVER_PORT("controlServerPort",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "port",
            "The port for the API server to run. Will be ignored if the API server is not enabled (check the parameter above to enable it). If not specified, server will run on its default port.",
            "8080",
            1
    ),
    CONTROL_SERVER_AUTH_TOKEN("controlServerAuthToken",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "token",
            "If you want your control server to be secured with a `Authorization: Bearer <token>` header on each request. If enabled, any request without the same matching token will be rejected with 401 code",
            "AbCdEf123456",
            1,
            true
    ),

    // Updates,
    UPDATE_DOWNLOAD_FINISHED(
            "updateDownloadFinished",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Inform LifeCompanion that the update download was finished on last LifeCompanion use. When launched with the arg, LifeCompanion will try to install the newly downloaded update and restart itself."
    ),
    UPDATE_FINISHED(
            "updateFinished",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Inform LifeCompanion that the update installation was done on the previous launch. Typically, this arg is added on LifeCompanion restart after update installation."
    ),
    ENABLE_PREVIEW_UPDATES(
            "enablePreviewUpdates",
            GlobalRuntimeConfigurationType.COMMAND_LINE,
            "Enable LifeCompanion preview updates. This can be useful to test update before their production version to be ready."
    ),

    // TODO : add a token auth configuration if wanted
    // TODO : server side configuration synchronization
    // TODO : image repository from backoffice

    // Dev env
    PROP_DEV_MODE(
            "org.lifecompanion.dev.mode",
            GlobalRuntimeConfigurationType.JAVA_PROPERTY,
            "A general configuration that can be used to check if we are running LifeCompanion in a dev context." +
                    "This can be useful to add currently developed feature with this check, this will secure for an unfinished feature to be pushed in production."
    ),

    PROP_DISABLE_UPDATES(
            "org.lifecompanion.disable.updates",
            GlobalRuntimeConfigurationType.JAVA_PROPERTY,
            "Will disable all the update checking process (for both app and plugins). " +
                    "Will not try to reach the update server at all."
    ),

    PROP_LOAD_PLUGIN_FROM_CP(
            "org.lifecompanion.load.plugins.from.cp",
            GlobalRuntimeConfigurationType.JAVA_PROPERTY,
            "When enabled, will try to load plugins from classpath instead of the classpath configuration file. This is useful to make the plugin dev easier."
    ),

    PROP_DEBUG_LOADED_IMAGE(
            "org.lifecompanion.debug.loaded.images",
            GlobalRuntimeConfigurationType.JAVA_PROPERTY,
            "When enabled, a checking Thread is launched in background to display the loaded image count. This can be useful to detect memory leaks on images. See [`ImageDictionaries#startImageLoadingDebug()`](../lifecompanion/lc-app/src/main/java/org/lifecompanion/model/impl/imagedictionary/ImageDictionaries.java) for details"
    ),

    PROP_DEBUG_LOADED_CONFIGURATION(
            "org.lifecompanion.debug.loaded.configuration",
            GlobalRuntimeConfigurationType.JAVA_PROPERTY,
            "When enabled, a checking Thread is launched in background to display the loaded configuration count. This can be useful to detect memory leaks on configuration (for example, if a configuration is not released on configuration changed). See [`ConfigurationMemoryLeakChecker`](../lifecompanion/lc-app/src/main/java/org/lifecompanion/util/debug/ConfigurationMemoryLeakChecker.java) for details"
    ),
    ;

    private final String name, parameters, description, parametersExample;
    private final int expectedParameterCount;
    private final boolean securedParameters;
    private final GlobalRuntimeConfigurationType type;

    GlobalRuntimeConfiguration(String name,
                               GlobalRuntimeConfigurationType type,
                               String parameters,
                               String description,
                               String parametersExample,
                               int expectedParameterCount,
                               boolean securedParameters) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.description = description;
        this.parametersExample = parametersExample;
        this.expectedParameterCount = expectedParameterCount;
        this.securedParameters = securedParameters;
    }

    GlobalRuntimeConfiguration(String name, GlobalRuntimeConfigurationType type, String parameters, String description, String parametersExample, int expectedParameterCount) {
        this(name, type, parameters, description, parametersExample, expectedParameterCount, false);
    }

    GlobalRuntimeConfiguration(String name, GlobalRuntimeConfigurationType type, String description) {
        this(name, type, null, description, null, 0);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GlobalRuntimeConfigurationType getType() {
        return type;
    }

    @Override
    public String getParameters() {
        return parameters;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getParametersExample() {
        return parametersExample;
    }

    @Override
    public int getExpectedParameterCount() {
        return expectedParameterCount;
    }

    @Override
    public String toString() {
        return getType().getPrefix() + getName();
    }

    @Override
    public boolean isSecuredParameters() {
        return securedParameters;
    }

    @Override
    public String getMarkdownDocumentation() {
        return "|`" + getType().getPrefix() + getName() + (StringUtils.isNotBlank(getParameters()) ? (" " + getParameters()) : "") + "`|" +
                (StringUtils.isNotBlank(getParametersExample()) ? ("`" + getParametersExample() + "`") : "*`NONE`*") + "|" + getDescription() + "|\n";
    }

    public static String getMarkdownDocumentation(GlobalRuntimeConfigurationType type) {
        StringBuilder all = new StringBuilder();
        all.append("|Configuration|Param. example|Description|\n|-|-|-|\n");
        Stream.of(values())
                .filter(c -> c.getType() == type)
                .map(GlobalRuntimeConfiguration::getMarkdownDocumentation)
                .forEach(all::append);
        return all.toString();
    }

    public static List<GlobalRuntimeConfiguration> getAll(GlobalRuntimeConfigurationType type) {
        return Stream.of(values())
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }
}
