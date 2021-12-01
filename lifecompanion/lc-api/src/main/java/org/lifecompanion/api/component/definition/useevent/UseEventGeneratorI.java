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
package org.lifecompanion.api.component.definition.useevent;

import org.lifecompanion.api.component.definition.eventaction.CategorizedElementI;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.mode.ModeListenerI;

import java.util.List;

/**
 * Represent a module that can create event if required condition are met.<br>
 * The generated event will have already known possible {@link UseVariableDefinitionI} instance given on the event generation.<br>
 * The use event generator implements {@link ModeListenerI} because it will inform the event generator when it's ok to send event, and when it's not.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseEventGeneratorI extends CategorizedElementI<UseEventSubCategoryI>, ModeListenerI, UseActionTriggerComponentI {
    /**
     * @return the list of all generated variables by this event.<br>
     * The list can be null if the event doesn't generate any variables.
     */
    List<UseVariableDefinitionI> getGeneratedVariables();

    /**
     * Attach the listener to this use event generator.<br>
     * This method will be called when the use mode start,
     *
     * @param listener the listener to attach to this generator.<br>
     *                 It's this listener that should be called when the generator wants to create a event
     */
    void attachListener(UseEventListenerI listener);

    /**
     * This should remove the previously attached listener.<br>
     * Once the listener is removed, the event generator will not be able to generator any events.
     */
    void detachListener();
}
