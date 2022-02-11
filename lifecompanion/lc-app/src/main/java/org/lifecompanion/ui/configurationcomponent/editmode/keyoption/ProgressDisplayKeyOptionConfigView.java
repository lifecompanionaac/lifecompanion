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

package org.lifecompanion.ui.configurationcomponent.editmode.keyoption;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.ProgressDisplayKeyOption;
import org.lifecompanion.controller.editaction.KeyOptionActions;
import org.lifecompanion.util.LCConfigBindingUtils;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProgressDisplayKeyOptionConfigView extends BaseKeyOptionConfigView<ProgressDisplayKeyOption> {

    private ChangeListener<ProgressDisplayKeyOption.ProgressDisplayMode> changeListenerProgressDisplayMode;
    private ChangeListener<ProgressDisplayKeyOption.ProgressDisplayType> changeListenerProgressDisplayType;
    private ChangeListener<Color> changeListenerProgressColor;

    private LCColorPicker pickerProgressColor;
    private ComboBox<ProgressDisplayKeyOption.ProgressDisplayMode> comboboxProgressDisplayMode;
    private ComboBox<ProgressDisplayKeyOption.ProgressDisplayType> comboboxProgressDisplayType;

    @Override
    public Class<ProgressDisplayKeyOption> getConfiguredKeyOptionType() {
        return ProgressDisplayKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        this.pickerProgressColor = new LCColorPicker();
        this.comboboxProgressDisplayMode = new ComboBox<>(FXCollections.observableArrayList(ProgressDisplayKeyOption.ProgressDisplayMode.values()));
        comboboxProgressDisplayMode.setCellFactory(lv -> new ProgressDisplayModeListCell());
        comboboxProgressDisplayMode.setButtonCell(new ProgressDisplayModeListCell());
        this.comboboxProgressDisplayType = new ComboBox<>(FXCollections.observableArrayList(ProgressDisplayKeyOption.ProgressDisplayType.values()));
        comboboxProgressDisplayType.setCellFactory(lv -> new ProgressDisplayTypeListCell());
        comboboxProgressDisplayType.setButtonCell(new ProgressDisplayTypeListCell());

        UIUtils.setFixedWidth(comboboxProgressDisplayMode, 150.0);
        UIUtils.setFixedWidth(comboboxProgressDisplayType, 150.0);
        UIUtils.setFixedWidth(pickerProgressColor, 150.0);

        int rowIndex = 0;
        GridPane gridPane = new GridPane();
        gridPane.setVgap(4.0);
        gridPane.setHgap(10.0);
        gridPane.add(new Label(Translation.getText("progress.display.keyoption.field.progress.type")), 0, rowIndex);
        gridPane.add(comboboxProgressDisplayType, 1, rowIndex++);
        gridPane.add(new Label(Translation.getText("progress.display.keyoption.field.progress.color")), 0, rowIndex);
        gridPane.add(pickerProgressColor, 1, rowIndex++);
        gridPane.add(new Label(Translation.getText("progress.display.keyoption.field.progress.mode")), 0, rowIndex);
        gridPane.add(comboboxProgressDisplayMode, 1, rowIndex++);
        this.getChildren().addAll(gridPane);
        this.setAlignment(Pos.CENTER);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initBinding() {
        super.initBinding();
        changeListenerProgressColor = LCConfigBindingUtils.createSimpleBinding(this.pickerProgressColor.valueProperty(), this.model,
                c -> c.progressColorProperty().get(), KeyOptionActions.ChangeProgressDisplayColorAction::new);
        this.changeListenerProgressDisplayMode = LCConfigBindingUtils.createSelectionModelBinding(this.comboboxProgressDisplayMode.getSelectionModel(), //
                this.model, model -> model.progressDisplayModeProperty().get(), //
                KeyOptionActions.ChangeProgressDisplayModeAction::new);
        this.changeListenerProgressDisplayType = LCConfigBindingUtils.createSelectionModelBinding(this.comboboxProgressDisplayType.getSelectionModel(), //
                this.model, model -> model.progressDisplayTypeProperty().get(), //
                KeyOptionActions.ChangeProgressDisplayTypeAction::new);
    }

    @Override
    public void bind(final ProgressDisplayKeyOption model) {
        this.pickerProgressColor.valueProperty().set(model.progressColorProperty().get());
        this.comboboxProgressDisplayMode.getSelectionModel().select(model.progressDisplayModeProperty().get());
        this.comboboxProgressDisplayType.getSelectionModel().select(model.progressDisplayTypeProperty().get());
        model.progressColorProperty().addListener(changeListenerProgressColor);
        model.progressDisplayModeProperty().addListener(changeListenerProgressDisplayMode);
        model.progressDisplayTypeProperty().addListener(changeListenerProgressDisplayType);
    }

    @Override
    public void unbind(final ProgressDisplayKeyOption model) {
        model.progressColorProperty().removeListener(changeListenerProgressColor);
        model.progressDisplayModeProperty().removeListener(changeListenerProgressDisplayMode);
        model.progressDisplayTypeProperty().removeListener(changeListenerProgressDisplayType);
    }

    private static class ProgressDisplayModeListCell extends ListCell<ProgressDisplayKeyOption.ProgressDisplayMode> {
        @Override
        protected void updateItem(ProgressDisplayKeyOption.ProgressDisplayMode item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) setText(Translation.getText(item.getNameId()));
            else setText(null);
        }
    }

    private static class ProgressDisplayTypeListCell extends ListCell<ProgressDisplayKeyOption.ProgressDisplayType> {
        @Override
        protected void updateItem(ProgressDisplayKeyOption.ProgressDisplayType item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) setText(Translation.getText(item.getNameId()));
            else setText(null);
        }
    }
}
