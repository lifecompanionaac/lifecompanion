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

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.SoundResourceHolderI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.SoundResourceHolder;
import org.lifecompanion.model.impl.configurationcomponent.TimeOfDay;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.AbstractSimplerKeyContentContainer;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.calendar.controller.SoundAlarmController;
import org.lifecompanion.util.CopyUtils;

public class CalendarEvent extends AbstractSimplerKeyContentContainer {
    private final transient ObjectProperty<CalendarEventStatus> status;
    private final transient BooleanProperty hadBeenStarted, hadBeenFinished;

    private final BooleanProperty enableAutostartWhenPreviousFinished;

    private final BooleanProperty enableTextOnStart;
    private final StringProperty textOnStart;

    private final BooleanProperty enableTextOnFinish;
    private final StringProperty textOnFinish;

    private final IntegerProperty automaticItemTimeMs;
    private final BooleanProperty enableAutomaticItem;

    private final BooleanProperty enableAtFixedTime;
    private final TimeOfDay fixedTime;
    private final BooleanProperty enableTextOnAlarm;
    private final StringProperty textOnAlarm;

    private final BooleanProperty enableSoundOnAlarm;

    @XMLGenericProperty(SoundAlarmController.StandardAlarm.class)
    private final ObjectProperty<SoundAlarmController.StandardAlarm> soundOnAlarm;

    private final BooleanProperty enableLinkToSequence;
    private final StringProperty linkedSequenceId;

    private final BooleanProperty enableLeisureSelection;

    private final BooleanProperty enablePlayRecordedSoundPropertyOnStart;
    private final SoundResourceHolder soundOnStartResourceHolder;

    private final BooleanProperty enablePlayRecordedSoundPropertyOnEnd;
    private final SoundResourceHolder soundOnEndResourceHolder;


    public CalendarEvent() {
        status = new SimpleObjectProperty<>();
        enableTextOnStart = new SimpleBooleanProperty(true);
        hadBeenStarted = new SimpleBooleanProperty(false);
        hadBeenFinished = new SimpleBooleanProperty(false);
        textOnStart = new SimpleStringProperty();
        enableTextOnFinish = new SimpleBooleanProperty(false);
        textOnFinish = new SimpleStringProperty();
        enableAutostartWhenPreviousFinished = new SimpleBooleanProperty(true);
        enableAtFixedTime = new SimpleBooleanProperty(false);
        this.enableAutomaticItem = new SimpleBooleanProperty(false);
        this.automaticItemTimeMs = new SimpleIntegerProperty(10_000);
        this.enableLinkToSequence = new SimpleBooleanProperty(false);
        this.enableLeisureSelection = new SimpleBooleanProperty(false);
        this.enableSoundOnAlarm = new SimpleBooleanProperty(true);
        this.soundOnAlarm = new SimpleObjectProperty<>(SoundAlarmController.StandardAlarm.ALARM2);
        this.linkedSequenceId = new SimpleStringProperty();
        this.textOnAlarm = new SimpleStringProperty();
        this.enableTextOnAlarm = new SimpleBooleanProperty();
        this.fixedTime = new TimeOfDay();
        soundOnStartResourceHolder = new SoundResourceHolder();
        enablePlayRecordedSoundPropertyOnStart = new SimpleBooleanProperty();
        soundOnEndResourceHolder = new SoundResourceHolder();
        enablePlayRecordedSoundPropertyOnEnd = new SimpleBooleanProperty();
    }

    public BooleanProperty enablePlayRecordedSoundPropertyOnStartProperty() {
        return enablePlayRecordedSoundPropertyOnStart;
    }

    public SoundResourceHolderI getSoundOnStartResourceHolder() {
        return soundOnStartResourceHolder;
    }

    public BooleanProperty enablePlayRecordedSoundPropertyOnEndProperty() {
        return enablePlayRecordedSoundPropertyOnEnd;
    }

    public SoundResourceHolderI getSoundOnEndResourceHolder() {
        return soundOnEndResourceHolder;
    }

    public BooleanProperty enableTextOnStartProperty() {
        return enableTextOnStart;
    }

    public StringProperty textOnStartProperty() {
        return textOnStart;
    }

    public BooleanProperty enableTextOnFinishProperty() {
        return enableTextOnFinish;
    }

    public StringProperty textOnFinishProperty() {
        return textOnFinish;
    }

