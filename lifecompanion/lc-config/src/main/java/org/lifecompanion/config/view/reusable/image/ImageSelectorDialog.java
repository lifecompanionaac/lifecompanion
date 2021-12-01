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

package org.lifecompanion.config.view.reusable.image;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.image2.ImageDictionaries;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class ImageSelectorDialog extends Dialog<ImageElementI> implements LCViewInitHelper {
    public static final double IMAGE_DIALOGS_WIDTH = 750, IMAGE_DIALOGS_HEIGHT = 600;

    private static ImageSelectorDialog instance;
    private ImageSelectorSearchView imageSelectorSearchView;

    private ImageSelectorDialog() {
        initAll();
    }

    public static ImageSelectorDialog getInstance() {
        if (instance == null) {
            instance = new ImageSelectorDialog();
        }
        return instance;
    }

    @Override
    public void initUI() {
        // Dialog config
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);

        // Content
        imageSelectorSearchView = new ImageSelectorSearchView();

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        this.getDialogPane().setContent(imageSelectorSearchView);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> null);
    }

    @Override
    public void initListener() {
        this.imageSelectorSearchView.setSelectionCallback(imageElementI -> {
            setResult(imageElementI);
            hide();
        });
        this.setOnShown(e -> {
            setResult(null);
            imageSelectorSearchView.imageSelectorShowed();
        });
        this.setOnHidden(e -> {
            imageSelectorSearchView.clearResult();
            ImageDictionaries.INSTANCE.clearThumbnailCache();
        });
    }

    public ImageSelectorSearchView getImageSelectorSearchView() {
        return imageSelectorSearchView;
    }
}

