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

package org.lifecompanion.plugin.officialexample.spellgame.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.officialexample.ExamplePluginOfficial;
import org.lifecompanion.plugin.officialexample.ExamplePluginProperties;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpellGameGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {

    static final String STEP_ID = "SpellGameGeneralConfigView";

    private Spinner<Double> spinnerWordDisplayInS;

    public SpellGameGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "example.plugin.config.view.general.title";
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
        this.spinnerWordDisplayInS = FXControlUtils.createDoubleSpinner(0.1, 60, 5.0, 1, GeneralConfigurationStepViewI.FIELD_WIDTH);

        GridPane gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("example.plugin.config.general.configuration.title.part"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("example.plugin.config.view.field.word.display.second")), 0, gridRowIndex);
        gridPaneConfiguration.add(spinnerWordDisplayInS, 1, gridRowIndex++);

        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(gridPaneConfiguration);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void saveChanges() {
        ExamplePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(ExamplePluginOfficial.ID, ExamplePluginProperties.class);
        pluginConfigProperties.wordDisplayTimeInMsProperty().set((int) (spinnerWordDisplayInS.getValue() * 1000.0));
    }

    private LCConfigurationI configuration;

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        ExamplePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(ExamplePluginOfficial.ID, ExamplePluginProperties.class);
        spinnerWordDisplayInS.getValueFactory().setValue(pluginConfigProperties.wordDisplayTimeInMsProperty().get() / 1000.0);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
    }
}
