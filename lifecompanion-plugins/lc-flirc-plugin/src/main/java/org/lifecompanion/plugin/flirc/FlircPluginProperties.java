package org.lifecompanion.plugin.flirc;

import javafx.beans.property.ObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class FlircPluginProperties extends AbstractPluginConfigProperties {

    protected FlircPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
    }


    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element("FlircPluginProperties");
        XMLObjectSerializer.serializeInto(FlircPluginProperties.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(FlircPluginProperties.class, this, node);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
