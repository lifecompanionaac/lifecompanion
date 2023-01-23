package org.lifecompanion.plugin.ppp.model;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.exception.LCException;

public class Action implements XMLSerializable<IOContextI> {
    private String name;

    public Action() {
    }

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Element serialize(IOContextI context) {
        Element element = new Element("Action");
        XMLObjectSerializer.serializeInto(Action.class, this, element);
        return element;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(Action.class, this, node);
    }
}
