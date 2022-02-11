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
package org.lifecompanion.model.api.categorizedelement.useevent;

import javafx.collections.ObservableList;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.UseInformationSerializableI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

import java.util.Map;

/**
 * The event manager that list all the event generator for a {@link UseEventGeneratorHolderI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseEventManagerI extends XMLSerializable<IOContextI>, UseInformationSerializableI {
    /**
     * the list of all use event generator.<br>
     * The order of the list doesn't matter, because event generator generates their own event.
     *
     * @return the event generator list
     */
    ObservableList<UseEventGeneratorI> componentEventGenerators();

    /**
     * Useful method to call {@link UseEventGeneratorI#attachListener(UseEventListenerI)} and {@link UseEventGeneratorI#modeStart(LCConfigurationI)} on every generator in the list
     *
     * @param listener      the listener to attach
     * @param configuration the mode start configuration
     */
    void attachAndStart(UseEventListenerI listener, LCConfigurationI configuration);

    /**
     * Useful method to call {@link UseEventGeneratorI#detachListener()} and {@link UseEventGeneratorI#modeStop(LCConfigurationI)} on every generator in the list
     *
     * @param configuration the mode stop configuration
     */
    void detachAndStop(LCConfigurationI configuration);

    /**
     * See {@link DuplicableComponentI#idsChanged(Map)}
     */
    void dispatchIdsChanged(Map<String, String> changes);

}
