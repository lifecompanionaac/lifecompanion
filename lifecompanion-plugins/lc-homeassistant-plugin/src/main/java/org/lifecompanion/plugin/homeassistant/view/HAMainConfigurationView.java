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

package org.lifecompanion.plugin.homeassistant.view;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPlugin;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPluginProperties;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPluginService;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;


public class HAMainConfigurationView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    static final String STEP_ID = "HAMainConfigurationView";

    private TextField fieldServerUrl;
    private TextField fieldAuthToken;
    private ProgressIndicator progressIndicatorTesting;
    private Button buttonLaunchTest;
    private Label labelTestResult;

    public HAMainConfigurationView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "ha.plugin.main.configuration.view.title";
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


    // private ListView<OHABItem> listViewItems;
    private Button buttonRefresh;

    @Override
    public void initUI() {
        Label labelServerUrl = new Label(Translation.getText("ha.plugin.field.server.url"));
        labelServerUrl.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelServerUrl, Priority.ALWAYS);

        fieldServerUrl = new TextField();
        fieldServerUrl.setPromptText(Translation.getText("ha.plugin.field.server.url.prompt"));
        fieldServerUrl.setPrefColumnCount(25);


        Label labelAuthToken = new Label(Translation.getText("ha.plugin.field.auth.token"));
        labelAuthToken.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelAuthToken, Priority.ALWAYS);

        fieldAuthToken = new TextField();
        fieldAuthToken.setPromptText(Translation.getText("ha.plugin.field.auth.token.prompt"));
        fieldAuthToken.setPrefColumnCount(25);

        // Testing connection
        progressIndicatorTesting = new ProgressIndicator(-1);
        progressIndicatorTesting.setPrefSize(20, 20);
        labelTestResult = new Label();
        HBox boxProgressAndResult = new HBox(10, progressIndicatorTesting, labelTestResult);
        boxProgressAndResult.setAlignment(Pos.CENTER);


        buttonLaunchTest = new Button(Translation.getText("ha.plugin.button.test.connection"));
        GridPane.setHalignment(buttonLaunchTest, HPos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPane.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int rowIndex = 0;
        gridPane.add(labelServerUrl, 0, rowIndex);
        gridPane.add(fieldServerUrl, 1, rowIndex++);
        gridPane.add(labelAuthToken, 0, rowIndex);
        gridPane.add(fieldAuthToken, 1, rowIndex++);
        gridPane.add(buttonLaunchTest, 0, rowIndex++, 2, 1);

        gridPane.add(boxProgressAndResult, 0, rowIndex++, 2, 1);

        this.setCenter(gridPane);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
    }

    @Override
    public void initListener() {
        this.buttonLaunchTest.setOnAction(e -> {
            buttonLaunchTest.setDisable(true);
            progressIndicatorTesting.setProgress(-1);
            progressIndicatorTesting.setVisible(true);
            labelTestResult.setVisible(true);
            labelTestResult.setText(Translation.getText("ha.plugin.check.connection.running"));
            Thread testingThread = new Thread(() -> {
                try {
                    Pair<Boolean, String> result = HomeAssistantPluginService.INSTANCE.checkConnection(this.fieldServerUrl.getText(), fieldAuthToken.getText());
                    String resultText = result.getLeft() ?
                            Translation.getText("ha.plugin.check.connection.ok", result.getRight()) : Translation.getText("ha.plugin.check.connection.nok", result.getRight());
                    Platform.runLater(() -> labelTestResult.setText(resultText));
                } finally {
                    Platform.runLater(() -> {
                        buttonLaunchTest.setDisable(false);
                        progressIndicatorTesting.setProgress(1.0);
                        labelTestResult.setVisible(true);
                    });
                }
            });
            testingThread.setDaemon(true);
            testingThread.start();
        });
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        progressIndicatorTesting.setVisible(false);
        labelTestResult.setVisible(false);
    }

    @Override
    public void afterHide() {
        GeneralConfigurationStepViewI.super.afterHide();
        this.saveChanges();
    }

    @Override
    public void initBinding() {
    }

    private LCConfigurationI editedConfiguration;

    @Override
    public void saveChanges() {
        if (editedConfiguration != null) {
            final HomeAssistantPluginProperties openHABPluginProperties = editedConfiguration.getPluginConfigProperties(HomeAssistantPlugin.PLUGIN_ID, HomeAssistantPluginProperties.class);
            openHABPluginProperties.serverUrlProperty().set(fieldServerUrl.getText());
            openHABPluginProperties.authTokenProperty().set(fieldAuthToken.getText());
        }
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.editedConfiguration = model;
        final HomeAssistantPluginProperties openHABPluginProperties = editedConfiguration.getPluginConfigProperties(HomeAssistantPlugin.PLUGIN_ID, HomeAssistantPluginProperties.class);
        this.fieldServerUrl.setText(openHABPluginProperties.serverUrlProperty().get());
        this.fieldAuthToken.setText(openHABPluginProperties.authTokenProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.editedConfiguration = null;
    }
}
