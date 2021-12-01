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

package org.lifecompanion.base.data.plugins;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Represent plugin information.<br>
 * These information can be used to have information on a plugin before its loading (because they are located in the Manifest file) or they can be used once the plugin is loaded to display them to user.
 */
public class PluginInfo implements XMLSerializable<IOContextI> {
    private static final String NODE_PLUGIN_DEPENDENCY = "PluginDependency";

    private String pluginId, pluginVersion, pluginName, pluginDescription, pluginAuthor,pluginMinAppVersion;
    private Date pluginBuildDate;
    private String pluginClass, pluginPackageScanningBase;

    // TODO : loaded file name
    private transient String fileName;
    private transient final ObjectProperty<PluginInfoState> state;

    PluginInfo() {
        this.state = new SimpleObjectProperty<>(PluginInfoState.ADDED);
    }

    public ObjectProperty<PluginInfoState> stateProperty() {
        return state;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPluginAuthor() {
        return pluginAuthor;
    }

    public void setPluginAuthor(String pluginAuthor) {
        this.pluginAuthor = pluginAuthor;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public String getPluginPackageScanningBase() {
        return pluginPackageScanningBase;
    }

    public void setPluginPackageScanningBase(String pluginPackageScanningBase) {
        this.pluginPackageScanningBase = pluginPackageScanningBase;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public Date getPluginBuildDate() {
        return pluginBuildDate;
    }

    public void setPluginBuildDate(Date pluginBuildDate) {
        this.pluginBuildDate = pluginBuildDate;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    public void setPluginDescription(String pluginDescription) {
        this.pluginDescription = pluginDescription;
    }

    public String getPluginMinAppVersion() {
        return pluginMinAppVersion;
    }

    public void setPluginMinAppVersion(String pluginMinAppVersion) {
        this.pluginMinAppVersion = pluginMinAppVersion;
    }

    public static PluginInfo createFromJarManifest(File jarFile) throws IOException {
        try (JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile))) {
            Manifest mf = jarStream.getManifest();
            Attributes attributes = mf.getMainAttributes();
            PluginInfo pluginInfo = new PluginInfo();
            pluginInfo.setPluginClass(attributes.getValue("LifeCompanion-Plugin-Class"));
            pluginInfo.setPluginId(attributes.getValue("LifeCompanion-Plugin-Id"));
            pluginInfo.setPluginAuthor(attributes.getValue("LifeCompanion-Plugin-Author"));
            pluginInfo.setPluginVersion(attributes.getValue("LifeCompanion-Plugin-Version"));
            pluginInfo.setPluginMinAppVersion(attributes.getValue("LifeCompanion-Min-App-Version"));
            pluginInfo.setPluginBuildDate(new Date(Long.parseLong(attributes.getValue("LifeCompanion-Plugin-Build-Date"))));
            pluginInfo.setPluginPackageScanningBase(attributes.getValue("LifeCompanion-Plugin-Package-Scanning-Base"));
            pluginInfo.setPluginName(attributes.getValue("LifeCompanion-Plugin-Name"));
            pluginInfo.setPluginDescription(attributes.getValue("LifeCompanion-Plugin-Description"));
            pluginInfo.setFileName(jarFile.getName());
            return pluginInfo;
        }
    }

    @Override
    public String toString() {
        return "PluginInfo{" +
                "pluginId='" + pluginId + '\'' +
                ", pluginVersion='" + pluginVersion + '\'' +
                ", pluginMinAppVersion='" + pluginMinAppVersion + '\'' +
                ", pluginClass='" + pluginClass + '\'' +
                ", pluginPackageScanningBase='" + pluginPackageScanningBase + '\'' +
                '}';
    }

    @Override
    public Element serialize(IOContextI context) {
        Element xmlElement = new Element(NODE_PLUGIN_DEPENDENCY);
        XMLObjectSerializer.serializeInto(PluginInfo.class, this, xmlElement);
        return xmlElement;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(PluginInfo.class, this, node);
    }
}
