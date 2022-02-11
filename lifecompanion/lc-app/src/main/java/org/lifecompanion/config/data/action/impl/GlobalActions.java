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

package org.lifecompanion.config.data.action.impl;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.FileChooserType;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.common.LCFileChooser;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.lifecompanion.util.UIUtils.getSourceFromEvent;

public class GlobalActions {
    public static final EventHandler<ActionEvent> HANDLER_GO_USE_MODE = (ea) -> ConfigActionController.INSTANCE.executeAction(new GoUseModeAction(getSourceFromEvent(ea)));
    public static final KeyCombination KEY_COMBINATION_GO_USE_MODE = new KeyCodeCombination(KeyCode.F5, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_CANCEL = new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN);
    public static final EventHandler<ActionEvent> HANDLER_CANCEL = (ea) -> ConfigActionController.INSTANCE.executeAction(new ExitLCAction(getSourceFromEvent(ea)));

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalActions.class);

    public static class GoUseModeAction implements BaseEditActionI {
        private final Node source;

        public GoUseModeAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            if (AppModeController.INSTANCE.getEditModeContext().getConfiguration() != null) {
                checkModificationForCurrentConfiguration(
                        this,
                        source,
                        Translation.getText("go.to.use.mode.save.before.message"),
                        "go.to.use.mode.save.before.button",
                        () -> AppModeController.INSTANCE.startUseModeAfterEdit() //AppModeController.INSTANCE.modeProperty().set(AppMode.USE)
                );
            }
        }

        @Override
        public String getNameID() {
            return "action.go.use.mode.name";
        }
    }

    public static class ExitLCAction implements BaseEditActionI {
        private final Node source;

        public ExitLCAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            checkModificationForCurrentConfiguration(
                    this,
                    source,
                    Translation.getText("cancel.action.confirm.message"),
                    "cancel.action.button.name",
                    Platform::exit
            );
        }

        @Override
        public String getNameID() {
            return "action.cancel.name";
        }
    }

    public static class RestartAction implements BaseEditActionI {
        private final Node source;

        public RestartAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            checkModificationForCurrentConfiguration(
                    this,
                    source,
                    Translation.getText("restart.action.confirm.message"),
                    "restart.action.button.name",
                    () -> InstallationController.INSTANCE.restart("")
            );
        }

        @Override
        public String getNameID() {
            return "action.restart.name";
        }
    }

    public static class PackageLogAction implements BaseEditActionI {
        private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
        private final Node source;

        public PackageLogAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            FileChooser chooser = LCFileChooser.getOtherFileChooser(Translation.getText("package.log.chooser.title"),
                    new FileChooser.ExtensionFilter(Translation.getText("file.type.plugin.zip"), Collections.singletonList("*.zip")), FileChooserType.OTHER_MISC_EXTERNAL);
            chooser.setInitialFileName("LifeCompanion_logs_" + PackageLogAction.DEFAULT_DATE_FORMAT.format(new Date()) + ".zip");
            File zipFile = chooser.showSaveDialog(UIUtils.getSourceWindow(source));
            if (zipFile != null) {
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, new LCTask<>("package.log.task.title") {
                    @Override
                    protected Object call() throws Exception {
                        IOUtils.zipInto(zipFile, new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "logs"), "LifeCompanion logs zip");
                        return null;
                    }
                });
            }
        }

        @Override
        public String getNameID() {
            return "action.package.log";
        }
    }

    public static void checkModificationForCurrentConfiguration(BaseEditActionI action, Node source, String message, String thenButtonNameId, PostCheckModificationAction postContinueAction) throws LCException {
        checkModificationForCurrentConfiguration(true, action, source, message, thenButtonNameId, postContinueAction);
    }

    public static void checkModificationForCurrentConfiguration(boolean condition, BaseEditActionI action, Node source, String message, String thenButtonNameId, PostCheckModificationAction postContinueAction) throws LCException {
        if (condition && AppModeController.INSTANCE.modeProperty().get() != AppMode.USE) {
            //Confirm
            if (AppModeController.INSTANCE.getEditModeContext().getConfiguration() != null) {
                int unsaved = AppModeController.INSTANCE.getEditModeContext().getConfigurationUnsavedAction();
                if (unsaved > 0) {
                    Alert dlg = ConfigUIUtils.createAlert(source, Alert.AlertType.CONFIRMATION);
                    ButtonType typeYes = new ButtonType(Translation.getText("button.type.save.and.then", Translation.getText(thenButtonNameId)), ButtonBar.ButtonData.YES);
                    ButtonType typeNo = new ButtonType(Translation.getText("button.type.then.only", StringUtils.capitalize(Translation.getText(thenButtonNameId))), ButtonBar.ButtonData.NO);
                    ButtonType typeCancel = new ButtonType(Translation.getText("button.type.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                    dlg.getDialogPane().setContentText(Translation.getText("save.and.then.message.first.part", unsaved, message));
                    dlg.getDialogPane().setHeaderText(Translation.getText("save.and.then.header"));
                    dlg.getButtonTypes().setAll(typeNo, typeYes, typeCancel);
                    Optional<ButtonType> returned = dlg.showAndWait();
                    if (returned.get() == typeYes) {
                        LCConfigurationActions.SaveAction saveAction = new LCConfigurationActions.SaveAction(source, success -> {
                            if (success) {
                                try {
                                    postContinueAction.run();
                                } catch (LCException e) {
                                    ConfigActionController.INSTANCE.reportErrorOnConfigActionDoRedoUndo(action, e, "doAction()", "error.config.action.while.do");
                                }
                            }
                        });
                        saveAction.doAction();
                        return;
                    } else if (returned.get() != typeNo) {
                        return;
                    }
                }
            }
        }
        postContinueAction.run();
    }

    public interface PostCheckModificationAction {
        void run() throws LCException;
    }
}
