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

import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.ui.common.pane.generic.FittedViewI;
import org.lifecompanion.ui.common.pane.generic.MediaViewFittedView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VideoPlayerStage extends AbstractPlayerStage<VideoUseComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoPlayerStage.class);

    private AutoRetryVideoPlayerView autoRetryVideoPlayerView;

    public VideoPlayerStage(VideoUseComponentI videoUseComponent) {
        super(videoUseComponent, null);
    }

    @Override
    FittedViewI createContent(VideoUseComponentI videoUseComponent) {
        autoRetryVideoPlayerView = new AutoRetryVideoPlayerView();
        autoRetryVideoPlayerView.setVideoFile(videoUseComponent.videoProperty().get().getPath(), player -> {
            player.setOnEndOfMedia(this::hide);
            videoUseComponent.configureVideoPlayer(player, autoRetryVideoPlayerView);
        });
        return new MediaViewFittedView(autoRetryVideoPlayerView);
    }

    @Override
    void onHiding() {
        autoRetryVideoPlayerView.disposePlayer();
    }
}
