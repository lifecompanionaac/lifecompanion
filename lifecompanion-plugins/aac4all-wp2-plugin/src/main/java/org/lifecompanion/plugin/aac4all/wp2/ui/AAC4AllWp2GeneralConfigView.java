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

package org.lifecompanion.plugin.aac4all.wp2.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.aac4all.wp2.AAC4AllWp2Plugin;
import org.lifecompanion.plugin.aac4all.wp2.AAC4AllWp2PluginProperties;
import org.lifecompanion.plugin.aac4all.wp2.model.logs.RandomType;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.Arrays;

import static javafx.collections.FXCollections.*;
import static org.lifecompanion.plugin.aac4all.wp2.model.logs.RandomType.*;

public class AAC4AllWp2GeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "AAC4AllWp2GeneralConfigView";

    private TextField textFieldPatientId;
    private ComboBox<RandomType> comboBoxRandomType;

    public AAC4AllWp2GeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "aac4all.wp2.plugin.general.config.view.title";
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

        GridPane gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        this.textFieldPatientId = new TextField();

        gridPaneConfiguration.add(new Label(Translation.getText("aac4all.wp2.plugin.general.config.view.field.patient.id")), 0, gridRowIndex);
        gridPaneConfiguration.add(textFieldPatientId, 1, gridRowIndex++);

        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(gridPaneConfiguration);


        //TODO : liste des random type possible pour l'evaluation.

        comboBoxRandomType = new ComboBox<>();
        comboBoxRandomType.getItems().addAll(RandomType.values());

        gridPaneConfiguration.add(new Label(Translation.getText("aac4all.wp2.plugin.general.config.combo.box.random.type")), 0, gridRowIndex);
        gridPaneConfiguration.add(comboBoxRandomType, 1, gridRowIndex);



    }

    @Override
    public void initListener() {

    }

    @Override
    public void initBinding() {


    }

    private LCConfigurationI configuration;

    @Override
    public void saveChanges() {
        AAC4AllWp2PluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(AAC4AllWp2Plugin.ID, AAC4AllWp2PluginProperties.class);
        pluginConfigProperties.patientIdProperty().set(textFieldPatientId.getText());
        pluginConfigProperties.getRandomTypeEval().set(comboBoxRandomType.getValue().getName());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        AAC4AllWp2PluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(AAC4AllWp2Plugin.ID, AAC4AllWp2PluginProperties.class);
        textFieldPatientId.setText(pluginConfigProperties.patientIdProperty().get());
        comboBoxRandomType.setValue(RandomType.fromName(pluginConfigProperties.getRandomTypeEval().getValue()));

    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
        textFieldPatientId.setText(null);
        comboBoxRandomType.setValue(null);
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        return false;
    }
}
