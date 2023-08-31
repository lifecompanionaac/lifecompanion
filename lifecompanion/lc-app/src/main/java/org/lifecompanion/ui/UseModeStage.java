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

package org.lifecompanion.ui;

import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.StageMode;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UseModeStage extends Stage {
    private static final Logger LOGGER = LoggerFactory.getLogger(UseModeStage.class);

    public UseModeStage(LCProfileI profile, LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription, UseModeScene useModeScene) {
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.setTitle(StageUtils.getStageDefaultTitle() +
                (profile != null ? " - " + profile.nameProperty().get() : "") +
                (configurationDescription != null ? " - " + configurationDescription.configurationNameProperty().get() : "")
        );

        // Stage style
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_UNDECORATED)) {
            LOGGER.info("Stage style is forced to UNDECORATED because {} is enabled", GlobalRuntimeConfiguration.FORCE_WINDOW_UNDECORATED);
            this.initStyle(StageStyle.UNDECORATED);
        } else {
            this.initStyle(StageStyle.DECORATED);
        }

        this.setScene(useModeScene);

        // Stage size
        double stageWidth = configuration.computedFrameWidthProperty().get();
        double stageHeight = configuration.computedFrameHeightProperty().get();
        boolean sizeForced = false;
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE)) {
            List<String> parameters = GlobalRuntimeConfigurationController.INSTANCE.getParameters(GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE);
            Integer forcedWidth = LangUtils.safeParseInt(parameters.get(0));
            Integer forcedHeight = LangUtils.safeParseInt(parameters.get(1));
            if (forcedWidth != null && forcedHeight != null) {
                stageWidth = forcedWidth;
                stageHeight = forcedHeight;
                sizeForced = true;
                LOGGER.info("Stage size forced to {} because {} is enabled", parameters, GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE);
            } else {
                LOGGER.warn("Invalid width/height for use stage and arg {} : {} x {}", GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE, parameters.get(0), parameters.get(1));
            }
        }
        this.setWidth(stageWidth);
        this.setHeight(stageHeight);

        // Stage opacity
        double stageOpacity = configuration.frameOpacityProperty().get();
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_OPACITY)) {
            String opacityStr = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.FORCE_WINDOW_OPACITY);
            Double forcedOpacity = LangUtils.safeParseDouble(opacityStr);
            if (forcedOpacity != null) {
                stageOpacity = forcedOpacity;
                LOGGER.info("Stage opacity forced to {} because {} is enabled", stageOpacity, GlobalRuntimeConfiguration.FORCE_WINDOW_OPACITY);
            } else {
                LOGGER.warn("Invalid opacity for use stage and arg {} : {}", GlobalRuntimeConfiguration.FORCE_WINDOW_OPACITY, opacityStr);
            }
        }
        this.setOpacity(stageOpacity);

        this.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_WINDOW_ALWAYS_ON_TOP)) {
            this.setAlwaysOnTop(true);
        } else {
            LOGGER.info("Use mode stage will not be always on top because {} is enabled", GlobalRuntimeConfiguration.DISABLE_WINDOW_ALWAYS_ON_TOP);
        }

        this.setOnCloseRequest((we) -> {
            we.consume();
            GlobalActions.HANDLER_CANCEL.handle(null);
        });

        // Stage location
        if (!sizeForced && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION) && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_WINDOW_FULLSCREEN)) {
            StageMode mode = configuration.stageModeOnLaunchProperty().get();
            switch (mode) {
                case BASE:
                    StageUtils.moveStageTo(this, configuration.framePositionOnLaunchProperty().get());
                    break;
                case ICONIFIED:
                    this.setIconified(true);
                    break;
                case MAXIMIZED:
                    this.setMaximized(true);
                    break;
                case FULLSCREEN:
                    this.setFullScreen(true);
                    break;
            }
        } else {
            if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION)) {
                List<String> parameters = GlobalRuntimeConfigurationController.INSTANCE.getParameters(GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION);
                Integer forcedX = LangUtils.safeParseInt(parameters.get(0));
                Integer forcedY = LangUtils.safeParseInt(parameters.get(1));
                if (forcedX != null && forcedY != null) {
                    LOGGER.info("{} is enabled with parameter {}, the stage mode configuration {} is then ignored", GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION, parameters, configuration.stageModeOnLaunchProperty().get());
                    this.setX(forcedX);
                    this.setY(forcedY);
                } else {
                    LOGGER.warn("Invalid x/y for use stage and arg {} : {} x {}, will be moved to center", GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION, parameters.get(0), parameters.get(1));
                    StageUtils.moveStageTo(this, FramePosition.CENTER);
                }
            } else {
                LOGGER.info("Because {} or {} is enabled, will move the stage to screen center and ignore configuration {}", GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE, GlobalRuntimeConfiguration.DISABLE_WINDOW_FULLSCREEN, configuration.stageModeOnLaunchProperty().get());
                StageUtils.moveStageTo(this, FramePosition.CENTER);
            }
        }

        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_MINIMIZED)) {
            this.setIconified(true);
        }

        this.setOnShown(e1 -> {
            boolean isVirtualKeyboard = configuration.virtualKeyboardProperty().get() && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD);
            if (configuration.virtualKeyboardProperty().get() && GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD)) {
                LOGGER.info("Use mode stage unfocusable state and not focus ignored for virtual keyboard feature as {} is enabled", GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD);
            }
            if (isVirtualKeyboard) {
                StageUtils.setFocusableInternalAPI(this, false);
            }
            if(!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_MINIMIZED)) {
                VirtualMouseController.INSTANCE.centerMouseOnStage();
                if (!isVirtualKeyboard) {
                    useModeScene.requestFocus();
                } else {
                    // Issue #129
                    // Showing a stage steal the focus and this is a problem for virtual keyboard stages.
                    // To avoid this the stage should be iconified and shown again (dirty but no better solution found currently)
                    Thread fixStageFocusThread = new Thread(() -> {
                        ThreadUtils.safeSleep(200);
                        FXThreadUtils.runOnFXThread(() -> this.setIconified(true));
                        ThreadUtils.safeSleep(200);
                        FXThreadUtils.runOnFXThread(() -> this.setIconified(false));
                    }, "Fix stage focus thread");
                    fixStageFocusThread.setDaemon(true);
                    fixStageFocusThread.start();
                }
            }
        });
        this.setOnHidden(e -> {
            useModeScene.unbindAndClean();
        });
    }
}
