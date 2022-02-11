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
package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WordPredictionKeyOption;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

/**
 * Action to write the label of the parent key.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteWordPredictionAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    private BooleanProperty addSpace;

    public WriteWordPredictionAction() {
        super(GridPartKeyComponentI.class);
        this.category = DefaultUseActionSubCategories.PREDICTION;
        this.addSpace = new SimpleBooleanProperty(this, "addSpace", true);
        this.nameID = "action.write.word.prediction.name";
        this.staticDescriptionID = "action.write.word.prediction.description";
        this.configIconPath = "text/icon_write_word_prediction.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    public BooleanProperty addSpaceProperty() {
        return this.addSpace;
    }

    // Class part : "Execute"
    // ========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof WordPredictionKeyOption) {
                WordPredictionKeyOption predOption = (WordPredictionKeyOption) parentKey.keyOptionProperty().get();
                WordPredictionI prediction = predOption.predictionProperty().get();
                if (prediction != null) {
                    // Delete the text after caret if needed
                    int charToDelete = prediction.getNextCharCountToRemove();
                    if (charToDelete > 0) {
                        WritingStateController.INSTANCE.removeNextChars(WritingEventSource.SYSTEM, charToDelete);
                    }
                    int previousCharToDelete = prediction.getPreviousCharCountToRemove();
                    if (previousCharToDelete > 0) {
                        WritingStateController.INSTANCE.removeLastChars(WritingEventSource.SYSTEM, previousCharToDelete);
                    }
                    // Write just the needed text
                    WritingStateController.INSTANCE.insertWordPrediction(WritingEventSource.USER_ACTIONS,
                            prediction.getTextToWrite() + (addSpace.get() && prediction.isSpacePossible() ? " " : ""), prediction);
                }
            }
        }
    }
    // ========================================================================
}
