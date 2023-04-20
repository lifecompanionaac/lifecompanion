package org.lifecompanion.plugin.flirc.model;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.exception.LCException;

public class IRCode implements XMLSerializable<IOContextI> {
    private static final double LONG_PRESS_FACTOR = 20.0; // 0.1 = 2 / 0.5 = 10 / 1 = 20
    private String pattern;
    private boolean longPress;
    private double longPressThreshold = 0.5;

    public IRCode() {
    }

    public IRCode(String pattern, boolean longPress, double longPressThreshold) {
        this.pattern = pattern;
        this.longPress = longPress;
        this.longPressThreshold = longPressThreshold;
    }

    public String getPattern() {
        return pattern;
    }

    public int getComputedSendCount() {
        return (int) Math.max(2.0, longPressThreshold * LONG_PRESS_FACTOR);
    }

    public boolean isLongPress() {
        return longPress;
    }

    public double getLongPressThreshold() {
        return longPressThreshold;
    }

    public void setLongPress(boolean longPress) {
        this.longPress = longPress;
    }

    public void setLongPressThreshold(double longPressThreshold) {
        this.longPressThreshold = longPressThreshold;
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
                ", longPress=" + longPress +
                ", longPressThreshold=" + longPressThreshold +
                '}';
    }
}
