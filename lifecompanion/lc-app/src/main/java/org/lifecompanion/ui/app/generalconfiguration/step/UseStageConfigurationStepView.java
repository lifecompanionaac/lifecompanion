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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.controlsfx.glyphfont.Glyph;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.StageMode;
import org.lifecompanion.ui.common.pane.specific.cell.StageModeSimpleCell;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.ui.common.pane.specific.cell.FramePositionDetailledCell;
import org.lifecompanion.ui.common.pane.specific.cell.FramePositionSimpleCell;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class UseStageConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {

    /**
     * To enable/disable autosizing
     */
    private ToggleSwitch toggleEnableAutoSizing;

    /**
     * Spinner to set the width/height
     */
    private Spinner<Double> spinnerWidth, spinnerHeight, spinnerAutoChangeFrameOpacityDelay;

    private ToggleSwitch toggleKeepConfigurationRatio;

    /**
     * Spinner to set the frame width/height
     */
    private Spinner<Double> spinnerFrameWidth, spinnerFrameHeight;

    private ComboBox<FramePosition> comboboxFramePosition;

    private ComboBox<StageMode> comboBoxStageModeOnLaunch;

    private Glyph glyphKeep, glyphNotKept;
    private Button buttonKeepRatioFrameSize;

    private Slider sliderFrameOpacity;

    private ToggleSwitch toggleAutoChangeFrameOpacityOnMouseExited;
    private Slider sliderFrameOpacityOnMouseExited;

    private double stageWidthHeightRatio;
    private ChangeListener<? super Double> changeListenerSpinnerStageHeight;
    private ChangeListener<? super Double> changeListenerSpinnerStageWidth;

    private LCConfigurationI model;

    private boolean dirty;

    public UseStageConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.use.stage.configuration.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.STAGE_SETTINGS.name();
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

        Label labelConfigurationSize = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.stage.configuration.size.title"));

        this.toggleEnableAutoSizing = FXControlUtils.createToggleSwitch("config.size.auto", "tooltip.explain.configuration.size.auto");
        //Size
        Label labelWidth = new Label(Translation.getText("config.size.width"));
        labelWidth.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        Label labelHeight = new Label(Translation.getText("config.size.height"));
        this.spinnerWidth = FXControlUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        FXControlUtils.createAndAttachTooltip(spinnerWidth, "tooltip.explain.configuration.size.width");
        GridPane.setHalignment(spinnerWidth, HPos.RIGHT);
        this.spinnerHeight = FXControlUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        GridPane.setHalignment(spinnerHeight, HPos.RIGHT);
        FXControlUtils.createAndAttachTooltip(spinnerHeight, "tooltip.explain.configuration.size.height");
        this.toggleKeepConfigurationRatio = FXControlUtils.createToggleSwitch("configuration.keep.ratio.field",
                "tooltip.explain.configuration.style.keep.ratio");
        Label labelExplainConfigurationSize = new Label(Translation.getText("general.configuration.stage.configuration.size.explain"));
        labelExplainConfigurationSize.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");


        glyphKeep = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LOCK).size(12).color(LCGraphicStyle.MAIN_DARK);
        glyphNotKept = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UNLOCK).size(12).color(LCGraphicStyle.MAIN_DARK);
        buttonKeepRatioFrameSize = FXControlUtils.createGraphicButton(glyphKeep, "general.configuration.stage.size.keep.ratio");
        buttonKeepRatioFrameSize.getStyleClass().add("padding-0");
        buttonKeepRatioFrameSize.setMinWidth(20.0);

        Label labelStageSize = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.stage.stage.size.title"));
        //Size
        this.spinnerFrameWidth = FXControlUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        FXControlUtils.createAndAttachTooltip(spinnerFrameWidth, "tooltip.explain.configuration.frame.width");
        GridPane.setHalignment(spinnerFrameWidth, HPos.RIGHT);
        this.spinnerFrameHeight = FXControlUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        FXControlUtils.createAndAttachTooltip(spinnerFrameHeight, "tooltip.explain.configuration.frame.height");
        GridPane.setHalignment(spinnerFrameHeight, HPos.RIGHT);
        Label labelStageWidth = new Label(Translation.getText("configuration.frame.width"));
        Label labelStageHeight = new Label(Translation.getText("configuration.frame.height"));

        GridPane subGridStageSize = new GridPane();
        subGridStageSize.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        subGridStageSize.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        subGridStageSize.add(buttonKeepRatioFrameSize, 0, 0, 1, 2);
        subGridStageSize.add(spinnerFrameWidth, 1, 0);
        subGridStageSize.add(spinnerFrameHeight, 1, 1);
        subGridStageSize.setAlignment(Pos.CENTER_RIGHT);

        comboBoxStageModeOnLaunch = new ComboBox<>(FXCollections.observableArrayList(StageMode.values()));
        this.comboBoxStageModeOnLaunch.setCellFactory((lv) -> new StageModeSimpleCell());
        this.comboBoxStageModeOnLaunch.setButtonCell(new StageModeSimpleCell());
        this.comboBoxStageModeOnLaunch.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboBoxStageModeOnLaunch, Priority.ALWAYS);
        FXControlUtils.createAndAttachTooltip(comboBoxStageModeOnLaunch, "tooltip.explain.configuration.full.screen");
        Label labelStageMode = new Label(Translation.getText("configuration.launch.fullscreen"));

        this.comboboxFramePosition = new ComboBox<>(FXCollections.observableArrayList(FramePosition.values()));
        this.comboboxFramePosition.setCellFactory((lv) -> new FramePositionDetailledCell());
        this.comboboxFramePosition.setButtonCell(new FramePositionSimpleCell());
        this.comboboxFramePosition.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboboxFramePosition, Priority.ALWAYS);
        FXControlUtils.createAndAttachTooltip(comboboxFramePosition, "tooltip.explain.configuration.frame.position");
        Label labelFramePosition = new Label(Translation.getText("frame.position.on.launch"));

        this.sliderFrameOpacity = FXControlUtils.createBaseSlider(0.1, 1.0, 1.0);
        this.sliderFrameOpacity.setShowTickLabels(false);
        this.sliderFrameOpacity.setMajorTickUnit(0.1);
        this.sliderFrameOpacity.setMinorTickCount(0);
        GridPane.setHgrow(sliderFrameOpacity, Priority.ALWAYS);
        sliderFrameOpacity.setMaxWidth(Double.MAX_VALUE);
        FXControlUtils.createAndAttachTooltip(sliderFrameOpacity, "tooltip.explain.configuration.style.opacity");
        this.toggleKeepConfigurationRatio = FXControlUtils.createToggleSwitch("configuration.keep.ratio.field",
                "tooltip.explain.configuration.style.keep.ratio");
        Label labelFOpacity = new Label(Translation.getText("configuration.frame.opacity"));

        Label labelChangeOpacityOnExitTitle = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.change.opacity.exit.title"));
        Label labelChangeOpacityOnExit = new Label(Translation.getText("general.configuration.change.opacity.exit.explain"));
        labelChangeOpacityOnExit.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");

        this.toggleAutoChangeFrameOpacityOnMouseExited = FXControlUtils.createToggleSwitch("configuration.change.frame.opacity",
                "tooltip.explain.configuration.style.change.opacity");

        this.sliderFrameOpacityOnMouseExited = FXControlUtils.createBaseSlider(0.1, 1.0, 1.0);
        this.sliderFrameOpacityOnMouseExited.setShowTickLabels(false);
        this.sliderFrameOpacityOnMouseExited.setMajorTickUnit(0.1);
        this.sliderFrameOpacityOnMouseExited.setMinorTickCount(0);
        GridPane.setHgrow(sliderFrameOpacityOnMouseExited, Priority.ALWAYS);
        sliderFrameOpacityOnMouseExited.setMaxWidth(Double.MAX_VALUE);
        FXControlUtils.createAndAttachTooltip(sliderFrameOpacityOnMouseExited, "tooltip.explain.configuration.style.change.opacity");
        Label labelOpacityOnExit = new Label(Translation.getText("configuration.opacity.on.exit.value"));

        Label labelFrameOpacityDelay = new Label(Translation.getText("configuration.latency.frame.opacity"));
        this.spinnerAutoChangeFrameOpacityDelay = FXControlUtils.createDoubleSpinner(0.0, 120.0, 0.0, 0.1, 150);
        FXControlUtils.createAndAttachTooltip(spinnerAutoChangeFrameOpacityDelay, "tooltip.explain.configuration.style.latency.opacity");
        GridPane.setHalignment(spinnerAutoChangeFrameOpacityDelay, HPos.RIGHT);

        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        int columnCount = 3;
        gridPaneTotal.add(labelConfigurationSize, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(labelExplainConfigurationSize, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(toggleEnableAutoSizing, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(labelWidth, 0, gridRowIndex);
        gridPaneTotal.add(spinnerWidth, 1, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelHeight, 0, gridRowIndex);
        gridPaneTotal.add(spinnerHeight, 1, gridRowIndex++, 2, 1);
        gridPaneTotal.add(toggleKeepConfigurationRatio, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(labelStageSize, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(labelStageMode, 0, gridRowIndex);
        gridPaneTotal.add(comboBoxStageModeOnLaunch, 1, gridRowIndex++);
        gridPaneTotal.add(labelStageWidth, 0, gridRowIndex);
        gridPaneTotal.add(subGridStageSize, 1, gridRowIndex++, 1, 2);
        gridPaneTotal.add(labelStageHeight, 0, gridRowIndex++);
        gridPaneTotal.add(labelFramePosition, 0, gridRowIndex);
        gridPaneTotal.add(comboboxFramePosition, 1, gridRowIndex++);
        gridPaneTotal.add(labelFOpacity, 0, gridRowIndex, 2, 1);
        gridPaneTotal.add(sliderFrameOpacity, 1, gridRowIndex++);
        gridPaneTotal.add(labelChangeOpacityOnExitTitle, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(labelChangeOpacityOnExit, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(toggleAutoChangeFrameOpacityOnMouseExited, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(labelOpacityOnExit, 0, gridRowIndex, 2, 1);
        gridPaneTotal.add(sliderFrameOpacityOnMouseExited, 1, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelFrameOpacityDelay, 0, gridRowIndex);
        gridPaneTotal.add(spinnerAutoChangeFrameOpacityDelay, 1, gridRowIndex, 2, 1);

        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(gridPaneTotal);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);
    }

    @Override
    public void initListener() {
        // When disable fullscreen : default size to automatic size
        this.comboBoxStageModeOnLaunch.valueProperty().addListener((obs, ov, nv) -> {
            if (model != null && nv == StageMode.BASE) {
                setFrameWidthValueWithoutFireListener(model.automaticFrameWidthProperty().get());
                setFrameHeightValueWithoutFireListener(model.automaticFrameHeightProperty().get());
                updateFrameSizeRatio();
            }
        });

        // When disable fixed size : default size to automatic size
        this.toggleEnableAutoSizing.selectedProperty().addListener((obs, ov, nv) -> {
            if (model != null && !nv) {
                spinnerWidth.getValueFactory().setValue(model.automaticWidthProperty().get());
                spinnerHeight.getValueFactory().setValue(model.automaticHeightProperty().get());
            }
        });

        // Keep ratio stage size
        buttonKeepRatioFrameSize.setOnAction(e -> {
            buttonKeepRatioFrameSize.setGraphic(isRatioKeptFrameSize() ? glyphNotKept : glyphKeep);
            updateFrameSizeRatio();
        });
        changeListenerSpinnerStageWidth = (obs, ov, nv) -> {
            if (isRatioKeptFrameSize()) {
                setFrameHeightValueWithoutFireListener(Math.ceil(nv / stageWidthHeightRatio));
            }
        };
        changeListenerSpinnerStageHeight = (obs, ov, nv) -> {
            if (isRatioKeptFrameSize()) {
                setFrameWidthValueWithoutFireListener(Math.ceil(nv * stageWidthHeightRatio));
            }
        };
        spinnerFrameWidth.valueProperty().addListener(changeListenerSpinnerStageWidth);
        spinnerFrameHeight.valueProperty().addListener(changeListenerSpinnerStageHeight);

        sliderFrameOpacityOnMouseExited.disableProperty().bind(toggleAutoChangeFrameOpacityOnMouseExited.selectedProperty().not());
        spinnerAutoChangeFrameOpacityDelay.disableProperty().bind(toggleAutoChangeFrameOpacityOnMouseExited.selectedProperty().not());
    }

    private void setFrameWidthValueWithoutFireListener(double value) {
        spinnerFrameWidth.valueProperty().removeListener(changeListenerSpinnerStageWidth);
        spinnerFrameWidth.getValueFactory().setValue(value);
        spinnerFrameWidth.valueProperty().addListener(changeListenerSpinnerStageWidth);
    }

    private void setFrameHeightValueWithoutFireListener(double value) {
        spinnerFrameHeight.valueProperty().removeListener(changeListenerSpinnerStageHeight);
        spinnerFrameHeight.getValueFactory().setValue(value);
        spinnerFrameHeight.valueProperty().addListener(changeListenerSpinnerStageHeight);
    }

    private void updateFrameSizeRatio() {
        if (isRatioKeptFrameSize()) {
            stageWidthHeightRatio = spinnerFrameWidth.getValue() / spinnerFrameHeight.getValue();
        }
    }


    private boolean isRatioKeptFrameSize() {
        return buttonKeepRatioFrameSize.getGraphic() == glyphKeep;
    }

    @Override
    public void initBinding() {
        this.spinnerFrameWidth.disableProperty().bind(this.comboBoxStageModeOnLaunch.valueProperty().isNotEqualTo(StageMode.BASE));
        this.spinnerFrameHeight.disableProperty().bind(this.comboBoxStageModeOnLaunch.valueProperty().isNotEqualTo(StageMode.BASE));
        this.comboboxFramePosition.disableProperty().bind(this.comboBoxStageModeOnLaunch.valueProperty().isNotEqualTo(StageMode.BASE));
        this.buttonKeepRatioFrameSize.disableProperty().bind(this.comboBoxStageModeOnLaunch.valueProperty().isNotEqualTo(StageMode.BASE));
        this.spinnerHeight.disableProperty().bind(this.toggleEnableAutoSizing.selectedProperty());
        this.spinnerWidth.disableProperty().bind(this.toggleEnableAutoSizing.selectedProperty());

        InvalidationListener invalidationListener = inv -> dirty = true;
        sliderFrameOpacity.valueProperty().addListener(invalidationListener);
        toggleKeepConfigurationRatio.selectedProperty().addListener(invalidationListener);
        spinnerFrameWidth.valueProperty().addListener(invalidationListener);
        spinnerFrameHeight.valueProperty().addListener(invalidationListener);
        comboBoxStageModeOnLaunch.valueProperty().addListener(invalidationListener);
        comboboxFramePosition.valueProperty().addListener(invalidationListener);
        spinnerWidth.valueProperty().addListener(invalidationListener);
        spinnerHeight.valueProperty().addListener(invalidationListener);
        toggleEnableAutoSizing.selectedProperty().addListener(invalidationListener);
        toggleAutoChangeFrameOpacityOnMouseExited.selectedProperty().addListener(invalidationListener);
        sliderFrameOpacityOnMouseExited.valueProperty().addListener(invalidationListener);
        spinnerAutoChangeFrameOpacityDelay.valueProperty().addListener(invalidationListener);
    }
    //========================================================================


    @Override
    public void saveChanges() {
        model.frameOpacityProperty().set(sliderFrameOpacity.getValue());
        model.keepConfigurationRatioProperty().set(toggleKeepConfigurationRatio.isSelected());
        model.frameWidthProperty().set(spinnerFrameWidth.getValue());
        model.frameHeightProperty().set(spinnerFrameHeight.getValue());
        model.stageModeOnLaunchProperty().set(comboBoxStageModeOnLaunch.getValue());
        model.framePositionOnLaunchProperty().set(this.comboboxFramePosition.getValue());
        model.widthProperty().set(spinnerWidth.getValue());
        model.heightProperty().set(spinnerHeight.getValue());
        model.fixedSizeProperty().set(!toggleEnableAutoSizing.isSelected());
        model.autoChangeFrameOpacityOnMouseExitedProperty().set(toggleAutoChangeFrameOpacityOnMouseExited.isSelected());
        model.frameOpacityOnMouseExitedProperty().set(sliderFrameOpacityOnMouseExited.getValue());
        model.autoChangeFrameOpacityDelayProperty().set((int) (spinnerAutoChangeFrameOpacityDelay.getValue() * 1000.0));
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.buttonKeepRatioFrameSize.setGraphic(glyphNotKept);
        this.sliderFrameOpacity.setValue(model.frameOpacityProperty().get());
        this.toggleKeepConfigurationRatio.setSelected(model.keepConfigurationRatioProperty().get());
        setFrameWidthValueWithoutFireListener(model.frameWidthProperty().get());
        setFrameHeightValueWithoutFireListener(model.frameHeightProperty().get());
        this.comboBoxStageModeOnLaunch.getSelectionModel().select(model.stageModeOnLaunchProperty().get());
        this.comboboxFramePosition.getSelectionModel().select(model.framePositionOnLaunchProperty().get());
        this.spinnerWidth.getValueFactory().setValue(model.widthProperty().get());
        this.spinnerHeight.getValueFactory().setValue(model.heightProperty().get());
        this.toggleEnableAutoSizing.setSelected(!model.fixedSizeProperty().get());
        this.buttonKeepRatioFrameSize.setGraphic(glyphKeep);
        this.toggleAutoChangeFrameOpacityOnMouseExited.setSelected(model.autoChangeFrameOpacityOnMouseExitedProperty().get());
        this.sliderFrameOpacityOnMouseExited.setValue(model.frameOpacityOnMouseExitedProperty().get());
        this.spinnerAutoChangeFrameOpacityDelay.getValueFactory().setValue(model.autoChangeFrameOpacityDelayProperty().get() / 1000.0);
        updateFrameSizeRatio();
        dirty = false;
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        return dirty;
    }
}
