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
import javafx.beans.value.ChangeListener;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
    private final BooleanProperty videoShouldBeDisplayed;

    public VideoUseComponentPropertyWrapper(final VideoUseComponentI videoUseComponent) {
        this.videoUseComponent = videoUseComponent;
        this.videoShouldBeDisplayed = new SimpleBooleanProperty(false);
        video = new SimpleObjectProperty<>();
        ChangeListener<File> thumbnailPathListener = (obs, ov, nv) -> {
            if (!videoUseComponent.imageVTwoProperty().isBound()) {
                if (nv != null) {
                    videoUseComponent.imageVTwoProperty().set(ImageDictionaries.INSTANCE.getOrAddToHiddenImageDictionary(nv));
                } else {
                    videoUseComponent.imageVTwoProperty().set(null);
                }
            } else {
                // FIXME : handling already bound
                System.err.println("IMAGE IS ALREADY BOUND !");
            }
        };
        video.addListener((obs, ov, nv) -> {
            if (ov != null) ov.thumbnailPathProperty().removeListener(thumbnailPathListener);
            if (nv != null) {
                thumbnailPathListener.changed(null, null, nv.thumbnailPathProperty().get());
                nv.thumbnailPathProperty().addListener(thumbnailPathListener);
            }
        });
    }

    public void useActionEventExecuted(final UseActionEvent event) {
        if (event == UseActionEvent.ACTIVATION) {
            FXThreadUtils.runOnFXThread(() -> this.videoShouldBeDisplayed.set(true));
        }
    }

    public ObjectProperty<VideoElementI> videoProperty() {
        return this.video;
    }

    public ReadOnlyBooleanProperty videoShouldBeDisplayedProperty() {
        return this.videoShouldBeDisplayed;
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
