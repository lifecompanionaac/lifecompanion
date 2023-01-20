/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
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
package org.lifecompanion.plugin.email;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;

import java.util.Map;

public class EmailPluginProperties extends AbstractPluginConfigProperties {

    // Email connection parameters
    private final StringProperty imapsHost;
    private final StringProperty imapsPort;
    private final StringProperty imapsFolder;
    private final StringProperty smtpHost;
    private final StringProperty smtpPort;

    private final StringProperty proxyHost;
    private final StringProperty proxyPort;

    private final StringProperty fromName;
    private final StringProperty login;
    private final StringProperty password;

    protected EmailPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        imapsHost = new SimpleStringProperty();
        imapsPort = new SimpleStringProperty();
        imapsFolder = new SimpleStringProperty();
        smtpHost = new SimpleStringProperty();
        smtpPort = new SimpleStringProperty();
        fromName = new SimpleStringProperty();
        login = new SimpleStringProperty();
        password = new SimpleStringProperty();
        proxyHost = new SimpleStringProperty();
        proxyPort = new SimpleStringProperty();
    }

    public StringProperty imapsHostProperty() {
        return imapsHost;
    }

    public StringProperty imapsPortProperty() {
        return imapsPort;
    }

    public StringProperty imapsFolderProperty() {
        return imapsFolder;
    }

    public StringProperty smtpHostProperty() {
        return smtpHost;
    }

    public StringProperty smtpPortProperty() {
        return smtpPort;
    }

    public StringProperty fromNameProperty() {
        return fromName;
    }

    public StringProperty loginProperty() {
        return login;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty proxyHostProperty() {
        return proxyHost;
    }

    public StringProperty proxyPortProperty() {
        return proxyPort;
    }

    @Override
    public String toString() {
        return "EmailPluginProperties{" +
                "imapsHost=" + imapsHost +
                ", imapsPort=" + imapsPort +
                ", imapsFolder=" + imapsFolder +
                ", smtpHost=" + smtpHost +
                ", smtpPort=" + smtpPort +
                ", proxyHost=" + proxyHost +
                ", proxyPort=" + proxyPort +
                ", fromName=" + fromName +
                ", login=" + login +
                '}';
    }

    public boolean isEmailConfigurationSet() {
        return !StringUtils.isBlank(this.imapsHost.get()) || !StringUtils.isBlank(this.smtpHost.get());
    }

    private static final String NODE_EMAIL_PLUGIN = "EmailPluginInformations";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(NODE_EMAIL_PLUGIN);
        XMLObjectSerializer.serializeInto(EmailPluginProperties.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(EmailPluginProperties.class, this, node);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
