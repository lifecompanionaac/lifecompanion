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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeKeyBackgroundColorAction;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.ui.common.control.specific.selector.ComponentSelectorControl;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;

public class ChangeKeyBackgroundColorActionConfigView extends VBox implements UseActionConfigurationViewI<ChangeKeyBackgroundColorAction> {

    private ComponentSelectorControl<GridPartKeyComponentI> componentSelector;
    private LCColorPicker pickerWantedColor;
    private ToggleSwitch toggleSwitchRestoreParentStyle;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<ChangeKeyBackgroundColorAction> getConfiguredActionType() {
        return ChangeKeyBackgroundColorAction.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        //Key
        this.componentSelector = new ComponentSelectorControl<>(GridPartKeyComponentI.class,
                Translation.getText("key.action.change.background.color.field.selected.key"));
        //Image
        this.pickerWantedColor = new LCColorPicker();
        this.toggleSwitchRestoreParentStyle = ConfigUIUtils.createToggleSwitch("key.action.change.background.color.field.restore.parent", "");

        Label labelColor = new Label(Translation.getText("key.action.change.background.color.field.wanted.color"));
        HBox.setHgrow(labelColor, Priority.ALWAYS);
        labelColor.setMaxWidth(Double.MAX_VALUE);
        HBox boxColor = new HBox(5.0, labelColor, pickerWantedColor);

        this.getChildren().addAll(this.componentSelector, this.toggleSwitchRestoreParentStyle, boxColor);
    }

    @Override
    public void initBinding() {
        this.pickerWantedColor.disableProperty().bind(this.toggleSwitchRestoreParentStyle.selectedProperty());
    }

    @Override
    public void editStarts(final ChangeKeyBackgroundColorAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.componentSelector.selectedComponentProperty().set(element.targetKeyProperty().get());
        this.pickerWantedColor.setValue(element.wantedColorProperty().get());
        this.toggleSwitchRestoreParentStyle.setSelected(element.restoreParentColorProperty().get());
    }

    @Override
    public void editEnds(final ChangeKeyBackgroundColorAction element) {
        element.targetKeyProperty().set(this.componentSelector.selectedComponentProperty().get());
        element.wantedColorProperty().set(this.pickerWantedColor.getValue());
        element.restoreParentColorProperty().set(this.toggleSwitchRestoreParentStyle.isSelected());
        componentSelector.clearSelection();
    }

    @Override
    public void editCancelled(final ChangeKeyBackgroundColorAction element) {
        this.componentSelector.clearSelection();
    }
}
