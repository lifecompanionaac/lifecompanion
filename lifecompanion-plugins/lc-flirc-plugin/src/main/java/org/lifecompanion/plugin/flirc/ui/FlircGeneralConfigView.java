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

package org.lifecompanion.plugin.flirc.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.flirc.FlircPlugin;
import org.lifecompanion.plugin.flirc.FlircPluginProperties;
import org.lifecompanion.plugin.flirc.controller.FlircController;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;

public class FlircGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "FlircGeneralConfigView";
    private FlircPluginProperties flircPluginProperties;
    private ToggleSwitch toggleSwitchEnableDebugCharts;
    private Button buttonGetKeyInfo;
    private Label labelInformation;
    private ProgressIndicator progressIndicatorLoadingInformation;
    private final BooleanProperty infoUpdateRunning;

    public FlircGeneralConfigView() {
        infoUpdateRunning = new SimpleBooleanProperty(false);
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "flirc.plugin.ui.general.config.view.title";
    }

    @Override
    public String getStep() {
        return STEP_ID;
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        Label labelGeneralInfo = FXControlUtils.createTitleLabel(Translation.getText("flirc.plugin.ui.part.title.general.information"));
        toggleSwitchEnableDebugCharts = FXControlUtils.createToggleSwitch("flirc.plugin.ui.field.enable.debug.charts.toggle", "flirc.plugin.ui.field.enable.debug.charts.toggle.tooltip");
        buttonGetKeyInfo = FXControlUtils.createLeftTextButton(Translation.getText("flirc.plugin.ui.button.get.information"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REFRESH).size(12).color(LCGraphicStyle.MAIN_PRIMARY),
                null);
        GridPane.setHalignment(buttonGetKeyInfo, HPos.CENTER);

        labelInformation = new Label(Translation.getText("flirc.plugin.ui.label.no.information"));
        labelInformation.setMaxWidth(Double.MAX_VALUE);
        labelInformation.setAlignment(Pos.TOP_CENTER);
        GridPane.setHgrow(labelInformation, Priority.ALWAYS);
        GridPane.setValignment(labelInformation, VPos.TOP);
        GridPane.setHalignment(labelInformation, HPos.CENTER);
        progressIndicatorLoadingInformation = new ProgressIndicator(-1);
        progressIndicatorLoadingInformation.setPrefSize(30, 30);
        GridPane.setValignment(progressIndicatorLoadingInformation, VPos.CENTER);
        GridPane.setHalignment(progressIndicatorLoadingInformation, HPos.CENTER);

        Pane filler = new Pane();
        GridPane.setHgrow(filler, Priority.ALWAYS);
        filler.setMaxWidth(Double.MAX_VALUE);

        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));

        int gridRowIndex = 0;
        gridPaneTotal.add(filler, 0, 0);
        gridPaneTotal.add(labelGeneralInfo, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(toggleSwitchEnableDebugCharts, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(buttonGetKeyInfo, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(progressIndicatorLoadingInformation, 0, gridRowIndex, 2, 4);
        gridPaneTotal.add(labelInformation, 0, gridRowIndex += 4, 2, 4);

        setCenter(gridPaneTotal);
    }


    @Override
    public void initListener() {
        this.buttonGetKeyInfo.setOnAction(e -> {
            if (!infoUpdateRunning.get()) {
                infoUpdateRunning.set(true);
                AsyncExecutorController.INSTANCE.addAndExecute(false, true, () -> {
                    try {
                        String settings = FlircController.INSTANCE.getSettings();
                        FXThreadUtils.runOnFXThread(() -> labelInformation.setText(settings));
                    } catch (Throwable t) {
                        ErrorHandlingController.INSTANCE.showExceptionDialog(t);
                        FXThreadUtils.runOnFXThread(() -> labelInformation.setText(Translation.getText("flirc.plugin.ui.label.no.information")));
                    } finally {
                        infoUpdateRunning.set(false);
                    }
                });
            }
        });
    }

    @Override
    public void initBinding() {
        this.buttonGetKeyInfo.disableProperty().bind(infoUpdateRunning);
        this.labelInformation.managedProperty().bind(labelInformation.visibleProperty());
        this.progressIndicatorLoadingInformation.managedProperty().bind(progressIndicatorLoadingInformation.visibleProperty());
        this.labelInformation.visibleProperty().bind(infoUpdateRunning.not());
        this.progressIndicatorLoadingInformation.visibleProperty().bind(infoUpdateRunning);
    }

    @Override
    public void saveChanges() {
        this.flircPluginProperties.enableDebugChartsProperty().set(this.toggleSwitchEnableDebugCharts.isSelected());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.flircPluginProperties = model.getPluginConfigProperties(FlircPlugin.ID, FlircPluginProperties.class);
        this.toggleSwitchEnableDebugCharts.setSelected(flircPluginProperties.enableDebugChartsProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.flircPluginProperties = null;
    }
}
