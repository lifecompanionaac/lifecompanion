/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

public class ChangeSuggestedSentencesAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public ChangeSuggestedSentencesAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = CAAAIActionSubCategories.TODO;
        this.parameterizableAction = true;
        this.nameID = "caa.ai.plugin.todo";
        this.staticDescriptionID = "caa.ai.plugin.todo";
        this.configIconPath = "filler_icon_32px.png";
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        // TODO : implementation
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(ChangeSuggestedSentencesAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeSuggestedSentencesAction.class, this, nodeP);
    }
}
