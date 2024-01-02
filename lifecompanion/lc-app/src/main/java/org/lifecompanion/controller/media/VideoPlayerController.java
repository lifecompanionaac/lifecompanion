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

import javafx.scene.media.MediaException;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;


public enum VideoPlayerController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoPlayerController.class);

    public static final long MAX_THUMBNAIL_GENERATION_DELAY = 5000;
    public static final int THUMBNAIL_WIDTH = 600, THUMBNAIL_HEIGHT = 400;

    VideoPlayerController() {
    }

    public void createVideoElement(File videoPath, Consumer<CreateVideoResult> callback) {
        try {
            // TODO : optimize hash for big files ?
            final String id = IOUtils.fileSha256HexToString(videoPath);
            VideoElement videoElement = new VideoElement(id, videoPath);
            File thumbnailPath = getThumbnailPath(videoElement);

            if (!thumbnailPath.exists()) {
                VideoAnalyzerStage videoAnalyzerStage = new VideoAnalyzerStage(thumbnailPath, videoElement, callback);
                videoAnalyzerStage.show();
            } else {
                callback.accept(new VideoPlayerController.CreateVideoResult(videoElement,
                        ImageDictionaries.INSTANCE.getOrAddForVideoThumbnail(thumbnailPath,
                                videoElement.getThumbnailName())));
            }
        } catch (Exception e) {
            LOGGER.error("Can't create the video from {}", videoPath, e);
            callback.accept(new CreateVideoResult(e));
        }
    }

    private static File getThumbnailPath(VideoElement videoElement) {
        return new File(InstallationConfigurationController.INSTANCE.getUserDirectory()
                .getPath() + File.separator + LCConstant.VIDEO_THUMBNAIL_DIR_NAME + File.separator + videoElement.getId() + ".png");
    }

    public static class CreateVideoResult {
        private final VideoElementI videoElement;
        private final Throwable rawError;
        private final ImageElementI thumbnail;

        private CreateVideoResult(VideoElementI videoElement, ImageElementI thumbnail, Throwable rawError) {
            this.videoElement = videoElement;
            this.rawError = rawError;
            this.thumbnail = thumbnail;
        }

        public CreateVideoResult(Throwable error) {
            this(null, null, error);
        }

        public CreateVideoResult(VideoElementI videoElement, ImageElementI thumbnail) {
            this(videoElement, thumbnail, null);
        }

        public VideoElementI getVideoElement() {
            return videoElement;
        }

        public Throwable getRawError() {
            return rawError;
        }

        public Throwable getConvertedError() {
            if (rawError instanceof MediaException) {
                return LCException.newException().withCause(rawError).withHeaderId("video.file.error.exception.header").withMessageId("video.file.error.exception.message").build();
            }
            return rawError;
        }

        public ImageElementI getThumbnail() {
            return thumbnail;
        }
    }
}
