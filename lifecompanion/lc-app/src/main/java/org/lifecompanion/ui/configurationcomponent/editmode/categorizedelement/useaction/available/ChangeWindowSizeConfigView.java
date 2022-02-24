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
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeWindowSizeAction;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * Action configuration view for {@link ChangeWindowSizeAction}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>, Paul BREUIL <tykapl.breuil@gmail.com>
 */
public class ChangeWindowSizeConfigView extends GridPane implements UseActionConfigurationViewI<ChangeWindowSizeAction> {

    private Spinner<Double> spinnerChangeRatio;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<ChangeWindowSizeAction> getConfiguredActionType() {
        return ChangeWindowSizeAction.class;
    }

    @Override
    public void initUI() {
        final Label labelLevelSelectionField = new Label(Translation.getText("use.action.change.window.size.change.ratio"));
        GridPane.setHgrow(labelLevelSelectionField, Priority.ALWAYS);
        labelLevelSelectionField.setMaxWidth(Double.MAX_VALUE);
        spinnerChangeRatio = FXControlUtils.createDoubleSpinner(0.1, 10, 1, 0.05, 120.0);
        int rowIndex = 0;
        this.add(labelLevelSelectionField, 0, rowIndex);
        this.add(spinnerChangeRatio, 1, rowIndex++);
    }

    @Override
    public void editStarts(final ChangeWindowSizeAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        spinnerChangeRatio.getValueFactory().setValue(element.changeRatioProperty().get());
    }

    @Override
    public void editEnds(final ChangeWindowSizeAction element) {
        element.changeRatioProperty().set(spinnerChangeRatio.getValue());
    }
}