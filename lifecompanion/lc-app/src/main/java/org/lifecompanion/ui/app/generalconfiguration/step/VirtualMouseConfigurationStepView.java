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

package org.lifecompanion.ui.app.generalconfiguration.step;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseDrawing;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.pane.specific.cell.VirtualMouseDrawingListCell;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class VirtualMouseConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private Slider sliderMouseSpeed, sliderMouseSize;
    private LCColorPicker pickerMouseColor, pickerMouseStrokeColor;
    private ComboBox<VirtualMouseDrawing> comboboxVirtualMouseDrawing;

    private LCConfigurationI model;

    public VirtualMouseConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.virtual.mouse.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.VIRTUAL_MOUSE.name();
    }


    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }


    // UI
    //========================================================================
    @Override
    public void initUI() {
        Label labelMousePartTitle = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.mouse.config.part.circle.mouse.title"));
        GridPane.setHgrow(labelMousePartTitle, Priority.ALWAYS);
        Label labelMousePartExplain = new Label(Translation.getText("general.configuration.mouse.config.part.circle.mouse.explain"));
        labelMousePartExplain.getStyleClass().add("explain-text");
        labelMousePartExplain.setMaxWidth(Double.MAX_VALUE);

        this.pickerMouseColor = new LCColorPicker();
        FXControlUtils.createAndAttachTooltip(pickerMouseColor, "tooltip.explain.use.param.virtual.mouse.color");
        Label labelMouseColor = new Label(Translation.getText("virtual.mouse.color"));
        labelMouseColor.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        GridPane.setHalignment(pickerMouseColor, HPos.RIGHT);

        this.pickerMouseStrokeColor = new LCColorPicker(LCColorPicker.ColorPickerMode.DARK);
        FXControlUtils.createAndAttachTooltip(pickerMouseStrokeColor, "tooltip.explain.use.param.virtual.mouse.stroke.color");
        Label labelMouseStrokeColor = new Label(Translation.getText("virtual.mouse.stroke.color"));
        labelMouseColor.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        GridPane.setHalignment(pickerMouseStrokeColor, HPos.RIGHT);

        this.sliderMouseSize = FXControlUtils.createBaseSlider(4, 20, 10);
        FXControlUtils.createAndAttachTooltip(sliderMouseSize, "tooltip.explain.use.param.virtual.mouse.size");
        Label labelMouseSize = new Label(Translation.getText("virtual.mouse.size"));
        labelMouseSize.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelMouseSize, Priority.ALWAYS);

        this.sliderMouseSpeed = FXControlUtils.createBaseSlider(1, 10, 5);
        FXControlUtils.createAndAttachTooltip(sliderMouseSpeed, "tooltip.explain.use.param.virtual.mouse.speed");
        Label labelMouseSpeed = new Label(Translation.getText("virtual.mouse.speed"));

        Label labelMouseDrawing = new Label(Translation.getText("virtual.mouse.drawing.label"));
        this.comboboxVirtualMouseDrawing = new ComboBox<>(FXCollections.observableArrayList(VirtualMouseDrawing.values()));
        this.comboboxVirtualMouseDrawing.setButtonCell(new VirtualMouseDrawingListCell());
        this.comboboxVirtualMouseDrawing.setCellFactory(lv -> new VirtualMouseDrawingListCell());
        this.comboboxVirtualMouseDrawing.setMaxWidth(Double.MAX_VALUE);
        FXControlUtils.createAndAttachTooltip(comboboxVirtualMouseDrawing, "tooltip.explain.use.param.virtual.mouse.draw");

        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        gridPaneTotal.add(labelMousePartTitle, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelMousePartExplain, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelMouseSpeed, 0, gridRowIndex);
        gridPaneTotal.add(sliderMouseSpeed, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseDrawing, 0, gridRowIndex);
        gridPaneTotal.add(comboboxVirtualMouseDrawing, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseColor, 0, gridRowIndex);
        gridPaneTotal.add(pickerMouseColor, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseStrokeColor, 0, gridRowIndex);
        gridPaneTotal.add(pickerMouseStrokeColor, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseSize, 0, gridRowIndex);
        gridPaneTotal.add(sliderMouseSize, 1, gridRowIndex++);

        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        setCenter(gridPaneTotal);
    }

    //========================================================================
    @Override
    public void saveChanges() {
        model.getVirtualMouseParameters().mouseColorProperty().set(pickerMouseColor.getValue());
        model.getVirtualMouseParameters().mouseStrokeColorProperty().set(pickerMouseStrokeColor.getValue());
        model.getVirtualMouseParameters().mouseSizeProperty().set((int) sliderMouseSize.getValue());
        model.getVirtualMouseParameters().mouseSpeedProperty().set((int) sliderMouseSpeed.getValue());
        model.getVirtualMouseParameters().mouseDrawingProperty().set(this.comboboxVirtualMouseDrawing.getValue());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.pickerMouseColor.setValue(model.getVirtualMouseParameters().mouseColorProperty().get());
        this.pickerMouseStrokeColor.setValue(model.getVirtualMouseParameters().mouseStrokeColorProperty().get());
        this.sliderMouseSize.adjustValue(model.getVirtualMouseParameters().mouseSizeProperty().get());
        this.sliderMouseSpeed.adjustValue(model.getVirtualMouseParameters().mouseSpeedProperty().get());
        this.comboboxVirtualMouseDrawing.getSelectionModel().select(model.getVirtualMouseParameters().mouseDrawingProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }
}
