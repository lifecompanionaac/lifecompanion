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

package org.lifecompanion.plugin.calendar.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.SoundResourceHolder;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.AbstractSimplerKeyContentContainer;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.CopyUtils;

public class CalendarLeisure extends AbstractSimplerKeyContentContainer {
    private transient final BooleanProperty selectedProperty;

    private final BooleanProperty enableLeisureSpeech;
    private final StringProperty leisureSpeech;

    private final BooleanProperty enablePlayRecordedSoundPropertyOnSelection;
    private final SoundResourceHolder soundOnSelectionResourceHolder;

    public CalendarLeisure() {
        selectedProperty = new SimpleBooleanProperty(false);
        enableLeisureSpeech = new SimpleBooleanProperty(false);
        leisureSpeech = new SimpleStringProperty();
        soundOnSelectionResourceHolder = new SoundResourceHolder();
        enablePlayRecordedSoundPropertyOnSelection = new SimpleBooleanProperty();
    }

    public BooleanProperty selectedPropertyProperty() {
        return selectedProperty;
    }

    public BooleanProperty enableLeisureSpeechProperty() {
        return enableLeisureSpeech;
    }

    public StringProperty leisureSpeechProperty() {
        return leisureSpeech;
    }

    public BooleanProperty enablePlayRecordedSoundPropertyOnSelectionProperty() {
        return enablePlayRecordedSoundPropertyOnSelection;
    }

    public SoundResourceHolder getSoundOnSelectionResourceHolder() {
        return soundOnSelectionResourceHolder;
    }

    @Override
    public CalendarLeisure duplicate(boolean changeId) {
        final CalendarLeisure deepCopyViaXMLSerialization = (CalendarLeisure) CopyUtils.createDeepCopyViaXMLSerialization(this, false);
        if (changeId) {
            deepCopyViaXMLSerialization.changeId(StringUtils.getNewID());
        }
        return deepCopyViaXMLSerialization;
    }

    @Override
    protected String getNodeName() {
        return "CalendarLeisure";
    }

    @Override
    public Element serialize(IOContextI context) {
        Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(CalendarLeisure.class, this, node);
        // Sound on selection
        final Element recordedSoundOnSelection = new Element("RecordedSoundOnSelection");
        node.addContent(recordedSoundOnSelection);
        soundOnSelectionResourceHolder.serializeIfNeeded(recordedSoundOnSelection, context);

        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CalendarLeisure.class, this, node);
        // Sound on selection
        final Element recordedSoundOnSelection = node.getChild("RecordedSoundOnSelection");
        if (recordedSoundOnSelection != null)
            soundOnSelectionResourceHolder.deserializeIfNeeded(recordedSoundOnSelection, context, null, null);
    }


}
