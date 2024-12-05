package org.lifecompanion.plugin.phonecontrol;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class PhoneControlPluginProperties extends AbstractPluginConfigProperties {
    public static final String NODE_PHONECONTROL_PLUGIN = "PhoneControlPluginInformations";

    protected PhoneControlPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
    }

    @Override
    public String toString() {
        return "PhoneControlPluginProperties{}";
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(NODE_PHONECONTROL_PLUGIN);
        XMLObjectSerializer.serializeInto(PhoneControlPluginProperties.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(PhoneControlPluginProperties.class, this, node);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
