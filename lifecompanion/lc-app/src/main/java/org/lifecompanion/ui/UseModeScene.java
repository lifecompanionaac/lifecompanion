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

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.editaction.CommonActions;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.configurationcomponent.usemode.SimpleUseConfigurationDisplayer;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * The scene that display the component when the application is in use mode in "normal" LifeCompanion (use and config mode)<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseModeScene extends Scene implements LCViewInitHelper {

    /**
     * The root of this scene
     */
    private final Group root;

    /**
     * The configuration displayer
     */
    private SimpleUseConfigurationDisplayer configurationDisplayer;

    /**
     * Button to go back to configuration mode
     */
    private Button buttonGoToConfigMode;

    /**
     * Button to enable/disable fullscreen
     */
    private Button buttonFullscreen;

    private final LCConfigurationI configuration;

    public UseModeScene(LCConfigurationI configuration) {
        super(new Group());
        this.configuration = configuration;
        this.root = (Group) getRoot();
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
    }

    @Override
    public void initUI() {
        this.configurationDisplayer = new SimpleUseConfigurationDisplayer(this.configuration, this.widthProperty(), this.heightProperty());
        this.root.getChildren().add(this.configurationDisplayer);

        // Button go config mode
        this.buttonGoToConfigMode = FXControlUtils.createTextButtonWithIcon(null, "actions/icon_go_back_config_mode.png", null);
        this.buttonGoToConfigMode.setLayoutX(-5.0);
        this.buttonGoToConfigMode.setLayoutY(-10.0);
        this.buttonGoToConfigMode.setFocusTraversable(false);

        // Button to switch fullscreen state
        buttonFullscreen = FXControlUtils.createTextButtonWithIcon(null, "actions/icon_switch_fullscreen.png", null);
        this.buttonFullscreen.layoutXProperty().bind(widthProperty().subtract(32.0));
        this.buttonFullscreen.setLayoutY(-10.0);
        this.buttonFullscreen.setFocusTraversable(false);

        this.root.getChildren().addAll(this.buttonGoToConfigMode, buttonFullscreen);
    }

    @Override
    public void initBinding() {
        this.fillProperty().bind(this.configurationDisplayer.backgroundColorProperty());
    }

    @Override
    public void initListener() {
        //Keyboard shortcut
        this.addEventHandler(KeyEvent.KEY_RELEASED, eventP -> {
            if (CommonActions.KEY_COMBINATION_GO_CONFIG_MODE.match(eventP)) {
                CommonActions.HANDLER_GO_CONFIG_MODE_SKIP_CHECK.handle(null);
                eventP.consume();
            }
            if (CommonActions.KEY_COMBINATION_SWITCH_FULLSCREEN.match(eventP)) {
                CommonActions.HANDLER_SWITCH_FULLSCREEN.handle(null);
                eventP.consume();
            }
        });
        //Filter mouse event to keep the goToConfig event
        this.configurationDisplayer.addMouseListener(this, (mouseEvent) -> {
            return mouseEvent.getTarget() != this.buttonGoToConfigMode && mouseEvent.getTarget() != this.buttonFullscreen;
        });
        this.buttonGoToConfigMode.setOnAction(CommonActions.HANDLER_GO_CONFIG_MODE_CHECK);
        // Button to switch fullscreen mode
        this.buttonFullscreen.setOnAction(CommonActions.HANDLER_SWITCH_FULLSCREEN);

        SessionStatsController.INSTANCE.registerScene(this);
    }

    public void unbindAndClean() {
        SessionStatsController.INSTANCE.unregisterScene(this);
        this.configurationDisplayer.unbindAndClean();
    }
}
