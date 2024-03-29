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

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Consumer;

public class ImageElementView extends BorderPane implements LCViewInitHelper {
    private static Tooltip descriptionTooltip;
    private final ImageElementI imageElement;
    private final Consumer<ImageElementI> selectionCallback;
    private Label labelName;
    private ImageView imageView;

    public ImageElementView(ImageElementI imageElement, Consumer<ImageElementI> selectionCallback) {
        this.imageElement = imageElement;
        this.selectionCallback = selectionCallback;
        this.initAll();
    }

    @Override
    public void initUI() {
        this.getStyleClass().addAll("opacity-85-hover","text-font-size-90");

        this.labelName = new Label(imageElement.getName());
        this.labelName.prefWidthProperty().bind(this.widthProperty());
        this.labelName.setTextAlignment(TextAlignment.CENTER);

        this.imageView = new ImageView();

        this.setCenter(imageView);
        this.setBottom(labelName);

        if (descriptionTooltip == null) {
            descriptionTooltip = FXControlUtils.createTooltip("");
        }
        // Same tooltip for every node : change text before showing
        Tooltip.install(this, descriptionTooltip);
        this.setOnMouseEntered(m -> descriptionTooltip.setText(imageElement.getDescription()));
    }

    @Override
    public void initListener() {
        this.setOnMousePressed(e -> selectionCallback.accept(this.imageElement));
    }

    @Override
    public void initBinding() {
        ImageDictionaries.INSTANCE.requestLoadThumbnail(imageElement, cachedThumbnailInformation -> {
            imageView.setImage(cachedThumbnailInformation.getLoadedImage());
            imageView.setViewport(cachedThumbnailInformation.getViewport());
        });
    }
}
