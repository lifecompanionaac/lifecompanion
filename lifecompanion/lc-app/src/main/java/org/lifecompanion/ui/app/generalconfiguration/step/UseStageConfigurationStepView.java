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

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.UIControlHelper;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.util.ConfigUIUtils;
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
    private Spinner<Double> spinnerWidth, spinnerHeight;

    private ToggleSwitch toggleKeepConfigurationRatio;

    /**
     * Spinner to set the frame width/height
     */
    private Spinner<Double> spinnerFrameWidth, spinnerFrameHeight;
    /**
     * To enable/disable fullscreen
     */
    private ToggleSwitch toggleEnableFullScreen;

    private ComboBox<FramePosition> comboboxFramePosition;

    private Glyph glyphKeep, glyphNotKept;
    private Button buttonKeepRatioFrameSize;

    private Slider sliderFrameOpacity;

    private double stageWidthHeightRatio;
    private ChangeListener<? super Double> changeListenerSpinnerStageHeight;
    private ChangeListener<? super Double> changeListenerSpinnerStageWidth;

    private LCConfigurationI model;

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

        Label labelConfigurationSize = UIControlHelper.createTitleLabel(Translation.getText("general.configuration.stage.configuration.size.title"));

        this.toggleEnableAutoSizing = ConfigUIUtils.createToggleSwitch("config.size.auto", "tooltip.explain.configuration.size.auto");
        //Size
        Label labelWidth = new Label(Translation.getText("config.size.width"));
        labelWidth.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        Label labelHeight = new Label(Translation.getText("config.size.height"));
        this.spinnerWidth = UIUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        UIUtils.createAndAttachTooltip(spinnerWidth, "tooltip.explain.configuration.size.width");
        GridPane.setHalignment(spinnerWidth, HPos.RIGHT);
        this.spinnerHeight = UIUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        GridPane.setHalignment(spinnerHeight, HPos.RIGHT);
        UIUtils.createAndAttachTooltip(spinnerHeight, "tooltip.explain.configuration.size.height");
        this.toggleKeepConfigurationRatio = ConfigUIUtils.createToggleSwitch("configuration.keep.ratio.field",
                "tooltip.explain.configuration.style.keep.ratio");
        Label labelExplainConfigurationSize = new Label(Translation.getText("general.configuration.stage.configuration.size.explain"));
        labelExplainConfigurationSize.getStyleClass().add("explain-text");

        glyphKeep = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LOCK).size(12).color(LCGraphicStyle.MAIN_DARK);
        glyphNotKept = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UNLOCK).size(12).color(LCGraphicStyle.MAIN_DARK);
        buttonKeepRatioFrameSize = UIUtils.createGraphicButton(glyphKeep, "general.configuration.stage.size.keep.ratio");
        buttonKeepRatioFrameSize.getStyleClass().add("button-without-padding");
        buttonKeepRatioFrameSize.setMinWidth(20.0);

        Label labelStageSize = UIControlHelper.createTitleLabel(Translation.getText("general.configuration.stage.stage.size.title"));
        //Size
        this.spinnerFrameWidth = UIUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        UIUtils.createAndAttachTooltip(spinnerFrameWidth, "tooltip.explain.configuration.frame.width");
        GridPane.setHalignment(spinnerFrameWidth, HPos.RIGHT);
        this.spinnerFrameHeight = UIUtils.createDoubleSpinner(LCConstant.CONFIG_ROOT_COMPONENT_GAP, Double.MAX_VALUE, 50, 10.0, 110.0);
        UIUtils.createAndAttachTooltip(spinnerFrameHeight, "tooltip.explain.configuration.frame.height");
        GridPane.setHalignment(spinnerFrameHeight, HPos.RIGHT);
        Label labelStageWidth = new Label(Translation.getText("configuration.frame.width"));
        Label labelStageHeight = new Label(Translation.getText("configuration.frame.height"));
        this.toggleEnableFullScreen = ConfigUIUtils.createToggleSwitch("configuration.launch.fullscreen",
                "tooltip.explain.configuration.full.screen");
        this.comboboxFramePosition = new ComboBox<>(FXCollections.observableArrayList(FramePosition.values()));
        this.comboboxFramePosition.setCellFactory((lv) -> new FramePositionDetailledCell());
        this.comboboxFramePosition.setButtonCell(new FramePositionSimpleCell());
        this.comboboxFramePosition.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboboxFramePosition, Priority.ALWAYS);
        UIUtils.createAndAttachTooltip(comboboxFramePosition, "tooltip.explain.configuration.frame.position");
        Label labelFramePosition = new Label(Translation.getText("frame.position.on.launch"));

        this.sliderFrameOpacity = UIUtils.createBaseSlider(0.0, 1.0, 1.0);
        this.sliderFrameOpacity.setShowTickLabels(false);
        this.sliderFrameOpacity.setMajorTickUnit(0.1);
        this.sliderFrameOpacity.setMinorTickCount(0);
        GridPane.setHgrow(sliderFrameOpacity, Priority.ALWAYS);
        sliderFrameOpacity.setMaxWidth(Double.MAX_VALUE);
        UIUtils.createAndAttachTooltip(sliderFrameOpacity, "tooltip.explain.configuration.style.opacity");
        this.toggleKeepConfigurationRatio = ConfigUIUtils.createToggleSwitch("configuration.keep.ratio.field",
                "tooltip.explain.configuration.style.keep.ratio");
        Label labelFOpacity = new Label(Translation.getText("configuration.frame.opacity"));

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
        gridPaneTotal.add(toggleEnableFullScreen, 0, gridRowIndex++, columnCount, 1);
        gridPaneTotal.add(buttonKeepRatioFrameSize, 2, gridRowIndex, 1, 2);
        gridPaneTotal.add(labelStageWidth, 0, gridRowIndex);
        gridPaneTotal.add(spinnerFrameWidth, 1, gridRowIndex++);
        gridPaneTotal.add(labelStageHeight, 0, gridRowIndex);
        gridPaneTotal.add(spinnerFrameHeight, 1, gridRowIndex++);
        gridPaneTotal.add(labelFramePosition, 0, gridRowIndex);
        gridPaneTotal.add(comboboxFramePosition, 1, gridRowIndex++);
        gridPaneTotal.add(labelFOpacity, 0, gridRowIndex, 2, 1);
        gridPaneTotal.add(sliderFrameOpacity, 1, gridRowIndex++, 2, 1);

        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        setCenter(gridPaneTotal);
    }

    @Override
    public void initListener() {
        // When disable fullscreen : default size to automatic size
        this.toggleEnableFullScreen.selectedProperty().addListener((obs, ov, nv) -> {
            if (model != null && !nv) {
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
        this.spinnerFrameWidth.disableProperty().bind(this.toggleEnableFullScreen.selectedProperty());
        this.spinnerFrameHeight.disableProperty().bind(this.toggleEnableFullScreen.selectedProperty());
        this.comboboxFramePosition.disableProperty().bind(this.toggleEnableFullScreen.selectedProperty());
        this.buttonKeepRatioFrameSize.disableProperty().bind(this.toggleEnableFullScreen.selectedProperty());
        this.spinnerHeight.disableProperty().bind(this.toggleEnableAutoSizing.selectedProperty());
        this.spinnerWidth.disableProperty().bind(this.toggleEnableAutoSizing.selectedProperty());
    }
    //========================================================================


    @Override
    public void saveChanges() {
        model.frameOpacityProperty().set(sliderFrameOpacity.getValue());
        model.keepConfigurationRatioProperty().set(toggleKeepConfigurationRatio.isSelected());
        model.frameWidthProperty().set(spinnerFrameWidth.getValue());
        model.frameHeightProperty().set(spinnerFrameHeight.getValue());
        model.fullScreenOnLaunchProperty().set(toggleEnableFullScreen.isSelected());
        model.framePositionOnLaunchProperty().set(this.comboboxFramePosition.getValue());
        model.widthProperty().set(spinnerWidth.getValue());
        model.heightProperty().set(spinnerHeight.getValue());
        model.fixedSizeProperty().set(!toggleEnableAutoSizing.isSelected());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.buttonKeepRatioFrameSize.setGraphic(glyphNotKept);
        this.sliderFrameOpacity.setValue(model.frameOpacityProperty().get());
        this.toggleKeepConfigurationRatio.setSelected(model.keepConfigurationRatioProperty().get());
        setFrameWidthValueWithoutFireListener(model.frameWidthProperty().get());
        setFrameHeightValueWithoutFireListener(model.frameHeightProperty().get());
        this.toggleEnableFullScreen.setSelected(model.fullScreenOnLaunchProperty().get());
        this.comboboxFramePosition.getSelectionModel().select(model.framePositionOnLaunchProperty().get());
        this.spinnerWidth.getValueFactory().setValue(model.widthProperty().get());
        this.spinnerHeight.getValueFactory().setValue(model.heightProperty().get());
        this.toggleEnableAutoSizing.setSelected(!model.fixedSizeProperty().get());
        this.buttonKeepRatioFrameSize.setGraphic(glyphKeep);
        updateFrameSizeRatio();
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }
}
