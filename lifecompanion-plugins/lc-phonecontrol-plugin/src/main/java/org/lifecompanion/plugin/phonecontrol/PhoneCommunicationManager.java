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
                LOGGER.info("Initializing ADB protocol.");
                File adb = ConnexionController.INSTANCE.installAdb();
                GlobalState.INSTANCE.setCommunicationProtocol(new AdbCommunicationProtocol(adb));
                ConnexionController.INSTANCE.startController();
                ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).openConnection();
                ((AdbCommunicationProtocol) GlobalState.INSTANCE.getCommunicationProtocol()).installApk();

                break;
            case BLUETOOTH:
                LOGGER.info("Initializing Bluetooth protocol.");
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
        // TODO
        return new ArrayList<>();
    }

    public String getDeviceName(String deviceSerialNumber) {
        // TODO
        return "";
    }

    public boolean installApp(String deviceSerialNumber) {
        // TODO
        // apkPath is GlobalState.INSTANCE.getDataDirectory() + "/apk/lc-service.apk"
    
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
