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
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeSelectionModeAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteTextAction;
import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;
import org.lifecompanion.ui.common.control.specific.usevariable.UseVariableTextArea;
import org.lifecompanion.ui.common.pane.specific.cell.SelectionModeDetailListCell;
import org.lifecompanion.ui.common.pane.specific.cell.SelectionModeSimpleListCell;

/**
 * Action configuration view for {@link WriteTextAction}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ChangeSelectionModeConfigView extends VBox implements UseActionConfigurationViewI<ChangeSelectionModeAction> {

    private ComboBox<SelectionModeEnum> comboboxSelectionMode;

    public ChangeSelectionModeConfigView() {
    }

    @Override
    public void initUI() {
        this.comboboxSelectionMode = new ComboBox<>(FXCollections.observableArrayList(SelectionModeEnum.values()));
        this.comboboxSelectionMode.setCellFactory((lv) -> new SelectionModeDetailListCell());
        this.comboboxSelectionMode.setButtonCell(new SelectionModeSimpleListCell());
        this.comboboxSelectionMode.setVisibleRowCount(4);

        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        this.getChildren().addAll(new Label(Translation.getText("use.action.change.selection.mode.to")), this.comboboxSelectionMode);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final ChangeSelectionModeAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.comboboxSelectionMode.getSelectionModel().select(SelectionModeEnum.getEnumFor(action.selectionModeTypeProperty().get()));
    }

    @Override
    public void editEnds(final ChangeSelectionModeAction action) {
        action.selectionModeTypeProperty().set(comboboxSelectionMode.getValue().getModeClass());
    }

    @Override
    public Class<ChangeSelectionModeAction> getConfiguredActionType() {
        return ChangeSelectionModeAction.class;
    }

}
