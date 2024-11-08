package org.lifecompanion.plugin.phonecontrol;

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

public class PhoneControlPluginProperties extends AbstractPluginConfigProperties {

    // PROPERTIES
    // Possible properties: IntegerProperty, DoubleProperty, BooleanProperty, StringProperty, ObjectProperty, ListProperty
    private final BooleanProperty validateWithEnter;
    private final BooleanProperty enableFeedbackSound;
    private final DoubleProperty feedbacksVolume;

    protected PhoneControlPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        this.validateWithEnter = new SimpleBooleanProperty(true);
        this.enableFeedbackSound = new SimpleBooleanProperty(true);
        this.feedbacksVolume = new SimpleDoubleProperty(0.5);
    }

    public BooleanProperty validateWithEnterProperty() {
        return validateWithEnter;
    }

    public BooleanProperty enableFeedbackSoundProperty() {
        return enableFeedbackSound;
    }

    public DoubleProperty feedbacksVolumeProperty() {
        return feedbacksVolume;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = XMLObjectSerializer.serializeInto(PhoneControlPluginProperties.class, this, new Element("PhoneControlPluginProperties"));
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