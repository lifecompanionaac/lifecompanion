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
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeStageSizeAction;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * Action configuration view for {@link ChangeStageSizeAction}
 *
 * @author Paul BREUIL <tykapl.breuil@gmail.com>
 */
public class ChangeStageSizeConfigView extends GridPane implements UseActionConfigurationViewI<ChangeStageSizeAction> {

    private Spinner<Double> spinnerChangeRatio;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<ChangeStageSizeAction> getConfiguredActionType() {
        return ChangeStageSizeAction.class;
    }

    @Override
    public void initUI() {
        final GridPane spinnerGridPane = new GridPane();
        final Label labelChangeRatioPercent = new Label(Translation.getText("use.action.change.stage.size.change.ratio"));
        GridPane.setHgrow(labelChangeRatioPercent, Priority.ALWAYS);
        labelChangeRatioPercent.setMaxWidth(Double.MAX_VALUE);
        spinnerChangeRatio = FXControlUtils.createDoubleSpinner(0, 1000, 100, 5, 120.0);
        // Adding percent sign to spinner
        SpinnerValueFactory<Double> changeRatioValueFactory = spinnerChangeRatio.getValueFactory();
        changeRatioValueFactory.setConverter(new StringConverter<>() {

            @Override
            public String toString(Double value) {
                return value.toString() + " %";
            }

            @Override
            public Double fromString(String string) {
                String valueWithoutUnits = string.replaceAll("%", "").trim();
                if (valueWithoutUnits.isEmpty()) {
                    return 0.;
                } else {
                    return Double.valueOf(valueWithoutUnits);
                }
            }

        });
        spinnerChangeRatio.setValueFactory(changeRatioValueFactory);
        Label labelExplain = new Label(Translation.getText("use.action.change.stage.size.description"));
        labelExplain.getStyleClass().addAll("text-fill-gray", "text-font-italic", "text-wrap-enabled");
        spinnerGridPane.add(labelChangeRatioPercent, 0, 0);
        spinnerGridPane.add(spinnerChangeRatio, 1, 0);
        this.add(spinnerGridPane, 0, 0);
        this.add(labelExplain, 0, 1);
    }

    @Override
    public void editStarts(final ChangeStageSizeAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        spinnerChangeRatio.getValueFactory().setValue(element.changeRatioProperty().get());
    }

    @Override
    public void editEnds(final ChangeStageSizeAction element) {
        element.changeRatioProperty().set(spinnerChangeRatio.getValue());
    }
}