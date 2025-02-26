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
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WhiteboardSetPencilSizeAction;
import org.lifecompanion.util.javafx.FXControlUtils;

public class WhiteboardSetPencilSizeConfigView extends VBox implements UseActionConfigurationViewI<WhiteboardSetPencilSizeAction> {

    private Spinner<Double> spinnerPencilSize;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<WhiteboardSetPencilSizeAction> getConfiguredActionType() {
        return WhiteboardSetPencilSizeAction.class;
    }

    @Override
    public void initUI() {
        Label labelTimeToReach = new Label(Translation.getText("action.whiteboard.set.pencil.size.field"));
        this.spinnerPencilSize = FXControlUtils.createDoubleSpinner(1.0, 200, 10.0, 10.0, 120.0);
        this.spinnerPencilSize.setMaxWidth(Double.MAX_VALUE);
        this.getChildren().addAll(labelTimeToReach, this.spinnerPencilSize);
    }

    @Override
    public void editEnds(final WhiteboardSetPencilSizeAction element) {
        element.pencilSizeProperty().set(spinnerPencilSize.getValue());
    }

    @Override
    public void editStarts(final WhiteboardSetPencilSizeAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.spinnerPencilSize.getValueFactory().setValue(element.pencilSizeProperty().get());
    }

}
