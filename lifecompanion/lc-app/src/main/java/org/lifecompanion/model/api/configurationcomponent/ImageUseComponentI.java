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
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;

/**
 * Represent a component that can use a image.<br>
 * The component should inform if the image will be displayed (e.g. if it should be loaded) via {@link #imageUseComponentDisplayedProperty()}.<br>
 * Manual loading request can be added with {@link #addExternalLoadingRequest(String)} but should be then delete with {@link #removeExternalLoadingRequest(String)} when image can be freed.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ImageUseComponentI extends IdentifiableComponentI {
    /**
     * @return the property that contains the image used by this component
     */
    SimpleObjectProperty<ImageElementI> imageVTwoProperty();

    /**
     * @return the object property that can contains the loaded image associated to the {@link #imageVTwoProperty()} ()}
     */
    ReadOnlyObjectProperty<Image> loadedImageProperty();

    /**
     * @return a property to preserve the image ratio
     */
    BooleanProperty preserveRatioProperty();

    /**
     * @return the rotate value for the image
     */
    DoubleProperty rotateProperty();

    /**
     * @return the scale X value for the image
     */
    DoubleProperty scaleXProperty();

    /**
     * @return the scale Y value for the image
     */
    DoubleProperty scaleYProperty();

    /**
     * @return a property enabling the grey image to be activated
     */
    BooleanProperty enableColourToGreyProperty();

    /**
     * @return if this image uses a viewport to define how the image is displayed
     */
    BooleanProperty useViewPortProperty();

    /**
     * @return the view port resulting of the current image use parameters.<br>
     * Returns null if the view port is not enabled, or if it's not needed.
     */
    ReadOnlyObjectProperty<Rectangle2D> viewportProperty();

    /**
     * @return to enable the color replace
     */
    BooleanProperty enableReplaceColorProperty();

    /**
     * @return the color to replace with a transparent color (only if {@link #enableReplaceColorProperty()})
     */
    ObjectProperty<Color> colorToReplaceProperty();

    /**
     * @return the color to fill the replaced color
     */
    ObjectProperty<Color> replacingColorProperty();

    /**
     * @return the color threshold to find pixel to replace
     */
    IntegerProperty replaceColorThresholdProperty();

    /**
     * @return to enable the remove background
     */
    BooleanProperty enableRemoveBackgroundProperty();

    /**
     * @return the colour threshold to determine the background colour to be removed
     */
    IntegerProperty removeBackgroundThresholdProperty();

    /**
     * @return the viewport x position in percent relative to the image width
     */
    DoubleProperty viewportXPercentProperty();

    /**
     * @return the viewport y position in percent relative to the image height
     */
    DoubleProperty viewportYPercentProperty();

    /**
     * @return the viewport width in percent relative to the image width
     */
    DoubleProperty viewportWidthPercentProperty();

    /**
     * @return the viewport height in percent relative to the image height
     */
    DoubleProperty viewportHeightPercentProperty();

    /**
     * @return a property that should return the wanted image width
     */
    ReadOnlyDoubleProperty wantedImageWidthProperty();

    /**
     * @return a property that should return the wanted image height
     */
    ReadOnlyDoubleProperty wantedImageHeightProperty();

    /**
     * @return true if this image use component is displayed : this should be used to unload hidden images
     */
    ObservableBooleanValue imageUseComponentDisplayedProperty();

    /**
     * @return a runtime property that indicates if this element image was selected automatically, can be useful to know if the previous auto selected image can be replaced
     */
    BooleanProperty imageAutomaticallySelectedProperty();

    void addExternalLoadingRequest(String id);

    void removeExternalLoadingRequest(String id);
}
