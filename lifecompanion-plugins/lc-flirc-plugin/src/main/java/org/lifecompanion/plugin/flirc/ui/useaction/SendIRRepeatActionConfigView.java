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

package org.lifecompanion.plugin.flirc.ui.useaction;

import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.flirc.model.useaction.SendIRAction;
import org.lifecompanion.plugin.flirc.model.useaction.SendIRRepeatAction;
import org.lifecompanion.plugin.flirc.ui.control.IRRecorderField;

public class SendIRRepeatActionConfigView extends VBox implements UseActionConfigurationViewI<SendIRRepeatAction> {
    private IRRecorderField irRecorderField;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<SendIRRepeatAction> getConfiguredActionType() {
        return SendIRRepeatAction.class;
    }

    @Override
    public void initUI() {
        irRecorderField = new IRRecorderField(false);
        this.getChildren().add(irRecorderField);
    }

    @Override
    public void editStarts(final SendIRRepeatAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        irRecorderField.valueProperty().set(action.irCodeProperty().get());
    }

    @Override
    public void editEnds(final SendIRRepeatAction action) {
        action.irCodeProperty().set(irRecorderField.valueProperty().get());
    }
}
