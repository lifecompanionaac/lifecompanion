package org.lifecompanion.plugin.phonecontrol.controller;

import java.io.File;

import org.lifecompanion.plugin.phonecontrol.PhoneControlPluginProperties;
import org.lifecompanion.plugin.phonecontrol.PhoneCommunicationManager.ProtocolType;
import org.lifecompanion.plugin.phonecontrol.server.PhoneCommunicationProtocol;

public enum GlobalState {
    INSTANCE;

    private ProtocolType currentProtocolType;
    private PhoneCommunicationProtocol communicationProtocol;
    private String deviceSerialNumber;
    private File dataDirectory;
    private PhoneControlPluginProperties pluginProperties;

    public void setProtocolType(ProtocolType protocolType) {
        this.currentProtocolType = protocolType;
    }

    public ProtocolType getProtocolType() {
        return currentProtocolType;
    }

    public void setCommunicationProtocol(PhoneCommunicationProtocol communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

    public PhoneCommunicationProtocol getCommunicationProtocol() {
        return communicationProtocol;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public File getDataDirectory() {
        return dataDirectory;
    }

    public void setPluginProperties(PhoneControlPluginProperties pluginProperties) {
        this.pluginProperties = pluginProperties;
    }

    public PhoneControlPluginProperties getPluginProperties() {
        return pluginProperties;
    }
}
