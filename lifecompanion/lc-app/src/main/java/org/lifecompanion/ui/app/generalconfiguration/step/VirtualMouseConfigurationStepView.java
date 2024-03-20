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

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.DirectionalMouseDrawing;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseType;
import org.lifecompanion.ui.common.pane.specific.cell.PointingMouseDrawingListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
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
    private ComboBox<VirtualMouseType> comboboxVirtualMouseType;
    private ComboBox<DirectionalMouseDrawing> comboboxDirectionalMouseDrawing;
    private ToggleSwitch toggleSwitchMouseAccuracy;
    private Spinner<Integer> spinnerMouseMaxLoop;
    private LCConfigurationI model;
    private boolean dirty;

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
        labelMousePartExplain.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");
        labelMousePartExplain.setMaxWidth(Double.MAX_VALUE);

        Label labelMouseType = new Label(Translation.getText("virtual.mouse.type.label"));
        this.comboboxVirtualMouseType = new ComboBox<>(FXCollections.observableArrayList(VirtualMouseType.values()));
        this.comboboxVirtualMouseType.setButtonCell(new VirtualMouseDrawingListCell());
        this.comboboxVirtualMouseType.setCellFactory(lv -> new VirtualMouseDrawingListCell());
        this.comboboxVirtualMouseType.setMaxWidth(Double.MAX_VALUE);
        FXControlUtils.createAndAttachTooltip(this.comboboxVirtualMouseType, "tooltip.explain.use.param.virtual.mouse.type");

        this.sliderMouseSpeed = FXControlUtils.createBaseSlider(1, 10, 5);
        FXControlUtils.createAndAttachTooltip(this.sliderMouseSpeed, "tooltip.explain.use.param.virtual.mouse.speed");
        Label labelMouseSpeed = new Label(Translation.getText("virtual.mouse.speed"));
        this.pickerMouseColor = new LCColorPicker();

        this.sliderMouseSize = FXControlUtils.createBaseSlider(4, 20, 10);
        FXControlUtils.createAndAttachTooltip(this.sliderMouseSize, "tooltip.explain.use.param.virtual.mouse.size");
        Label labelMouseSize = new Label(Translation.getText("virtual.mouse.size"));
        labelMouseSize.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelMouseSize, Priority.ALWAYS);

        FXControlUtils.createAndAttachTooltip(this.pickerMouseColor, "tooltip.explain.use.param.virtual.mouse.color");
        Label labelMouseColor = new Label(Translation.getText("virtual.mouse.color"));
        labelMouseColor.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        GridPane.setHalignment(this.pickerMouseColor, HPos.RIGHT);

        Label labelMousePartSecondeTitle = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.mouse.config.part.arrow.mouse.title"));
        GridPane.setHgrow(labelMousePartSecondeTitle, Priority.ALWAYS);

        Label labelMouseDrawing = new Label(Translation.getText("virtual.mouse.drawing.label"));
        this.comboboxDirectionalMouseDrawing = new ComboBox<>(FXCollections.observableArrayList(DirectionalMouseDrawing.values()));
        this.comboboxDirectionalMouseDrawing.setButtonCell(new PointingMouseDrawingListCell());
        this.comboboxDirectionalMouseDrawing.setCellFactory(lv -> new PointingMouseDrawingListCell());
        this.comboboxDirectionalMouseDrawing.setMaxWidth(Double.MAX_VALUE);
        FXControlUtils.createAndAttachTooltip(this.comboboxDirectionalMouseDrawing, "tooltip.explain.use.param.virtual.mouse.draw");

        this.pickerMouseStrokeColor = new LCColorPicker(LCColorPicker.ColorPickerMode.DARK);
        FXControlUtils.createAndAttachTooltip(this.pickerMouseStrokeColor, "tooltip.explain.use.param.virtual.mouse.stroke.color");
        Label labelMouseStrokeColor = new Label(Translation.getText("virtual.mouse.stroke.color"));
        labelMouseColor.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        GridPane.setHalignment(this.pickerMouseStrokeColor, HPos.RIGHT);

        Label labelMousePartthirdTitle = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.mouse.config.part.cursor.strip.mouse.title"));
        GridPane.setHgrow(labelMousePartthirdTitle, Priority.ALWAYS);

        Label labelMouseAccuracy = new Label(Translation.getText("virtual.mouse.accuracy"));
        this.toggleSwitchMouseAccuracy = new ToggleSwitch();
        GridPane.setHalignment(this.toggleSwitchMouseAccuracy, HPos.RIGHT);


        Label labelMouseLoopMax = new Label(Translation.getText("virtual.mouse.loop.max"));
        this.spinnerMouseMaxLoop = FXControlUtils.createIntSpinner(0, 20, 3, 1, 95.0);
        FXControlUtils.createAndAttachTooltip(this.spinnerMouseMaxLoop, "tooltip.explain.use.param.virtual.mouse.loop.max");
        GridPane.setHalignment(this.spinnerMouseMaxLoop, HPos.RIGHT);

        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        gridPaneTotal.add(labelMousePartTitle, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelMousePartExplain, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelMouseType, 0, gridRowIndex);
        gridPaneTotal.add(this.comboboxVirtualMouseType, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseSpeed, 0, gridRowIndex);
        gridPaneTotal.add(this.sliderMouseSpeed, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseSize, 0, gridRowIndex);
        gridPaneTotal.add(this.sliderMouseSize, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseColor, 0, gridRowIndex);
        gridPaneTotal.add(this.pickerMouseColor, 1, gridRowIndex++);

        gridPaneTotal.add(labelMousePartSecondeTitle, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelMouseDrawing, 0, gridRowIndex);
        gridPaneTotal.add(this.comboboxDirectionalMouseDrawing, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseStrokeColor, 0, gridRowIndex);
        gridPaneTotal.add(this.pickerMouseStrokeColor, 1, gridRowIndex++);

        gridPaneTotal.add(labelMousePartthirdTitle, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelMouseAccuracy, 0, gridRowIndex);
        gridPaneTotal.add(this.toggleSwitchMouseAccuracy, 1, gridRowIndex++);
        gridPaneTotal.add(labelMouseLoopMax, 0, gridRowIndex);
        gridPaneTotal.add(this.spinnerMouseMaxLoop, 1, gridRowIndex);
        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));


        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        setCenter(gridPaneTotal);

        BooleanBinding crossScanningSelected = comboboxVirtualMouseType.getSelectionModel().selectedItemProperty().isEqualTo(VirtualMouseType.CROSS_SCANNING);
        this.toggleSwitchMouseAccuracy.disableProperty().bind(crossScanningSelected.not());
        this.spinnerMouseMaxLoop.disableProperty().bind(crossScanningSelected.not());
        this.pickerMouseStrokeColor.disableProperty().bind(crossScanningSelected);
        this.comboboxDirectionalMouseDrawing.disableProperty().bind(crossScanningSelected);
    }

    @Override
    public void initBinding() {
        InvalidationListener invalidationListener = inv -> dirty = true;
        this.pickerMouseColor.valueProperty().addListener(invalidationListener);
        this.pickerMouseStrokeColor.valueProperty().addListener(invalidationListener);
        this.sliderMouseSize.valueProperty().addListener(invalidationListener);
        this.sliderMouseSpeed.valueProperty().addListener(invalidationListener);
        this.comboboxVirtualMouseType.valueProperty().addListener(invalidationListener);
        this.comboboxDirectionalMouseDrawing.valueProperty().addListener(invalidationListener);
        this.toggleSwitchMouseAccuracy.selectedProperty().addListener(invalidationListener);
        this.spinnerMouseMaxLoop.valueProperty().addListener(invalidationListener);
    }

    //========================================================================
    @Override
    public void saveChanges() {
        this.model.getVirtualMouseParameters().mouseColorProperty().set(this.pickerMouseColor.getValue());
        this.model.getVirtualMouseParameters().mouseStrokeColorProperty().set(this.pickerMouseStrokeColor.getValue());
        this.model.getVirtualMouseParameters().mouseSizeProperty().set((int) this.sliderMouseSize.getValue());
        this.model.getVirtualMouseParameters().mouseSpeedProperty().set((int) this.sliderMouseSpeed.getValue());
        this.model.getVirtualMouseParameters().virtualMouseTypeProperty().set(this.comboboxVirtualMouseType.getValue());
        this.model.getVirtualMouseParameters().directionalMouseDrawingProperty().set(this.comboboxDirectionalMouseDrawing.getValue());
        this.model.getVirtualMouseParameters().mouseAccuracyProperty().set(this.toggleSwitchMouseAccuracy.isSelected());
        this.model.getVirtualMouseParameters().mouseMaxLoopProperty().set(this.spinnerMouseMaxLoop.getValue());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.pickerMouseColor.setValue(this.model.getVirtualMouseParameters().mouseColorProperty().get());
        this.pickerMouseStrokeColor.setValue(this.model.getVirtualMouseParameters().mouseStrokeColorProperty().get());
        this.sliderMouseSize.adjustValue(this.model.getVirtualMouseParameters().mouseSizeProperty().get());
        this.sliderMouseSpeed.adjustValue(this.model.getVirtualMouseParameters().mouseSpeedProperty().get());
        this.comboboxVirtualMouseType.getSelectionModel().select(this.model.getVirtualMouseParameters().virtualMouseTypeProperty().get());
        this.comboboxDirectionalMouseDrawing.getSelectionModel().select(this.model.getVirtualMouseParameters().directionalMouseDrawingProperty().get());
        this.toggleSwitchMouseAccuracy.setSelected(this.model.getVirtualMouseParameters().mouseAccuracyProperty().get());
        this.spinnerMouseMaxLoop.getValueFactory().setValue(this.model.getVirtualMouseParameters().mouseMaxLoopProperty().get());
        this.dirty = false;
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        return this.dirty;
    }
}
