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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.TimerStartAction;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;

public class TimerActionConfigView extends VBox implements UseActionConfigurationViewI<TimerStartAction> {

    private DurationPickerControl durationPickerAutomaticItemTimeMs;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<TimerStartAction> getConfiguredActionType() {
        return TimerStartAction.class;
    }

    @Override
    public void initUI() {

        Label label1 = new Label(Translation.getText("use.action.timer.time"));
        label1.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(label1);
        VBox.setMargin(label1, new Insets(5, 0, 10, 0));

        durationPickerAutomaticItemTimeMs = new DurationPickerControl();
        durationPickerAutomaticItemTimeMs.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(durationPickerAutomaticItemTimeMs);
    }

    @Override
    public void editEnds(final TimerStartAction element) {
       element.timerProperty().set(durationPickerAutomaticItemTimeMs.durationProperty().get());
    }

    @Override
    public void editStarts(final TimerStartAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        durationPickerAutomaticItemTimeMs.durationProperty().set(element.timerProperty().get());
        durationPickerAutomaticItemTimeMs.tryToPickBestUnit();
    }
}