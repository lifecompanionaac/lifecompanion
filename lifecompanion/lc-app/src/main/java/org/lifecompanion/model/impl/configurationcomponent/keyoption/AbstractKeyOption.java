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
package org.lifecompanion.model.impl.configurationcomponent.keyoption;

import javafx.beans.property.*;
import javafx.scene.layout.Region;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for {@link KeyOptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractKeyOption implements KeyOptionI {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyOption.class);

    protected transient String optionNameId;

    protected transient String iconName;

    protected transient final BooleanProperty disableTextContent;

    protected transient final BooleanProperty disableImage;

    protected transient final BooleanProperty considerKeyEmpty;

    protected transient final IntegerProperty maxTextLength;

    protected transient final ObjectProperty<Region> keyViewAddedNode;

    /**
     * Attached key
     */
    protected ObjectProperty<GridPartKeyComponentI> attachedKey;

    public AbstractKeyOption() {
        this.disableTextContent = new SimpleBooleanProperty(this, "disableTextContent", false);
        this.disableImage = new SimpleBooleanProperty(this, "disableImage", false);
        this.maxTextLength = new SimpleIntegerProperty(this, "maxTextLength", -1);
        this.considerKeyEmpty = new SimpleBooleanProperty(this, "considerKeyEmpty", false);
        this.attachedKey = new SimpleObjectProperty<>();
        this.keyViewAddedNode = new SimpleObjectProperty<>();
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(KeyOptionI.NODE_KEY_OPTION);
        IOManager.addTypeAlias(this, element, context);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        //Do nothing
    }

    @Override
    public void attachTo(final GridPartKeyComponentI key) {
        this.attachedKey.set(key);
        this.attachToImpl(key);
    }

    @Override
    public void detachFrom(final GridPartKeyComponentI key) {
        this.detachFromImpl(key);
        this.attachedKey.set(null);
    }

    @Override
    public void keyNewlyAttached() {
    }

    /**
     * Called when this key option is attached to a key.<br>
     * The key is given to this method, implementation can modify the key if needed.
     *
     * @param key the attached key.
     */
    protected abstract void attachToImpl(GridPartKeyComponentI key);

    /**
     * Called when this key option is detached from a key (removed)<br>
     * The previous attached key is given to this method, implementation should restore and unbind the key properties.<br>
     * Note that the key passed to this method was before passed once to {@link #attachToImpl(GridPartKeyComponentI)}.
     *
     * @param key the detached key.
     */
    protected abstract void detachFromImpl(GridPartKeyComponentI key);

    @Override
    public ReadOnlyBooleanProperty disableTextContentProperty() {
        return this.disableTextContent;
    }

    @Override
    public ReadOnlyIntegerProperty maxTextLengthProperty() {
        return this.maxTextLength;
    }

    @Override
    public ObjectProperty<GridPartKeyComponentI> attachedKeyProperty() {
        return this.attachedKey;
    }

    @Override
    public ReadOnlyBooleanProperty disableImageProperty() {
        return this.disableImage;
    }

    @Override
    public ReadOnlyBooleanProperty considerKeyEmptyProperty() {
        return this.considerKeyEmpty;
    }

    @Override
    public ObjectProperty<Region> keyViewAddedNodeProperty() {
        return keyViewAddedNode;
    }

    @Override
    public KeyOptionI getNewOptionInstance() {
        try {
            return this.getClass().getConstructor().newInstance();
        } catch (Exception e) {
            AbstractKeyOption.LOGGER.warn("Couldn't create option type for {}, with class type {}", this, this.getClass().getSimpleName());
        }
        return null;
    }

    @Override
    public String getOptionName() {
        return Translation.getText(this.optionNameId);
    }

    @Override
    public String getIconUrl() {
        if (this.iconName != null) {
            return "component/options/" + this.iconName;
        }
        return null;
    }
}
