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

package org.lifecompanion.plugin.caaai.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.util.javafx.FXControlUtils;

public class CAAAIGeneralConfigurationView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "CAAAIGeneralConfigurationView";

    private TextField textFieldApiEndpoint;
    private TextField textFieldApiToken;
    private TextArea textAreaSpeechToTextJsonConfig;
    private TextArea textAreaUserProfile;

    public CAAAIGeneralConfigurationView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "caa.ai.plugin.general.config.view.title";
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

        Label labelExample = new Label(Translation.getText("caa.ai.plugin.todo"));
        GridPane.setHgrow(labelExample, Priority.ALWAYS);
        labelExample.setMaxWidth(Double.MAX_VALUE);

        textFieldApiEndpoint = new TextField();
        textFieldApiToken = new TextField();
        textAreaSpeechToTextJsonConfig = new TextArea();
        textAreaSpeechToTextJsonConfig.setPrefRowCount(4);

        textAreaUserProfile = new TextArea();
        textAreaUserProfile.setPrefRowCount(10);

        int gridRowIndex = 0;
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("caa.ai.plugin.general.config.api.title"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("caa.ai.plugin.general.config.api.field.endpoint")), 0, gridRowIndex);
        gridPaneConfiguration.add(textFieldApiEndpoint, 1, gridRowIndex++);
        gridPaneConfiguration.add(new Label(Translation.getText("caa.ai.plugin.general.config.api.field.token")), 0, gridRowIndex);
        gridPaneConfiguration.add(textFieldApiToken, 1, gridRowIndex++);
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("caa.ai.plugin.general.config.speechToText.title"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("caa.ai.plugin.general.config.speechToText.field.jsonConfig")), 0, gridRowIndex);
        gridPaneConfiguration.add(textAreaSpeechToTextJsonConfig, 1, gridRowIndex++);

        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("caa.ai.plugin.general.config.user.title"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("caa.ai.plugin.general.config.user.profile.field")), 0, gridRowIndex);
        gridPaneConfiguration.add(textAreaUserProfile, 1, gridRowIndex++);

        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(gridPaneConfiguration);
        this.setCenter(scrollPane);
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
        CAAAIPluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);
        pluginConfigProperties.apiEndpointProperty().set(textFieldApiEndpoint.getText());
        pluginConfigProperties.apiTokenProperty().set(textFieldApiToken.getText());
        pluginConfigProperties.speechToTextJsonConfig().set(textAreaSpeechToTextJsonConfig.getText());
        pluginConfigProperties.userProfileProperty().set(textAreaUserProfile.getText());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        CAAAIPluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);
        textFieldApiEndpoint.setText(pluginConfigProperties.apiEndpointProperty().get());
        textFieldApiToken.setText(pluginConfigProperties.apiTokenProperty().get());
        textAreaSpeechToTextJsonConfig.setText(pluginConfigProperties.speechToTextJsonConfig().get());
        textAreaUserProfile.setText(pluginConfigProperties.userProfileProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        // TODO
        return GeneralConfigurationStepViewI.super.shouldCancelBeConfirmed();
    }
}
