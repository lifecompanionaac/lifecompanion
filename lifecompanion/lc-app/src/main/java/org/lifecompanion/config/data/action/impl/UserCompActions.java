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

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.component.definition.usercomp.UserCompDescriptionI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.usercomp.UserCompDescriptionImpl;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.base.data.io.task.UserCompSavingTask;
import org.lifecompanion.config.data.control.usercomp.UserCompController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.config.view.pane.usercomp.UserCompEditView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains all actions related to {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompActions {

    /**
     * To create or add an existing user comp.
     */
    public static class CreateOrUpdateUserComp implements BaseConfigActionI {
        private DisplayableComponentI component;

        public CreateOrUpdateUserComp(final DisplayableComponentI componentP) {
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            String author = profile.nameProperty().get();
            UserCompDescriptionI userComp = UserCompDescriptionImpl.createUserComp(this.component, this.component.nameProperty().get(), author);
            //Check if comp already exist
            UserCompDescriptionI previous = UserCompController.INSTANCE.getCompBy(this.component.getID());
            if (previous != null) {
                UserCompController.INSTANCE.replace(previous, userComp);
            } else {
                UserCompController.INSTANCE.getUserComponents().add(userComp);
            }
            UserCompActions.saveUserComp(userComp);
        }

        @Override
        public String getNameID() {
            return "action.create.or.update.user.comp";
        }
    }

    /**
     * Save the given user component in the current profile.<br>
     * Execute the save action and show notification on success.
     *
     * @param userComp the user component to save
     */
    private static void saveUserComp(final UserCompDescriptionI userComp) {
        LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
        //Save the comp
        UserCompSavingTask savingTask = IOManager.INSTANCE.createUserCompSavingTask(userComp, profile);
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, savingTask);
    }

    /**
     * To delete a list of user comp.
     */
    public static class DeleteUserComp implements BaseConfigActionI {
        private final Node source;
        private final List<UserCompDescriptionI> compToDelete;

        public DeleteUserComp(final Node source, final List<UserCompDescriptionI> compToDelete) {
            this.source = source;
            this.compToDelete = compToDelete;
        }

        @Override
        public void doAction() throws LCException {
            Alert dlg = ConfigUIUtils.createAlert(source, AlertType.CONFIRMATION);
            dlg.getDialogPane().setContentText(Translation.getText("action.remove.user.comp.message", this.compToDelete.size()));
            dlg.getDialogPane().setHeaderText(Translation.getText("action.remove.user.comp.header"));
            Optional<ButtonType> returned = dlg.showAndWait();
            if (returned.get() == ButtonType.OK) {
                LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
                //First, delete from list
                LCUtils.runOnFXThread(() -> {
                    UserCompController.INSTANCE.getUserComponents().removeAll(this.compToDelete);
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("action.delete.user.comp.success.message", compToDelete.size())));
                });
                //Then delete data
                for (UserCompDescriptionI toDelete : this.compToDelete) {
                    IOUtils.deleteDirectoryAndChildren(IOManager.INSTANCE.getUserCompPath(profile.getID(), toDelete.getSavedComponentId()));
                }
            }
        }

        @Override
        public String getNameID() {
            return "action.remove.user.comp";
        }
    }

    /**
     * Action to edit the user comp informations
     */
    public static class EditUserCompAction implements BaseConfigActionI {
        private static UserCompEditView editView;
        private final Node source;
        private final UserCompDescriptionI userComp;

        public EditUserCompAction(final Node source, final UserCompDescriptionI userComp) {
            this.source = source;
            this.userComp = userComp;
        }

        @Override
        public void doAction() throws LCException {
            if (EditUserCompAction.editView == null) {
                EditUserCompAction.editView = new UserCompEditView();
            }
            Alert dlg = ConfigUIUtils.createAlert(source, AlertType.NONE);
            dlg.getDialogPane().setContent(EditUserCompAction.editView);
            ButtonType buttonTypeSave = new ButtonType(Translation.getText("user.comp.save.button"), ButtonData.YES);
            ButtonType buttonTypeCancel = new ButtonType(Translation.getText("user.comp.cancel.button"), ButtonData.CANCEL_CLOSE);
            dlg.setHeaderText(Translation.getText("user.comp.edit.view.header"));
            dlg.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeSave);
            EditUserCompAction.editView.bind(this.userComp);
            Optional<ButtonType> returned = dlg.showAndWait();
            if (returned.get() == buttonTypeSave) {
                EditUserCompAction.editView.unbind(this.userComp);//Unbind save info.
                UserCompActions.saveUserComp(this.userComp);
            }
        }

        @Override
        public String getNameID() {
            return "action.edit.user.comp";
        }

    }
}
