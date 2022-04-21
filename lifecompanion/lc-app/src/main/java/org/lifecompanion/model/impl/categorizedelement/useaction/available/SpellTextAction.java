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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

public class SpellTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final StringProperty textToSpell;

    public SpellTextAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SPELL_TEXT;
        this.nameID = "action.spell.text.name";
        this.staticDescriptionID = "action.spell.text.static.description";
        this.configIconPath = "sound/spell_text.png";
        this.parameterizableAction = true;
        this.order = 0;
        this.textToSpell = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.spell.text.variable.description", this.textToSpell));
    }

    public StringProperty textToSpellProperty() {
        return this.textToSpell;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        VoiceSynthesizerController.INSTANCE.spellSync(UseVariableController.INSTANCE.createText(this.textToSpell.get(), variables), VoiceSynthesizerController.DEFAULT_SPELL_PAUSE);
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        return XMLObjectSerializer.serializeInto(SpellTextAction.class, this, node);
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SpellTextAction.class, this, nodeP);
    }
}
