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
import org.lifecompanion.ui.app.main.ribbon.AbstractRibbonTabContent;
import org.lifecompanion.ui.app.main.ribbon.AbstractSelectionChangeRibbonTab;
import org.lifecompanion.ui.app.main.ribbon.available.SelectedRibbonParts.*;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.configurationcomponent.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Ribbon that contains a content that change when a component is selected.<br>
 * The main content of this ribbon change with the current selected.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RibbonTabSelected extends AbstractSelectionChangeRibbonTab {

    public RibbonTabSelected() {
        super(true, false);
    }

    @Override
    public Map<Class<?>, AbstractRibbonTabContent> getSelectionComponent() {
        HashMap<Class<?>, AbstractRibbonTabContent> componentSelection = new HashMap<>();
        componentSelection.put(StackComponent.class, new SelectedStackPart());
        componentSelection.put(TextEditorComponent.class, new SelectedTextEditorPart());
        componentSelection.put(GridPartKeyComponent.class, new SelectedKeyPart());
        componentSelection.put(MultiSelection.class, new MultiSelectedKeyPart());// Multi selection : keys
        componentSelection.put(GridPartGridComponent.class, new SelectedGridPart());
        componentSelection.put(GridPartStackComponent.class, new SelectedGridPartStack());
        componentSelection.put(GridPartTextEditorComponent.class, new SelectedGridTextEditorPart());
        return componentSelection;
    }

    @Override
    public String getNoSelectionTabTitle() {
        return Translation.getText("tabs.title.selected");
    }

    @Override
    protected boolean isValidAsSingleComponent(final DisplayableComponentI displayableComponent) {
        return true;//Every thing is valid because the tab is displayed even if no component is selected
    }

    @Override
    protected boolean disableOnNullSelection() {
        return false;
    }

    @Override
    protected boolean disableOnMultiSelection() {
        return true;
    }
}
