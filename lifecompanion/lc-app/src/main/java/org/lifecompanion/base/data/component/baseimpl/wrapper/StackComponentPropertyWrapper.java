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

package org.lifecompanion.base.data.component.baseimpl.wrapper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.StackComponentI;

import java.util.ArrayList;

/**
 * This class is a simple wrapper for class that implements {@link StackComponentI}, to avoid theses class to have to instantiate every properties.<br>
 * This class is here just for easy maintenance purpose, because it's easier to remove/add/change behavior on one class instead of every classes.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StackComponentPropertyWrapper {
    /**
     * The list that contains every component added to the stack
     */
    private final ObservableList<GridComponentI> components;

    /**
     * The component currently "on top", the component displayed
     */
    private final ObjectProperty<GridComponentI> displayed;

    /**
     * Boolean property to indicate if next/previous action are possible
     */
    private final BooleanProperty previousPossible, nextPossible;

    public StackComponentPropertyWrapper(final StackComponentI stackComponent) {
        this.components = FXCollections.observableList(new ArrayList<>(5));
        this.displayed = new SimpleObjectProperty<>(this, "displayed");
        this.previousPossible = new SimpleBooleanProperty(this, "previousPossible");
        this.nextPossible = new SimpleBooleanProperty(this, "nextPossible");
    }

    // Class part : "Stack component part"
    //========================================================================
    public ObservableList<GridComponentI> getComponentList() {
        return this.components;
    }

    public ObjectProperty<GridComponentI> displayedComponentProperty() {
        return this.displayed;
    }

    public BooleanProperty nextPossibleProperty() {
        return this.nextPossible;
    }

    public BooleanProperty previousPossibleProperty() {
        return this.previousPossible;
    }
    //========================================================================

}
