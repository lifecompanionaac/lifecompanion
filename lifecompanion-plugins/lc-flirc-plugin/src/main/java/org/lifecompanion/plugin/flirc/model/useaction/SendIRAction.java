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

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.flirc.controller.FlircController;
import org.lifecompanion.plugin.flirc.model.IRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendIRAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendIRAction.class);

    private SendIRActionWrapper sendIRActionWrapper;

    private final transient AtomicBoolean sending;

    public SendIRAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 10;
        this.category = FlircActionSubCategories.GENERAL;
        this.nameID = "flirc.plugin.use.action.send.ir.name";
        this.staticDescriptionID = "flirc.plugin.use.action.send.ir.description";
        this.configIconPath = "flirc/icon_send_ir_code.png";
        this.parameterizableAction = true;
        this.sendIRActionWrapper = new SendIRActionWrapper();
        this.sending = new AtomicBoolean(false);
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public ObjectProperty<IRCode> irCodeProperty() {
        return sendIRActionWrapper.irCodeProperty();
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        if (!sending.getAndSet(true)) {
            try {
                IRCode irCode = sendIRActionWrapper.irCodeProperty().get();
                if (irCode != null) {
                    FlircController.INSTANCE.sendIr(irCode);
                }
            } catch (Throwable t) {
                LOGGER.error("Could not send IR code", t);
            } finally {
                sending.set(false);
            }
        }
    }

    public Element serialize(IOContextI context) {
        Element element = XMLObjectSerializer.serializeInto(SendIRAction.class, this, super.serialize(context));
        sendIRActionWrapper.serializeImpl(element, context);
        return element;
    }

    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(SendIRAction.class, this, node);
        sendIRActionWrapper.deserializeImpl(node, context);
    }
}
