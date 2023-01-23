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

package org.lifecompanion.plugin.email.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.email.EmailPlugin;
import org.lifecompanion.plugin.email.EmailPluginProperties;
import org.lifecompanion.plugin.email.EmailService;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.util.javafx.FXControlUtils;

public class EmailPluginConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {

    private TextField fieldLogin, fieldFromName;
    private PasswordField fieldPassword;
    private TextField fieldImapdHost, fieldImapPort, fieldImapFolder;
    private TextField fieldProxyHost, fieldProxyPort;
    private TextField fieldSmtpdHost, fieldSmtpPort;

    private Button buttonLaunchTest;
    private ProgressIndicator progressIndicatorTesting;
    private Label labelTestResult;

    public EmailPluginConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "email.plugin.general.config.view.title";
    }

    @Override
    public String getStep() {
        return "EmailPluginConfigurationGeneralView";
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

        // Login configuration
        fieldLogin = new TextField();
        fieldFromName = new TextField();
        fieldPassword = new PasswordField();

        // Imap
        fieldImapdHost = new TextField();
        fieldImapPort = new TextField();
        fieldImapFolder = new TextField();

        // Proxy
        fieldProxyHost = new TextField();
        fieldProxyPort = new TextField();

        // Smtp
        fieldSmtpdHost = new TextField();
        fieldSmtpPort = new TextField();

        // Testing connection
        progressIndicatorTesting = new ProgressIndicator(-1);
        progressIndicatorTesting.setPrefSize(30, 30);
        VBox paneProgress = new VBox(progressIndicatorTesting);
        paneProgress.setAlignment(Pos.CENTER);

        buttonLaunchTest = new Button(Translation.getText("email.plugin.main.config.view.button.test"));
        buttonLaunchTest.setAlignment(Pos.CENTER);

        labelTestResult = new Label();
        labelTestResult.setAlignment(Pos.CENTER);
        HBox.setHgrow(labelTestResult, Priority.ALWAYS);

        HBox boxTesting = new HBox(10.0, buttonLaunchTest, paneProgress, labelTestResult);
        boxTesting.setAlignment(Pos.CENTER_LEFT);

        VBox vboxTotal = new VBox(5.0,
                FXControlUtils.createTitleLabel(Translation.getText("email.plugin.main.config.view.title")),
                new Label(Translation.getText("email.plugin.main.config.view.label.from.name")), fieldFromName,
                new Label(Translation.getText("email.plugin.main.config.view.label.login")), fieldLogin,
                new Label(Translation.getText("email.plugin.main.config.view.label.password")), fieldPassword,
                FXControlUtils.createTitleLabel(Translation.getText("email.plugin.tech.config.imap.view.title")),
                new Label(Translation.getText("email.plugin.main.config.view.label.host")), fieldImapdHost,
                new Label(Translation.getText("email.plugin.main.config.view.label.port")), fieldImapPort,
                new Label(Translation.getText("email.plugin.main.config.view.label.folder")), fieldImapFolder,
                FXControlUtils.createTitleLabel(Translation.getText("email.plugin.tech.config.smtp.view.title")),
                new Label(Translation.getText("email.plugin.main.config.view.label.host")), fieldSmtpdHost,
                new Label(Translation.getText("email.plugin.main.config.view.label.port")), fieldSmtpPort,
                FXControlUtils.createTitleLabel(Translation.getText("email.plugin.tech.config.proxy.view.title")),
                new Label(Translation.getText("email.plugin.main.config.view.label.host")), fieldProxyHost,
                new Label(Translation.getText("email.plugin.main.config.view.label.port")), fieldProxyPort,
                FXControlUtils.createTitleLabel(Translation.getText("email.plugin.tech.config.check.view.title")),
                boxTesting);
        vboxTotal.setPadding(new Insets(5.0));
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(vboxTotal);
        scrollPane.setFitToWidth(true);
        this.setCenter(scrollPane);
    }

    @Override
    public void initListener() {
        this.buttonLaunchTest.setOnAction(e -> {
            buttonLaunchTest.setDisable(true);
            progressIndicatorTesting.setProgress(-1);
            progressIndicatorTesting.setVisible(true);
            labelTestResult.setVisible(false);
            saveChanges();
            Thread testingThread = new Thread(() -> {
                try {
                    final Pair<Boolean, Boolean> result = EmailService.INSTANCE.testConnections(editedConfiguration.getPluginConfigProperties(EmailPlugin.PLUGIN_ID, EmailPluginProperties.class));
                    final String labelResult = Translation.getText("email.plugin.test.result.global",
                            Translation.getText(result.getKey() ? "email.plugin.test.result.yes" : "email.plugin.test.result.no"),
                            Translation.getText(result.getValue() ? "email.plugin.test.result.yes" : "email.plugin.test.result.no"));
                    Platform.runLater(() -> labelTestResult.setText(labelResult));
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
    }

    @Override
    public void saveChanges() {
        final EmailPluginProperties emailPluginProperties = editedConfiguration.getPluginConfigProperties(EmailPlugin.PLUGIN_ID, EmailPluginProperties.class);
        emailPluginProperties.loginProperty().set(fieldLogin.getText());
        emailPluginProperties.passwordProperty().set(fieldPassword.getText());
        emailPluginProperties.fromNameProperty().set(fieldFromName.getText());
        emailPluginProperties.proxyHostProperty().set(fieldProxyHost.getText());
        emailPluginProperties.proxyPortProperty().set(fieldProxyPort.getText());
        emailPluginProperties.imapsFolderProperty().set(fieldImapFolder.getText());
        emailPluginProperties.imapsHostProperty().set(fieldImapdHost.getText());
        emailPluginProperties.imapsPortProperty().set(fieldImapPort.getText());
        emailPluginProperties.smtpHostProperty().set(fieldSmtpdHost.getText());
        emailPluginProperties.smtpPortProperty().set(fieldSmtpPort.getText());
    }

    @Override
    public void cancelChanges() {
    }

    private LCConfigurationI editedConfiguration;

    @Override
    public void bind(LCConfigurationI model) {
        this.editedConfiguration = model;
        final EmailPluginProperties emailPluginProperties = editedConfiguration.getPluginConfigProperties(EmailPlugin.PLUGIN_ID, EmailPluginProperties.class);
        this.fieldLogin.setText(emailPluginProperties.loginProperty().get());
        this.fieldPassword.setText(emailPluginProperties.passwordProperty().get());
        this.fieldFromName.setText(emailPluginProperties.fromNameProperty().get());
        this.fieldProxyHost.setText(emailPluginProperties.proxyHostProperty().get());
        this.fieldProxyPort.setText(emailPluginProperties.proxyPortProperty().get());
        this.fieldImapFolder.setText(emailPluginProperties.imapsFolderProperty().get());
        this.fieldImapPort.setText(emailPluginProperties.imapsPortProperty().get());
        this.fieldImapdHost.setText(emailPluginProperties.imapsHostProperty().get());
        this.fieldSmtpdHost.setText(emailPluginProperties.smtpHostProperty().get());
        this.fieldSmtpPort.setText(emailPluginProperties.smtpPortProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.editedConfiguration = null;
    }
}
