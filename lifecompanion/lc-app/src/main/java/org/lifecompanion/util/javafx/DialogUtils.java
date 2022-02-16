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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.model.impl.constant.LCConstant;

public class DialogUtils {

    public static AbstractAlertBuilder.TextInputDialogBuilder textInputDialogWithSource(Window window) {
        return new AbstractAlertBuilder.TextInputDialogBuilder(window);
    }

    public static AbstractAlertBuilder.TextInputDialogBuilder textInputDialogWithSource(Node source) {
        return new AbstractAlertBuilder.TextInputDialogBuilder(FXUtils.getSourceWindow(source));
    }

    public static AbstractAlertBuilder.AlertBuilder alertWithSourceAndType(Window owner, Alert.AlertType type) {
        return new AbstractAlertBuilder.AlertBuilder(owner, type);
    }

    public static AbstractAlertBuilder.AlertBuilder alertWithSourceAndType(Node source, Alert.AlertType type) {
        return new AbstractAlertBuilder.AlertBuilder(FXUtils.getSourceWindow(source), type);
    }
}
