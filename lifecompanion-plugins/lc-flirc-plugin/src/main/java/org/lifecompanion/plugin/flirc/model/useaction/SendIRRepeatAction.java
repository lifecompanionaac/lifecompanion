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

import javafx.beans.property.ObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.RepeatActionBaseImpl;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.flirc.controller.FlircController;
import org.lifecompanion.plugin.flirc.model.IRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SendIRRepeatAction extends RepeatActionBaseImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendIRRepeatAction.class);

    private SendIRActionWrapper sendIRActionWrapper;

    public SendIRRepeatAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 10;
        this.category = FlircActionSubCategories.GENERAL;
        this.nameID = "flirc.plugin.use.action.send.repeat.ir.name";
        this.staticDescriptionID = "flirc.plugin.use.action.send.repeat.ir.description";
        this.configIconPath = "flirc/icon_send_ir_code.png";
        this.parameterizableAction = true;
        this.sendIRActionWrapper = new SendIRActionWrapper();
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public ObjectProperty<IRCode> irCodeProperty() {
        return sendIRActionWrapper.irCodeProperty();
    }

    public Element serialize(IOContextI context) {
        Element element = XMLObjectSerializer.serializeInto(SendIRRepeatAction.class, this, super.serialize(context));
        sendIRActionWrapper.serializeImpl(element, context);
        return element;
    }

    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(SendIRRepeatAction.class, this, node);
        sendIRActionWrapper.deserializeImpl(node, context);
    }

    @Override
    protected void executeFirstBeforeRepeat(UseActionEvent useActionEvent) {
    }

    @Override
    protected void executeOnRepeat(UseActionEvent useActionEvent) {
        try {
            IRCode irCode = sendIRActionWrapper.irCodeProperty().get();
            if (irCode != null) {
                FlircController.INSTANCE.sendIr(irCode);
            }
            // FIXME : error handling...
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void repeatEnded(UseActionEvent useActionEvent) {
    }

    @Override
    protected long getDelayBeforeRepeatStartMillis() {
        return 0;
    }

    @Override
    protected long getDelayBetweenEachRepeatMillis() {
        return 0;
    }
}
