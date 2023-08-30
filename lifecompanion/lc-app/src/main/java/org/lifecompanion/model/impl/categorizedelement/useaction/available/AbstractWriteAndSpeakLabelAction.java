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

import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

// Abstract class just to have the action in both categories (text and sound)
public abstract class AbstractWriteAndSpeakLabelAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public AbstractWriteAndSpeakLabelAction() {
        super(GridPartKeyComponentI.class);
        this.category = getCategoryToSet();
        this.order = 0;
        this.parameterizableAction = false;
        this.nameID = "action.write.and.speak.label.name";
        this.staticDescriptionID = "action.write.and.speak.label.description";
        this.configIconPath = "sound/icon_write_speak_label.png";
        this.parentComponentProperty().addListener((obs, ov, nv) -> {
            this.variableDescriptionProperty().unbind();
            if (nv != null) {
                this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.write.and.speak.label.description.variable", nv.textContentProperty()));
            }
        });
    }

    abstract DefaultUseActionSubCategories getCategoryToSet();


    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        WriteLabelAction.executeWriteLabelFor(parentKey);
        SpeakLabelAction.executeSpeakLabelFor(parentKey);
    }

}
