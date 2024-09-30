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
package org.lifecompanion.plugin.caaai.model.useaction;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionManagerI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.caaai.controller.CAAAIController;
import org.lifecompanion.plugin.caaai.model.MoodAiContextValue;
import org.lifecompanion.plugin.caaai.model.useaction.common.AppendContextForNextSuggestionsAction;

import java.util.Map;

public class AppendMoodContextForNextSuggestionsAction extends SimpleUseActionImpl<UseActionTriggerComponentI> implements AppendContextForNextSuggestionsAction<UseActionTriggerComponentI, MoodAiContextValue> {
    @XMLGenericProperty(MoodAiContextValue.class)
    private final ObjectProperty<MoodAiContextValue> contextValue;

    public AppendMoodContextForNextSuggestionsAction() {
        super(UseActionTriggerComponentI.class);
        this.category = CAAAIActionSubCategories.TODO;
        this.nameID = "caa.ai.plugin.actions.append_mood_context_for_next_suggestions.name";
        this.staticDescriptionID = "caa.ai.plugin.actions.append_mood_context_for_next_suggestions.description";
        this.configIconPath = "filler_icon_32px.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty().set(getStaticDescription());

        this.contextValue = new SimpleObjectProperty<>();
    }

    public ObjectProperty<MoodAiContextValue> contextValueProperty() {
        return this.contextValue;
    }

    // Class part : "Execute"
    // ========================================================================

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.contextValue.get() != null) {
            CAAAIController.INSTANCE.defineMoodContextValue(this.contextValue.get());
            this.updateActionColors();
        }
    }

    // ========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(AppendMoodContextForNextSuggestionsAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(AppendMoodContextForNextSuggestionsAction.class, this, nodeP);
    }

    public void updateActionColors() {
        UseActionTriggerComponentI parentComp = parentComponentProperty().get();
        if (parentComp != null && parentComp.configurationParentProperty().get() != null) {
            LCConfigurationI configuration = parentComp.configurationParentProperty().get();
            configuration.getAllComponent().values().stream().filter(c -> c instanceof UseActionTriggerComponentI).forEach(c -> {
                UseActionManagerI actionManager = ((UseActionTriggerComponentI) c).getActionManager();
                AppendMoodContextForNextSuggestionsAction appendMoodAction = actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, AppendMoodContextForNextSuggestionsAction.class);
                if (appendMoodAction != null ) {
                    if(appendMoodAction.contextValue.get() == contextValue.get()){
                        setColorOn(c, Color.RED);
                    }else {
                        setColorOn(c, null);
                    }

                }
            });
        }
    }

    private void setColorOn(DisplayableComponentI c, Color o) {
        if(c instanceof GridPartKeyComponentI key){
            key.getKeyStyle().backgroundColorProperty().forced().setValue(o);
        }
    }
}
