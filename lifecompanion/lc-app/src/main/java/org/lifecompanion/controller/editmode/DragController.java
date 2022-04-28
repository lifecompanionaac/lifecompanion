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
package org.lifecompanion.controller.editmode;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller that control every drag and drop action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DragController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(DragController.class);
    /**
     * Current dragged key, to invert key
     */
    private final ObjectProperty<GridPartKeyComponentI> currentDraggedKey;

    DragController() {
        this.currentDraggedKey = new SimpleObjectProperty<>();
    }

    public ObjectProperty<GridPartKeyComponentI> currentDraggedKeyProperty() {
        return this.currentDraggedKey;
    }
}
