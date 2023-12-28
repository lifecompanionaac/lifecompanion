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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public VideoUseComponentPropertyWrapper(final VideoUseComponentI videoUseComponent) {
        this.videoUseComponent = videoUseComponent;
        video = new SimpleObjectProperty<>();
    }

    public ObjectProperty<VideoElementI> videoProperty() {
        return this.video;
    }


    private static final String ATB_IMAGE_ID = "imageId", ATB_IMAGE_NAME = "imageName";
    private static final String ATB_IMAGE_ID2 = "imageId2";

    public void serialize(final Element element, final IOContextI contextP) {
        //        // Image information are saved only if a image is selected (saving space!)
        //        if (this.imageVTwo.get() != null) {
        //            XMLObjectSerializer.serializeInto(VideoUseComponentPropertyWrapper.class, this, element);
        //
        //            // Some parameter are also saved if needed
        //            if (!useViewPort.get()) {
        //                element.removeAttribute("viewportXPercent");
        //                element.removeAttribute("viewportYPercent");
        //                element.removeAttribute("viewportWidthPercent");
        //                element.removeAttribute("viewportHeightPercent");
        //            }
        //            if (!enableReplaceColor.get()) {
        //                element.removeAttribute("colorToReplace");
        //                element.removeAttribute("replacingColor");
        //                element.removeAttribute("replaceColorThreshold");
        //            }
        //
        //            //Image saving : just set the id and delegate to root action the "real" saving
        //            serializeImageUse(this.imageVTwo.get(), element, contextP);
        //        }
    }

    public void deserialize(final Element element, final IOContextI contextP) {
        //        // Check there is an ID
        //        if (element.getAttribute(ATB_IMAGE_ID2) != null || element.getAttribute(ATB_IMAGE_ID) != null) {
        //            XMLObjectSerializer.deserializeInto(VideoUseComponentPropertyWrapper.class, this, element);
        //            // backward compatibility - enableReplaceColorByTransparent > enableReplaceColor
        //            if (element.getAttribute("enableReplaceColorByTransparent") != null) {
        //                XMLUtils.read(enableReplaceColor, "enableReplaceColorByTransparent", element);
        //            }
        //            //Image loading from gallery
        //            this.imageVTwo.set(deserializeImageUseV2(element, contextP));
        //        }
    }

    public static void serializeVideoUse(ImageElementI image, final Element element, final IOContextI contextP) {
        //        contextP.getImagesToSaveV2().add(image);
        //        if (image.shouldSaveImageId()) {
        //            XMLUtils.write(image.getId(), VideoUseComponentPropertyWrapper.ATB_IMAGE_ID2, element);
        //            XMLUtils.write(image.getName(), VideoUseComponentPropertyWrapper.ATB_IMAGE_NAME, element);
        //        } else {
        //            XMLUtils.write("null", VideoUseComponentPropertyWrapper.ATB_IMAGE_ID2, element);
        //        }
    }

    public static VideoElementI deserializeVideoUse(final Element element, IOContextI contextP) {
        //        String imageId;
        //        // Backward compatibly : replace old IDS
        //        String oldImageId = XMLUtils.readString(VideoUseComponentPropertyWrapper.ATB_IMAGE_ID, element);
        //        if (StringUtils.isNotBlank(oldImageId)) {
        //            imageId = contextP.getBackwardImageCompatibilityIdsMap().get(oldImageId);
        //        } else {
        //            imageId = XMLUtils.readString(VideoUseComponentPropertyWrapper.ATB_IMAGE_ID2, element);
        //        }
        //        // Image was loaded by the task before going into this part (see AbstractLoadUtilsTask)
        //        ImageElementI imageElement = ImageDictionaries.INSTANCE.getById(imageId);
        //        String imageName = XMLUtils.readString(VideoUseComponentPropertyWrapper.ATB_IMAGE_NAME, element);
        //        if (StringUtils.isNotBlank(imageName) && imageElement != null && (imageElement.getDictionary() == null || imageElement.getDictionary().isCustomDictionary())) {
        //            imageElement.updateNameAndKeywords(imageName, UserConfigurationController.INSTANCE.userLanguageProperty().get(), new String[]{imageName});
        //        }
        //        return imageElement;
        return null;
    }


}
