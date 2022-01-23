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

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.InstallationConfigurationController;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WebcamCaptureDialog extends Dialog<File> implements LCViewInitHelper {
    private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
    private static final long UPDATE_DELAY = 50; // ~20 FPS if instant capture

    private static final Logger LOGGER = LoggerFactory.getLogger(WebcamCaptureDialog.class);

    private static WebcamCaptureDialog instance;

    private final AtomicReference<UpdateWebcamTask> currentUpdateWebcamTask;
    private final ExecutorService executorService;

    private BorderPane dialogContent;
    private ImageView imageViewWebcamPreview;
    private Button buttonTakePicture, buttonSwitchCamera;
    private ImageView takenImagePreview;
    private Text labelWebcamInformations;
    private Label labelNoWebcam;
    private StackPane stackPaneWebcamDisplay;
    private ProgressIndicator progressIndicatorLoadingWebcam;
    private File resultFile;

    private WebcamCaptureDialog() {
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("lifecompanion-webcam-update-thread");
            return t;
        });
        currentUpdateWebcamTask = new AtomicReference<>();
        initAll();
    }

    public static WebcamCaptureDialog getInstance() {
        if (instance == null) {
            instance = new WebcamCaptureDialog();
        }
        return instance;
    }

    @Override
    public void initUI() {
        // Dialog config
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);

        // Preview
        imageViewWebcamPreview = new ImageView();
        imageViewWebcamPreview.setPreserveRatio(true);
        imageViewWebcamPreview.setFitWidth(ImageSelectorDialog.IMAGE_DIALOGS_WIDTH - 150.0);

        labelWebcamInformations = new Text();
        labelWebcamInformations.setStroke(Color.BLACK);
        labelWebcamInformations.setStrokeWidth(0.3);
        labelWebcamInformations.setFill(Color.WHITE);
        labelWebcamInformations.setFont(Font.font("monospace", 12));

        stackPaneWebcamDisplay = new StackPane(imageViewWebcamPreview, labelWebcamInformations);
        StackPane.setAlignment(imageViewWebcamPreview, Pos.CENTER);
        StackPane.setAlignment(labelWebcamInformations, Pos.BOTTOM_CENTER);

        progressIndicatorLoadingWebcam = new ProgressIndicator(-1);
        progressIndicatorLoadingWebcam.setPrefSize(100.0, 100.0);

        // Button
        buttonTakePicture = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CAMERA).size(44).color(LCGraphicStyle.SECOND_DARK), "image.webcam.button.take.picture");
        buttonSwitchCamera = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.REFRESH).size(18).color(LCGraphicStyle.LC_GRAY), "image.webcam.button.switch.camera");
        takenImagePreview = new ImageView();
        takenImagePreview.setFitWidth(100.0);
        takenImagePreview.setFitHeight(100.0);
        takenImagePreview.setPreserveRatio(true);

        BorderPane rightBP = new BorderPane();
        rightBP.setTop(buttonSwitchCamera);
        BorderPane.setAlignment(buttonSwitchCamera, Pos.CENTER);
        rightBP.setCenter(buttonTakePicture);
        rightBP.setBottom(takenImagePreview);
        BorderPane.setAlignment(takenImagePreview, Pos.CENTER);

        // Content
        dialogContent = new BorderPane();
        dialogContent.setRight(rightBP);
        dialogContent.setCenter(progressIndicatorLoadingWebcam);
        dialogContent.setPrefSize(ImageSelectorDialog.IMAGE_DIALOGS_WIDTH, ImageSelectorDialog.IMAGE_DIALOGS_HEIGHT);

        labelNoWebcam = new Label(Translation.getText("image.webcam.capture.no.camera.or.problem"));
        labelNoWebcam.setStyle("-fx-font-size: 16px");
        labelNoWebcam.setWrapText(true);

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        this.getDialogPane().setContent(dialogContent);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? resultFile : null);
    }


    @Override
    public void initListener() {
        this.setOnShowing(e -> {
            setLoadingView();
            resultFile = null;
            this.takenImagePreview.setImage(null);
            this.executorService.submit(cancelPreviousTaskAndSetNewOne(new UpdateWebcamTask()));
        });
        this.setOnHidden(e -> {
            cancelPreviousTaskAndSetNewOne(null);
        });
        this.buttonTakePicture.setOnAction(a -> {
            final UpdateWebcamTask updateWebcamTask = currentUpdateWebcamTask.get();
            if (updateWebcamTask != null) {
                updateWebcamTask.takePicture();
                final Image imageToSave = takenImagePreview.getImage();
                if (imageToSave != null) {
                    try {
                        File destinationFile = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.WEBCAM_CAPTURE_DIR_NAME + DATE_FORMAT_FILENAME.format(new Date()) + ".png");
                        destinationFile.getParentFile().mkdirs();
                        ImageIO.write(SwingFXUtils.fromFXImage(imageToSave, null), "png", destinationFile);
                        resultFile = destinationFile;
                        LOGGER.info("Taken camera picture saved to {}", destinationFile);
                    } catch (Exception e) {
                        LOGGER.error("Couldn't save taken picture from webcam", e);
                    }
                }
            }
        });
        this.buttonSwitchCamera.setOnAction(a -> {
            final UpdateWebcamTask updateWebcamTask = currentUpdateWebcamTask.get();
            if (updateWebcamTask != null) {
                updateWebcamTask.switchWebcam();
            }
        });
    }

    private void setLoadingView() {
        Platform.runLater(() -> {
            dialogContent.setCenter(progressIndicatorLoadingWebcam);
            labelWebcamInformations.setText("");
        });
    }

    private UpdateWebcamTask cancelPreviousTaskAndSetNewOne(UpdateWebcamTask updateWebcamTask) {
        UpdateWebcamTask previousTask = currentUpdateWebcamTask.getAndSet(updateWebcamTask);
        if (previousTask != null && !previousTask.isDone()) {
            previousTask.cancel(false);
        }
        return updateWebcamTask;
    }

    private class UpdateWebcamTask extends Task<Void> {
        private int currentWebcamIndex;
        private Webcam currentWebcam;

        private BufferedImage currentImage;

        private final List<Webcam> webcams;
        private final AtomicBoolean requestCameraChange;

        UpdateWebcamTask() {
            webcams = new ArrayList<>();
            requestCameraChange = new AtomicBoolean(true);
        }

        @Override
        protected Void call() {
            if (!isCancelled()) {
                try {
                    // List all available webcams
                    webcams.addAll(Webcam.getWebcams());
                    LOGGER.info("Found {} webcams", webcams.size());

                    // Find camera from last selected (if available)
                    final String lastSelectedWebcamName = LCStateController.INSTANCE.getLastSelectedWebcamName();
                    LOGGER.info("Last selected camera in LC : {}",lastSelectedWebcamName);
                    for (int i = 0; i < webcams.size(); i++) {
                        if (StringUtils.isEquals(lastSelectedWebcamName, webcams.get(i).getName())) {
                            currentWebcamIndex = i;
                        }
                    }

                    while (!isCancelled()) {
                        // Detect and execute camera change when needed
                        if (requestCameraChange.getAndSet(false)) {
                            setLoadingView();
                            closeCurrentWebcamIfNeeded();

                            // Get the wanted webcam
                            currentWebcam = webcams.get(currentWebcamIndex);
                            LCStateController.INSTANCE.setLastSelectedWebcamName(currentWebcam.getName());

                            // Find and set higher resolution
                            final Dimension higherWebcamDimension = Arrays.stream(currentWebcam.getViewSizes()).min((d1, d2) -> Double.compare(d2.width * d2.height, d1.width * d1.height)).orElse(currentWebcam.getViewSize());
                            LOGGER.info("Will try to open camera {} with resolution {}", currentWebcam.getName(), higherWebcamDimension);
                            currentWebcam.setViewSize(higherWebcamDimension);

                            // Open webcam
                            currentWebcam.open(true);

                            Platform.runLater(() -> dialogContent.setCenter(stackPaneWebcamDisplay));
                        }

                        currentImage = currentWebcam.getImage();
                        if (currentImage != null) {
                            final WritableImage fxImage = SwingFXUtils.toFXImage(currentImage, null);
                            Platform.runLater(() -> {
                                imageViewWebcamPreview.setImage(fxImage);
                                final Dimension viewSize = currentWebcam.getViewSize();
                                labelWebcamInformations.setText(Translation.getText("image.webcam.capture.webcam.informations", currentWebcam.getName(), viewSize.width, viewSize.height, (int) currentWebcam.getFPS()));
                            });
                        }
                        Thread.sleep(UPDATE_DELAY);

                    }
                } catch (Throwable t) {
                    LOGGER.error("Problem with webcam task", t);
                    Platform.runLater(() -> dialogContent.setCenter(labelNoWebcam));
                }
            }
            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            closeCurrentWebcamIfNeeded();
            return super.cancel(mayInterruptIfRunning);
        }

        private void closeCurrentWebcamIfNeeded() {
            if (currentWebcam != null && currentWebcam.isOpen()) {
                LOGGER.info("Webcam {} closed", currentWebcam);
                currentWebcam.close();
            }
        }

        void takePicture() {
            if (currentImage != null) {
                takenImagePreview.setImage(imageViewWebcamPreview.getImage());
            }
        }

        void switchWebcam() {
            if (currentWebcamIndex < webcams.size() - 1) {
                currentWebcamIndex++;
            } else {
                currentWebcamIndex = 0;
            }
            requestCameraChange.set(true);
        }
    }
}
