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

import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

public class SpellAllTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public SpellAllTextAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SPELL_TEXT;
        this.nameID = "action.spell.all.text.name";
        this.staticDescriptionID = "action.spell.all.text.description";
        this.configIconPath = "sound/icon_speak_all.png";
        this.parameterizableAction = false;
        this.order = 3;
        this.variableDescriptionProperty().set(Translation.getText("action.spell.all.text.description"));
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        String toSpeak = WritingStateController.INSTANCE.currentTextProperty().get();
        VoiceSynthesizerController.INSTANCE.spellSync(toSpeak, 200);

    }
    //========================================================================
}
