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

package org.lifecompanion.installer.ui.model.step;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.task.InitializeInstallationTask;
import org.lifecompanion.installer.ui.model.InstallerStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;

public class InitialStep extends VBox implements InstallerStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitialStep.class);

    private final BooleanProperty nextButtonEnabled;
    private Button buttonRetryConnection;
    private Label labelExplainConnectionFailed;
    private Label labelCheckingInternetConnection;
    private Hyperlink hyperlinkWebsite;

    public InitialStep() {
        super(10.0);
        this.nextButtonEnabled = new SimpleBooleanProperty(false);
    }

    @Override
    public Node getContent() {
        return this;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        this.setAlignment(Pos.CENTER);
        Label labelExplainTitle = new Label(Translation.getText("lc.installer.step.init.label.explain.title"));
        labelExplainTitle.getStyleClass().add("label-explain-title");
        Label labelExplainText = new Label(Translation.getText("lc.installer.step.init.label.explain.text"));
        labelExplainText.setWrapText(true);
        labelExplainText.setTextAlignment(TextAlignment.JUSTIFY);
        labelExplainConnectionFailed = new Label(Translation.getText("lc.installer.step.init.label.connection.failed"));
        labelExplainConnectionFailed.setWrapText(true);
        buttonRetryConnection = new Button(Translation.getText("lc.installer.step.init.button.retry.connection"));
        buttonRetryConnection.setVisible(false);
        labelExplainConnectionFailed.setVisible(false);
        labelCheckingInternetConnection = new Label(Translation.getText("lc.installer.checking.internet.connection"));
        labelCheckingInternetConnection.setVisible(false);
        labelCheckingInternetConnection.getStyleClass().add("label-i-text");

        hyperlinkWebsite = new Hyperlink("https://lifecompanionaac.org");

        this.getChildren().addAll(labelExplainTitle, labelExplainText, hyperlinkWebsite, labelExplainConnectionFailed, labelCheckingInternetConnection, buttonRetryConnection);
    }

    @Override
    public void initListener() {
        this.buttonRetryConnection.setOnAction(e -> launchInitTask());
        this.hyperlinkWebsite.setOnAction(e -> {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                LCNamedThreadFactory.daemonThreadFactory("DesktopUtils").newThread(() -> {
                    try {
                        desktop.browse(new URI(hyperlinkWebsite.getText()));
                    } catch (Exception ex) {
                        LOGGER.warn("Couldn't open default browser to {}", hyperlinkWebsite.getText(), ex);
                    }
                }).start();
            }
        });
    }
    //========================================================================

    // MODEL
    //========================================================================
    @Override
    public void stepDisplayed() {
        launchInitTask();
    }

    private void launchInitTask() {
        nextButtonEnabled.set(false);
        buttonRetryConnection.setDisable(true);
        labelCheckingInternetConnection.setVisible(true);
        InitializeInstallationTask initTask = new InitializeInstallationTask(InstallerManager.INSTANCE.getConfiguration(), InstallerManager.INSTANCE.getClient());
        initTask.setOnSucceeded(t -> {
            labelCheckingInternetConnection.setVisible(false);
            if (initTask.getValue()) {
                nextButtonEnabled.set(true);
                labelExplainConnectionFailed.setVisible(false);
                buttonRetryConnection.setVisible(false);
            } else {
                labelExplainConnectionFailed.setVisible(true);
                buttonRetryConnection.setVisible(true);
                buttonRetryConnection.setDisable(false);
            }
        });
        InstallerManager.INSTANCE.submitTask(initTask);
    }

    @Override
    public void stepHidden() {
    }

    @Override
    public ReadOnlyBooleanProperty previousButtonAvailable() {
        return new SimpleBooleanProperty(false);
    }

    @Override
    public ReadOnlyBooleanProperty nextButtonAvailable() {
        return this.nextButtonEnabled;
    }
    //========================================================================

}
