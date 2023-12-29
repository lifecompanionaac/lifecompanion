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

import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.media.VideoPlayerController;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Control to select a image from the gallery.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class Image2SelectorControl extends BorderPane implements LCViewInitHelper {
    private static final double IMAGE_WIDTH = 180, IMAGE_HEIGHT = 100;

    /**
     * Image use in key
     */
    private ImageView imageViewSelected;

    /**
     * Button to select or remove image
     */
    private Button buttonRemoveImage, buttonSelectImage, buttonSelectVideo;

    private Tooltip tooltipImageKeywords;

    /**
     * Property that contains the selected image
     */
    private final ObjectProperty<ImageElementI> selectedImage;

    /**
     * Property that contains the selected video
     */
    private final ObjectProperty<VideoElementI> selectedVideo;

    /**
     * Property to disable image selection
     */
    private final BooleanProperty disableImageSelection;

    /**
     * Default text supplier
     */
    private Supplier<String> defaultSearchTextSupplier;

    private final String nodeIdForImageLoading;

    private final ObjectProperty<ImageUseComponentI> imageUseComponent;

    public Image2SelectorControl() {
        this.nodeIdForImageLoading = "Image2SelectorControl" + this.hashCode();
        this.disableImageSelection = new SimpleBooleanProperty(false);
        this.selectedImage = new SimpleObjectProperty<>();
        this.selectedVideo = new SimpleObjectProperty<>();
        this.imageUseComponent = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Class part : "Init"
    //========================================================================
    @Override
    public void initUI() {
        //Create buttons
        this.buttonRemoveImage = FXControlUtils.createTextButtonWithGraphics(Translation.getText("remove.image.component"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(20.0).color(LCGraphicStyle.SECOND_DARK), "tooltip.remove.image.button");
        this.buttonSelectImage = FXControlUtils.createTextButtonWithGraphics(Translation.getText("select.image.component"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PICTURE_ALT).size(20.0).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.select.image.button");
        this.buttonSelectVideo = FXControlUtils.createTextButtonWithGraphics(Translation.getText("select.video.component"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FILM).size(20.0).color(LCGraphicStyle.MAIN_PRIMARY),
                null);
        HBox boxSelectButtons = new HBox(2.0, buttonSelectImage, buttonSelectVideo);

        //Image
        this.imageViewSelected = new ImageView();
        this.imageViewSelected.setFitHeight(Image2SelectorControl.IMAGE_HEIGHT);
        this.imageViewSelected.setFitWidth(Image2SelectorControl.IMAGE_WIDTH);
        this.imageViewSelected.setPreserveRatio(true);

        tooltipImageKeywords = FXControlUtils.createTooltip("");
        Tooltip.install(imageViewSelected, tooltipImageKeywords);

        //Add
        VBox boxButton = new VBox(boxSelectButtons, this.buttonRemoveImage);
        boxButton.setAlignment(Pos.CENTER);
        this.setCenter(this.imageViewSelected);
        this.setRight(boxButton);
    }

    @Override
    public void initBinding() {
        bindImageViewToCurrentSelection();
        //Disable remove when there is no image
        this.buttonRemoveImage.disableProperty().bind(this.disableImageSelection.or(this.imageViewSelected.imageProperty().isNull()));
        this.buttonSelectImage.disableProperty().bind(this.disableImageSelection);
    }

    @Override
    public void initListener() {
        //Remove image
        this.buttonRemoveImage.setOnAction((ae) -> {
            this.selectedImage.set(null);
            this.selectedVideo.set(null);
        });
        //Select image
        this.buttonSelectImage.setOnAction((ea) -> {
            ImageSelectorDialog imageSelectorDialog = ImageSelectorDialog.getInstance();
            if (defaultSearchTextSupplier != null) {
                imageSelectorDialog.getImageSelectorSearchView().setSearchTextAndFireSearch(defaultSearchTextSupplier.get());
            }
            StageUtils.centerOnOwnerOrOnCurrentStage(imageSelectorDialog);
            Optional<ImageElementI> img = imageSelectorDialog.showAndWait();
            if (img.isPresent()) {
                selectedImage.set(img.get());
                selectedVideo.set(null);
                imageSelectorDialog.getImageSelectorSearchView().clearResult();
            }
        });
        this.buttonSelectVideo.setOnAction(e -> {
            FileChooser fileChooserVideo = LCFileChoosers.getChooserVideo(FileChooserType.SELECT_VIDEOS);
            File chosenFile = fileChooserVideo.showOpenDialog(FXUtils.getSourceWindow(buttonSelectVideo));
            if (chosenFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.SELECT_VIDEOS, chosenFile.getParentFile());
            }
            if (chosenFile != null && IOUtils.isSupportedVideo(chosenFile)) {
                VideoPlayerController.INSTANCE.createVideoElement(chosenFile, result -> {
                    if (result.getVideoElement() != null) {
                        selectedVideo.set(result.getVideoElement());
                        selectedImage.set(result.getThumbnail());
                    } else {
                        // FIXME : notification
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createError("VIDEO ERROR"));
                        System.err.println(result.getError().getClass().getSimpleName() + " : " + result.getError().getMessage());
                    }
                });
            }
        });
        this.selectedImage.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.requestImageUnload(this.nodeIdForImageLoading);
            }
            if (nv != null) {
                nv.requestImageLoad(this.nodeIdForImageLoading, IMAGE_WIDTH, IMAGE_HEIGHT, true, true);
                tooltipImageKeywords.setText(nv.getDescription());
            } else {
                tooltipImageKeywords.setText(null);
            }
        });
        this.imageUseComponent.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ConfigurationComponentUtils.unbindImageViewFromImageUseComponent(this.imageViewSelected);
                bindImageViewToCurrentSelection();
            }
            if (nv != null) {
                ConfigurationComponentUtils.bindImageViewWithImageUseComponent(this.imageViewSelected, nv);
            }
        });
    }

    private void bindImageViewToCurrentSelection() {
        this.imageViewSelected.imageProperty().bind(EasyBind.select(this.selectedImage).selectObject(ImageElementI::loadedImageProperty));
    }

    //========================================================================

    //========================================================================
    public ObjectProperty<ImageElementI> selectedImageProperty() {
        return this.selectedImage;
    }

    public ObjectProperty<VideoElementI> selectedVideoProperty() {
        return selectedVideo;
    }

    public ObjectProperty<ImageUseComponentI> imageUseComponentProperty() {
        return this.imageUseComponent;
    }

    public BooleanProperty disableImageSelectionProperty() {
        return this.disableImageSelection;
    }

    public void setDefaultSearchTextSupplier(Supplier<String> defaultSearchTextSupplier) {
        this.defaultSearchTextSupplier = defaultSearchTextSupplier;
    }
    //========================================================================
}
