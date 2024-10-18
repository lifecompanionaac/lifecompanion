/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.aac4all.wp2.model.keyoption;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.aac4all.wp2.model.useaction.WriteAAC4AllPredictionAction;


public abstract class AbstractAAC4AllKeyOption extends AbstractKeyOption {

    private final transient StringProperty prediction;

    private WriteAAC4AllPredictionAction writeReoLocPredictionAction;

    public AbstractAAC4AllKeyOption() {
        super();
        this.disableTextContent.set(true);
        this.optionNameId = "aac4all.wp2.plugin.current.word.key.option.name";
        this.optionDescriptionId = "aac4all.wp2.plugin.current.word.key.option.description";
        this.iconName = "icon_type_char_prediction.png";
        // filler_icon_32px.png
        this.prediction = new SimpleStringProperty(this, "prediction", "");
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
       //  key.textContentProperty().set(null);
        this.writeReoLocPredictionAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteAAC4AllPredictionAction.class);
        if (this.writeReoLocPredictionAction == null) {
            this.writeReoLocPredictionAction = new WriteAAC4AllPredictionAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.writeReoLocPredictionAction);
        }
        this.writeReoLocPredictionAction.attachedToKeyOptionProperty().set(true);
        if (key.getKeyStyle().textPositionProperty().value().getValue() != TextPosition.CENTER) {
            key.getKeyStyle().textPositionProperty().selected().setValue(TextPosition.CENTER);
        }
        this.prediction.bindBidirectional(key.textContentProperty());
    }

    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.writeReoLocPredictionAction);
        this.prediction.unbindBidirectional(key.textContentProperty());
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(AbstractAAC4AllKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AbstractAAC4AllKeyOption.class, this, node);
    }


    public StringProperty predictionProperty() {
        return this.prediction;
    }
}
