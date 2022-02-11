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
package org.lifecompanion.model.api.textprediction;

import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.framework.commons.SystemType;

import java.util.List;

/**
 * Base predictor interface for common prediction methods.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface BasePredictorI extends ModeListenerI, XMLSerializable<IOContextI> {
    /**
     * @return a unique ID for this predictor, this id shouldn't change in the time
     */
    String getId();

    /**
     * @return the configuration step ID ({@link org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI}
     */
    String getConfigStepId();

    /**
     * @return a name for this predictor, the name should be understandable for a user
     */
    String getName();

    /**
     * @return a full description of this word predictor
     */
    String getDescription();

    /**
     * @return the list of all compatible systems with this predictor.<br>
     * If the current system is not compatible, the predictor will not be added to available synthesizers
     */
    List<SystemType> getCompatibleSystems();

    /**
     * Must initialize the predictor.<br>
     * The predictor should be able to work after this call.
     */
    void initialize() throws Exception;

    /**
     * @return true if and if only the predictor is initialized.<br>
     * On certain predictor, this can return true even if the {@link #initialize()} was not called.
     */
    boolean isInitialized();

    /**
     * Should dispose all resources that this predictor use.
     *
     * @throws Exception if dispose can't be done
     */
    void dispose() throws Exception;

    /**
     * Called by software to train predictions.</br>
     * Will be called even if the predictor is not initialized.</br>
     *
     * @param text text to train the predictor (raw)
     */
    void trainDynamicModel(String text);
}
