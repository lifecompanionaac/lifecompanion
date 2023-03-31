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
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.flirc.model.IRCode;

public class SendIRActionWrapper {
    private final ObjectProperty<IRCode> irCode;

    public SendIRActionWrapper() {
        this.irCode = new SimpleObjectProperty<>();
    }

    public ObjectProperty<IRCode> irCodeProperty() {
        return irCode;
    }

    private static final String NODE_IR_CODE = "IRCode";

    Element serializeImpl(Element element, IOContextI context) {
        if (irCode.get() != null) {
            Element irCodeContainer = new Element(NODE_IR_CODE);
            element.addContent(irCodeContainer);
            irCodeContainer.addContent(irCode.get().serialize(context));
        }
        return element;
    }

    void deserializeImpl(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(SendIRActionWrapper.class, this, node);
        Element irCodeContainer = node.getChild(NODE_IR_CODE);
        if (irCodeContainer != null && !irCodeContainer.getChildren().isEmpty()) {
            IRCode irCodeVal = new IRCode();
            irCodeVal.deserialize(irCodeContainer.getChildren().get(0), context);
            this.irCode.set(irCodeVal);
        }
    }
}
