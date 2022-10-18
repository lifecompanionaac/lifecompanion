/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.spellgame.model.useaction;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;

import java.util.Map;

public class StartSpellGameAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final StringProperty wordListId, wordListName;

    public StartSpellGameAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = SpellGameActionSubCategories.SPELLGAME;
        this.parameterizableAction = true;
        this.nameID = "spellgame.plugin.action.start.game.name";
        this.staticDescriptionID = "spellgame.plugin.action.start.game.static.description";
        wordListId = new SimpleStringProperty();
        wordListName = new SimpleStringProperty();
        this.configIconPath = "filler_icon.png";
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("spellgame.plugin.action.start.game.variable.description", wordListName));
    }

    public String getWordListId() {
        return wordListId.get();
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        SpellGameController.INSTANCE.startGame(wordListId.get());
    }

    public void updateSelectedWordList(SpellGameWordList wordList) {
        if (wordList != null) {
            this.wordListId.set(wordList.getId());
            this.wordListName.set(wordList.nameProperty().get());
        } else {
            this.wordListId.set(null);
            this.wordListName.set(null);
        }
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(StartSpellGameAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(StartSpellGameAction.class, this, nodeP);
    }
}
