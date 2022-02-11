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

package org.lifecompanion.config.view.useaction.impl.configuration.change;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeSelectionModeStrokeColorAction;
import org.lifecompanion.config.view.reusable.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;

public class ChangeSelectionModeStrokeColorConfigView extends VBox implements UseActionConfigurationViewI<ChangeSelectionModeStrokeColorAction> {

    private LCColorPicker pickerWantedColor;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<ChangeSelectionModeStrokeColorAction> getConfiguredActionType() {
        return ChangeSelectionModeStrokeColorAction.class;
    }

    @Override
    public void editEnds(final ChangeSelectionModeStrokeColorAction element) {
        element.wantedColorProperty().set(this.pickerWantedColor.getValue());
    }

    @Override
    public void editStarts(final ChangeSelectionModeStrokeColorAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.pickerWantedColor.setValue(element.wantedColorProperty().get());
    }

    @Override
    public void initUI() {
        Label labelWantedColor = new Label(Translation.getText("use.action.change.selection.color.wanted.color"));
        this.pickerWantedColor = new LCColorPicker();
        this.getChildren().addAll(labelWantedColor, this.pickerWantedColor);
    }

}
