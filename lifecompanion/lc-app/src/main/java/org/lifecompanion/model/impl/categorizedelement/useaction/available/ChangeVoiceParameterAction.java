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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.voicesynthesizer.VoiceAndSynthesizerInfoI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerParameterI;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.model.impl.voicesynthesizer.VoiceSynthesizerInfoImpl;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;

import java.util.Map;

public class ChangeVoiceParameterAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    /**
     * The voice to use
     */
    protected ObjectProperty<VoiceAndSynthesizerInfoI> selectedVoice;

    private VoiceSynthesizerI previousVoiceSynthesizer;
    private String previousVoiceId;

    protected transient boolean restore = false;

    public ChangeVoiceParameterAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SPEAK_PARAMETERS;
        this.nameID = "action.change.voice.parameter.name";
        this.staticDescriptionID = "action.change.voice.parameter.static.description";
        this.configIconPath = "sound/icon_change_voice.png";
        this.parameterizableAction = true;
        this.order = 2;
        this.selectedVoice = new SimpleObjectProperty<>(this, "selectedVoice", null);
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.change.voice.parameter.variable.description", Bindings.createStringBinding(() -> {
                    return this.selectedVoice.get() != null ? this.selectedVoice.get().getDisplayableLabel() : "null";
                }, this.selectedVoice)));
        this.allowSystems = SystemType.allExpectMobile();
    }

    public ObjectProperty<VoiceAndSynthesizerInfoI> selectedVoiceProperty() {
        return this.selectedVoice;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        //Try to switch engine and voice
        UseActionTriggerComponentI parentComp = this.parentComponentProperty().get();
        LCConfigurationI config = parentComp.configurationParentProperty().get();
        VoiceAndSynthesizerInfoI selectedVoiceInfo = this.selectedVoice.get();
        if (parentComp != null && config != null && selectedVoiceInfo != null) {
            VoiceSynthesizerParameterI syntParam = config.getVoiceSynthesizerParameter();
            //Select the synthesizer
            VoiceSynthesizerI foundVoiceSynthesizer = VoiceSynthesizerController.INSTANCE.getVoiceSynthesizer(selectedVoiceInfo.getSynthesizerId());
            if (foundVoiceSynthesizer != null) {
                this.previousVoiceSynthesizer = syntParam.selectedVoiceSynthesizerProperty().get();
                syntParam.selectedVoiceSynthesizerProperty().set(foundVoiceSynthesizer);
                //Try to select voice
                this.previousVoiceId = syntParam.getVoiceParameter().voiceIdProperty().get();
                syntParam.getVoiceParameter().voiceIdProperty().set(selectedVoiceInfo.getVoiceId());
                VoiceSynthesizerController.INSTANCE.selectCorrectVoice(syntParam.getVoiceParameter(), foundVoiceSynthesizer);
            }
            //If we restore, try to restore previous setting at the end
            if (this.restore) {
                UseActionController.INSTANCE.getEndOfSimpleActionExecutionListener(UseActionEvent.ACTIVATION).add((r) -> {
                    syntParam.selectedVoiceSynthesizerProperty().set(this.previousVoiceSynthesizer);
                    syntParam.getVoiceParameter().voiceIdProperty().set(this.previousVoiceId);
                    VoiceSynthesizerController.INSTANCE.selectCorrectVoice(syntParam.getVoiceParameter(), this.previousVoiceSynthesizer);
                });
            }
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        //XMLObjectSerializer.serializeInto(SwitchVoiceSynthesizerActivationAction.class, this, node);
        //Save the voice if exist
        if (this.selectedVoice.get() != null) {
            node.addContent(this.selectedVoice.get().serialize(contextP));
        }
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        //XMLObjectSerializer.deserializeInto(SwitchVoiceSynthesizerActivationAction.class, this, nodeP);
        //Load the voice if exist
        Element selectedVoiceChild = nodeP.getChild(VoiceSynthesizerInfoImpl.NODE_SELECTED_VOICE);
        if (selectedVoiceChild != null) {
            VoiceSynthesizerInfoImpl voice = new VoiceSynthesizerInfoImpl();
            voice.deserialize(selectedVoiceChild, contextP);
            this.selectedVoice.set(voice);
        } else {
            this.selectedVoice.set(null);
        }
    }
    //========================================================================
}
