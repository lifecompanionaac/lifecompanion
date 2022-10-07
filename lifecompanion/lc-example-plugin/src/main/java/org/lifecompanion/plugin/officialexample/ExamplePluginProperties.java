package org.lifecompanion.plugin.officialexample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class ExamplePluginProperties extends AbstractPluginConfigProperties {
    private final IntegerProperty wordDisplayTimeInMs;

    protected ExamplePluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        this.wordDisplayTimeInMs = new SimpleIntegerProperty(5000);
    }

    public IntegerProperty wordDisplayTimeInMsProperty() {
        return wordDisplayTimeInMs;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element("ExamplePluginProperties");
        XMLObjectSerializer.serializeInto(ExamplePluginProperties.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(ExamplePluginProperties.class, this, node);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
