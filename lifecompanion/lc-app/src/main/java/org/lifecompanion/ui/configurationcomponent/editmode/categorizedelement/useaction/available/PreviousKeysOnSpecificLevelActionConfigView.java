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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PreviousKeysOnSpecificLevelAction;
import org.lifecompanion.framework.commons.translation.Translation;

public class PreviousKeysOnSpecificLevelActionConfigView extends GridPane implements UseActionConfigurationViewI<PreviousKeysOnSpecificLevelAction> {

    private Spinner<Integer> spinnerSelectedLevel;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<PreviousKeysOnSpecificLevelAction> getConfiguredActionType() {
        return PreviousKeysOnSpecificLevelAction.class;
    }

    @Override
    public void initUI() {
        final Label labelLevelSelectionField = new Label(Translation.getText("keylist.action.field.specific.level.selection"));
        GridPane.setHgrow(labelLevelSelectionField, Priority.ALWAYS);
        labelLevelSelectionField.setMaxWidth(Double.MAX_VALUE);
        spinnerSelectedLevel = UIUtils.createIntSpinner(1, 999, 1, 1, 120.0);
        int rowIndex = 0;
        this.add(labelLevelSelectionField, 0, rowIndex);
        this.add(spinnerSelectedLevel, 1, rowIndex++);
    }

    @Override
    public void editStarts(final PreviousKeysOnSpecificLevelAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        spinnerSelectedLevel.getValueFactory().setValue(element.selectedLevelProperty().get());
    }

    @Override
    public void editEnds(final PreviousKeysOnSpecificLevelAction element) {
        element.selectedLevelProperty().set(spinnerSelectedLevel.getValue());
    }
}
