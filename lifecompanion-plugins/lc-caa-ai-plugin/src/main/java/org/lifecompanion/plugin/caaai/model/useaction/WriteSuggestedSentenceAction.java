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
package org.lifecompanion.plugin.caaai.model.useaction;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WordPredictionKeyOption;
import org.lifecompanion.plugin.caaai.model.keyoption.SuggestedSentenceKeyOption;

import java.util.Map;

public class WriteSuggestedSentenceAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public WriteSuggestedSentenceAction() {
        super(GridPartKeyComponentI.class);
        this.category = CAAAIActionSubCategories.TODO;
        this.nameID = "caa.ai.plugin.todo";
        this.staticDescriptionID = "caa.ai.plugin.todo";
        this.configIconPath = "filler_icon_32px.png";
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    // Class part : "Execute"
    // ========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof SuggestedSentenceKeyOption suggestionOption) {
                String suggestion = suggestionOption.suggestionProperty().get();
                if (suggestion != null) {
                    WritingStateController.INSTANCE.saveState();
                    WritingStateController.INSTANCE.disableAutoSavedStateCleaning();
                    WritingStateController.INSTANCE.insertText(WritingEventSource.USER_ACTIONS, " " + suggestion);
                    WritingStateController.INSTANCE.enableAutoSavedStateCleaning();
                }
            }
        }
    }


    // ========================================================================
}
