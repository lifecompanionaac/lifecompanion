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
package org.lifecompanion.api.component.definition.useaction;

import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.DuplicableComponentI;
import org.lifecompanion.api.component.definition.UseInformationSerializableI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;

import java.util.List;
import java.util.Map;

/**
 * Action manager for a {@link UseActionTriggerComponentI}.<br>
 * This component is in charge to dispatch action and store action function of its parent component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseActionManagerI extends XMLSerializable<IOContextI>, UseInformationSerializableI {
    /**
     * @return a map that should contains a observable list for every supported event of this action trigger.<br>
     * If a UseActionEvent is not contained as key in this map, we should consider that this component doesn't support the event type.
     */
    Map<UseActionEvent, ObservableList<BaseUseActionI<?>>> componentActions();

    /**
     * To search the first action of a type for a event type
     *
     * @param eventType  the event type to search
     * @param actionType the action to search
     * @return the action, or null if there is no such action
     */
    <T> T getFirstActionOfType(UseActionEvent eventType, Class<T> actionType);

    /**
     * Should count every action registered to this action manager.<br>
     * Both complex and simple action are taken.
     *
     * @return the total count of actions
     */
    int countAllActions();

    /**
     * @return the action parent component (the component that hold this manager)
     */
    UseActionTriggerComponentI getActionParent();

    /**
     * Shift a action up in the list
     *
     * @param event  the event type
     * @param action the action to shift up
     */
    void shiftActionUp(UseActionEvent event, BaseUseActionI<?> action);

    /**
     * Shift a action down in the list
     *
     * @param event  the event type
     * @param action the action to shift down
     */
    void shiftActionDown(UseActionEvent event, BaseUseActionI<?> action);

    /**
     * To know if the action manager has simple action for a given event type.
     *
     * @param eventType the event type
     * @return true if there is simple action for this kind of event
     */
    boolean hasSimpleAction(UseActionEvent eventType);

    /**
     * To know if the action manager has complex action for a given event type.
     *
     * @param eventType the event type
     * @return true if there is complex action for this kind of event
     */
    boolean hasComplexAction(UseActionEvent eventType);

    boolean containsActions();

    /**
     * Remove all the action on all kind of {@link UseActionEvent}
     */
    void clear();

    /**
     * See {@link DuplicableComponentI#idsChanged(Map)}
     */
    void dispatchIdsChanged(Map<String, String> changes);

}
