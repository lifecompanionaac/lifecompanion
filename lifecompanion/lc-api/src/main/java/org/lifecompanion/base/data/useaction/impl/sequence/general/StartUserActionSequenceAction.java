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

package org.lifecompanion.base.data.useaction.impl.sequence.general;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.control.UserActionSequenceController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StartUserActionSequenceAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final StringProperty sequenceToStartId;

    public StartUserActionSequenceAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.sequenceToStartId = new SimpleStringProperty();
        this.category = DefaultUseActionSubCategories.UA_SEQUENCE_GENERAL;
        this.nameID = "start.user.action.sequence.name";
        this.staticDescriptionID = "start.user.action.sequence.description";
        this.configIconPath = "sequence/start_ua_sequence.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }


    public StringProperty sequenceToStartIdProperty() {
        return sequenceToStartId;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (StringUtils.isNotBlank(sequenceToStartId.get())) {
            UserActionSequenceController.INSTANCE.startSequenceById(sequenceToStartId.get());
        }
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(StartUserActionSequenceAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(StartUserActionSequenceAction.class, this, nodeP);
    }
}
