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
package org.lifecompanion.plugin.ppp.events.view;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.plugin.ppp.events.OnAssessmentEndEventGenerator;
import org.lifecompanion.plugin.ppp.model.AssessmentType;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;

import java.util.function.Supplier;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnAssessmentEndEventConfigView extends VBox
        implements UseEventGeneratorConfigurationViewI<OnAssessmentEndEventGenerator> {

    private ComboBox<AssessmentType> assessmentTypeField;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<OnAssessmentEndEventGenerator> getConfiguredActionType() {
        return OnAssessmentEndEventGenerator.class;
    }

    @Override
    public void initUI() {
        Supplier<ListCell<AssessmentType>> listCellFactory = () -> new FormatterListCell<>(
                OnAssessmentEndEventGenerator::formatAssessmentTypeCondition,
                () -> OnAssessmentEndEventGenerator.formatAssessmentTypeCondition(null));

        this.assessmentTypeField = new ComboBox<>(FXCollections.observableArrayList(AssessmentType.values()));
        this.assessmentTypeField.getItems().add(0, null);
        this.assessmentTypeField.setButtonCell(listCellFactory.get());
        this.assessmentTypeField.setCellFactory(lv -> listCellFactory.get());

        this.getChildren().addAll(
                new Label(Translation.getText("ppp.plugin.events.assessments.end.fields.assessment_type")),
                this.assessmentTypeField);
    }

    @Override
    public void editEnds(final OnAssessmentEndEventGenerator element) {
        element.assessmentTypeConditionProperty().set(this.assessmentTypeField.getValue());
    }

    @Override
    public void editStarts(final OnAssessmentEndEventGenerator element) {
        this.assessmentTypeField.setValue(element.assessmentTypeConditionProperty().get());
    }
}
