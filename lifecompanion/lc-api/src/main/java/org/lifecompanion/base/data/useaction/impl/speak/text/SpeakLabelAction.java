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

package org.lifecompanion.base.data.useaction.impl.speak.text;

import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;

import java.util.Map;

public class SpeakLabelAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public SpeakLabelAction() {
        super(GridPartKeyComponentI.class);
        this.category = DefaultUseActionSubCategories.SPEAK_TEXT;
        this.nameID = "action.speak.key.label";
        this.staticDescriptionID = "action.speak.key.label.static.description";
        this.configIconPath = "sound/icon_speak_key_text.png";
        this.parameterizableAction = false;
        this.order = 1;
        this.parentComponentProperty().addListener((obs, ov, nv) -> {
            this.variableDescriptionProperty().unbind();
            if (nv != null) {
                this.variableDescriptionProperty()
                        .bind(TranslationFX.getTextBinding("action.speak.key.label.variable.description", nv.textContentProperty()));
            }
        });
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            VoiceSynthesizerController.INSTANCE.speakSync(parentKey.textContentProperty().get());
        }
    }
    //========================================================================
}
