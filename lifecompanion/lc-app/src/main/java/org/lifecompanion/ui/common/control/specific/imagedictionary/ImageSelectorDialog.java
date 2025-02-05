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

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.DialogUtils;

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

        this.setWidth(IMAGE_DIALOGS_WIDTH);
        this.setHeight(IMAGE_DIALOGS_HEIGHT);

        SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(this);
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

        this.showingProperty().addListener(DialogUtils.createScreenBoundsShowingListener(this));
    }

    public ImageSelectorSearchView getImageSelectorSearchView() {
        return imageSelectorSearchView;
    }
}

