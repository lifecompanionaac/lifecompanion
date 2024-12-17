package org.lifecompanion.plugin.phonecontrol;

import java.io.File;
import java.util.ArrayList;

import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.server.AdbCommunicationProtocol;
import org.lifecompanion.plugin.phonecontrol.server.BluetoothCommunicationProtocol;
import org.lifecompanion.plugin.phonecontrol.server.PhoneCommunicationProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PhoneCommunicationManager handles the selection and initialization of the communication protocol (ADB or Bluetooth).
 */
public enum PhoneCommunicationManager {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneCommunicationManager.class);

    public enum ProtocolType {
        ADB,
        BLUETOOTH
    }

    private ProtocolType currentProtocolType;
    private PhoneCommunicationProtocol communicationProtocol;
    private String deviceSerialNumber;

    /**
     * Sets the communication protocol type and initializes the corresponding protocol.
     *
     * @param protocolType The selected protocol type (ADB or Bluetooth).
     */
    public void setProtocolType(ProtocolType protocolType, File dataDirectory) {
        this.currentProtocolType = protocolType;

        switch (protocolType) {
            case ADB:
                LOGGER.info("Initializing ADB protocol.");
                this.communicationProtocol = new AdbCommunicationProtocol(dataDirectory);
                ConnexionController.INSTANCE.startController(this.communicationProtocol);
                ConnexionController.INSTANCE.installAdb(dataDirectory);
                ((AdbCommunicationProtocol) this.communicationProtocol).openConnection();
                ((AdbCommunicationProtocol) this.communicationProtocol).installApk();

                break;
            case BLUETOOTH:
                LOGGER.info("Initializing Bluetooth protocol.");
                this.communicationProtocol = new BluetoothCommunicationProtocol();

                break;
            default:
                throw new IllegalArgumentException("Unsupported protocol type: " + protocolType);
        }
    }

    /**
     * Gets the currently selected communication protocol.
     *
     * @return The active communication protocol.
     */
    public PhoneCommunicationProtocol getCommunicationProtocol() {
        return communicationProtocol;
    }

    /**
     * Gets the current protocol type.
     *
     * @return The currently selected protocol type.
     */
    public ProtocolType getCurrentProtocolType() {
        return currentProtocolType;
    }

    public void stop() {
        if (communicationProtocol != null) {
            communicationProtocol.close();
            communicationProtocol = null;
        }
    }

    public ArrayList<String> getDevices() {
        // TODO
        return new ArrayList<>();
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public String getDeviceName(String deviceSerialNumber) {
        // TODO
        return "";
    }

    public boolean installApp(String deviceSerialNumber, String apkPath) {
        // TODO
        return false;
    }

    public boolean isAppInstalled(String deviceSerialNumber) {
        // TODO
        return false;
    }

    public void startApp(String deviceSerialNumber) {
        // TODO
    }
}
