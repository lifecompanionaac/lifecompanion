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

package org.lifecompanion.model.api.categorizedelement.useaction;

import org.lifecompanion.model.api.configurationcomponent.ConfigurationChildComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This represents a component that could trigger the use action execution.<br>
 * Basically a key can trigger action when a user select it, or when a scan selection mode is over.<br>
 * But a event generator also fire action when they create event.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseActionTriggerComponentI extends XMLSerializable<IOContextI>, ConfigurationChildComponentI {
    /**
     * @return the action manager for this component
     */
    UseActionManagerI getActionManager();

    void eventFired(ActionEventType type, UseActionEvent event);

    boolean hasEventHandlingFor(ActionEventType type, UseActionEvent event);

    void addEventFiredListener(final BiConsumer<ActionEventType, UseActionEvent> eventListener);

    void removeEventFiredListener(final BiConsumer<ActionEventType, UseActionEvent> eventListener);
}
