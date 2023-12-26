/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.ui.common.control.specific.selector;

import org.lifecompanion.controller.editaction.GridStackActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.util.javafx.DialogUtils;

import java.util.Comparator;

public class GridComponentSelectorControl extends ComponentSelectorControl<GridComponentI> {
    public GridComponentSelectorControl(String labelText, boolean enableAddButton) {
        super(GridComponentI.class, labelText, enableAddButton ? Translation.getText("field.grid.component.selector.add.grid.button") : null, sourceNode -> {
            LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().getConfiguration();
            StackComponentI destStack = (StackComponentI) configuration.getAllComponent().values().stream()
                    .filter(d -> d instanceof StackComponentI)
                    .max(Comparator.comparingInt(d -> ((StackComponentI) d).getComponentList().size()))
                    .orElse(null);
            if (destStack != null) {
                String gridName = DialogUtils.textInputDialogWithSource(sourceNode)
                        .withHeaderText(Translation.getText("field.grid.component.selector.add.grid.name.header"))
                        .withContentText(Translation.getText("field.grid.component.selector.add.grid.name.text"))
                        .showAndWait();
                if (gridName != null) {
                    GridStackActions.AddGridInStackAction addAction = new GridStackActions.AddGridInStackAction(destStack, false, true);
                    ConfigActionController.INSTANCE.executeAction(addAction);
                    GridComponentI addedGrid = addAction.getComponent();
                    addedGrid.userNameProperty().set(gridName);
                    return addedGrid;
                }
            }
            return null;
        });
    }
}
