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

package org.lifecompanion.plugin.phonecontrol.model.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.controller.PhoneControlController;

import java.util.Map;

public class CallTheNameInTheInputBox extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public CallTheNameInTheInputBox() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.APPEL;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.call.the.name.in.the.input.box.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.call.the.name.in.the.input.box.description";
        this.configIconPath = "current/call.png";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        //Appelle le numéro dans la zone de saisie
        PhoneControlController.INSTANCE.callTheNameInTheInputBox();
    }
}
