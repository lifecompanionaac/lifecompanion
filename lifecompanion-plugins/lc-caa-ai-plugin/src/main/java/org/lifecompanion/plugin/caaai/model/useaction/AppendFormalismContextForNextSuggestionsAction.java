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
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.caaai.controller.CAAAIController;
import org.lifecompanion.plugin.caaai.model.FormalismAiContextValue;
import org.lifecompanion.plugin.caaai.model.useaction.common.AppendContextForNextSuggestionsAction;

import java.util.Map;

public class AppendFormalismContextForNextSuggestionsAction extends SimpleUseActionImpl<UseActionTriggerComponentI> implements AppendContextForNextSuggestionsAction<UseActionTriggerComponentI, FormalismAiContextValue> {
    @XMLGenericProperty(FormalismAiContextValue.class)
    private final ObjectProperty<FormalismAiContextValue> contextValue;

    public AppendFormalismContextForNextSuggestionsAction() {
        super(UseActionTriggerComponentI.class);
        this.category = CAAAIActionSubCategories.TODO;
        this.nameID = "caa.ai.plugin.actions.append_formalism_context_for_next_suggestions.name";
        this.staticDescriptionID = "caa.ai.plugin.actions.append_formalism_context_for_next_suggestions.description";
        this.configIconPath = "filler_icon_32px.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty().set(getStaticDescription());

        this.contextValue = new SimpleObjectProperty<>();
    }

    public ObjectProperty<FormalismAiContextValue> contextValueProperty() {
        return this.contextValue;
    }

    // Class part : "Execute"
    // ========================================================================

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.contextValue.get() != null) {
            CAAAIController.INSTANCE.defineFormalismContextValue(this.contextValue.get());
        }
    }

    // ========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(AppendFormalismContextForNextSuggestionsAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(AppendFormalismContextForNextSuggestionsAction.class, this, nodeP);
    }
}
