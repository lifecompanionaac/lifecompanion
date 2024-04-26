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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

/**
 * Action to write a text entered by user
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final StringProperty textToWrite;
    private final SimpleObjectProperty<ImageElementI> imageToWrite;
    private final SimpleObjectProperty<ImageUseComponentI> sourceImageUseComponent;

    //TODO : add text style configuration

    public WriteTextAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.WRITE_TEXT;
        this.order = 1;
        this.textToWrite = new SimpleStringProperty("");
        this.imageToWrite = new SimpleObjectProperty<>();
        this.sourceImageUseComponent = new SimpleObjectProperty<>();
        this.nameID = "action.simple.write.text.name";
        this.staticDescriptionID = "action.simple.write.text.static.description";
        this.configIconPath = "text/icon_write_text.png";
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.simple.write.text.variable.description", this.textToWrite));
    }

    public StringProperty textToWriteProperty() {
        return this.textToWrite;
    }


    public Property<ImageElementI> imageToWriteProperty() {
        return this.imageToWrite;
    }
    
    public Property<ImageUseComponentI> sourceImageUseComponentProperty(){
        return this.sourceImageUseComponent;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.textToWrite.get() != null) {
            String toWrite = UseVariableController.INSTANCE.createText(this.textToWrite.get(), variables);
            if (toWrite != null && toWrite.length() == 1) {
                WritingStateController.INSTANCE.insertText(WritingEventSource.USER_ACTIONS, toWrite);
            } else {
                final WriterEntry entryP = new WriterEntry(toWrite, true);
                if (this.imageToWrite.get() != null) {
                    entryP.imageProperty().set(imageToWrite.get());
                    entryP.sourceImageUseComponentProperty().set(this.sourceImageUseComponent.get());
                }
                WritingStateController.INSTANCE.insert(WritingEventSource.USER_ACTIONS, entryP);
            }
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(WriteTextAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(WriteTextAction.class, this, nodeP);
    }
    //========================================================================

}
