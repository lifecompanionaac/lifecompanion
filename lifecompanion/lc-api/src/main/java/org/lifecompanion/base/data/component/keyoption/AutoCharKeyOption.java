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
        key.textPositionProperty().set(ContentDisplay.CENTER);
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
