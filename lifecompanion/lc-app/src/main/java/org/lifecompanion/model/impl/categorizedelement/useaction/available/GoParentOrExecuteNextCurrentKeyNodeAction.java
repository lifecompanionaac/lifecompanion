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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.FlagUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GoParentOrExecuteNextCurrentKeyNodeAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final IntegerProperty parentLevel;

    public GoParentOrExecuteNextCurrentKeyNodeAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 50;
        this.category = DefaultUseActionSubCategories.KEY_LIST_CURRENT;
        this.nameID = "go.parent.list.current.or.execute.next.action.name";
        this.staticDescriptionID = "go.parent.list.current.or.execute.next.action.description";
        this.configIconPath = "keylist/icon_go_parent_keylist.png";
        this.parameterizableAction = true;
        this.parentLevel = new SimpleIntegerProperty(1);
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    public IntegerProperty parentLevelProperty() {
        return parentLevel;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        int level = KeyListController.INSTANCE.goParentKeyNode();
        if (level > parentLevel.get()) {
            variables.put(UseActionController.FLAG_INTERRUPT_EXECUTION, new FlagUseVariable(new UseVariableDefinition(UseActionController.FLAG_INTERRUPT_EXECUTION)));
        }
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(GoParentOrExecuteNextCurrentKeyNodeAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(GoParentOrExecuteNextCurrentKeyNodeAction.class, this, nodeP);
    }
}
