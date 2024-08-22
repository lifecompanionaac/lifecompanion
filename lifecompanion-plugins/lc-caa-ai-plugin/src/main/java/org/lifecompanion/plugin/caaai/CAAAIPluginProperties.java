package org.lifecompanion.plugin.caaai;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class CAAAIPluginProperties extends AbstractPluginConfigProperties {

    private final StringProperty apiEndpoint;
    private final StringProperty apiToken;
    private final StringProperty speechToTextJsonConfig;

    protected CAAAIPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        this.apiEndpoint = new SimpleStringProperty();
        this.apiToken = new SimpleStringProperty();
        this.speechToTextJsonConfig = new SimpleStringProperty();
    }

    public StringProperty apiEndpointProperty() { return apiEndpoint; }

    public StringProperty apiTokenProperty() { return apiToken; }

    public StringProperty speechToTextJsonConfig() { return speechToTextJsonConfig; }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = XMLObjectSerializer.serializeInto(CAAAIPluginProperties.class, this, new Element("CAAAIPluginProperties"));
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(CAAAIPluginProperties.class, this, node);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
