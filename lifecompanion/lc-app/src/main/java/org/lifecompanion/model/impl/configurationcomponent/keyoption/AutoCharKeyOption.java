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
package org.lifecompanion.model.impl.configurationcomponent.keyoption;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContentDisplay;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteCharPredictionAction;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Represent a key that contains a single character.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AutoCharKeyOption extends AbstractKeyOption {

    /**
     * Current prediction for this key
     */
    private final transient StringProperty prediction;

    private WriteCharPredictionAction writePredictionAction;

    public AutoCharKeyOption() {
        super();
        this.optionNameId = "key.option.name.auto.char";
        this.iconName = "icon_type_auto_char.png";
        this.disableTextContent.set(true);
        this.prediction = new SimpleStringProperty(this, "prediction", Translation.getText("prediction.auto.char.default.text"));
    }

    //TODO : option should save option that are modified in attach and restore it on detach (textposition, text...)

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        //Get the existing action, or create new one
        this.writePredictionAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteCharPredictionAction.class);
        if (this.writePredictionAction == null) {
            this.writePredictionAction = new WriteCharPredictionAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.writePredictionAction);
        }
        this.writePredictionAction.attachedToKeyOptionProperty().set(true);
        if (key.getKeyStyle().textPositionProperty().value().getValue() != TextPosition.CENTER) {
            key.getKeyStyle().textPositionProperty().selected().setValue(TextPosition.CENTER);
        }
        key.textContentProperty().bind(this.prediction);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.writePredictionAction);
        key.textContentProperty().unbind();
        key.textContentProperty().set("");
    }

    public StringProperty predictionProperty() {
        return this.prediction;
    }

}
