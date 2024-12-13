package org.lifecompanion.plugin.phonecontrol.server;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BluetoothCommunicationProtocol is an implementation of PhoneCommunicationProtocol using Bluetooth.
 * It handles sending and receiving JSON data to/from an Android device via Bluetooth.
 */
public class BluetoothCommunicationProtocol implements PhoneCommunicationProtocol {
    private static final Logger LOGGER = Logger.getLogger(BluetoothCommunicationProtocol.class.getName());
    private StreamConnection connection;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean connectionOpen;

    /**
     * Constructor for BluetoothCommunicationProtocol.
     */
    public BluetoothCommunicationProtocol() {
        this.connectionOpen = false;
    }

    @Override
    public void send(String data) {
        if (!isOpen()) {
            LOGGER.log(Level.WARNING, "Connection is not open. Unable to send data.");

            return;
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
            writer.write(data);
            writer.flush();
        }
    }

    @Override
    public String receive() {
        if (!isOpen()) {
            LOGGER.log(Level.WARNING, "Connection is not open. Unable to receive data.");

            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            return output.toString();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while receiving data via Bluetooth", e);
        }

        return null;
    }

    @Override
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (connection != null) {
                connection.close();
            }

            connectionOpen = false;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while closing Bluetooth connection", e);
        }
    }

    @Override
    public boolean isOpen() {
        return connectionOpen;
    }

    /**
     * Establish a Bluetooth connection.
     * 
     * @param deviceAddress The address of the Bluetooth device to connect to.
     * @return True if the connection was successfully opened, false otherwise.
     */
    public boolean openConnection(String deviceAddress) throws BluetoothStateException {
        try {
            RemoteDevice remoteDevice = findDeviceByAddress(deviceAddress);

            if (remoteDevice == null) {
                LOGGER.log(Level.WARNING, "Device with address " + deviceAddress + " not found.");

                return false;
            }

            // Create a connection URL using the provided device address
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = localDevice.getDiscoveryAgent();
            String connectionURL = agent.selectService(new UUID(0x1101), ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

            if (connectionURL == null) {
                LOGGER.log(Level.WARNING, "Could not find suitable service on the device.");

                return false;
            }

            connection = (StreamConnection) Connector.open(connectionURL);
            inputStream = connection.openInputStream();
            outputStream = connection.openOutputStream();
            connectionOpen = true;
            LOGGER.log(Level.INFO, "Successfully connected to device: " + deviceAddress);

            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while establishing Bluetooth connection", e);
        }

        return false;
    }

    /**
     * Finds a Bluetooth device by its address.
     * 
     * @param deviceAddress The address of the Bluetooth device.
     * @return The RemoteDevice if found, otherwise null.
     */
    public RemoteDevice findDeviceByAddress(String deviceAddress) {
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = localDevice.getDiscoveryAgent();
            RemoteDevice[] devices = agent.retrieveDevices(DiscoveryAgent.CACHED);

            if (devices != null) {
                for (RemoteDevice device : devices) {
                    if (device.getBluetoothAddress().equals(deviceAddress)) {
                        return device;
                    }
                }
            }
        } catch (BluetoothStateException e) {
            LOGGER.log(Level.SEVERE, "Error while searching for Bluetooth devices", e);
        }

        return null;
    }
}
