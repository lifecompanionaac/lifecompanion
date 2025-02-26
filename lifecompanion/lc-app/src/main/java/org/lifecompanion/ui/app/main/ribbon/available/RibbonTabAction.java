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
package org.lifecompanion.ui.app.main.ribbon.available;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.ui.app.main.ribbon.AbstractRibbonTabContent;
import org.lifecompanion.ui.app.main.ribbon.AbstractSelectionChangeRibbonTab;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.HashMap;
import java.util.Map;

/**
 * Ribbon that contains a content that change if the current component can have actions.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RibbonTabAction extends AbstractSelectionChangeRibbonTab {

    public RibbonTabAction() {
        super(false, false);
        this.tabTitle.set(Translation.getText("tabs.title.actions"));
        this.disableTab.set(true);
    }

    @Override
    public Map<Class<?>, AbstractRibbonTabContent> getSelectionComponent() {
        HashMap<Class<?>, AbstractRibbonTabContent> componentSelection = new HashMap<>();
        componentSelection.put(GridPartKeyComponent.class, new ActionRibbonParts.ActionKeyPart());
        componentSelection.put(GridPartGridComponent.class, new ActionRibbonParts.ActionGridPart());
        componentSelection.put(MultiSelection.class, new ActionRibbonParts.MultiKeyActionKey());
        return componentSelection;
    }

    @Override
    protected boolean isValidAsSingleComponent(final DisplayableComponentI displayableComponent) {
        return displayableComponent instanceof UseActionTriggerComponentI;
    }

    @Override
    protected boolean disableOnNullSelection() {
        return true;
    }

    @Override
    protected boolean disableOnMultiSelection() {
        return false;
    }
}
