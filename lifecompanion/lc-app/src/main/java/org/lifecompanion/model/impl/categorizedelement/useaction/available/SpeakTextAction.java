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

import java.util.Map;

import org.jdom2.Element;

import org.lifecompanion.controller.easteregg.JPDRetirementController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Simple action to speak a user choosen text
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SpeakTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private StringProperty textToSpeak;

    public SpeakTextAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SPEAK_TEXT;
        this.nameID = "action.speak.text.name";
        this.staticDescriptionID = "action.speak.text.static.description";
        this.configIconPath = "sound/icon_speak_text.png";
        this.parameterizableAction = true;
        this.order = 0;
        this.textToSpeak = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.speak.text.variable.description", this.textToSpeak));
    }

    public StringProperty textToSpeakProperty() {
        return this.textToSpeak;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (StringUtils.isEquals("jpd", textToSpeak.get())) {//FIXME : remove temp test
            JPDRetirementController.INSTANCE.startJPDRetirementJourney();
        } else {
            VoiceSynthesizerController.INSTANCE.speakSync(UseVariableController.INSTANCE.createText(this.textToSpeak.get(), variables));
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SpeakTextAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SpeakTextAction.class, this, nodeP);
    }
    //========================================================================
}
