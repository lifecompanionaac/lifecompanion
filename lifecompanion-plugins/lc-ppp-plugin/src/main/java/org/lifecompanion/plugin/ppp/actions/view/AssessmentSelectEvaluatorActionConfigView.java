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
package org.lifecompanion.plugin.ppp.actions.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.ppp.actions.SelectEvaluatorAction;
import org.lifecompanion.plugin.ppp.model.EvaluatorType;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;

public class AssessmentSelectEvaluatorActionConfigView extends VBox implements UseActionConfigurationViewI<SelectEvaluatorAction> {
    private ComboBox<EvaluatorType> evaluatorTypeField;

    public AssessmentSelectEvaluatorActionConfigView() {
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));

        Label evaluatorTypeLabel = new Label(
                Translation.getText("ppp.plugin.actions.select_evaluator.fields.evaluatorType.label"));

        this.evaluatorTypeField = new ComboBox<>(FXCollections.observableArrayList(EvaluatorType.values()));
        this.evaluatorTypeField.setButtonCell(new FormatterListCell<>(EvaluatorType::getText));
        this.evaluatorTypeField.setCellFactory((lv) -> new FormatterListCell<>(EvaluatorType::getText));

        this.getChildren().addAll(evaluatorTypeLabel, this.evaluatorTypeField);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final SelectEvaluatorAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.evaluatorTypeField.getSelectionModel().select(action.evaluatorTypeProperty().get());
    }

    @Override
    public void editEnds(final SelectEvaluatorAction action) {
        action.evaluatorTypeProperty().set(this.evaluatorTypeField.getValue());
    }

    @Override
    public Class<SelectEvaluatorAction> getConfiguredActionType() {
        return SelectEvaluatorAction.class;
    }

}
