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
package org.lifecompanion.plugin.email.actions;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.actions.categories.EmailActionSubCategories;

import java.util.Map;

public class NextEmailContentPartAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public NextEmailContentPartAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "email.plugin.use.action.next.email.content.name";
        this.staticDescriptionID = "email.plugin.use.action.next.email.content.description";
        this.category = EmailActionSubCategories.CURRENT;
        this.order = 20;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/icon_action_scroll_down.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        EmailPluginService.INSTANCE.nextContentInSelectedMessage();
    }

}
