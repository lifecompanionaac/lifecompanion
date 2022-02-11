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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.PredictionParameterI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.base.data.control.prediction.AutoCharPredictionController;
import org.lifecompanion.base.data.control.prediction.WordPredictionController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

public class PredictionParameter implements PredictionParameterI {
    private StringProperty selectedWordPredictorId;
    private StringProperty selectedCharPredictorId;
    private StringProperty charPredictionSpaceChar;

    public PredictionParameter() {
        this.selectedCharPredictorId = new SimpleStringProperty(this, "selectedCharPredictorId", AutoCharPredictionController.INSTANCE.getDefaultPredictor().getId());
        this.selectedWordPredictorId = new SimpleStringProperty(this, "selectedWordPredictorId", WordPredictionController.INSTANCE.getDefaultPredictor().getId());
        this.charPredictionSpaceChar = new SimpleStringProperty(this, "charPredictionSpaceChar", "_");
    }

    @Override
    public StringProperty selectedWordPredictorIdProperty() {
        return this.selectedWordPredictorId;
    }

    @Override
    public StringProperty selectedCharPredictorIdProperty() {
        return this.selectedCharPredictorId;
    }

    @Override
    public StringProperty charPredictionSpaceCharProperty() {
        return this.charPredictionSpaceChar;
    }

    // Class part : "IO"
    //========================================================================
    public static final String NODE_PREDICTION_PARAMETER = "PredictionParameter";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(PredictionParameter.NODE_PREDICTION_PARAMETER);
        XMLObjectSerializer.serializeInto(PredictionParameter.class, this, element);
        WordPredictionController.INSTANCE.serializeCustomInformation(element, this, context);
        String wordPredictorPluginId = WordPredictionController.INSTANCE.getPluginIdForPredictor(selectedWordPredictorId.get());
        if (wordPredictorPluginId != null) {
            context.getAutomaticPluginDependencyIds().add(wordPredictorPluginId);
        }
        AutoCharPredictionController.INSTANCE.serializeCustomInformation(element, this, context);
        String charPredictorPluginId = AutoCharPredictionController.INSTANCE.getPluginIdForPredictor(selectedWordPredictorId.get());
        if (charPredictorPluginId != null) {
            context.getAutomaticPluginDependencyIds().add(charPredictorPluginId);
        }
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(PredictionParameter.class, this, node);
        AutoCharPredictionController.INSTANCE.deserializeCustomInformation(node, this, context);
        WordPredictionController.INSTANCE.deserializeCustomInformation(node, this, context);
    }
    //========================================================================
}
