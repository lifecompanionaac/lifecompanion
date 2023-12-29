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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.*;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.VideoDisplayMode;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.configurationcomponent.VideoPlayMode;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VideoUseComponentPropertyWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoUseComponentPropertyWrapper.class);
    /**
     * Image use component associated
     */
    private final VideoUseComponentI videoUseComponent;

    private final ObjectProperty<VideoElementI> video;

    //@XMLGenericProperty(VideoPlayMode.class)
    private final ObjectProperty<VideoPlayMode> videoPlayMode;

    //@XMLGenericProperty(VideoDisplayMode.class)
    private final ObjectProperty<VideoDisplayMode> videoDisplayMode;

    @XMLIgnoreDefaultBooleanValue(true)
    private final BooleanProperty muteVideo;

    public VideoUseComponentPropertyWrapper(final VideoUseComponentI videoUseComponent) {
        this.videoUseComponent = videoUseComponent;
        this.video = new SimpleObjectProperty<>();
        this.muteVideo = new SimpleBooleanProperty(true);
        this.videoPlayMode = new SimpleObjectProperty<>(VideoPlayMode.ON_ACTIVATION);
        this.videoDisplayMode = new SimpleObjectProperty<>(VideoDisplayMode.IN_KEY);
    }

    public ObjectProperty<VideoElementI> videoProperty() {
        return this.video;
    }


    public ObjectProperty<VideoDisplayMode> videoDisplayModeProperty() {
        return this.videoDisplayMode;
    }

    public ObjectProperty<VideoPlayMode> videoPlayModeProperty() {
        return this.videoPlayMode;
    }

    public BooleanProperty muteVideoProperty() {
        return muteVideo;
    }

    private static final String ATB_VIDEO_ID = "videoId";

    public void serialize(final Element element, final IOContextI contextP) {
        if (this.videoProperty().get() != null) {
            XMLObjectSerializer.serializeInto(VideoUseComponentPropertyWrapper.class, this, element);
            serializeVideoUse(this.videoProperty().get(), element, contextP);
        }
    }

    public void deserialize(final Element element, final IOContextI contextP) {
        if (element.getAttribute(ATB_VIDEO_ID) != null) {
            XMLObjectSerializer.deserializeInto(VideoUseComponentPropertyWrapper.class, this, element);
            this.video.set(deserializeVideoUse(element, contextP));
        }
    }

    public static void serializeVideoUse(VideoElementI video, final Element element, final IOContextI contextP) {
        contextP.getVideos().put(video.getId(), video);
        XMLUtils.write(video.getId(), VideoUseComponentPropertyWrapper.ATB_VIDEO_ID, element);
    }

    public static VideoElementI deserializeVideoUse(final Element element, IOContextI contextP) {
        String videoId = XMLUtils.readString(VideoUseComponentPropertyWrapper.ATB_VIDEO_ID, element);
        if (StringUtils.isNotBlank(videoId)) {
            return contextP.getVideos().get(videoId);
        } else {
            return null;
        }
    }


}
