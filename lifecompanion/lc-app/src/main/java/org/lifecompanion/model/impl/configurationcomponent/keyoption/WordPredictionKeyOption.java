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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteWordPredictionAction;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Key option to display a predicted word.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WordPredictionKeyOption extends AbstractKeyOption {

    /**
     * Current prediction for this key
     */
    private final ObjectProperty<WordPredictionI> prediction;

    /**
     * Add space after prediction
     */
    private final BooleanProperty addSpace;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> correctionColor;

    /**
     * Action to write the prediction
     */
    private WriteWordPredictionAction writeWordPredictionAction;

    public WordPredictionKeyOption() {
        super();
        this.optionNameId = "key.option.name.word.prediction";
        this.iconName = "icon_type_word_prediction.png";
        this.disableTextContent.set(true);
        this.addSpace = new SimpleBooleanProperty(this, "addSpace", true);
        this.correctionColor = new SimpleObjectProperty<>(this, "correctionColor", null);
        this.prediction = new SimpleObjectProperty<>(this, "prediction", null);

        this.prediction.addListener((obs, ov, pred) -> {
            final GridPartKeyComponentI key = attachedKey.get();
            if (key != null) {
                key.textContentProperty().set(pred != null ? pred.getPredictionToDisplay() : Translation.getText("prediction.key.default.text"));
                if (pred != null && correctionColor.get() != null && pred.getPreviousCharCountToRemove() > 0) {
                    key.getKeyTextStyle().colorProperty().forced().setValue(correctionColor.get());
                } else {
                    key.getKeyTextStyle().colorProperty().forced().setValue(null);
                }
            } else {
                key.getKeyTextStyle().colorProperty().forced().setValue(null);
                key.textContentProperty().set(Translation.getText("prediction.key.default.text"));
            }
        });
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        //Get the existing action, or create new one
        this.writeWordPredictionAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteWordPredictionAction.class);
        if (this.writeWordPredictionAction == null) {
            this.writeWordPredictionAction = new WriteWordPredictionAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(0, this.writeWordPredictionAction);
        }
        this.writeWordPredictionAction.attachedToKeyOptionProperty().set(true);
        //Bind space
        this.writeWordPredictionAction.addSpaceProperty().bind(this.addSpace);
        key.textPositionProperty().set(ContentDisplay.CENTER);
        if (this.prediction.get() == null) {
            key.textContentProperty().set(Translation.getText("prediction.key.default.text"));
        }
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.writeWordPredictionAction);
        key.textContentProperty().unbind();
        key.textContentProperty().set("");
    }

    public ObjectProperty<WordPredictionI> predictionProperty() {
        return this.prediction;
    }

    public ObjectProperty<Color> correctionColorProperty() {
        return this.correctionColor;
    }

    public BooleanProperty addSpaceProperty() {
        return this.addSpace;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(WordPredictionKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(WordPredictionKeyOption.class, this, node);
    }

}
