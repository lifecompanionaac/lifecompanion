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

package org.lifecompanion.model.impl.configurationcomponent.dynamickey;

import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.TreeIdentifiableComponentI;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.configurationcomponent.VideoUseComponentPropertyWrapper;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.ImageUseComponentPropertyWrapper;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.binding.BindingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractSimplerKeyContentContainer implements SimplerKeyContentContainerI {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimplerKeyContentContainer.class);

    private String id;

    private transient final DoubleProperty wantedImageWidth;
    private transient final DoubleProperty wantedImageHeight;
    private transient final BooleanProperty imageUseComponentDisplayed;

    @XMLIgnoreNullValue
    private final StringProperty text;

    @XMLGenericProperty(Color.class)
    @XMLIgnoreNullValue
    private final ObjectProperty<Color> backgroundColor, strokeColor, textColor;

    private final ImageUseComponentPropertyWrapper imageUseComponentPropertyWrapper;
    private final VideoUseComponentPropertyWrapper videoUseComponentPropertyWrapper;

    @XMLGenericProperty(TextPosition.class)
    private final ObjectProperty<TextPosition> textPosition;

    protected AbstractSimplerKeyContentContainer() {
        this.generateID();
        text = new SimpleStringProperty("");
        backgroundColor = new SimpleObjectProperty<>(null);
        strokeColor = new SimpleObjectProperty<>(null);
        textColor = new SimpleObjectProperty<>(null);
        wantedImageWidth = new SimpleDoubleProperty(0.0);
        wantedImageHeight = new SimpleDoubleProperty(0.0);
        imageUseComponentDisplayed = new SimpleBooleanProperty(false);
        imageUseComponentPropertyWrapper = new ImageUseComponentPropertyWrapper(this);
        videoUseComponentPropertyWrapper = new VideoUseComponentPropertyWrapper(this);
        textPosition = new SimpleObjectProperty<>();
    }

    @Override
    public ObjectProperty<VideoElementI> videoProperty() {
        return this.videoUseComponentPropertyWrapper.videoProperty();
    }

    @Override
    public ReadOnlyBooleanProperty displayVideoInsteadOfThumbnail() {
        return this.videoUseComponentPropertyWrapper.videoShouldBeDisplayedProperty();
    }

    @Override
    public void useActionEventExecuted(UseActionEvent event) {
        this.videoUseComponentPropertyWrapper.useActionEventExecuted(event);
    }

    // PROPS
    //========================================================================
    @Override
    public StringProperty textProperty() {
        return text;
    }

    @Override
    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    @Override
    public ObjectProperty<Color> strokeColorProperty() {
        return strokeColor;
    }

    @Override
    public ObjectProperty<TextPosition> textPositionProperty() {
        return textPosition;
    }

    @Override
    public ObjectProperty<Color> textColorProperty() {
        return textColor;
    }

    @Override
    public String getID() {
        return id;
    }

    @Deprecated
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String generateID() {
        id = StringUtils.getNewID();
        return this.id;
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
    }

    protected void changeId(String id) {
        this.id = id;
    }

    @Override
    public boolean isTreeIdentifiableComponentLeaf() {
        return true;
    }

    @Override
    public <T extends TreeIdentifiableComponentI> List<T> getTreeIdentifiableChildren() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(text.get()) && imageVTwoProperty().get() == null;
    }
    //========================================================================


    // IMAGE
    //========================================================================
    @Override
    public void bindImageDisplayProperties(ImageUseComponentI imageUseComponent) {
        if (imageUseComponent != null) {
            this.imageUseComponentDisplayed.bind(imageUseComponent.imageUseComponentDisplayedProperty());
            this.wantedImageWidth.bind(imageUseComponent.wantedImageWidthProperty());
            this.wantedImageHeight.bind(imageUseComponent.wantedImageHeightProperty());
        } else {
            BindingUtils.unbindAndSet(imageUseComponentDisplayed, false);
            BindingUtils.unbindAndSet(wantedImageWidth, 0.0);
            BindingUtils.unbindAndSet(wantedImageHeight, 0.0);
        }
    }

    @Override
    public ObservableBooleanValue imageUseComponentDisplayedProperty() {
        return imageUseComponentDisplayed;
    }

    @Override
    public void addExternalLoadingRequest(String id) {
        imageUseComponentPropertyWrapper.addExternalLoadingRequest(id);
    }

    @Override
    public void removeExternalLoadingRequest(String id) {
        imageUseComponentPropertyWrapper.removeExternalLoadingRequest(id);
    }

    @Override
    public SimpleObjectProperty<ImageElementI> imageVTwoProperty() {
        return imageUseComponentPropertyWrapper.imageVTwoProperty();
    }

    @Override
    public ReadOnlyObjectProperty<Image> loadedImageProperty() {
        return imageUseComponentPropertyWrapper.loadedImageProperty();
    }

    @Override
    public BooleanProperty preserveRatioProperty() {
        return imageUseComponentPropertyWrapper.preserveRatioProperty();
    }

    @Override
    public DoubleProperty rotateProperty() {
        return imageUseComponentPropertyWrapper.rotateProperty();
    }

    @Override
    public BooleanProperty useViewPortProperty() {
        return imageUseComponentPropertyWrapper.useViewPortProperty();
    }

    @Override
    public ReadOnlyObjectProperty<Rectangle2D> viewportProperty() {
        return imageUseComponentPropertyWrapper.viewportProperty();
    }

    @Override
    public BooleanProperty enableReplaceColorProperty() {
        return imageUseComponentPropertyWrapper.enableReplaceColorProperty();
    }

    @Override
    public ObjectProperty<Color> colorToReplaceProperty() {
        return imageUseComponentPropertyWrapper.colorToReplaceProperty();
    }

    @Override
    public ObjectProperty<Color> replacingColorProperty() {
        return imageUseComponentPropertyWrapper.replacingColorProperty();
    }

    @Override
    public IntegerProperty replaceColorThresholdProperty() {
        return imageUseComponentPropertyWrapper.replaceColorThresholdProperty();
    }

    @Override
    public DoubleProperty viewportXPercentProperty() {
        return imageUseComponentPropertyWrapper.viewportXPercentProperty();
    }

    @Override
    public DoubleProperty viewportYPercentProperty() {
        return imageUseComponentPropertyWrapper.viewportYPercentProperty();
    }

    @Override
    public DoubleProperty viewportWidthPercentProperty() {
        return imageUseComponentPropertyWrapper.viewportWidthPercentProperty();
    }

    @Override
    public DoubleProperty viewportHeightPercentProperty() {
        return imageUseComponentPropertyWrapper.viewportHeightPercentProperty();
    }

    @Override
    public ReadOnlyDoubleProperty wantedImageWidthProperty() {
        return wantedImageWidth;
    }

    @Override
    public ReadOnlyDoubleProperty wantedImageHeightProperty() {
        return wantedImageHeight;
    }


    //========================================================================

    // IO
    //========================================================================
    protected abstract String getNodeName();

    @Override
    public Element serialize(IOContextI context) {
        Element node = new Element(getNodeName());
        ConfigurationComponentIOHelper.addTypeAlias(this, node, context);
        XMLObjectSerializer.serializeInto(AbstractSimplerKeyContentContainer.class, this, node);
        this.imageUseComponentPropertyWrapper.serialize(node, context);
        this.videoUseComponentPropertyWrapper.serialize(node, context);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(AbstractSimplerKeyContentContainer.class, this, node);
        this.imageUseComponentPropertyWrapper.deserialize(node, context);
        this.videoUseComponentPropertyWrapper.deserialize(node, context);
    }
    //========================================================================
}
