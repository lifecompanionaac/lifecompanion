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

package org.lifecompanion.ui.common.control.generic;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.lifecompanion.ui.common.pane.generic.cell.MouseButtonListCell;
import org.lifecompanion.model.api.selectionmode.MouseButton;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class MouseButtonSelectorControl extends ComboBox<MouseButton> implements LCViewInitHelper {

    public MouseButtonSelectorControl() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setItems(FXCollections.observableArrayList(MouseButton.values()));
        this.setCellFactory(lv -> new MouseButtonListCell());
        this.setButtonCell(new MouseButtonListCell());
        this.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHalignment(this, HPos.RIGHT);
    }
}
