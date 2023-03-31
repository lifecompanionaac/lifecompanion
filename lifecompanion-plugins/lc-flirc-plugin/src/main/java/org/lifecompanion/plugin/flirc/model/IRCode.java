package org.lifecompanion.plugin.flirc.model;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.exception.LCException;

public class IRCode implements XMLSerializable<IOContextI> {
    private String pattern;
    private int sendCount;

    public IRCode() {
    }

    public IRCode(String pattern, int sendCount) {
        this.pattern = pattern;
        this.sendCount = sendCount;
    }

    public String getPattern() {
        return pattern;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    @Override
    public Element serialize(IOContextI ioContextI) {
        return XMLObjectSerializer.serializeInto(IRCode.class, this, new Element("IRCode"));
    }

    @Override
    public void deserialize(Element element, IOContextI ioContextI) throws LCException {
        XMLObjectSerializer.deserializeInto(IRCode.class, this, element);
    }

    @Override
    public String toString() {
        return "IRCode{" +
                "pattern='" + pattern + '\'' +
                ", sendCount=" + sendCount +
                '}';
    }
}
