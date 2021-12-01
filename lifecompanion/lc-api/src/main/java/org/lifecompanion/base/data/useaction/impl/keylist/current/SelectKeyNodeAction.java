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

package org.lifecompanion.base.data.useaction.impl.keylist.current;

import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.control.KeyListController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectKeyNodeAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public SelectKeyNodeAction() {
        super(GridPartKeyComponentI.class);
        this.order = 100;
        this.category = DefaultUseActionSubCategories.KEY_LIST_CURRENT;
        this.nameID = "select.current.keylist.keyoption.name";
        this.staticDescriptionID = "select.current.keylist.keyoption.description";
        this.configIconPath = "keylist/icon_select_keylist.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        final GridPartKeyComponentI parentComponent = this.parentComponentProperty().get();
        KeyListController.INSTANCE.selectKeyNodeAction(parentComponent);
    }

}
