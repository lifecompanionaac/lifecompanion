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

package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useaction.available;

import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.MoveToGridAction;
import org.lifecompanion.ui.common.control.specific.selector.ComponentSelectorControl;
import org.lifecompanion.ui.common.control.specific.selector.GridComponentSelectorControl;

public class MoveToGridConfigView extends VBox implements UseActionConfigurationViewI<MoveToGridAction> {
    private ComponentSelectorControl<GridComponentI> componentSelector;

    public MoveToGridConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final MoveToGridAction actionP, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.componentSelector.selectedComponentProperty().set(actionP.targetGridProperty().get());
    }

    @Override
    public void editEnds(final MoveToGridAction actionP) {
        actionP.targetGridIdProperty().set(this.componentSelector.getSelectedComponentID());
        this.componentSelector.clearSelection();
    }

    @Override
    public void editCancelled(final MoveToGridAction element) {
        this.componentSelector.clearSelection();
    }

    @Override
    public Class<MoveToGridAction> getConfiguredActionType() {
        return MoveToGridAction.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(4.0);
        this.componentSelector = new GridComponentSelectorControl(Translation.getText("use.action.go.to.grid.grid.to.display"), true);
        this.getChildren().add(this.componentSelector);
    }
}
