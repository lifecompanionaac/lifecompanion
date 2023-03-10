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
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.flirc.model.useaction.SendIRAction;
import org.lifecompanion.plugin.flirc.ui.control.IRRecorderField;

public class SendIRActionConfigView extends VBox implements UseActionConfigurationViewI<SendIRAction> {
    private IRRecorderField irRecorderField;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<SendIRAction> getConfiguredActionType() {
        return SendIRAction.class;
    }

    @Override
    public void initUI() {
        irRecorderField = new IRRecorderField();
        this.getChildren().add(irRecorderField);
    }

    @Override
    public void editStarts(final SendIRAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        irRecorderField.valueProperty().set(action.patternProperty().get());
    }

    @Override
    public void editEnds(final SendIRAction action) {
        action.patternProperty().set(irRecorderField.valueProperty().get());
    }
}
