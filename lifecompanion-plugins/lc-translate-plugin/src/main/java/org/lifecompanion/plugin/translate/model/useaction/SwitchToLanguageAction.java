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

package org.lifecompanion.plugin.translate.model.useaction;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.translate.controller.TranslateController;

import java.util.Map;

public class SwitchToLanguageAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    @XMLGenericProperty(AvailableTranslation.class)
    private final ObjectProperty<AvailableTranslation> targetLanguage;

    public SwitchToLanguageAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 10;
        this.category = TranslateActionSubCategories.GENERAL;
        this.nameID = "lc.translate.plugin.use.action.switch.to.language.name";
        this.staticDescriptionID = "lc.translate.plugin.use.action.switch.to.language.description";
        this.configIconPath = "filler_icon_32px.png";
        this.parameterizableAction = true;
        this.targetLanguage = new SimpleObjectProperty<>(AvailableTranslation.EN);
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public ObjectProperty<AvailableTranslation> targetLanguageProperty() {
        return targetLanguage;
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        if (this.targetLanguage != null) {
            TranslateController.INSTANCE.switchToLanguage(targetLanguage.get().name().toLowerCase());
        }
    }

    @Override
    public Element serialize(IOContextI contextP) {
        Element element = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SwitchToLanguageAction.class, this, element);
        return element;
    }

    @Override
    public void deserialize(Element nodeP, IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SwitchToLanguageAction.class, this, nodeP);
    }

}
