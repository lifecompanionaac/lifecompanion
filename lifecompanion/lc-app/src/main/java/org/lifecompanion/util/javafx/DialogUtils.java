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

package org.lifecompanion.util.javafx;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.model.impl.constant.LCConstant;

public class DialogUtils {
    public static TextInputDialog createInputDialog(Node source, final String defaultValue) {
        return createInputDialog(FXUtils.getSourceWindow(source), defaultValue);
    }

    public static TextInputDialog createInputDialog(Window window, final String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        setDialogIcon(dialog);
        dialog.setTitle(LCConstant.NAME);
        dialog.initOwner(window);
        SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(dialog);
        return dialog;
    }

    public static Alert createAlert(Node source, final Alert.AlertType type) {
        return createAlert(FXUtils.getSourceWindow(source), type);
    }

    public static Alert createAlert(Window window, final Alert.AlertType type) {
        Alert dlg = new Alert(type);
        setDialogIcon(dlg);
        dlg.setTitle(LCConstant.NAME);
        dlg.initOwner(window);
        dlg.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(dlg);
        return dlg;
    }

    private static void setDialogIcon(final Dialog<?> dialog) {
        Stage ownerWindow = (Stage) dialog.getDialogPane().getScene().getWindow();
        ownerWindow.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
    }
}
