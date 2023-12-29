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

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;

public class AutoRetryVideoPlayerView extends MediaView {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoRetryVideoPlayerView.class);
    private static final int RETRY_COUNT = 3;
    private int tryCount = 0;
    private String currentId;
    private Consumer<Throwable> errorHandler;

    public AutoRetryVideoPlayerView() {
    }

    public void setVideoFile(File videoFile, Consumer<MediaPlayer> playerConfig) {
        this.tryCount = 0;
        String id = UUID.randomUUID().toString();
        this.currentId = id;
        this.initPlayer(id, videoFile, playerConfig);
    }

    private void initPlayer(String id, File videoFile, Consumer<MediaPlayer> playerConfig) {
        if (StringUtils.isEquals(id, currentId)) {
            LOGGER.info("initPlayer()");
            this.disposePlayer();
            Media media = new Media(videoFile.toURI().toString());
            media.setOnError(() -> handleError(id, videoFile, playerConfig, media.getError()));
            MediaPlayer player = new MediaPlayer(media);
            playerConfig.accept(player);
            player.setOnError(() -> handleError(id, videoFile, playerConfig, player.getError()));
            this.setMediaPlayer(player);
        }
    }

    private void handleError(String id, File videoFile, Consumer<MediaPlayer> playerConfig, MediaException mediaException) {
        LOGGER.warn("Error in video player : {}, {}", mediaException.getType(), mediaException.getMessage());
        if (tryCount++ < RETRY_COUNT) {
            this.initPlayer(id, videoFile, playerConfig);
        } else {
            LOGGER.info("Too many failures, will not retry playing and report the errors");
            if (this.errorHandler != null) {
                this.errorHandler.accept(mediaException);
            }
        }
    }

    public void disposePlayer() {
        MediaPlayer mediaPlayer = this.getMediaPlayer();
        if (mediaPlayer != null) {
            LOGGER.info("disposePlayer()");
            mediaPlayer.dispose();
        }
    }

    public void setErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }
}
