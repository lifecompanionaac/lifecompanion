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

package org.lifecompanion.ui.common.control.specific.imagedictionary;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.javafx.StageUtils;

public class ImageUseComponentConfigurationStage extends Stage {
    private static ImageUseComponentConfigurationStage instance;

    private final ImageUseComponentConfigurationView imageUseComponentConfigurationView;

    public ImageUseComponentConfigurationStage() {
        StageUtils.applyDefaultStageConfiguration(this);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(ImageSelectorDialog.IMAGE_DIALOGS_WIDTH);
        this.setHeight(ImageSelectorDialog.IMAGE_DIALOGS_HEIGHT);
        imageUseComponentConfigurationView = new ImageUseComponentConfigurationView();
        imageUseComponentConfigurationView.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setScene(new Scene(imageUseComponentConfigurationView));
        this.addEventFilter(KeyEvent.KEY_RELEASED, k -> {
            if (k.getCode() == KeyCode.ESCAPE) {
                k.consume();
                this.hide();
            }
        });
        this.setOnHidden(e -> imageUseComponentConfigurationView.modelProperty().set(null));
        StageUtils.fixMaximizedVisualBounds(this);
    }

    public static ImageUseComponentConfigurationStage getInstance() {
        if (instance == null) {
            instance = new ImageUseComponentConfigurationStage();
        }
        return instance;
    }

    public void prepareAndShow(VideoUseComponentI imageUseComponent) {
        imageUseComponentConfigurationView.modelProperty().set(imageUseComponent);
        StageUtils.centerOnOwnerOrOnCurrentStageAndShow(this);
    }
}
