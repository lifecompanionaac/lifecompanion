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

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.ui.common.pane.generic.FittedViewPane;
import org.lifecompanion.ui.common.pane.generic.MediaViewFittedView;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class VideoAnalyzerStage extends Stage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoRetryVideoPlayerView.class);

    private AutoRetryVideoPlayerView autoRetryVideoPlayerView;

    public VideoAnalyzerStage(File thumbnailPath, VideoElement videoElement, Consumer<VideoPlayerController.CreateVideoResult> callback) {

        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(StageUtils.getStageDefaultTitle());
        this.setWidth(600);
        this.setHeight(400);
        this.setResizable(false);
        StageUtils.centerOnScreen(StageUtils.getDestinationScreen(), this);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.setAlwaysOnTop(true);
        this.setOnCloseRequest(Event::consume);
        this.setOnHidden(e -> autoRetryVideoPlayerView.disposePlayer());
        this.setScene(this.createScene());

        // Hide on error or after a delay
        AtomicBoolean videoAnalyzed = new AtomicBoolean();
        ThreadUtils.runAfter(VideoPlayerController.MAX_THUMBNAIL_GENERATION_DELAY, () -> {
            FXThreadUtils.runOnFXThread(this::hideIfShowing);
            if (!videoAnalyzed.get()) {
                callback.accept(new VideoPlayerController.CreateVideoResult(new Exception("Video analyze timeout")));
            }
        });
        autoRetryVideoPlayerView.setErrorHandler(exception -> {
            videoAnalyzed.set(true);
            callback.accept(new VideoPlayerController.CreateVideoResult(exception));
            this.hideIfShowing();
        });

        // Init playing
        autoRetryVideoPlayerView.setVideoFile(videoElement.getPath(), player -> {
            player.setOnPlaying(() -> ThreadUtils.runAfter(500, () -> {
                FXThreadUtils.runOnFXThread(() -> {
                    try {
                        videoAnalyzed.set(true);
                        BufferedImage buffImage = SwingFXUtils.fromFXImage(SnapshotUtils.takeNodeSnapshot(autoRetryVideoPlayerView,
                                VideoPlayerController.THUMBNAIL_WIDTH,
                                VideoPlayerController.THUMBNAIL_HEIGHT), null);
                        IOUtils.createParentDirectoryIfNeeded(thumbnailPath);
                        ImageIO.write(buffImage, "png", thumbnailPath);
                        callback.accept(new VideoPlayerController.CreateVideoResult(videoElement,
                                ImageDictionaries.INSTANCE.getOrAddForVideoThumbnail(thumbnailPath,
                                        videoElement.getThumbnailName())));
                    } catch (Exception e) {
                        LOGGER.error("Can't save video thumbnail", e);
                        callback.accept(new VideoPlayerController.CreateVideoResult(e));
                    } finally {
                        hideIfShowing();
                    }
                });
            }));
            player.setMute(true);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setAutoPlay(true);
        });
    }

    private void hideIfShowing() {
        if (this.isShowing()) {
            hide();
        }
    }

    private Scene createScene() {
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);

        // Background : video playing
        autoRetryVideoPlayerView = new AutoRetryVideoPlayerView();
        FittedViewPane fittedViewPane = new FittedViewPane(new MediaViewFittedView(autoRetryVideoPlayerView));
        fittedViewPane.setEffect(new BoxBlur());

        // Front : message and loading indicator
        Label labelAnalyze = new Label(Translation.getText("video.analyze.message"));
        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.setPrefSize(80, 80);
        labelAnalyze.setStyle("-fx-font-size: 20.0px; -fx-font-weight: bold; -fx-text-fill: darkgray;");
        VBox messageAndLoading = new VBox(10.0, labelAnalyze, progressIndicator);
        messageAndLoading.setAlignment(Pos.CENTER);
        messageAndLoading.setStyle("-fx-background-color:rgba(50,50,50,0.8);");
        messageAndLoading.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Total
        stackPane.getChildren().add(fittedViewPane);
        StackPane.setAlignment(messageAndLoading, Pos.CENTER);
        stackPane.getChildren().add(messageAndLoading);

        return scene;
    }
}
