package org.lifecompanion.plugin.aac4all.wp2;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class AAC4AllWp2PluginProperties extends AbstractPluginConfigProperties {

    private final StringProperty patientId;
    private final StringProperty randomTypeEval;

    protected AAC4AllWp2PluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        patientId = new SimpleStringProperty();
        randomTypeEval = new SimpleStringProperty();
    }

    public StringProperty patientIdProperty() {
        return patientId;
    }

    public StringProperty getRandomTypeEval(){
        return randomTypeEval;
    }
    @Override
    public Element serialize(final IOContextI context) {
        return XMLObjectSerializer.serializeInto(AAC4AllWp2PluginProperties.class, this, new Element("AAC4AllWp2PluginProperties"));
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(AAC4AllWp2PluginProperties.class, this, node);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {

    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
