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

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.fx.io.*;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wrapper for image use component properties.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ImageUseComponentPropertyWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUseComponentPropertyWrapper.class);

    /**
     * The minimum size increase that must be done to update the loaded image.<br>
     * A low value will cause a lot of image loading on resizing
     */
    private final static int RESIZE_THRESHOLD = 15;

    /**
     * The scale factor that is use to load a image : this is used to always request a greater image than current key size, to have a better quality
     */
    private final static double LOADING_SCALE_FACTOR = 1.2;

    /**
     * The object property that contains the loaded image, the content mustn't be change by external user
     */
    private final ObjectProperty<Image> loadedImage;

    /**
     * The last width and height that was used to update the image size.<br>
     * This is use with {@link #RESIZE_THRESHOLD} to only ask for a image loading when it's needed
     */
    private final transient AtomicInteger previousWidthUpdate, previousHeightUpdate;

    /**
     * Rotate property for the image
     */
    @XMLIgnoreDefaultDoubleValue(0.0)
    private final DoubleProperty rotate;

    @XMLIgnoreDefaultDoubleValue(1.0)
    private final DoubleProperty scaleX;

    @XMLIgnoreDefaultDoubleValue(1.0)
    private final DoubleProperty scaleY;

    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty enableReplaceColorToGrey;

    /**
     * The gallery image used by this comp..<br>
     * Sometime, the image can not exist into gallery, if the user set a image and delete it from gallery.
     */
    private final SimpleObjectProperty<ImageElementI> imageVTwo;

    /**
     * Preserve the image ratio
     */
    @XMLIgnoreDefaultBooleanValue(true)
    private final BooleanProperty preserveRatio;

    /**
     * Use viewport
     */
    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty useViewPort;

    /**
     * Computed viewport
     */
    private final ObjectProperty<Rectangle2D> viewport;

    /**
     * Viewport properties
     */
    @XMLIgnoreNullValue
    private final DoubleProperty viewportXPercent;

    @XMLIgnoreNullValue
    private final DoubleProperty viewportYPercent;

    @XMLIgnoreNullValue
    private final DoubleProperty viewportWidthPercent;

    @XMLIgnoreNullValue
    private final DoubleProperty viewportHeightPercent;

    /**
     * Replace color
     */
    @XMLIgnoreNullValue
    private final IntegerProperty replaceColorThreshold;

    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty enableRemoveBackground;

    @XMLIgnoreNullValue
    private final IntegerProperty removeBackgroundThreshold;

    /**
     * Image use component associated
     */
    private final ImageUseComponentI imageUseComponent;

    /**
     * Color to replace
     */
    @XMLIgnoreNullValue
    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> colorToReplace;

    @XMLIgnoreNullValue
    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> replacingColor;

    /**
     * Enable image color replace
     */
    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty enableReplaceColor;

    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty displayInFullScreen;

    private final Set<String> externalLoadingRequest;

    private final InvalidationListener invalidationListenerForAppMode;

    private final transient BooleanProperty imageAutomaticallySelected;

    public ImageUseComponentPropertyWrapper(final ImageUseComponentI imageUseComponentP) {
        this.imageUseComponent = imageUseComponentP;
        this.preserveRatio = new SimpleBooleanProperty(true);
        this.loadedImage = new SimpleObjectProperty<>();
        this.imageVTwo = new SimpleObjectProperty<>();
        this.useViewPort = new SimpleBooleanProperty();
        this.enableReplaceColor = new SimpleBooleanProperty();
        this.colorToReplace = new SimpleObjectProperty<>(Color.WHITE);
        this.replacingColor = new SimpleObjectProperty<>(Color.TRANSPARENT);
        this.viewport = new SimpleObjectProperty<>();
        this.replaceColorThreshold = new SimpleIntegerProperty(LCConstant.DEFAULT_COLOR_REPLACE_THRESHOLD);
        this.enableRemoveBackground = new SimpleBooleanProperty();
        this.removeBackgroundThreshold = new SimpleIntegerProperty(LCConstant.DEFAULT_COLOR_REPLACE_THRESHOLD);
        this.viewportXPercent = new SimpleDoubleProperty(0.0);
        this.viewportYPercent = new SimpleDoubleProperty(0.0);
        this.viewportWidthPercent = new SimpleDoubleProperty(0.0);
        this.viewportHeightPercent = new SimpleDoubleProperty(0.0);
        this.imageAutomaticallySelected = new SimpleBooleanProperty(false);
        this.rotate = new SimpleDoubleProperty(0.0);
        this.scaleX = new SimpleDoubleProperty(1.0);
        this.scaleY = new SimpleDoubleProperty(1.0);
        this.enableReplaceColorToGrey = new SimpleBooleanProperty(false);
        this.previousWidthUpdate = new AtomicInteger();
        this.previousHeightUpdate = new AtomicInteger();
        this.externalLoadingRequest = new HashSet<>();
        this.displayInFullScreen = new SimpleBooleanProperty(false);
        this.invalidationListenerForAppMode = inv -> this.requestImageLoadingForThisImageUseComponentIfNeeded();
        this.initImageBinding();
        this.initViewportBinding();
    }

    // Class part : "Binding"
    //========================================================================
    private void addListenerForSizeUpdate(ReadOnlyDoubleProperty sizeProp, AtomicInteger previousSizeUpdate) {
        sizeProp.addListener((observableP, oldValueP, newValueP) -> {
            if (oldValueP.intValue() < newValueP.intValue()) {
                if (previousSizeUpdate.get() == 0 && newValueP.intValue() > 0 || newValueP.intValue() - previousSizeUpdate.get() > ImageUseComponentPropertyWrapper.RESIZE_THRESHOLD) {
                    previousSizeUpdate.set(newValueP.intValue());
                    this.requestImageLoadingForThisImageUseComponentIfNeeded();
                }
            }
        });
    }


    /**
     * Init image binding : change size on increase, load when needed, etc...
     */
    private void initImageBinding() {
        addListenerForSizeUpdate(this.imageUseComponent.wantedImageWidthProperty(), previousWidthUpdate);
        addListenerForSizeUpdate(this.imageUseComponent.wantedImageHeightProperty(), previousHeightUpdate);
        //Bind the loaded image (and request load on change)
        this.imageVTwo.addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.loadedImage.unbind();
                ov.requestImageUnload(getImageUseComponentID());
                externalLoadingRequest.forEach(ov::requestImageUnload);
            }
            if (nv != null) {
                this.loadedImage.bind(nv.loadedImageProperty());
                this.requestImageLoadingForThisImageUseComponentIfNeeded();
                externalLoadingRequest.forEach(this::requestImageLoadingForComponentWithId);
            } else {
                this.loadedImage.set(null);
            }
        });

        // When image should be loaded, request the loading.
        // If the image shouldn't be loaded, remove the loaded image => free memory
        this.imageUseComponent.imageUseComponentDisplayedProperty().addListener((obs, ov, nv) -> {
            if (this.imageVTwo.get() != null) {
                if (nv) {
                    this.requestImageLoadingForThisImageUseComponentIfNeeded();
                } else {
                    this.imageVTwo.get().requestImageUnload(getImageUseComponentID());
                }
            }
        });
        // On mode changed, check that image should be loaded
        AppModeController.INSTANCE.modeProperty().addListener(new WeakInvalidationListener(invalidationListenerForAppMode));
    }

    private String getImageUseComponentID() {
        // Combine component ID and instance ID (as the same component can be used in use/edit mode at the same time)
        return this.imageUseComponent.getID() + "-" + this.imageUseComponent.hashCode();
    }

    // FIXME : config value
    private double getWantedImageWidthValue() {
        return LOADING_SCALE_FACTOR * (AppModeController.INSTANCE.isUseMode() || this.imageUseComponent.wantedImageWidthProperty().get() > 0 ? this.imageUseComponent.wantedImageWidthProperty()
                .get() : 100.0);
    }

    private double getWantedImageHeightValue() {
        return LOADING_SCALE_FACTOR * (AppModeController.INSTANCE.isUseMode() || this.imageUseComponent.wantedImageHeightProperty().get() > 0 ? this.imageUseComponent.wantedImageHeightProperty()
                .get() : 100.0);
    }


    private void requestImageLoadingForThisImageUseComponentIfNeeded() {
        if (this.imageUseComponent.imageUseComponentDisplayedProperty().get()) {
            this.requestImageLoadingForComponentWithId(getImageUseComponentID(), null, null, null);
        }
    }

    private void requestImageLoadingForComponentWithId(String id) {
        requestImageLoadingForComponentWithId(id, null, null, null);
    }

    private void requestImageLoadingForComponentWithId(String id, Double width, Double height, Double scale) {
        if (imageVTwo.get() != null) {
            imageVTwo.get().requestImageLoad(id, (scale != null ? scale : 1.0) * (width != null ? width : getWantedImageWidthValue()), (scale != null ? scale : 1.0) * (height != null ? height : getWantedImageHeightValue()), true, true);
        }
    }

    public void addExternalLoadingRequest(String id) {
        addExternalLoadingRequest(id,null,null,null);
    }

    public void addExternalLoadingRequest(String id, double scale) {
        addExternalLoadingRequest(id,null,null,scale);
    }

    public void addExternalLoadingRequest(String id, double width, double height) {
        addExternalLoadingRequest(id,width,height,null);
    }

    public void addExternalLoadingRequest(String id, Double width, Double height, Double scale) {
        externalLoadingRequest.add(id);
        if (imageVTwo.get() != null) {
            this.requestImageLoadingForComponentWithId(id, width, height, scale);
        }
    }

    public void removeExternalLoadingRequest(String id) {
        externalLoadingRequest.remove(id);
        if (imageVTwo.get() != null) {
            imageVTwo.get().requestImageUnload(id);
        }
    }

    /**
     * Initialize the image viewport binding
     */
    private void initViewportBinding() {
        //When viewport is enabled, the default viewport is the image bounds
        this.useViewPort.addListener((obs, ov, nv) -> {
            if (nv) {
                this.viewportXPercent.set(0);
                this.viewportYPercent.set(0);
                this.viewportWidthPercent.set(1.0);
                this.viewportHeightPercent.set(1.0);
            }
        });
        //View should change when disabled, or value changed
        this.viewport.bind(Bindings.createObjectBinding(() -> {
            if (this.useViewPort.get() && this.loadedImage.get() != null) {
                return this.computeViewport();
            } else {
                return null;
            }
        }, this.viewportXPercent, this.viewportYPercent, this.viewportWidthPercent, this.viewportHeightPercent, this.useViewPort, this.loadedImage));
    }

    private Rectangle2D computeViewport() {
        Image image = this.loadedImage.get();
        return new Rectangle2D(this.viewportXPercent.get() * image.getWidth(), this.viewportYPercent.get() * image.getHeight(),
                this.viewportWidthPercent.get() * image.getWidth(), this.viewportHeightPercent.get() * image.getHeight());
    }
    //========================================================================

    // Class part : "Properties"
    //========================================================================
    public BooleanProperty preserveRatioProperty() {
        return this.preserveRatio;
    }

    public BooleanProperty useViewPortProperty() {
        return this.useViewPort;
    }

    public ReadOnlyObjectProperty<Rectangle2D> viewportProperty() {
        return this.viewport;
    }

    public DoubleProperty viewportXPercentProperty() {
        return this.viewportXPercent;
    }

    public DoubleProperty viewportYPercentProperty() {
        return this.viewportYPercent;
    }

    public DoubleProperty viewportWidthPercentProperty() {
        return this.viewportWidthPercent;
    }

    public DoubleProperty viewportHeightPercentProperty() {
        return this.viewportHeightPercent;
    }

    public ReadOnlyObjectProperty<Image> loadedImageProperty() {
        return this.loadedImage;
    }

    public SimpleObjectProperty<ImageElementI> imageVTwoProperty() {
        return this.imageVTwo;
    }

    public DoubleProperty rotateProperty() {
        return this.rotate;
    }

    public DoubleProperty scaleXProperty() {
        return this.scaleX;
    }

    public DoubleProperty scaleYProperty() {
        return this.scaleY;
    }

    public BooleanProperty enableColorToGreyProperty() {
        return this.enableReplaceColorToGrey;
    }

    public BooleanProperty enableReplaceColorProperty() {
        return this.enableReplaceColor;
    }

    public ObjectProperty<Color> colorToReplaceProperty() {
        return this.colorToReplace;
    }

    public ObjectProperty<Color> replacingColorProperty() {
        return this.replacingColor;
    }

    public IntegerProperty replaceColorThresholdProperty() {
        return this.replaceColorThreshold;
    }

    public BooleanProperty enableRemoveBackgroundProperty() {
        return this.enableRemoveBackground;
    }

    public IntegerProperty removeBackgroundThresholdProperty() {
        return this.removeBackgroundThreshold;
    }

    public BooleanProperty imageAutomaticallySelectedProperty() {
        return imageAutomaticallySelected;
    }

    public BooleanProperty displayInFullScreenProperty() {
        return this.displayInFullScreen;
    }
    //========================================================================

    // Class part : "XML serialization"
    //========================================================================
    private static final String ATB_IMAGE_ID = "imageId", ATB_IMAGE_NAME = "imageName";
    private static final String ATB_IMAGE_ID2 = "imageId2";

    public void serialize(final Element element, final IOContextI contextP) {
        // Image information are saved only if a image is selected (saving space!)
        if (this.imageVTwo.get() != null) {
            XMLObjectSerializer.serializeInto(ImageUseComponentPropertyWrapper.class, this, element);

            // Some parameter are also saved if needed
            if (!useViewPort.get()) {
                element.removeAttribute("viewportXPercent");
                element.removeAttribute("viewportYPercent");
                element.removeAttribute("viewportWidthPercent");
                element.removeAttribute("viewportHeightPercent");
            }
            if (!enableReplaceColor.get()) {
                element.removeAttribute("colorToReplace");
                element.removeAttribute("replacingColor");
                element.removeAttribute("replaceColorThreshold");
            }
            if (!enableRemoveBackground.get()) {
                element.removeAttribute("enableRemoveBackground");
                element.removeAttribute("removeBackgroundThreshold");
            }

            //Image saving : just set the id and delegate to root action the "real" saving
            serializeImageUse(this.imageVTwo.get(), element, contextP);
        }
    }

    public void deserialize(final Element element, final IOContextI contextP) {
        // Check there is an ID
        if (element.getAttribute(ATB_IMAGE_ID2) != null || element.getAttribute(ATB_IMAGE_ID) != null) {
            XMLObjectSerializer.deserializeInto(ImageUseComponentPropertyWrapper.class, this, element);
            // backward compatibility - enableReplaceColorByTransparent > enableReplaceColor
            if (element.getAttribute("enableReplaceColorByTransparent") != null) {
                XMLUtils.read(enableReplaceColor, "enableReplaceColorByTransparent", element);
            }
            //Image loading from gallery
            this.imageVTwo.set(deserializeImageUseV2(element, contextP));
        }
    }
    //========================================================================

    // Class part : "Public API : image user"
    //========================================================================
    public static void serializeImageUse(ImageElementI image, final Element element, final IOContextI contextP) {
        contextP.getImagesToSaveV2().add(image);
        if (image.shouldSaveImageId()) {
            XMLUtils.write(image.getId(), ImageUseComponentPropertyWrapper.ATB_IMAGE_ID2, element);
            XMLUtils.write(image.getName(), ImageUseComponentPropertyWrapper.ATB_IMAGE_NAME, element);
        } else {
            XMLUtils.write("null", ImageUseComponentPropertyWrapper.ATB_IMAGE_ID2, element);
        }
    }

    public static ImageElementI deserializeImageUseV2(final Element element, IOContextI contextP) {
        String imageId;
        // Backward compatibly : replace old IDS
        String oldImageId = XMLUtils.readString(ImageUseComponentPropertyWrapper.ATB_IMAGE_ID, element);
        if (StringUtils.isNotBlank(oldImageId)) {
            imageId = contextP.getBackwardImageCompatibilityIdsMap().get(oldImageId);
        } else {
            imageId = XMLUtils.readString(ImageUseComponentPropertyWrapper.ATB_IMAGE_ID2, element);
        }
        // Image was loaded by the task before going into this part (see AbstractLoadUtilsTask)
        ImageElementI imageElement = ImageDictionaries.INSTANCE.getById(imageId);
        String imageName = XMLUtils.readString(ImageUseComponentPropertyWrapper.ATB_IMAGE_NAME, element);
        if (StringUtils.isNotBlank(imageName) && imageElement != null && (imageElement.getDictionary() == null || imageElement.getDictionary().isCustomDictionary())) {
            imageElement.updateNameAndKeywords(imageName, UserConfigurationController.INSTANCE.userLanguageProperty().get(), new String[]{imageName});
        }
        return imageElement;
    }
    //========================================================================
}
