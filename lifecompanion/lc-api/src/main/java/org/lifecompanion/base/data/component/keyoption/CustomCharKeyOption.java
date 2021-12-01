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
package org.lifecompanion.base.data.component.keyoption;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContentDisplay;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.base.data.useaction.impl.text.prediction.WriteCharPredictionAction;

/**
 * Represent a key that contains a single character.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CustomCharKeyOption extends AbstractKeyOption {

    /**
     * Current prediction for this key
     */
    private final transient StringProperty prediction;

    private WriteCharPredictionAction writeCharPredictionAction;

    public CustomCharKeyOption() {
        super();
        this.optionNameId = "key.option.name.custom.char";
        this.iconName = "icon_type_char_prediction.png";
        //the user should be able to use a text longer than 1 char to be able to have its one space char.
        //this.maxTextLength.set(1);
        this.prediction = new SimpleStringProperty(this, "prediction", "");
    }

    //TODO : option should save option that are modified in attach and restore it on detach (textposition, text...)

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        //Get the existing action, or create new one
        this.writeCharPredictionAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteCharPredictionAction.class);
        if (this.writeCharPredictionAction == null) {
            this.writeCharPredictionAction = new WriteCharPredictionAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.writeCharPredictionAction);
        }
        this.writeCharPredictionAction.attachedToKeyOptionProperty().set(true);
        key.textPositionProperty().set(ContentDisplay.CENTER);
        this.prediction.bindBidirectional(key.textContentProperty());
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.writeCharPredictionAction);
        this.prediction.unbindBidirectional(key.textContentProperty());
    }

    public StringProperty predictionProperty() {
        return this.prediction;
    }

    @Override
    public String toString() {
        return "Option [ Key = " + this.attachedKey.get() + " , prediction = " + this.prediction.get() + " ]";
    }
}
