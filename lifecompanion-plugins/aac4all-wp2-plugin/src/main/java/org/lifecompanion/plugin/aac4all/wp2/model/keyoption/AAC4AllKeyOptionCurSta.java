/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.aac4all.wp2.model.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AAC4AllKeyOptionCurSta extends AbstractAAC4AllKeyOption {
    private final ObjectProperty<ActionType> actionType;

    public AAC4AllKeyOptionCurSta() {
        this.optionNameId = "aac4all.wp2.plugin.current.word.key.option.cursta.name";
        this.actionType = new SimpleObjectProperty<>();
    }

    public ObjectProperty<ActionType> actionTypeProperty() {
        return actionType;
    }

    public enum ActionType {
        WRITE_PRED(""), DELETE_LAST_CHAR("Supprimer"), MOVE_BACK("Retour"), VALIDATE("Valider");

        private final String text;

        ActionType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
