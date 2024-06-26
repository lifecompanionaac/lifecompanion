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

package org.lifecompanion.model.api.configurationcomponent;

import javafx.beans.property.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Represent a component that can use a video.<br>
 * A component using a video should always be a component that can also use an image.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface VideoUseComponentI extends ImageUseComponentI, IdentifiableComponentI {
    ObjectProperty<VideoElementI> videoProperty();

    ObjectProperty<VideoDisplayMode> videoDisplayModeProperty();

    ObjectProperty<VideoPlayMode> videoPlayModeProperty();

    BooleanProperty muteVideoProperty();

    default void configureVideoPlayer(MediaPlayer mediaPlayer, MediaView mediaView) {
        VideoPlayMode videoPlayMode = this.videoPlayModeProperty().get();
        VideoDisplayMode videoDisplayMode = this.videoDisplayModeProperty().get();
        mediaPlayer.setAutoPlay(videoDisplayMode == VideoDisplayMode.FULLSCREEN || videoPlayMode.isAutoplay());
        mediaPlayer.setCycleCount(videoDisplayMode == VideoDisplayMode.FULLSCREEN ? MediaPlayer.INDEFINITE : videoPlayMode.getCycleCount());
        mediaPlayer.setMute(muteVideoProperty().get());
        mediaView.setPreserveRatio(preserveRatioProperty().get());
        mediaView.setRotate(rotateProperty().get());
        mediaView.setScaleX(scaleXProperty().get());
        mediaView.setScaleY(scaleYProperty().get());
    }
}
