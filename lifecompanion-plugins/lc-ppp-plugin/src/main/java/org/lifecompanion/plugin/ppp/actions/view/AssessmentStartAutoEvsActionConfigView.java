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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.ppp.actions.AssessmentStartAutoEvsAction;

public class AssessmentStartAutoEvsActionConfigView extends VBox implements UseActionConfigurationViewI<AssessmentStartAutoEvsAction> {
    private ToggleSwitch askPainLocalizationField;

    public AssessmentStartAutoEvsActionConfigView() {
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));

        this.askPainLocalizationField = new ToggleSwitch(
                Translation.getText("ppp.plugin.actions.assessment.start.auto_evs.fields.ask_pain_localization.label"));
        this.askPainLocalizationField.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(this.askPainLocalizationField);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final AssessmentStartAutoEvsAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.askPainLocalizationField.setSelected(action.askPainLocalizationProperty().get());
    }

    @Override
    public void editEnds(final AssessmentStartAutoEvsAction action) {
        action.askPainLocalizationProperty().set(this.askPainLocalizationField.isSelected());
    }

    @Override
    public Class<AssessmentStartAutoEvsAction> getConfiguredActionType() {
        return AssessmentStartAutoEvsAction.class;
    }

}
