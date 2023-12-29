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

import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;


public enum VideoPlayerController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoPlayerController.class);

    public static final long MAX_THUMBNAIL_GENERATION_DELAY = 3000;
    public static final int THUMBNAIL_WIDTH = 600, THUMBNAIL_HEIGHT = 400;

    VideoPlayerController() {
    }

    public void createVideoElement(File videoPath, Consumer<CreateVideoResult> callback) {
        try {
            // TODO : optimize hash for big files
            final String id = IOUtils.fileSha256HexToString(videoPath);
            VideoElement videoElement = new VideoElement(id, videoPath);
            File thumbnailPath = getThumbnailPath(videoElement);

            // TODO : check if already exist before analyze
            VideoAnalyzerStage videoAnalyzerStage = new VideoAnalyzerStage(thumbnailPath, videoElement, callback);
            videoAnalyzerStage.show();

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
        private final Throwable error;
        private final ImageElementI thumbnail;

        private CreateVideoResult(VideoElementI videoElement, ImageElementI thumbnail, Throwable error) {
            this.videoElement = videoElement;
            this.error = error;
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

        public Throwable getError() {
            return error;
        }

        public ImageElementI getThumbnail() {

            return thumbnail;
        }
    }
}
