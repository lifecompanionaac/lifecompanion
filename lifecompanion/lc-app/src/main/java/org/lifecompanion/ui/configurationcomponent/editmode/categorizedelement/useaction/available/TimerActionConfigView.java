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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.TimerAction;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;

public class TimerActionConfigView extends VBox implements UseActionConfigurationViewI<TimerAction> {

    private DurationPickerControl durationPickerAutomaticItemTimeMs;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<TimerAction> getConfiguredActionType() {
        return TimerAction.class;
    }

    @Override
    public void initUI() {
        Label labelTimeToReach = new Label(Translation.getText("use.action.timer.time"));

        durationPickerAutomaticItemTimeMs = new DurationPickerControl();
        durationPickerAutomaticItemTimeMs.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(durationPickerAutomaticItemTimeMs);
    }

    @Override
    public void editEnds(final TimerAction element) {
        // element.timerProperty().set(durationPickerAutomaticItemTimeMs.durationProperty.get());
    }

    @Override
    public void editStarts(final TimerAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        // durationPickerAutomaticItemTimeMs.durationProperty().set(element.timerProperty().get());
        // durationPickerAutomaticItemTimeMs.tryToPickBestUnit();
    }
}