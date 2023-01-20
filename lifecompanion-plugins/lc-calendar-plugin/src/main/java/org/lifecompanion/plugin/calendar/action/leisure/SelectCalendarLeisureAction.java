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

package org.lifecompanion.plugin.calendar.action.leisure;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.calendar.action.category.CalendarActionSubCategories;
import org.lifecompanion.plugin.calendar.controller.CalendarController;
import org.lifecompanion.plugin.calendar.keyoption.CalendarLeisureKeyOption;

import java.util.Map;

public class SelectCalendarLeisureAction extends SimpleUseActionImpl<GridPartKeyComponentI> {


    public SelectCalendarLeisureAction() {
        super(GridPartKeyComponentI.class);
        this.order = 0;
        this.category = CalendarActionSubCategories.LEISURE;
        this.nameID = "calendar.plugin.action.select.calendar.leisure.name";
        this.staticDescriptionID = "calendar.plugin.action.select.calendar.leisure.description";
        this.configIconPath = "icon_select_leisure.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof CalendarLeisureKeyOption) {
                CalendarController.INSTANCE.selectCalendarLeisure(parentKey, ((CalendarLeisureKeyOption) parentKey.keyOptionProperty().get()).currentSimplerKeyContentContainerProperty().get());
            }
        }
    }
}