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

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.control.KeyListController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class NextInCurrentKeyListNoLoopAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {


    public NextInCurrentKeyListNoLoopAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 200;
        this.category = DefaultUseActionSubCategories.KEY_LIST_CURRENT;
        this.nameID = "action.next.key.in.keylist.in.current.noloop.name";
        this.staticDescriptionID = "action.next.key.in.keylist.in.current.noloop.description";
        this.configIconPath = "keylist/icon_next_keylist_stop.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        KeyListController.INSTANCE.nextInCurrentWithoutLoop();
    }
}