    public IntegerProperty automaticItemTimeMsProperty() {
        return automaticItemTimeMs;
    }

    public BooleanProperty enableAutomaticItemProperty() {
        return enableAutomaticItem;
    }

    public ObjectProperty<CalendarEventStatus> statusProperty() {
        return status;
    }

    public BooleanProperty enableLinkToSequenceProperty() {
        return enableLinkToSequence;
    }

    public BooleanProperty enableAtFixedTimeProperty() {
        return enableAtFixedTime;
    }

    public TimeOfDay getFixedTime() {
        return fixedTime;
    }

    public StringProperty linkedSequenceIdProperty() {
        return linkedSequenceId;
    }

    public BooleanProperty enableTextOnAlarmProperty() {
        return enableTextOnAlarm;
    }

    public StringProperty textOnAlarmProperty() {
        return textOnAlarm;
    }

    public BooleanProperty hadBeenStartedProperty() {
        return hadBeenStarted;
    }

    public BooleanProperty hadBeenFinishedProperty() {
        return hadBeenFinished;
    }

    public BooleanProperty enableLeisureSelectionProperty() {
        return enableLeisureSelection;
    }

    public BooleanProperty enableAutostartWhenPreviousFinishedProperty() {
        return enableAutostartWhenPreviousFinished;
    }

    public BooleanProperty enableSoundOnAlarmProperty() {
        return enableSoundOnAlarm;
    }

    public ObjectProperty<SoundAlarmController.StandardAlarm> soundOnAlarmProperty() {
        return soundOnAlarm;
    }

    @Override
    public CalendarEvent duplicate(boolean changeId) {
        final CalendarEvent deepCopyViaXMLSerialization = (CalendarEvent) CopyUtils.createDeepCopyViaXMLSerialization(this, false);
        if (changeId) {
            deepCopyViaXMLSerialization.changeId(StringUtils.getNewID());
        }
        return deepCopyViaXMLSerialization;
    }

    @Override
    protected String getNodeName() {
        return "CalendarEventItem";
    }

    @Override
    public Element serialize(IOContextI context) {
        Element node = XMLObjectSerializer.serializeInto(CalendarEvent.class, this, super.serialize(context));
        ConfigurationComponentIOHelper.addTypeAlias(this, node, context);
        node.addContent(fixedTime.serialize(context));
        // Sound on start
        serializeSoundResourceHolder(context, node, soundOnStartResourceHolder, "RecordedSoundOnStart");
        serializeSoundResourceHolder(context, node, soundOnEndResourceHolder, "RecordedSoundOnEnd");
        return node;
    }


    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CalendarEvent.class, this, node);
        final Element fixedTimeNode = node.getChild(TimeOfDay.NODE_NAME);
        if (fixedTimeNode != null) {
            fixedTime.deserialize(fixedTimeNode, context);
        }
        deserializeSoundResourceHolder(context, node, soundOnStartResourceHolder, "RecordedSoundOnStart");
        deserializeSoundResourceHolder(context, node, soundOnEndResourceHolder, "RecordedSoundOnEnd");
    }

    private static void serializeSoundResourceHolder(IOContextI context, Element node, SoundResourceHolderI soundResourceHolder, String name) {
        final Element soundResNode = new Element(name);
        node.addContent(soundResNode);
        soundResourceHolder.serializeIfNeeded(soundResNode, context);
    }

    private void deserializeSoundResourceHolder(IOContextI context, Element node, SoundResourceHolderI soundResourceHolder, String name) throws LCException {
        final Element soundResNode = node.getChild(name);
        if (soundResNode != null)
            soundResourceHolder.deserializeIfNeeded(soundResNode, context, null, null);
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "status=" + status.get() +
                ", hadBeenStarted=" + hadBeenStarted.get() +
                ", hadBeenFinished=" + hadBeenFinished.get() +
                ", enableTextOnStart=" + enableTextOnStart.get() +
                ", textOnStart=" + textOnStart.get() +
                ", enableTextOnFinish=" + enableTextOnFinish.get() +
                ", textOnFinish=" + textOnFinish.get() +
                ", enableAutomaticItem=" + enableAutomaticItem.get() +
                ", enableAtFixedTime=" + enableAtFixedTime.get() +
                ", fixedTime=" + fixedTime +
                ", enableLinkToSequence=" + enableLinkToSequence.get() +
                ", enableLeisureSelection=" + enableLeisureSelection.get() +
                '}';
    }
}
