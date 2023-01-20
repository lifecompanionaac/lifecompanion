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
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.actions.categories.EmailActionSubCategories;
import org.lifecompanion.plugin.email.keyoption.EmailCellKeyOption;

import java.util.Map;

public class SelectEmailAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public SelectEmailAction() {
        super(GridPartKeyComponentI.class);
        this.nameID = "email.plugin.use.action.select.email.name";
        this.staticDescriptionID = "email.plugin.use.action.select.email.description";
        this.category = EmailActionSubCategories.CURRENT;
        this.order = 10;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/icon_action_select_email.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof EmailCellKeyOption) {
                EmailCellKeyOption emailCellKeyOption = (EmailCellKeyOption) parentKey.keyOptionProperty().get();
                EmailPluginService.INSTANCE.selectEmail(emailCellKeyOption.messageProperty().get());
            }
        }
    }

}
