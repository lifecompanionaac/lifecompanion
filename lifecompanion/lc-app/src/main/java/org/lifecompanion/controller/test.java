/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller;

import javafx.beans.property.StringProperty;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.exception.LCException;

public class test {
    public static void main(String[] args) {
        try {

        } catch (Throwable t) {
            // Display an error notification
            ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails("User friendly message", t);

            // Directly an Exception Dialog
            ErrorHandlingController.INSTANCE.showExceptionDialog(t);
        }

    }

}
