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

package org.lifecompanion.base.data.useaction.impl.keylist.selected;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.control.KeyListController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class NextKeysOnSpecificLevelAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final IntegerProperty selectedLevel;

    public NextKeysOnSpecificLevelAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 10;
        this.category = DefaultUseActionSubCategories.KEY_LIST_SELECTED;
        this.nameID = "action.next.key.in.keylist.in.selected.level.name";
        this.staticDescriptionID = "action.next.key.in.keylist.in.selected.level.description";
        this.configIconPath = "keylist/icon_next_specific_keylist.png";
        this.parameterizableAction = true;
        this.selectedLevel = new SimpleIntegerProperty(1);
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    public IntegerProperty selectedLevelProperty() {
        return selectedLevel;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        KeyListController.INSTANCE.nextOnLevel(selectedLevel.get());
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(NextKeysOnSpecificLevelAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(NextKeysOnSpecificLevelAction.class, this, nodeP);
    }

}
