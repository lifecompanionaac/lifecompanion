package org.lifecompanion.plugin.phonecontrol;

import java.io.File;
import java.util.ArrayList;

import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.controller.GlobalState;
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

    /**
     * Sets the communication protocol type and initializes the corresponding protocol.
     *
     * @param protocolType The selected protocol type (ADB or Bluetooth).
     */
    public void setProtocolType(ProtocolType protocolType) {
        GlobalState.INSTANCE.setProtocolType(protocolType);

        switch (protocolType) {
            case ADB:
                LOGGER.info("Initializing ADB protocol");
                File adb = ConnexionController.INSTANCE.installAdb();
                GlobalState.INSTANCE.setCommunicationProtocol(new AdbCommunicationProtocol(adb));
                ConnexionController.INSTANCE.startController();
                ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).openConnection();
                ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).installApk();

                break;
            case BLUETOOTH:
                LOGGER.info("Initializing Bluetooth protocol");
                GlobalState.INSTANCE.setCommunicationProtocol(new BluetoothCommunicationProtocol());
                // TODO

                break;
            default:
                throw new IllegalArgumentException("Unsupported protocol type: " + protocolType);
        }
    }

    public void stop() {
        PhoneCommunicationProtocol communicationProtocol = GlobalState.INSTANCE.getCommunicationProtocol();

        if (communicationProtocol != null) {
            communicationProtocol.close();
            GlobalState.INSTANCE.setCommunicationProtocol(null);
        }
    }

    public ArrayList<String> getDevices() {
        ArrayList<String> devices = new ArrayList<>();

        switch(GlobalState.INSTANCE.getProtocolType()) {
            case ADB:
                devices = ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).getDevices();
            case BLUETOOTH:
                // TODO

                break;
            default:
                LOGGER.warn("Unsupported communication protocol");

                break;
        }

        return devices;
    }

    public String getDeviceName(String deviceSerialNumber) {
        String deviceName = null;

        switch(GlobalState.INSTANCE.getProtocolType()) {
            case ADB:
                deviceName = ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).getDeviceName(deviceSerialNumber);
            case BLUETOOTH:
                // TODO

                break;
            default:
                LOGGER.warn("Unsupported communication protocol");

                break;
        }

        return deviceName;
    }

    public boolean installApp() {
        boolean success = false;

        switch(GlobalState.INSTANCE.getProtocolType()) {
            case ADB:
                success = ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).installApk();
            case BLUETOOTH:
                // TODO

                break;
            default:
                LOGGER.warn("Unsupported communication protocol");

                break;
        }

        return success;
    }

    public boolean isAppInstalled(String deviceSerialNumber) {
        boolean installed = false;

        switch(GlobalState.INSTANCE.getProtocolType()) {
            case ADB:
                installed = ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).isAppInstalled(deviceSerialNumber);
            case BLUETOOTH:
                // TODO

                break;
            default:
                LOGGER.warn("Unsupported communication protocol");

                break;
        }

        return installed;
    }

    public void startApp(String deviceSerialNumber) {
        switch(GlobalState.INSTANCE.getProtocolType()) {
            case ADB:
                ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).startApp(deviceSerialNumber);
            case BLUETOOTH:
                // TODO

                break;
            default:
                LOGGER.warn("Unsupported communication protocol");

                break;
        }
    }
}
