package org.lifecompanion.plugin.phonecontrol;

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

    PhoneCommunicationManager() {
        // Default to ADB
        setProtocolType(ProtocolType.ADB);
    }

    /**
     * Sets the communication protocol type and initializes the corresponding protocol.
     *
     * @param protocolType The selected protocol type (ADB or Bluetooth).
     */
    public void setProtocolType(ProtocolType protocolType) {
        this.currentProtocolType = protocolType;

        switch (protocolType) {
            case ADB:
                LOGGER.info("Initializing ADB protocol.");
                this.communicationProtocol = new AdbCommunicationProtocol("/path/to/adb"); // Update path as needed

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
}
