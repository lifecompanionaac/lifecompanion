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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.categorizedelement.useaction.ActionEventType;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionManagerI;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionManager;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.*;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.BasicKeyOption;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.QuickComKeyOption;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This component is a final component in grid : its the key that user will select to do final action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartKeyComponent extends GridPartComponentBaseImpl implements GridPartKeyComponentI {

    /**
     * The text property that represent the textual content of this key
     */
    private final StringProperty textContent;

    /**
     * The current key option for this key
     */
    private final ObjectProperty<KeyOptionI> keyOption;

    /**
     * Wanted image size for loading (binding on layout with configuration scale)
     */
    protected transient final DoubleProperty wantedImageWidth, wantedImageHeight;

    /**
     * Component that handle the action on this key
     */
    private final UseActionManagerI actionManager;

    /**
     * Image use property wrapper
     */
    private final ImageUseComponentPropertyWrapper imageUseComponentPropertyWrapper;

    private final VideoUseComponentPropertyWrapper videoUseComponentPropertyWrapper;

    private final Set<BiConsumer<ActionEventType, UseActionEvent>> eventFiredListeners;

    public GridPartKeyComponent() {
        super();
        this.textContent = new SimpleStringProperty(this, "textContent");
        this.actionManager = new SimpleUseActionManager(this, UseActionEvent.ACTIVATION, UseActionEvent.OVER);
        this.keyOption = new SimpleObjectProperty<>(this, "keyOption");
        this.wantedImageWidth = new SimpleDoubleProperty(this, "wantedImageWidth");
        this.wantedImageHeight = new SimpleDoubleProperty(this, "wantedImageHeight");
        this.imageUseComponentPropertyWrapper = new ImageUseComponentPropertyWrapper(this);
        this.videoUseComponentPropertyWrapper = new VideoUseComponentPropertyWrapper(this);
        this.eventFiredListeners = new HashSet<>(2);
        // Binding wanted image size
        this.configurationParent.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.wantedImageWidth.bind(nv.displayedConfigurationScaleXProperty().multiply(layoutWidth));
                this.wantedImageHeight.bind(nv.displayedConfigurationScaleYProperty().multiply(layoutHeight));
            } else {
                this.wantedImageWidth.unbind();
                this.wantedImageHeight.unbind();
            }
        });
        //Binding name
        this.defaultName.bind(Bindings.createStringBinding(() -> {
            if (StringUtils.isNotBlank(this.textContent.get()) && !this.textContent.isBound()) {
                return this.textContent.get();
            } else {
                final ImageElementI selectedImage = this.imageUseComponentPropertyWrapper.imageVTwoProperty().get();
                if (selectedImage != null && StringUtils.isNotBlank(selectedImage.getName())) {
                    return selectedImage.getName();
                } else {
                    return this.getDisplayableTypeName() + " (" + (this.column.get() + 1) + "," + (this.row.get() + 1) + ")";
                }
            }
        }, this.row, this.column, this.textContent, this.imageUseComponentPropertyWrapper.imageVTwoProperty()));
        //Set "normal" option to the key
        this.changeKeyOption(new BasicKeyOption(), true);
    }

    // Class part : "Base properties"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyObjectProperty<Image> loadedImageProperty() {
        return this.imageUseComponentPropertyWrapper.loadedImageProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty rotateProperty() {
        return this.imageUseComponentPropertyWrapper.rotateProperty();
    }

    @Override
    public DoubleProperty scaleXProperty() {return this.imageUseComponentPropertyWrapper.scaleXProperty();}

    @Override
    public DoubleProperty scaleYProperty() {return this.imageUseComponentPropertyWrapper.scaleYProperty();}

    @Override
    public BooleanProperty enableColorToGreyProperty() {return this.imageUseComponentPropertyWrapper.enableColorToGreyProperty();}

    @Override
    public SimpleObjectProperty<ImageElementI> imageVTwoProperty() {
        return this.imageUseComponentPropertyWrapper.imageVTwoProperty();
    }

    private BooleanBinding imageUseComponentDisplayed;

    @Override
    public ObservableBooleanValue imageUseComponentDisplayedProperty() {
        if (imageUseComponentDisplayed == null) {
            imageUseComponentDisplayed = removedProperty().not().and(displayedProperty());
        }
        return imageUseComponentDisplayed;
    }

    @Override
    public BooleanProperty imageAutomaticallySelectedProperty() {
        return this.imageUseComponentPropertyWrapper.imageAutomaticallySelectedProperty();
    }

    @Override
    public BooleanProperty displayInFullScreenProperty() {
        return this.imageUseComponentPropertyWrapper.displayInFullScreenProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringProperty textContentProperty() {
        return this.textContent;
    }

    @Override
    public ObjectProperty<VideoElementI> videoProperty() {
        return this.videoUseComponentPropertyWrapper.videoProperty();
    }

    @Override
    public ObjectProperty<VideoDisplayMode> videoDisplayModeProperty() {
        return videoUseComponentPropertyWrapper.videoDisplayModeProperty();
    }

    @Override
    public ObjectProperty<VideoPlayMode> videoPlayModeProperty() {
        return videoUseComponentPropertyWrapper.videoPlayModeProperty();
    }

    @Override
    public BooleanProperty muteVideoProperty() {
        return videoUseComponentPropertyWrapper.muteVideoProperty();
    }


    @Override
    public void eventFired(ActionEventType type, UseActionEvent event) {
        for (BiConsumer<ActionEventType, UseActionEvent> eventFiredListener : eventFiredListeners) {
            eventFiredListener.accept(type, event);
        }
    }

    @Override
    public boolean hasEventHandlingFor(ActionEventType type, UseActionEvent event) {
        if (videoProperty().get() != null) {
            VideoDisplayMode videoDisplayMode = videoDisplayModeProperty().get();
            VideoPlayMode videoPlayMode = videoPlayModeProperty().get();
            if (type == ActionEventType.SIMPLE) {
                return videoDisplayMode == VideoDisplayMode.FULLSCREEN || videoPlayMode == VideoPlayMode.ON_ACTIVATION;
            }
            if (type == ActionEventType.COMPLEX) {
                return videoDisplayMode == VideoDisplayMode.IN_KEY && videoPlayMode == VideoPlayMode.WHILE_OVER;
            }
        }
        return false;
    }

    @Override
    public void addEventFiredListener(BiConsumer<ActionEventType, UseActionEvent> eventListener) {
        this.eventFiredListeners.add(eventListener);
    }

    @Override
    public void removeEventFiredListener(BiConsumer<ActionEventType, UseActionEvent> eventListener) {
        this.eventFiredListeners.remove(eventListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyObjectProperty<KeyOptionI> keyOptionProperty() {
        return this.keyOption;
    }

    /*
     * This method is useful because we don't want the change event on key option to be fired before the previous option was detached from this key.
     * So with this method, we can detach option and then set the value.
     */
    @Override
    public void changeKeyOption(final KeyOptionI newKeyOption, final boolean fireKeyNewlyAttached) throws IllegalArgumentException {
        KeyOptionI previous = this.keyOption.get();
        //Detach previous key
        if (previous != null) {
            previous.detachFrom(this);
        }
        if (newKeyOption != null) {
            newKeyOption.attachTo(this);
            if (fireKeyNewlyAttached) {
                newKeyOption.keyNewlyAttached();
            }
        } else {
            throw new IllegalArgumentException("The key option can't be null");
        }
        this.keyOption.set(newKeyOption);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty preserveRatioProperty() {
        return this.imageUseComponentPropertyWrapper.preserveRatioProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty useViewPortProperty() {
        return this.imageUseComponentPropertyWrapper.useViewPortProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyObjectProperty<Rectangle2D> viewportProperty() {
        return this.imageUseComponentPropertyWrapper.viewportProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty viewportXPercentProperty() {
        return this.imageUseComponentPropertyWrapper.viewportXPercentProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty viewportYPercentProperty() {
        return this.imageUseComponentPropertyWrapper.viewportYPercentProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty viewportWidthPercentProperty() {
        return this.imageUseComponentPropertyWrapper.viewportWidthPercentProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty viewportHeightPercentProperty() {
        return this.imageUseComponentPropertyWrapper.viewportHeightPercentProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyDoubleProperty wantedImageWidthProperty() {
        return this.wantedImageWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyDoubleProperty wantedImageHeightProperty() {
        return this.wantedImageHeight;
    }

    @Override
    public void addExternalLoadingRequest(String id) {
        imageUseComponentPropertyWrapper.addExternalLoadingRequest(id);
    }

    @Override
    public void addExternalLoadingRequest(String id, double width, double height) {
        imageUseComponentPropertyWrapper.addExternalLoadingRequest(id, width, height);
    }

    @Override
    public void removeExternalLoadingRequest(String id) {
        imageUseComponentPropertyWrapper.removeExternalLoadingRequest(id);
    }

    @Override
    public String toString() {
        return "Key [" + this.column.get() + "," + this.row.get() + " / " + this.textContent.get() + "]";
    }

    @Override
    public BooleanProperty enableReplaceColorProperty() {
        return this.imageUseComponentPropertyWrapper.enableReplaceColorProperty();
    }

    @Override
    public ObjectProperty<Color> colorToReplaceProperty() {
        return this.imageUseComponentPropertyWrapper.colorToReplaceProperty();
    }

    @Override
    public ObjectProperty<Color> replacingColorProperty() {
        return this.imageUseComponentPropertyWrapper.replacingColorProperty();
    }

    @Override
    public IntegerProperty replaceColorThresholdProperty() {
        return this.imageUseComponentPropertyWrapper.replaceColorThresholdProperty();
    }

    @Override
    public BooleanProperty enableRemoveBackgroundProperty() {
        return this.imageUseComponentPropertyWrapper.enableRemoveBackgroundProperty();
    }

    @Override
    public IntegerProperty removeBackgroundThresholdProperty() {
        return this.imageUseComponentPropertyWrapper.removeBackgroundThresholdProperty();
    }

    @Override
    public void forEachKeys(final Consumer<GridPartKeyComponentI> action) {
        super.forEachKeys(action);
        action.accept(this);
    }
    //========================================================================

    // Class part : "Screen help"
    //========================================================================

    /**
     * @param layoutX the screen x position relative to key top left corner
     * @return the column index for the given x position
     */
    public int getColumnIndex(final double layoutX) {
        return this.column.get() + (int) (layoutX / (this.layoutWidthProperty().get() / this.columnSpanProperty().get()));
    }

    /**
     * @param layoutY the screen y position relative to key top left corner
     * @return the row index for the given y position
     */
    public int getRowIndex(final double layoutY) {
        return this.row.get() + (int) (layoutY / (this.layoutHeightProperty().get() / this.rowSpanProperty().get()));
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.actionManager.dispatchIdsChanged(changes);
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element content = super.serialize(contextP);
        //Base properties
        XMLObjectSerializer.serializeInto(GridPartKeyComponent.class, this, content);
        this.imageUseComponentPropertyWrapper.serialize(content, contextP);
        this.videoUseComponentPropertyWrapper.serialize(content, contextP);
        //Action
        Element actionManagerElement = this.actionManager.serialize(contextP);
        if (actionManagerElement != null) {
            content.addContent(actionManagerElement);
        }
        //Option (save only if it's not a base key)
        if (!(this.keyOption.get() instanceof BasicKeyOption)) {
            content.addContent(this.keyOption.get().serialize(contextP));
        }
        return content;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        //Base properties
        XMLObjectSerializer.deserializeInto(GridPartKeyComponent.class, this, nodeP);

        // Backward comp : get the text position from old param
        if (nodeP.getAttribute("textPosition") != null) {
            Enum<TextPosition> textPosition = XMLUtils.readEnum(TextPosition.class, "textPosition", nodeP);
            if (textPosition != null) {
                this.getKeyStyle().textPositionProperty().selected().setValue((TextPosition) textPosition);
            }
        }

        //Image
        this.imageUseComponentPropertyWrapper.deserialize(nodeP, contextP);
        this.videoUseComponentPropertyWrapper.deserialize(nodeP, contextP);
        //Action
        Element actionManagerNodeOld = nodeP.getChild(SimpleUseActionManager.NODE_USE_ACTION_MANAGER_OLD);
        Element actionManagerNode = nodeP.getChild(SimpleUseActionManager.NODE_USE_ACTION_MANAGER);
        if (actionManagerNode != null || actionManagerNodeOld != null) {
            this.actionManager.deserialize(actionManagerNode != null ? actionManagerNode : actionManagerNodeOld, contextP);
        }
        //Option (if it doesn't exist : it's a base key)
        Element keyOptionNode = nodeP.getChild(KeyOptionI.NODE_KEY_OPTION);
        KeyOptionI loadedkeyOption;
        if (keyOptionNode != null) {
            Pair<Boolean, XMLSerializable<IOContextI>> loadedkeyOptionResult = ConfigurationComponentIOHelper.create(keyOptionNode, contextP, BasicKeyOption::new);
            loadedkeyOption = (KeyOptionI) loadedkeyOptionResult.getRight();
            if (!loadedkeyOptionResult.getLeft()) {
                loadedkeyOption.deserialize(keyOptionNode, contextP);
            }
        } else {
            loadedkeyOption = new BasicKeyOption();
        }
        this.changeKeyOption(loadedkeyOption, false);
    }
    //========================================================================

    // Class part : "Use information"
    //========================================================================
    @Override
    public void serializeUseInformation(final Map<String, Element> elements) {
        super.serializeUseInformation(elements);
        this.actionManager.serializeUseInformation(elements);
    }

    @Override
    public void deserializeUseInformation(final Map<String, Element> elements) throws LCException {
        super.deserializeUseInformation(elements);
        this.actionManager.deserializeUseInformation(elements);
    }
    //========================================================================

    // Class part : "Tree"
    //========================================================================
    @Override
    public ObservableList<TreeDisplayableComponentI> getChildrenNode() {
        return null;
    }

    @Override
    public boolean isNodeLeaf() {
        return true;
    }

    @Override
    public TreeDisplayableType getNodeType() {
        return TreeDisplayableType.KEY;
    }
    //========================================================================

    // Class part : "Actions"
    //========================================================================
    @Override
    public UseActionManagerI getActionManager() {
        return this.actionManager;
    }

    @Override
    public boolean isTextContentWritten() {
        boolean textContentWritten = false;
        textContentWritten |= this.keyOption.get() instanceof QuickComKeyOption;
        textContentWritten |= this.actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, WriteLabelAction.class) != null;
        textContentWritten |= this.actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, WriteCharPredictionAction.class) != null;
        textContentWritten |= this.actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, WriteWordPredictionAction.class) != null;
        textContentWritten |= this.actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, WriteAndSpeakLabelActionInText.class) != null;
        textContentWritten |= this.actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, WriteAndSpeakLabelActionInSound.class) != null;
        return textContentWritten;
    }
    //========================================================================

}
