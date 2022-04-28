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

import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

public class SpellLabelAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public SpellLabelAction() {
        super(GridPartKeyComponentI.class);
        this.category = DefaultUseActionSubCategories.SPELL_TEXT;
        this.nameID = "action.spell.key.label";
        this.staticDescriptionID = "action.spell.key.label.static.description";
        this.configIconPath = "sound/spell_key_text.png";
        this.parameterizableAction = false;
        this.order = 1;
        this.parentComponentProperty().addListener((obs, ov, nv) -> {
            this.variableDescriptionProperty().unbind();
            if (nv != null) {
                this.variableDescriptionProperty()
                        .bind(TranslationFX.getTextBinding("action.spell.key.label.variable.description", nv.textContentProperty()));
            }
        });
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            VoiceSynthesizerController.INSTANCE.spellSync(parentKey.textContentProperty().get(), VoiceSynthesizerController.DEFAULT_SPELL_PAUSE);
        }
    }
    //========================================================================
}
