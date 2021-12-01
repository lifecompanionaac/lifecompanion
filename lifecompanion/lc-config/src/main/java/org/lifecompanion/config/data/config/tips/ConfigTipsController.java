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
package org.lifecompanion.config.data.config.tips;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.tips.ConfigTipViewPane;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller to manage and display config tips to user.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ConfigTipsController {
    INSTANCE;

    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigTipsController.class);

    private Stage stage;
    private ConfigTipViewPane configView;
    private IntegerProperty currentDisplayedTipIndex;

    ConfigTipsController() {
        this.currentDisplayedTipIndex = new SimpleIntegerProperty(this, "currentDisplayedTipIndex", -1);
    }

    public void showConfigTipsStage() {
        //		this.initStage();
        //		if (!this.stage.isShowing()) {
        //			this.stage.centerOnScreen();
        //			this.stage.show();
        //			configView.updateModel();
        //			this.currentDisplayedTipIndex.set(new Random().nextInt(AvailableConfigTipsEnum.values().length));
        //		}
    }

    private void initStage() {
        if (this.stage == null) {
            //Display it into a stage
            configView = new ConfigTipViewPane();
            Scene scene = new Scene(configView);
            scene.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
            stage = ConfigUIUtils.createApplicationModalStage(AppController.INSTANCE.getMainStage(), LCConstant.TIPS_STAGE_WIDTH, LCConstant.TIPS_STAGE_HEIGHT);
            stage.setScene(scene);
            stage.initModality(Modality.NONE);
            stage.setTitle(Translation.getText("config.tips.stage.title"));
            stage.centerOnScreen();
            stage.setOnHidden(e -> this.tipStageHidden());
        }
    }

    private void tipStageHidden() {
        this.currentDisplayedTipIndex.set(-1);
        //Save user config if changed
        if (UserBaseConfiguration.INSTANCE.showTipsOnStartupProperty().get() != this.configView.isShowOnStartupEnabled()) {
            UserBaseConfiguration.INSTANCE.showTipsOnStartupProperty().set(this.configView.isShowOnStartupEnabled());
            try {
                UserBaseConfiguration.INSTANCE.save();
            } catch (IOException e) {
                LOGGER.warn("Couldn't save user configuration after change for config tips", e);
            }
        }
    }

    public void displayNext() {
        int i = this.currentDisplayedTipIndex.get();
        if (i < AvailableConfigTipsEnum.values().length - 1) {
            this.currentDisplayedTipIndex.set(i + 1);
        } else {
            this.currentDisplayedTipIndex.set(0);
        }
    }

    public void displayPrevious() {
        int i = this.currentDisplayedTipIndex.get();
        if (i > 0) {
            this.currentDisplayedTipIndex.set(i - 1);
        } else {
            this.currentDisplayedTipIndex.set(AvailableConfigTipsEnum.values().length - 1);
        }
    }

    public void hideConfigTipsStage() {
        stage.hide();
    }

    public ReadOnlyIntegerProperty currentDisplayedTipIndexProperty() {
        return this.currentDisplayedTipIndex;
    }
}
