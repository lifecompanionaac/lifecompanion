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
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.OpenCloseNoteKeyAction;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;

public class SaveLoadNoteConfigView extends VBox implements UseActionConfigurationViewI<OpenCloseNoteKeyAction> {

    private LCColorPicker pickerWantedColor;
    private Spinner<Integer> spinnerStrokeSize;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<OpenCloseNoteKeyAction> getConfiguredActionType() {
        return OpenCloseNoteKeyAction.class;
    }

    @Override
    public void editEnds(final OpenCloseNoteKeyAction element) {
        element.wantedActivatedColorProperty().set(this.pickerWantedColor.getValue());
        element.wantedStrokeSizeProperty().set(this.spinnerStrokeSize.getValue());
    }

    @Override
    public void editStarts(final OpenCloseNoteKeyAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.pickerWantedColor.setValue(element.wantedActivatedColorProperty().get());
        this.spinnerStrokeSize.getValueFactory().setValue(element.wantedStrokeSizeProperty().get());
    }

    @Override
    public void initUI() {
        Label labelWantedColor = new Label(Translation.getText("use.action.save.load.note.color.field"));
        this.pickerWantedColor = new LCColorPicker();
        Label labelWantedStrokeSize = new Label(Translation.getText("use.action.save.load.note.stroke.size.field"));
        this.spinnerStrokeSize = FXControlUtils.createIntSpinner(0, 30, 3, 1, 75.0);
        this.setSpacing(5.0);
        this.getChildren().addAll(labelWantedColor, this.pickerWantedColor, labelWantedStrokeSize, spinnerStrokeSize);
    }

}
