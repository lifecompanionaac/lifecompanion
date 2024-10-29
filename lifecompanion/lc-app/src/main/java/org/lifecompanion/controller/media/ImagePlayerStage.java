/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.media;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.ui.common.pane.generic.FittedViewI;
import org.lifecompanion.ui.common.pane.generic.FittedViewPane;
import org.lifecompanion.ui.common.pane.generic.ImageViewFittedView;
import org.lifecompanion.ui.common.pane.generic.MediaViewFittedView;
import org.lifecompanion.util.javafx.StageUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImagePlayerStage extends AbstractPlayerStage<ImageUseComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImagePlayerStage.class);
    private ImageView imageView;

    public ImagePlayerStage(ImageUseComponentI imageUseComponent, final Color backgroundColor) {
        super(imageUseComponent, backgroundColor);
    }

    @Override
    FittedViewI createContent(ImageUseComponentI imageUseComponent) {
        this.imageView = new ImageView();
        imageView.setSmooth(true);
        ConfigurationComponentUtils.bindImageViewWithImageUseComponent(imageView, this.model);
        return new ImageViewFittedView(imageView);
    }

    @Override
    protected void onShown() {
        if (this.model != null) {
            Screen stageScreen = StageUtils.getStageScreen(this);
            if(stageScreen!=null){
                Rectangle2D stageScreenBounds = stageScreen.getBounds();
                this.model.addExternalLoadingRequest(String.valueOf(this.hashCode()), stageScreenBounds.getWidth(), stageScreenBounds.getHeight());
            }

        }
    }

    @Override
    void onHiding() {
        ConfigurationComponentUtils.unbindImageViewFromImageUseComponent(this.imageView);
        this.model.removeExternalLoadingRequest(String.valueOf(this.hashCode()));
    }
}
