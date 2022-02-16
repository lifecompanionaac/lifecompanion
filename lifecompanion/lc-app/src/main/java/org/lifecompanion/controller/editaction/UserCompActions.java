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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import org.lifecompanion.controller.editmode.DisplayableComponentSnapshotController;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.profile.UserCompDescriptionImpl;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.UserCompSavingTask;
import org.lifecompanion.controller.profile.UserCompController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.ui.app.main.usercomponent.UserCompEditView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.util.List;

/**
 * Class that contains all actions related to {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompActions {

    /**
     * To create or add an existing user comp.
     */
    public static class CreateOrUpdateUserComp implements BaseEditActionI {
        private DisplayableComponentI component;

        public CreateOrUpdateUserComp(final DisplayableComponentI componentP) {
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            UserCompDescriptionI userComp = new UserCompDescriptionImpl(component);
            userComp.componentImageProperty().set(DisplayableComponentSnapshotController.getComponentSnapshot(component, false, 200, 200));
            userComp.nameProperty().set(this.component.nameProperty().get());
            userComp.authorProperty().set(profile.nameProperty().get());

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
        UserCompSavingTask savingTask = IOHelper.createUserCompSavingTask(userComp, profile);
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, savingTask);
    }

    /**
     * To delete a list of user comp.
     */
    public static class DeleteUserComp implements BaseEditActionI {
        private final Node source;
        private final List<UserCompDescriptionI> compToDelete;

        public DeleteUserComp(final Node source, final List<UserCompDescriptionI> compToDelete) {
            this.source = source;
            this.compToDelete = compToDelete;
        }

        @Override
        public void doAction() throws LCException {
            if (DialogUtils
                    .alertWithSourceAndType(source, AlertType.CONFIRMATION)
                    .withHeaderText(Translation.getText("action.remove.user.comp.header"))
                    .withContentText(Translation.getText("action.remove.user.comp.message", this.compToDelete.size()))
                    .showAndWait() == ButtonType.OK) {
                LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
                //First, delete from list
                FXThreadUtils.runOnFXThread(() -> {
                    UserCompController.INSTANCE.getUserComponents().removeAll(this.compToDelete);
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("action.delete.user.comp.success.message", compToDelete.size())));
                });
                //Then delete data
                for (UserCompDescriptionI toDelete : this.compToDelete) {
                    IOUtils.deleteDirectoryAndChildren(IOHelper.getUserCompPath(profile.getID(), toDelete.getSavedComponentId()));
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
    public static class EditUserCompAction implements BaseEditActionI {
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
            ButtonType buttonTypeSave = new ButtonType(Translation.getText("user.comp.save.button"), ButtonData.YES);
            ButtonType buttonTypeCancel = new ButtonType(Translation.getText("user.comp.cancel.button"), ButtonData.CANCEL_CLOSE);
            EditUserCompAction.editView.bind(this.userComp);
            if (DialogUtils
                    .alertWithSourceAndType(source, AlertType.NONE)
                    .withContent(EditUserCompAction.editView)
                    .withHeaderText(Translation.getText("user.comp.edit.view.header"))
                    .withButtonTypes(buttonTypeCancel, buttonTypeSave).showAndWait() == buttonTypeSave) {
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
