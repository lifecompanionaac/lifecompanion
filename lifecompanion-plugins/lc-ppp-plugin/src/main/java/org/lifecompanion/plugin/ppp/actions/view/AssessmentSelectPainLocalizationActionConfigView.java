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

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.ppp.actions.AssessmentSelectPainLocalizationAction;

public class AssessmentSelectPainLocalizationActionConfigView extends VBox implements UseActionConfigurationViewI<AssessmentSelectPainLocalizationAction> {
    private TextField localizationField;

    public AssessmentSelectPainLocalizationActionConfigView() {
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));

        Label localizationLabel = new Label(Translation.getText(
                "ppp.plugin.actions.assessment.select_pain_localization.fields.localization.label"));

        this.localizationField = new TextField();

        this.getChildren().addAll(localizationLabel, this.localizationField);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final AssessmentSelectPainLocalizationAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.localizationField.setText(action.localizationProperty().get());
    }

    @Override
    public void editEnds(final AssessmentSelectPainLocalizationAction action) {
        action.localizationProperty().set(this.localizationField.getText());
    }

    @Override
    public Class<AssessmentSelectPainLocalizationAction> getConfiguredActionType() {
        return AssessmentSelectPainLocalizationAction.class;
    }

}
