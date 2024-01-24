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
package org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.binding.BindingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractSimplerKeyContentContainerKeyOption<T extends SimplerKeyContentContainerI> extends AbstractKeyOption {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimplerKeyContentContainerKeyOption.class);

    protected final ObjectProperty<T> currentSimplerKeyContentContainer;

    public AbstractSimplerKeyContentContainerKeyOption() {
        super();

        this.disableImage.set(true);
        this.disableTextContent.set(true);
        currentSimplerKeyContentContainer = new SimpleObjectProperty<>();

        // Key update handling
        InvalidationListener invUpdateKey = this::updateKeyFromCurrentContent;
        currentSimplerKeyContentContainer.addListener(invUpdateKey);
        attachedKey.addListener(invUpdateKey);

        // Unbind from when needed
        this.currentSimplerKeyContentContainer.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.bindImageDisplayProperties(null);
            }
        });
    }

    // PROPS
    //========================================================================
    public ObjectProperty<T> currentSimplerKeyContentContainerProperty() {
        return currentSimplerKeyContentContainer;
    }
    //========================================================================

    // UPDATE KEY
    //========================================================================
    private void updateKeyFromCurrentContent(Observable observableChanged) {
        final GridPartKeyComponentI key = attachedKey.get();
        if (key != null) {
            final T simplerKeyContentContainer = currentSimplerKeyContentContainer.get();
            if (simplerKeyContentContainer == null) {
                // Text content
                key.textContentProperty().set(getDefaultTextContentProperty());
                // Image content
                key.imageVTwoProperty().set(null);
                key.preserveRatioProperty().set(true);
                key.rotateProperty().set(0.0);
                key.useViewPortProperty().set(false);
                key.enableReplaceColorProperty().set(false);
                // Video content
                key.videoProperty().set(null);
                // Style
                key.getKeyStyle().backgroundColorProperty().forced().setValue(null);
                key.getKeyStyle().strokeColorProperty().forced().setValue(null);
                key.getKeyStyle().textPositionProperty().forced().setValue(null);
                key.getKeyStyle().shapeStyleProperty().forced().setValue(null);
                key.getKeyTextStyle().colorProperty().forced().setValue(null);
            } else {
                // Text content
                key.textContentProperty().set(simplerKeyContentContainer.textProperty().get());
                // Image content
                key.imageVTwoProperty().set(simplerKeyContentContainer.imageVTwoProperty().get());
                key.preserveRatioProperty().set(simplerKeyContentContainer.preserveRatioProperty().get());
                key.rotateProperty().set(simplerKeyContentContainer.rotateProperty().get());
                key.useViewPortProperty().set(simplerKeyContentContainer.useViewPortProperty().get());
                key.viewportXPercentProperty().set(simplerKeyContentContainer.viewportXPercentProperty().get());
                key.viewportYPercentProperty().set(simplerKeyContentContainer.viewportYPercentProperty().get());
                key.viewportWidthPercentProperty().set(simplerKeyContentContainer.viewportWidthPercentProperty().get());
                key.viewportHeightPercentProperty().set(simplerKeyContentContainer.viewportHeightPercentProperty().get());
                key.replaceColorThresholdProperty().set(simplerKeyContentContainer.replaceColorThresholdProperty().get());
                key.replacingColorProperty().set(simplerKeyContentContainer.replacingColorProperty().get());
                key.colorToReplaceProperty().set(simplerKeyContentContainer.colorToReplaceProperty().get());
                key.enableReplaceColorProperty().set(simplerKeyContentContainer.enableReplaceColorProperty().get());
                // Video content
                key.videoProperty().set(simplerKeyContentContainer.videoProperty().get());
                key.videoDisplayModeProperty().set(simplerKeyContentContainer.videoDisplayModeProperty().get());
                key.videoPlayModeProperty().set(simplerKeyContentContainer.videoPlayModeProperty().get());
                key.muteVideoProperty().set(simplerKeyContentContainer.muteVideoProperty().get());
                // Image display (size and loading)
                simplerKeyContentContainer.bindImageDisplayProperties(key);
                key.getKeyStyle().backgroundColorProperty().forced().setValue(simplerKeyContentContainer.backgroundColorProperty().get());
                key.getKeyStyle().strokeColorProperty().forced().setValue(simplerKeyContentContainer.strokeColorProperty().get());
                key.getKeyStyle().textPositionProperty().forced().setValue(simplerKeyContentContainer.textPositionProperty().get());
                key.getKeyStyle().shapeStyleProperty().forced().setValue(simplerKeyContentContainer.shapeStyleProperty().get());
                key.getKeyTextStyle().colorProperty().forced().setValue(simplerKeyContentContainer.textColorProperty().get());
            }
            updateKeyFor(key, simplerKeyContentContainer, AppModeController.INSTANCE.modeProperty().get());
        } else if (currentSimplerKeyContentContainer.get() != null) {
            currentSimplerKeyContentContainer.get().bindImageDisplayProperties(null);
        }
    }

    protected void updateKeyFor(GridPartKeyComponentI key, T simplerKeyContentContainer, AppMode appMode) {
        if (key != null) {
            handleActionsUpdate(key, simplerKeyContentContainer, appMode);
        }
    }

    protected void handleActionsUpdate(GridPartKeyComponentI key, T keyContentContainer, AppMode appMode) {
        // Remove actions if content is empty, or in config mode (don't want to change key behavior in config mode)
        if (keyContentContainer == null || appMode != AppMode.USE) {
            removeAllActionsFrom(key);
        }
        // Add actions if needed (content or in use mode)
        else {
            removeAllActionsFrom(key);
            for (UseActionEvent event : UseActionEvent.values()) {
                for (BaseUseActionI<?> baseUseAction : getActionsToAddFor(event)) {
                    key.getActionManager().componentActions().get(event).add(baseUseAction);
                }
            }
        }
    }

    private void removeAllActionsFrom(GridPartKeyComponentI key) {
        for (UseActionEvent event : UseActionEvent.values()) {
            for (BaseUseActionI<?> baseUseAction : getActionsToRemoveFor(event)) {
                key.getActionManager().componentActions().get(event).remove(baseUseAction);
            }
        }
    }

    protected List<BaseUseActionI<?>> getActionsToAddFor(UseActionEvent event) {
        return new ArrayList<>();
    }

    protected List<BaseUseActionI<?>> getActionsToRemoveFor(UseActionEvent event) {
        return new ArrayList<>();
    }


    protected String getDefaultTextContentProperty() {
        return "";
    }
    //========================================================================

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        BindingUtils.unbindAndSetNull(key.imageVTwoProperty());
        BindingUtils.unbindAndSetNull(key.videoProperty());
        BindingUtils.unbindAndSetNull(key.textContentProperty());
        BindingUtils.unbindAndSetNull(key.getKeyStyle().backgroundColorProperty().forced());
        BindingUtils.unbindAndSetNull(key.getKeyStyle().strokeColorProperty().forced());
        BindingUtils.unbindAndSetNull(key.getKeyStyle().textPositionProperty().forced());
        BindingUtils.unbindAndSetNull(key.getKeyTextStyle().colorProperty().forced());
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(AbstractSimplerKeyContentContainerKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AbstractSimplerKeyContentContainerKeyOption.class, this, node);
    }
}
