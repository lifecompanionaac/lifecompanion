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
package org.lifecompanion.controller.editaction;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.controller.devmode.DevModeController;
import org.lifecompanion.controller.appinstallation.task.DownloadPluginTask;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.io.task.CheckElementPluginTask;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * Class that keep actions relative to plugins.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PluginActions {
    private final static Logger LOGGER = LoggerFactory.getLogger(PluginActions.class);


    private static boolean firstAdd = true;

    private static void showAddPluginWarningDialog(Node source) {
        if (!DevModeController.INSTANCE.devModeProperty().get() && firstAdd) {
            Alert warningDialog = DialogUtils.createAlert(source, Alert.AlertType.WARNING);
            warningDialog.getButtonTypes().clear();
            warningDialog.setHeaderText(Translation.getText("add.plugin.warning.dialog.header"));
            warningDialog.setContentText(Translation.getText("add.plugin.warning.dialog.message"));
            warningDialog.getButtonTypes().add(new ButtonType(Translation.getText("add.plugin.warning.dialog.ok.button"), ButtonBar.ButtonData.OK_DONE));
            warningDialog.showAndWait();
            firstAdd = false;
        }
    }

    private static void addPluginFromFile(Node source, File pluginFile) {
        try {
            String loadResult = PluginController.INSTANCE.tryToAddPluginFrom(pluginFile).getLeft();
            Alert dialog = DialogUtils.createAlert(source, Alert.AlertType.INFORMATION);
            dialog.setHeaderText(Translation.getText("plugin.loading.header.text.info"));
            dialog.setContentText(loadResult);
            dialog.show();
        } catch (Throwable t) {
            PluginActions.LOGGER.error("Error while loading plugin {}", pluginFile, t);
            ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails(Translation.getText("plugin.error.unknown.error", pluginFile.getName()), t);
        }
    }

    public static class AddPluginAction implements BaseEditActionI {

        private final Node source;

        public AddPluginAction(final Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            showAddPluginWarningDialog(source);
            FileChooser pluginFileChooser = LCFileChoosers.getOtherFileChooser(Translation.getText("add.plugin.chooser.title"),
                    new FileChooser.ExtensionFilter(Translation.getText("file.type.plugin.jar"), Arrays.asList("*.jar")), FileChooserType.PLUGIN_ADD);
            File selectedPluginFile = pluginFileChooser.showOpenDialog(FXUtils.getSourceWindow(source));
            if (selectedPluginFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.PLUGIN_ADD, selectedPluginFile.getParentFile());
                addPluginFromFile(source, selectedPluginFile);
            }
        }

        @Override
        public String getNameID() {
            return "action.add.plugin.name";
        }
    }


    public static class AddPluginFromWeb implements BaseEditActionI {

        private final Node source;

        public AddPluginFromWeb(final Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            showAddPluginWarningDialog(source);
            TextInputDialog inputDialog = DialogUtils.createInputDialog(source, "");
            inputDialog.setHeaderText(Translation.getText("plugin.installation.dialog.selection.header"));
            inputDialog.setContentText(Translation.getText("plugin.installation.dialog.selection.message"));
            inputDialog.showAndWait().ifPresent(pluginId -> {
                DownloadPluginTask pluginDownloadTask = InstallationController.INSTANCE.createPluginDownloadTask(StringUtils.stripToEmpty(pluginId));
                pluginDownloadTask.setOnSucceeded(result -> {
                    Pair<ApplicationPluginUpdate, File> downloaded = pluginDownloadTask.getValue();
                    if (downloaded != null) {
                        addPluginFromFile(source, downloaded.getRight());
                    } else {
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createError(Translation.getText("plugin.installation.exception.unknown.plugin.id", pluginId)));
                    }
                });
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, pluginDownloadTask);
            });
        }

        @Override
        public String getNameID() {
            return "action.add.plugin.name";
        }
    }

    public static class RemovePluginAction implements BaseEditActionI {
        private final Node source;
        private final PluginInfo pluginInfo;

        public RemovePluginAction(Node source, PluginInfo pluginInfo) {
            this.source = source;
            this.pluginInfo = pluginInfo;
        }

        @Override
        public void doAction() throws LCException {
            // Confirm
            Alert dlg = DialogUtils.createAlert(source, Alert.AlertType.CONFIRMATION);
            dlg.getDialogPane().setHeaderText(Translation.getText("action.delete.plugin.confirm.header"));

            StringBuilder sb = new StringBuilder();
            sb.append(Translation.getText("action.delete.plugin.confirm.message", pluginInfo.getPluginName()));
            dlg.getDialogPane().setContentText(sb.toString());
            Optional<ButtonType> returned = dlg.showAndWait();
            if (returned.get() != ButtonType.OK) {
                return;
            }
            // Delete it
            PluginController.INSTANCE.removePlugin(pluginInfo);
        }

        @Override
        public String getNameID() {
            return "action.delete.plugin.name";
        }
    }

    // DEPENDENCY CHECK
    //========================================================================
    public static void warnOnPluginDependencies(Node source, File xmlFile, Runnable dependencyOk) {
        CheckElementPluginTask checkElementPluginTask = new CheckElementPluginTask(xmlFile);
        checkElementPluginTask.setOnSucceeded(event -> {
            try {
                String errorMessage = checkElementPluginTask.get();
                if (errorMessage != null) {
                    Alert dlg = DialogUtils.createAlert(source, Alert.AlertType.WARNING);
                    dlg.setHeaderText(Translation.getText("configuration.warning.plugin.message.header"));
                    dlg.setContentText(Translation.getText("configuration.warning.plugin.message") + errorMessage);
                    ButtonType typeCancel = new ButtonType(Translation.getText("button.type.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                    ButtonType typeContinueAnyway = new ButtonType(Translation.getText("button.type.continue.anyway"), ButtonBar.ButtonData.YES);
                    dlg.getButtonTypes().setAll(typeCancel, typeContinueAnyway);
                    Optional<ButtonType> buttonType = dlg.showAndWait();
                    if (buttonType.orElse(null) != typeContinueAnyway) {
                        return;
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't check plugin dependencies", e);
            }
            // Exit point : if dialog is display and user cancel
            dependencyOk.run();
        });
        checkElementPluginTask.setOnFailed(e -> {
            LOGGER.error("Couldn't check plugin dependency on {}", xmlFile, e.getSource().getException());
            dependencyOk.run();
        });
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, checkElementPluginTask);
    }
    //========================================================================

}
