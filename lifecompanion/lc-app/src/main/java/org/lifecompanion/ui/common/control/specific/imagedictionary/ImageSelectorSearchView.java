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

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.editmode.LCFileChooser;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class ImageSelectorSearchView extends BorderPane implements LCViewInitHelper {
    private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageSelectorSearchView.class);


    /**
     * Field to search an image
     */
    private TextField fieldSearchFilter;

    /**
     * Display search result
     */
    private ImageDictionariesListView imageDictionariesListView;

    /**
     * To import an image from the computer
     */
    private Button buttonImportFromComputer;

    /**
     * Button to take picture from camera
     */
    private Button buttonTakeFromCamera;

    /**
     * To import from clipboard
     */
    private Button buttonImportFromClipboard;


    /**
     * Called when image is selected
     */
    private Consumer<ImageElementI> selectionCallback;

    public ImageSelectorSearchView() {
        initAll();
    }

    @Override
    public void initUI() {
        // Search field
        this.fieldSearchFilter = TextFields.createClearableTextField();
        this.fieldSearchFilter.setPromptText(Translation.getText("image.gallery.search.tip"));

        // Dictionary view
        imageDictionariesListView = new ImageDictionariesListView();
        ScrollPane scrollCenter = new ScrollPane(imageDictionariesListView);
        scrollCenter.setFitToWidth(true);
        scrollCenter.getStyleClass().add("transparent-scroll-pane");

        // General
        this.setPadding(new Insets(10.0));
        this.setPrefWidth(ImageSelectorDialog.IMAGE_DIALOGS_WIDTH);
        this.setPrefHeight(ImageSelectorDialog.IMAGE_DIALOGS_HEIGHT);
        setMargin(fieldSearchFilter, new Insets(10.0));

        // Add from computer
        this.buttonImportFromComputer = UIUtils.createRightTextButton(Translation.getText("image.dictionary.import.image.from.computer.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER_OPEN).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "image.dictionary.import.image.from.computer.button.tooltip");
        this.buttonTakeFromCamera = UIUtils.createRightTextButton(Translation.getText("image.dictionary.take.image.webcam"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CAMERA).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "image.dictionary.take.image.webcam.tooltip");
        this.buttonImportFromClipboard = UIUtils.createRightTextButton(Translation.getText("image.dictionary.take.image.from.clipboard"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PASTE).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "image.dictionary.take.image.from.clipboard.tooltip");
        VBox boxButtonBottom = new VBox(5.0, buttonImportFromComputer, buttonTakeFromCamera, buttonImportFromClipboard);
        boxButtonBottom.setAlignment(Pos.CENTER);
        setMargin(boxButtonBottom, new Insets(10.0, 0, 0, 0));

        // Add
        this.setTop(fieldSearchFilter);
        this.setCenter(scrollCenter);
        this.setBottom(boxButtonBottom);
    }

    @Override
    public void initListener() {
        this.fieldSearchFilter.textProperty().addListener((obs, ov, nv) -> fireSearchFor(nv));
        this.buttonImportFromComputer.setOnAction(e -> {
            FileChooser fileChooserImage = LCFileChooser.getChooserImage(FileChooserType.SELECT_IMAGES);
            File chosenFile = fileChooserImage.showOpenDialog(UIUtils.getSourceWindow(buttonImportFromComputer));
            if (chosenFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.SELECT_IMAGES, chosenFile.getParentFile());
            }
            if (chosenFile != null && LCUtils.isSupportedImage(chosenFile) && selectionCallback != null) {
                selectionCallback.accept(ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(chosenFile));
            }
        });
        this.buttonTakeFromCamera.setOnAction(e -> {
            final File takenPicture = WebcamCaptureDialog.getInstance().showAndWait().orElse(null);
            if (takenPicture != null && LCUtils.isSupportedImage(takenPicture) && selectionCallback != null) {
                selectionCallback.accept(ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(takenPicture));
            }
        });
        this.buttonImportFromClipboard.setOnAction(e -> {
            Clipboard systemClipboard = Clipboard.getSystemClipboard();
            if (systemClipboard.hasImage() && selectionCallback != null) {
                try {
                    File destinationFile = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.WEBCAM_CAPTURE_DIR_NAME + DATE_FORMAT_FILENAME.format(new Date()) + ".png");
                    destinationFile.getParentFile().mkdirs();
                    ImageIO.write(SwingFXUtils.fromFXImage(systemClipboard.getImage(), null), "png", destinationFile);
                    selectionCallback.accept(ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(destinationFile));
                } catch (IOException ex) {
                    LOGGER.warn("Couldn't write image from clipboard to temp image to import", ex);
                }
            }
        });
    }

    private void fireSearchFor(String search) {
        LCUtils.debounce(600, "image-search-gallery", () -> {
            List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchResult = ImageDictionaries.INSTANCE.searchImage(search);
            Platform.runLater(() -> this.imageDictionariesListView.setDisplayedImages(searchResult));
        });
    }

    void setSearchTextAndFireSearch(String search) {
        this.fieldSearchFilter.setText(search);
        this.fieldSearchFilter.end();
        this.fireSearchFor(search);
    }

    void imageSelectorShowed() {
        fieldSearchFilter.requestFocus();
    }

    void clearResult() {
        this.imageDictionariesListView.setDisplayedImages(null);
    }

    public void setSelectionCallback(Consumer<ImageElementI> selectionCallback) {
        this.selectionCallback = selectionCallback;
        this.imageDictionariesListView.setSelectionCallback(selectionCallback);
    }
}
