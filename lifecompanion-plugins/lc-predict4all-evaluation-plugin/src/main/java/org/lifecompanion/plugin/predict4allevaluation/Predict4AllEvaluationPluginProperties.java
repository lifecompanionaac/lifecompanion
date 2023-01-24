/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.predict4allevaluation;

import javafx.beans.property.ObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class Predict4AllEvaluationPluginProperties extends AbstractPluginConfigProperties {
    protected Predict4AllEvaluationPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> map) {

    }

    @Override
    public void deserializeUseInformation(Map<String, Element> map) throws LCException {

    }

    private final static String NODE_PREDICT4ALL_EVALUATION = "Predict4AllEvaluation";

    @Override
    public Element serialize(IOContextI ioContextI) {
        // FIXME if needed
        return new Element(NODE_PREDICT4ALL_EVALUATION);
    }

    @Override
    public void deserialize(Element element, IOContextI ioContextI) throws LCException {
    }
}
