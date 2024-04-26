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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

/**
 * Action to write a text and speak it
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteAndSpeakTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final StringProperty textToWrite;
    private final StringProperty textToSpeak;
    private final BooleanProperty enableSpeak;
    private final BooleanProperty addSpace;

    public WriteAndSpeakTextAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.WRITE_TEXT;
        this.order = 0;
        this.parameterizableAction = true;
        this.textToWrite = new SimpleStringProperty("");
        this.textToSpeak = new SimpleStringProperty("");
        this.enableSpeak = new SimpleBooleanProperty(true);
        this.addSpace = new SimpleBooleanProperty(true);
        this.nameID = "action.write.and.speak.text.name";
        this.staticDescriptionID = "action.write.and.speak.text.static.description";
        this.configIconPath = "text/icon_write_speak_text.png";
        this.variableDescriptionProperty().bind(Bindings.createStringBinding(() -> {
            if (this.enableSpeak.get()) {
                return Translation.getText("action.write.and.speak.text.variable.description.both", this.textToWrite.get(), this.textToSpeak.get());
            } else {
                return Translation.getText("action.write.and.speak.text.variable.description.write.only", this.textToWrite.get(), this.textToSpeak.get());
            }
        }, this.textToWrite, this.textToSpeak, this.enableSpeak));
    }

    public StringProperty textToWriteProperty() {
        return this.textToWrite;
    }

    public StringProperty textToSpeakProperty() {
        return this.textToSpeak;
    }

    public BooleanProperty enableSpeakProperty() {
        return this.enableSpeak;
    }

    public BooleanProperty addSpaceProperty() {
        return this.addSpace;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.textToWrite.get() != null) {
            String toWrite = UseVariableController.INSTANCE.createText(this.textToWrite.get(), variables) + (this.addSpace.get() ? " " : "");
            WriterEntry textEntry = new WriterEntry(toWrite, true);
            if (this.parentComponentProperty().get() instanceof GridPartKeyComponentI) {
                GridPartKeyComponentI currentKey = (GridPartKeyComponentI) this.parentComponentProperty().get();
                if (currentKey != null && currentKey.imageVTwoProperty().get() != null) {
                    textEntry.imageProperty().set(currentKey.imageVTwoProperty().get());
                    textEntry.sourceImageUseComponentProperty().set(currentKey);
                }
            }
            WritingStateController.INSTANCE.insert(WritingEventSource.USER_ACTIONS, textEntry, null);
        }
        if (this.textToSpeak.get() != null && this.enableSpeak.get()) {
            VoiceSynthesizerController.INSTANCE.speakSync(UseVariableController.INSTANCE.createText(this.textToSpeak.get(), variables));
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(WriteAndSpeakTextAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(WriteAndSpeakTextAction.class, this, nodeP);
    }
    //========================================================================

}
