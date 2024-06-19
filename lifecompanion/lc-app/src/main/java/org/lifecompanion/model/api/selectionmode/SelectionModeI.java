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

package org.lifecompanion.model.api.selectionmode;

import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.Set;

/**
 * Represent a selection mode.<br>
 * A selection mode is a global selection behavior that determine if the user will select
 * key by scanning it, or by directly choosing it.<br>
 * There is only one current selection mode in use mode, but each component can have different {@link SelectionModeParameterI} that will change the selection mode if needed with the {@link SelectionModeParameterI#selectionModeTypeProperty()}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SelectionModeI {
    /**
     * The selection mode view, will be added on the top of current configuration view.<br>
     * Should display all visual selection information to user (stroke on a key, etc...)
     *
     * @return the selection mode view
     */
    Node getSelectionView();

    void viewDisplayed();

    /**
     * To change the selection mode parameters for this selection mode.<br>
     * Typically, the given parameters have the {@link SelectionModeParameterI#selectionModeTypeProperty()} equals to this mode.
     *
     * @param parametersP the new parameters
     */
    void setParameters(SelectionModeParameterI parametersP);

    /**
     * @return the current parameters associated with this selection mode
     */
    SelectionModeParameterI getParameters();

    /**
     * Should initialize the selection mode.<br>
     * Init can be called multiple times if dispose is called each time.<br>
     * Note that this method haven't any semantic value like start/stop... methods
     *
     * @param previousSelectionMode the previous used selection mode (can be null)
     */
    void init(SelectionModeI previousSelectionMode);

    /**
     * Should close and dispose all the data used by the mode.<br>
     * Note that a dispose can be called multiple times on the same selection mode. (if it was initialized again).<br>
     * Note that this method haven't any semantic value like start/stop... methods
     */
    void dispose();

    /**
     * @return a property that contains the grid currently display or go in.<br>
     * This is done even if direct selection mode to be able to know where the user is.
     */
    ObjectProperty<GridComponentI> currentGridProperty();

    /**
     * To go to a specific part in the current scanning/selection, this can be called with a given part inside the current scanned grid, but not necessary
     *
     * @param part the part we should go to
     */
    void goToGridPart(GridPartComponentI part);

    /**
     * This method should be called by different selection mode to inform that an action was done.</br>
     * This will be used to detect that time before repeat is correct.
     */
    void activationDone();

    /**
     * @return Listeners that should be called on each activation
     */
    Set<Runnable> getActivationDoneListener();

    /**
     * @return true if a new activation can be done, if the configured time before repeat is correct.
     */
    boolean isTimeBeforeRepeatCorrect();

    void showActivationRequest(Color color);

    void hideActivationRequest();
}
