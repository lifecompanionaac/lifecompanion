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
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WhiteboardSetColorAction;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;

public class WhiteboardSetColorActionConfigView extends VBox implements UseActionConfigurationViewI<WhiteboardSetColorAction> {

    private LCColorPicker pickerWantedColor;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<WhiteboardSetColorAction> getConfiguredActionType() {
        return WhiteboardSetColorAction.class;
    }

    @Override
    public void initUI() {
        this.setPadding(new Insets(10.0));
        this.pickerWantedColor = new LCColorPicker();
        Label labelColor = new Label(Translation.getText("field.action.whiteboard.set.color.picker"));
        this.getChildren().addAll(labelColor,pickerWantedColor);
    }

    @Override
    public void initBinding() {
    }

    @Override
    public void editStarts(final WhiteboardSetColorAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.pickerWantedColor.setValue(element.drawingColorProperty().get());
    }

    @Override
    public void editEnds(final WhiteboardSetColorAction element) {
        element.drawingColorProperty().set(this.pickerWantedColor.getValue());
    }
}
