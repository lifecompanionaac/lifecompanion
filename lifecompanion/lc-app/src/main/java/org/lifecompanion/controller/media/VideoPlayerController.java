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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;


public enum VideoPlayerController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoPlayerController.class);

    public static final int THUMBNAIL_WIDTH = 100, THUMBNAIL_HEIGHT = 100;

    private final ExecutorService thumbnailService;

    VideoPlayerController() {
        this.thumbnailService = Executors.newSingleThreadExecutor();
    }

    public VideoElementI createVideoElement(File videoPath) {
        try {
            // TODO : optimize hash for size
            final String id = IOUtils.fileSha256HexToString(videoPath);

            VideoElement videoElement = new VideoElement(id, videoPath);
            getOrGenerateThumbnail(id, videoElement);

            return videoElement;
        } catch (Exception e) {
            return null;
        }
    }

    public void getOrGenerateThumbnail(String videoId, VideoElement videoElement) {
        File thumbnailPath = new File(InstallationConfigurationController.INSTANCE.getUserDirectory()
                .getPath() + File.separator + LCConstant.VIDEO_THUMBNAIL_DIR_NAME + File.separator + videoId + ".png");
        LOGGER.info("Will try to generate video thumbnail to {}", thumbnailPath);

        thumbnailService.submit(() -> {
            // Initialize a media and a media view
            Media media = new Media(videoElement.getPath().toURI().toString());
            media.setOnError(createOnErrorHandler(media, Media::getError));
            MediaPlayer player = new MediaPlayer(media);
            MediaView mediaView = new MediaView(player);
            mediaView.setFitWidth(THUMBNAIL_WIDTH);
            mediaView.setFitHeight(THUMBNAIL_HEIGHT);
            mediaView.setPreserveRatio(true);
            player.setOnError(createOnErrorHandler(player, MediaPlayer::getError));
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setAutoPlay(true);

            AtomicReference<Stage> stageRef = new AtomicReference<>();
            FXThreadUtils.runOnFXThread(() -> {
                BorderPane borderPane = new BorderPane(mediaView);
                borderPane.setTop(new Label("Vérification de la vidéo"));
                Scene scene = new Scene(borderPane);
                Stage stage = new Stage(StageStyle.UTILITY);
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
                stage.show();
                stageRef.set(stage);
            });
            ThreadUtils.safeSleep(3000);//FIXME : should be better than this...
            LOGGER.info("Will now try to make a screenshot");
            FXThreadUtils.runOnFXThread(() -> {
                BufferedImage buffImage = SwingFXUtils.fromFXImage(SnapshotUtils.takeNodeSnapshot(mediaView, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT), null);
                LOGGER.info("Screenshot ok");
                try {
                    IOUtils.createParentDirectoryIfNeeded(thumbnailPath);
                    ImageIO.write(buffImage, "png", thumbnailPath);
                    LOGGER.info("Image write ok");
                    videoElement.updateThumbnailPath(thumbnailPath);
                    Stage stage = stageRef.get();
                    if (stage != null) {
                        stage.hide();
                    }
                } catch (Exception e) {
                    LOGGER.error("Can't save video thumbnail", e);
                }
            });
        });
    }

    private static <T> Runnable createOnErrorHandler(T item, Function<T, MediaException> getter) {
        return () -> {
            final MediaException exception = getter.apply(item);
            System.out.println("(thumbnail)" + exception.getType() + " : " + exception.getMessage());
        };
    }
}
