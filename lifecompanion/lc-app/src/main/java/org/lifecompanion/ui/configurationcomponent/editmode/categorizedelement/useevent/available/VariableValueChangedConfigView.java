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
package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.VariableValueChangedEventGenerator;
import org.lifecompanion.ui.common.pane.specific.cell.UseVariableDefinitionListCell;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;

public class VariableValueChangedConfigView extends VBox implements UseEventGeneratorConfigurationViewI<VariableValueChangedEventGenerator> {

    private ComboBox<UseVariableDefinitionI> comboBoxuseVariable;
    private Spinner<Double> spinnerDelayBetweenEvent;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final VariableValueChangedEventGenerator element) {
        comboBoxuseVariable.getSelectionModel().clearSelection();
        for (UseVariableDefinitionI def : this.comboBoxuseVariable.getItems()) {
            if (def.getId().equals(element.variableNameProperty().get())) {
                comboBoxuseVariable.getSelectionModel().select(def);
            }
        }
        spinnerDelayBetweenEvent.getValueFactory().setValue(element.delayBetweenFireProperty().get() / 1000.0);
    }

    @Override
    public void editEnds(final VariableValueChangedEventGenerator element) {
        UseVariableDefinitionI selectedItem = comboBoxuseVariable.getSelectionModel().getSelectedItem();
        element.variableNameProperty().set(selectedItem != null ? selectedItem.getId() : null);
        element.delayBetweenFireProperty().set((int) (spinnerDelayBetweenEvent.getValue() * 1000.0));
    }

    @Override
    public Class<VariableValueChangedEventGenerator> getConfiguredActionType() {
        return VariableValueChangedEventGenerator.class;
    }

    @Override
    public void initUI() {
        comboBoxuseVariable = new ComboBox<>(UseVariableController.INSTANCE.getPossibleVariableList(null));
        this.comboBoxuseVariable.setCellFactory((lv) -> new UseVariableDefinitionListCell());
        this.comboBoxuseVariable.setButtonCell(new UseVariableDefinitionListCell(true));
        spinnerDelayBetweenEvent = FXControlUtils.createDoubleSpinner(0.0001, 60.0 * 60.0, 1.0, 0.1, 100.0);

        this.setSpacing(10.0);
        this.getChildren().addAll(new Label(Translation.getText("label.field.variable.value.changed.event.variable.name")), comboBoxuseVariable,
                new Label(Translation.getText("label.field.variable.value.changed.event.delay.between")), spinnerDelayBetweenEvent);
    }

}
