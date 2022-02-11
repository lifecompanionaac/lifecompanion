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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.SoundResourceHolderI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyActionContainerI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.SoundResourceHolder;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreDefaultBooleanValue;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimplerKeyActionContainer extends AbstractSimplerKeyContentContainer implements SimplerKeyActionContainerI {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimplerKeyActionContainer.class);

    @XMLIgnoreNullValue
    private final StringProperty textToWrite, textToSpeak, textSpeakOnOver;

    @XMLIgnoreDefaultBooleanValue(true)
    private final BooleanProperty enableSpaceAfterWrite;

    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty enableSpeakOnOver;

    @XMLIgnoreDefaultBooleanValue(true)
    private final BooleanProperty enableWrite, enableSpeak;

    @XMLIgnoreDefaultBooleanValue(false)
    private final BooleanProperty enablePlayRecordedSound;

    private final SoundResourceHolderI soundResourceHolder;

    protected AbstractSimplerKeyActionContainer() {
        textToWrite = new SimpleStringProperty("");
        textToSpeak = new SimpleStringProperty("");
        textSpeakOnOver = new SimpleStringProperty("");
        enableSpaceAfterWrite = new SimpleBooleanProperty(true);
        enableWrite = new SimpleBooleanProperty(true);
        enableSpeak = new SimpleBooleanProperty(true);
        enableSpeakOnOver = new SimpleBooleanProperty(false);
        enablePlayRecordedSound = new SimpleBooleanProperty(false);
        this.soundResourceHolder = new SoundResourceHolder();
    }

    // PROPS
    //========================================================================
    @Override
    public StringProperty textToWriteProperty() {
        return textToWrite;
    }

    @Override
    public StringProperty textToSpeakProperty() {
        return textToSpeak;
    }

    @Override
    public BooleanProperty enableSpaceAfterWriteProperty() {
        return enableSpaceAfterWrite;
    }

    @Override
    public BooleanProperty enableWriteProperty() {
        return enableWrite;
    }

    @Override
    public BooleanProperty enableSpeakProperty() {
        return enableSpeak;
    }

    @Override
    public StringProperty textSpeakOnOverProperty() {
        return textSpeakOnOver;
    }

    @Override
    public BooleanProperty enableSpeakOnOverProperty() {
        return enableSpeakOnOver;
    }


    @Override
    public boolean isEmpty() {
        return super.isEmpty()
                && StringUtils.isBlank(textToWrite.get())
                && StringUtils.isBlank(textToSpeak.get());
    }
    //========================================================================

    // SOUND
    //========================================================================
    @Override
    public BooleanProperty enablePlayRecordedSoundProperty() {
        return enablePlayRecordedSound;
    }

    @Override
    public SoundResourceHolderI getSoundResourceHolder() {
        return soundResourceHolder;
    }
    //========================================================================

    // IO
    //========================================================================
    protected abstract String getNodeName();

    @Override
    public Element serialize(IOContextI context) {
        Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(AbstractSimplerKeyActionContainer.class, this, node);
        this.soundResourceHolder.serializeIfNeeded(node, context);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AbstractSimplerKeyActionContainer.class, this, node);
        this.soundResourceHolder.deserializeIfNeeded(node, context, "recordedSoundResourceId", "recordedSoundDurationInSecond");
    }
    //========================================================================
}
