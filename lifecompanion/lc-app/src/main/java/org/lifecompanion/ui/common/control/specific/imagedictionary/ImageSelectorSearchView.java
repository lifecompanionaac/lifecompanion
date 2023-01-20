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

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.lifecompanion.ui.controlsfx.control.textfield.TextFields;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
        this.buttonImportFromComputer = FXControlUtils.createRightTextButton(Translation.getText("image.dictionary.import.image.from.computer.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER_OPEN).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "image.dictionary.import.image.from.computer.button.tooltip");
        this.buttonTakeFromCamera = FXControlUtils.createRightTextButton(Translation.getText("image.dictionary.take.image.webcam"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CAMERA).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "image.dictionary.take.image.webcam.tooltip");
        this.buttonImportFromClipboard = FXControlUtils.createRightTextButton(Translation.getText("image.dictionary.take.image.from.clipboard"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PASTE).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "image.dictionary.take.image.from.clipboard.tooltip");
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
            FileChooser fileChooserImage = LCFileChoosers.getChooserImage(FileChooserType.SELECT_IMAGES);
            File chosenFile = fileChooserImage.showOpenDialog(FXUtils.getSourceWindow(buttonImportFromComputer));
            if (chosenFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.SELECT_IMAGES, chosenFile.getParentFile());
            }
            if (chosenFile != null && IOUtils.isSupportedImage(chosenFile) && selectionCallback != null) {
                selectionCallback.accept(ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(chosenFile));
            }
        });
        this.buttonTakeFromCamera.setOnAction(e -> {
            final File takenPicture = WebcamCaptureDialog.getInstance().showAndWait().orElse(null);
            if (takenPicture != null && IOUtils.isSupportedImage(takenPicture) && selectionCallback != null) {
                selectionCallback.accept(ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(takenPicture));
            }
        });
        this.buttonImportFromClipboard.setOnAction(e -> {
            Clipboard systemClipboard = Clipboard.getSystemClipboard();
            if (systemClipboard.hasImage() && selectionCallback != null) {
                try {
                    File destinationFile = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.CLIPBOARD_CAPTURE_DIR_NAME + DATE_FORMAT_FILENAME.format(new Date()) + ".png");
                    destinationFile.getParentFile().mkdirs();
                    ImageIO.write(SwingFXUtils.fromFXImage(systemClipboard.getImage(), null), "png", destinationFile);
                    selectionCallback.accept(ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(destinationFile));
                } catch (IOException ex) {
                    LOGGER.warn("Couldn't write image from clipboard to temp image to import", ex);
                }
            }
        });
    }

    // FIXME : find a correct image correction implementation
    //  if clipboard contains "Object Descriptor" and image, it indicates that the image is from docs/Paint/etc.
    //  current problem with this algo : transparency is converted to black...
    private WritableImage getCorrectImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        byte[] data = new byte[width * height * 3];
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getPixelReader().getArgb(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
//                System.out.println("Alpha : "+a);
//                data[i++] = (byte) r;
//                data[i++] = (byte) g;
//                data[i++] = (byte) b;
                if (a != 0) System.out.println(" a " + a);
                pixelWriter.setArgb(x, y, 255 << 24 | r << 16 | g << 8 | b);
            }
        }
//        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
//        PixelWriter pixelWriter = writableImage.getPixelWriter();
//        pixelWriter.setPixels(0, 0, (int) image.getWidth(), (int) image.getHeight(),
//                PixelFormat.getByteRgbInstance(), data, 0, (int) image.getWidth() * 3);
        return writableImage;
    }

    private void fireSearchFor(String search) {
        ThreadUtils.debounce(600, "image-search-gallery", () -> {
            List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchResult = ImageDictionaries.INSTANCE.searchImage(search);
            FXThreadUtils.runOnFXThread(() -> this.imageDictionariesListView.setDisplayedImages(searchResult));
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
