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

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.task.FullInstallTask;
import org.lifecompanion.installer.task.InstallResult;
import org.lifecompanion.installer.ui.model.InstallerStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallationStep extends VBox implements InstallerStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationStep.class);

    private ProgressBar progressBarDownload;
    private TextArea textAreaLog;

    private Label labelProgressMessage, labelTaskEnd;
    private Button buttonRestartInstall;

    private final BooleanProperty nextButtonEnabled;
    private final BooleanProperty previousButtonEnabled;


    public InstallationStep() {
        super(10.0);
        this.nextButtonEnabled = new SimpleBooleanProperty(false);
        this.previousButtonEnabled = new SimpleBooleanProperty(false);
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

        Label labelTitle = new Label(Translation.getText("lc.installer.install.step.title"));
        labelTitle.getStyleClass().add("label-explain-title");

        progressBarDownload = new ProgressBar(-1);
        progressBarDownload.setPrefWidth(300.0);
        this.textAreaLog = new TextArea();
        this.textAreaLog.setPrefRowCount(12);
        this.textAreaLog.setEditable(false);
        this.textAreaLog.setWrapText(true);
        this.textAreaLog.getStyleClass().add("text-area-install-log");
        this.labelProgressMessage = new Label();
        labelTaskEnd = new Label();
        labelTaskEnd.setWrapText(true);
        labelTaskEnd.setPrefWidth(300.0);
        labelTaskEnd.setAlignment(Pos.CENTER);
        buttonRestartInstall = new Button(Translation.getText("lc.installer.button.restart.installation"));
        buttonRestartInstall.setVisible(false);
        this.getChildren().addAll(
                labelTitle,
                labelProgressMessage,
                progressBarDownload,
                new Separator(Orientation.HORIZONTAL),
                textAreaLog,
                labelTaskEnd,
                buttonRestartInstall
        );
    }

    @Override
    public void initListener() {
        buttonRestartInstall.setOnAction(e -> launchInstallationTask());
    }
    //========================================================================

    // MODEL
    //========================================================================
    @Override
    public void stepDisplayed() {
        launchInstallationTask();
    }

    private void launchInstallationTask() {
        this.labelProgressMessage.setVisible(true);
        this.labelTaskEnd.setVisible(false);
        this.buttonRestartInstall.setDisable(true);
        this.previousButtonEnabled.set(false);
        this.nextButtonEnabled.set(false);
        this.textAreaLog.clear();
        FullInstallTask installTask = new FullInstallTask(msg ->
                Platform.runLater(() -> {
                    this.textAreaLog.appendText((this.textAreaLog.getText().isBlank() ? "" : "\n") + msg);
                    this.textAreaLog.positionCaret(this.textAreaLog.getText().length());
                })
                , InstallerManager.INSTANCE.getConfiguration(), InstallerManager.INSTANCE.getClient(), InstallerManager.INSTANCE.getSpecificOrDefault(), InstallerManager.INSTANCE.getBuildProperties());
        this.progressBarDownload.progressProperty().bind(installTask.progressProperty());
        this.labelProgressMessage.textProperty().bind(installTask.messageProperty());
        installTask.setOnSucceeded(t -> {
            InstallResult installationResult = installTask.getValue();
            InstallerManager.INSTANCE.setInstallationSuccess(installationResult.isSuccess());
            this.labelProgressMessage.setVisible(false);
            nextButtonEnabled.set(installationResult.isSuccess());
            labelTaskEnd.setText((!installationResult.isSuccess() ? Translation.getText("lc.installer.installation.result.failed.general") : "") + Translation.getText(installationResult.getTranslationId()));
            labelTaskEnd.setVisible(true);
            setLabelTaskEndStyle(installationResult.isSuccess() ? "success-label" : "error-label");
            previousButtonEnabled.set(!installationResult.isSuccess());
            buttonRestartInstall.setVisible(!installationResult.isSuccess());
            this.buttonRestartInstall.setDisable(false);
        });
        installTask.setOnFailed(t -> {
            LOGGER.error("Unknown installation error", t.getSource().getException());
            this.labelProgressMessage.setVisible(false);
            labelTaskEnd.setText(Translation.getText("lc.installer.installation.result.failed.general") + Translation.getText("lc.installer.installation.result.failed.unknown"));
            labelTaskEnd.setVisible(true);
            setLabelTaskEndStyle("error-label");
            buttonRestartInstall.setVisible(true);
            this.buttonRestartInstall.setDisable(false);
            previousButtonEnabled.set(true);
        });
        InstallerManager.INSTANCE.submitTask(installTask);
    }

    private void setLabelTaskEndStyle(String styleClass) {
        labelTaskEnd.getStyleClass().removeAll("success-label", "error-label");
        labelTaskEnd.getStyleClass().add(styleClass);
    }

    @Override
    public void stepHidden() {
    }

    public ReadOnlyBooleanProperty nextButtonAvailable() {
        return nextButtonEnabled;
    }

    public ReadOnlyBooleanProperty previousButtonAvailable() {
        return previousButtonEnabled;
    }
    //========================================================================

}
