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
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectSpecificKeyListAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final StringProperty linkedNodeId;

    public SelectSpecificKeyListAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.KEY_LIST_GENERAL;
        this.nameID = "select.specific.keylist.action.name";
        this.staticDescriptionID = "select.specific.keylist.action.description";
        this.configIconPath = "keylist/icon_select_specific_keylist.png";
        this.parameterizableAction = true;
        linkedNodeId = new SimpleStringProperty();
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (StringUtils.isNotBlank(linkedNodeId.get())) {
            KeyListController.INSTANCE.selectNodeById(linkedNodeId.get());
        }
    }

    public StringProperty linkedNodeIdProperty() {
        return linkedNodeId;
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SelectSpecificKeyListAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SelectSpecificKeyListAction.class, this, nodeP);
    }
}
