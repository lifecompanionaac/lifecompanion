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

package org.lifecompanion.config.view.reusable;

import javafx.scene.control.ListCell;
import org.lifecompanion.util.UIUtils;

public class FontListCell extends ListCell<String> {

    public FontListCell() {
        UIUtils.setFixedSize(this, 180.0, 28.0);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) return;
        if (item != null) {
            this.setText(item);
            this.setStyle("-fx-font-family: '" + item + "'");
        } else {
            this.setText(null);
            this.setStyle(null);
        }
    }
}
