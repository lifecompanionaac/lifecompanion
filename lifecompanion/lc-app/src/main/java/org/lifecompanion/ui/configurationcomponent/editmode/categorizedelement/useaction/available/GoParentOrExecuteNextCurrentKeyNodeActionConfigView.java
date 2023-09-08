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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.GoParentOrExecuteNextCurrentKeyNodeAction;
import org.lifecompanion.util.javafx.FXControlUtils;

public class GoParentOrExecuteNextCurrentKeyNodeActionConfigView extends VBox implements UseActionConfigurationViewI<GoParentOrExecuteNextCurrentKeyNodeAction> {

    private Spinner<Integer> spinnerParentLevel;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<GoParentOrExecuteNextCurrentKeyNodeAction> getConfiguredActionType() {
        return GoParentOrExecuteNextCurrentKeyNodeAction.class;
    }

    @Override
    public void initUI() {
        this.spinnerParentLevel = FXControlUtils.createIntSpinner(0, 100, 1, 1, 75.0);
        this.setSpacing(5.0);
        Label labelExplain = new Label(Translation.getText("parent.level.or.execute.spinner.explain"));
        labelExplain.getStyleClass().addAll("text-wrap-enabled", "text-fill-dimgrey");
        this.getChildren().addAll(new Label(Translation.getText("parent.level.or.execute.spinner")), spinnerParentLevel, labelExplain);
    }

    @Override
    public void editStarts(final GoParentOrExecuteNextCurrentKeyNodeAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        spinnerParentLevel.getValueFactory().setValue(element.parentLevelProperty().get());
    }

    @Override
    public void editEnds(final GoParentOrExecuteNextCurrentKeyNodeAction element) {
        element.parentLevelProperty().set(spinnerParentLevel.getValue());
    }
}
