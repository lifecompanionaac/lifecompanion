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

package org.lifecompanion.plugin.flirc.model.useaction;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.flirc.controller.FlircController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SendIRAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendIRAction.class);

    private StringProperty pattern;

    public SendIRAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 10;
        this.category = FlircActionSubCategories.GENERAL;
        this.nameID = "flirc.plugin.use.action.send.ir.name";
        this.staticDescriptionID = "flirc.plugin.use.action.send.ir.description";
        this.configIconPath = "flirc/icon_send_ir_code.png";
        this.parameterizableAction = true;
        this.pattern = new SimpleStringProperty();
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public StringProperty patternProperty() {
        return pattern;
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        try {
            LOGGER.info("Will send IR pattern {}", pattern.get());
            FlircController.INSTANCE.sendIr(pattern.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Element serialize(IOContextI context) {
        return XMLObjectSerializer.serializeInto(SendIRAction.class, this, super.serialize(context));
    }

    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(SendIRAction.class, this, node);
    }
}
