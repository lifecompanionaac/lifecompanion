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
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
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
import org.lifecompanion.ui.app.userconfiguration.PluginConfigSubmenu;
import org.lifecompanion.ui.app.userconfiguration.UserConfigurationView;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Set;

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
            DialogUtils
                    .alertWithSourceAndType(source, Alert.AlertType.WARNING)
                    .withHeaderText(Translation.getText("add.plugin.warning.dialog.header"))
                    .withContentText(Translation.getText("add.plugin.warning.dialog.message"))
                    .withButtonTypes((new ButtonType(Translation.getText("add.plugin.warning.dialog.ok.button"), ButtonBar.ButtonData.OK_DONE)))
                    .showAndWait();
            firstAdd = false;
        }
    }

    private static void addPluginFromFile(Node source, File pluginFile) {
        try {
            Pair<PluginController.PluginAddResult, PluginInfo> loadResult = PluginController.INSTANCE.tryToAddPluginFrom(pluginFile);
            PluginInfo addedPluginInfo = loadResult.getRight();
            boolean suggestRestart = loadResult.getLeft() == PluginController.PluginAddResult.ADDED_TO_NEXT_RESTART;
            ButtonType typeRestartOrOk = new ButtonType(Translation.getText(suggestRestart ? "button.type.restart.now" : "button.type.ok"), ButtonBar.ButtonData.YES);
            ButtonType typeLater = new ButtonType(Translation.getText("button.type.restart.later"), ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType[] buttonTypes = suggestRestart ? new ButtonType[]{typeRestartOrOk, typeLater} : new ButtonType[]{typeRestartOrOk};
            ButtonType result = DialogUtils
                    .alertWithSourceAndType(source, Alert.AlertType.INFORMATION)
                    .withHeaderText(Translation.getText("plugin.loading.header.text.info"))
                    .withContentText(
                            loadResult.getLeft() == PluginController.PluginAddResult.ADDED_TO_NEXT_RESTART ? Translation.getText("plugin.load.success.base.message",
                                    addedPluginInfo.getPluginName(),
                                    addedPluginInfo.getPluginVersion()) :
                                    loadResult.getLeft() == PluginController.PluginAddResult.NOT_ADDED_ALREADY_SAME_OR_NEWER ? Translation.getText("plugin.load.success.base.not.loaded.update",
                                            addedPluginInfo.getPluginName(),
                                            addedPluginInfo.getPluginVersion()) : null
                    )
                    .withButtonTypes(buttonTypes)
                    .showAndWait();
            if (suggestRestart && result == typeRestartOrOk) {
                InstallationController.INSTANCE.restart(null);
            }
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
                    new FileChooser.ExtensionFilter(Translation.getText("file.type.plugin.jar"), List.of("*.jar")), FileChooserType.PLUGIN_ADD);
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
        private final String presetPluginId;
        private final Node source;

        public AddPluginFromWeb(final Node source) {
            this(source, null);
        }

        public AddPluginFromWeb(final Node source, String presetPluginId) {
            this.source = source;
            this.presetPluginId = presetPluginId;
        }

        @Override
        public void doAction() throws LCException {
            showAddPluginWarningDialog(source);
            String pluginId;
            if (presetPluginId == null) {
                pluginId = DialogUtils
                        .textInputDialogWithSource(source)
                        .withHeaderText(Translation.getText("plugin.installation.dialog.selection.header"))
                        .withContentText(Translation.getText("plugin.installation.dialog.selection.message"))
                        .showAndWait();
            } else {
                pluginId = presetPluginId;
            }
            if (StringUtils.isNotBlank(pluginId)) {
                DownloadPluginTask pluginDownloadTask = InstallationController.INSTANCE.createPluginDownloadTask(StringUtils.stripToEmpty(pluginId));
                pluginDownloadTask.setOnSucceeded(result -> {
                    File downloaded = pluginDownloadTask.getValue();
                    if (downloaded != null) {
                        addPluginFromFile(source, downloaded);
                    } else {
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createError(Translation.getText("plugin.installation.exception.unknown.plugin.id", pluginId)));
                    }
                });
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, pluginDownloadTask);
            }
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
            if (DialogUtils.alertWithSourceAndType(source, Alert.AlertType.CONFIRMATION)
                    .withHeaderText(Translation.getText("action.delete.plugin.confirm.header"))
                    .withContentText(Translation.getText("action.delete.plugin.confirm.message", pluginInfo.getPluginName()))
                    .showAndWait() != ButtonType.OK) {
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
                Pair<String, Set<String>> loadedPluginIdsAndMessages = checkElementPluginTask.get();
                if (loadedPluginIdsAndMessages != null) {
                    ButtonType typeCancel = new ButtonType(Translation.getText("button.type.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                    ButtonType typeContinueAnyway = new ButtonType(Translation.getText("button.type.continue.anyway"), ButtonBar.ButtonData.NO);
                    ButtonType typeTryInstall = new ButtonType(Translation.getText("button.type.try.install"), ButtonBar.ButtonData.YES);
                    ButtonType result = DialogUtils.alertWithSourceAndType(source, Alert.AlertType.WARNING)
                            .withHeaderText(Translation.getText("configuration.warning.plugin.message.header"))
                            .withContentText(Translation.getText("configuration.warning.plugin.message") + loadedPluginIdsAndMessages.getLeft())
                            .withButtonTypes(typeCancel, typeContinueAnyway, typeTryInstall)
                            .showAndWait();
                    if (result == typeTryInstall) {
                        String pluginId = loadedPluginIdsAndMessages.getRight().isEmpty() ? "unknown" : loadedPluginIdsAndMessages.getRight().iterator().next();
                        UserConfigurationView userConfigurationView = UserConfigurationController.INSTANCE.getUserConfigurationView();
                        userConfigurationView.showView(() -> userConfigurationView.showTab(PluginConfigSubmenu.class, pluginConfigSubmenu -> pluginConfigSubmenu.launchDownloadForPlugin(pluginId)));
                        return;
                    } else if (result != typeContinueAnyway) {
                        return;
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't check plugin dependencies", e);
            }
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
