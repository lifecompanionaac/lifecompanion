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
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeStageSizeAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.GameAddCurrentScoreAction;
import org.lifecompanion.util.javafx.FXControlUtils;

public class GameAddCurrentScoreConfigView extends GridPane implements UseActionConfigurationViewI<GameAddCurrentScoreAction> {

    private Spinner<Integer> spinnerScore;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<GameAddCurrentScoreAction> getConfiguredActionType() {
        return GameAddCurrentScoreAction.class;
    }

    @Override
    public void initUI() {
        final Label labelField = new Label(Translation.getText("use.action.game.add.current.score.field"));
        GridPane.setHgrow(labelField, Priority.ALWAYS);
        labelField.setMaxWidth(Double.MAX_VALUE);
        spinnerScore = FXControlUtils.createIntSpinner(-1000, 1000, 1, 1, 120.0);
        this.add(labelField, 0, 0);
        this.add(spinnerScore, 1, 0);
    }

    @Override
    public void editStarts(final GameAddCurrentScoreAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.spinnerScore.getValueFactory().setValue(element.toAddProperty().get());
    }

    @Override
    public void editEnds(final GameAddCurrentScoreAction element) {
        element.toAddProperty().set(spinnerScore.getValue());
    }
}