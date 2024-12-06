package org.lifecompanion.plugin.phonecontrol.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AdbCommunicationProtocol is an implementation of PhoneCommunicationProtocol using ADB (Android Debug Bridge).
 * It handles sending and receiving JSON data to/from an Android device via ADB.
 */
public class AdbCommunicationProtocol implements PhoneCommunicationProtocol {
    private static final Logger LOGGER = Logger.getLogger(AdbCommunicationProtocol.class.getName());
    private String adbPath;
    private boolean connectionOpen;

    /**
     * Constructor for AdbCommunicationProtocol.
     * 
     * @param adbPath The path to the ADB executable.
     */
    public AdbCommunicationProtocol(String adbPath) {
        this.adbPath = adbPath;
        this.connectionOpen = false;
    }

    @Override
    public void send(String data) {
        if (!isOpen()) {
            LOGGER.log(Level.WARNING, "Connection is not open. Unable to send data.");
            return;
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "echo", data, ">",
                    "/data/local/tmp/phonecontrol/input.json");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error while sending data via ADB", e);
        }
    }
    
    @Override
    public String receive() {
        if (!isOpen()) {
            LOGGER.log(Level.WARNING, "Connection is not open. Unable to receive data.");
            return null;
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "cat",
                    "/data/local/tmp/phonecontrol/output.json");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error while receiving data via ADB", e);
        }
        return null;
    }

    @Override
    public void close() {
        connectionOpen = false;
    }

    @Override
    public boolean isOpen() {
        return connectionOpen;
    }

    /**
     * Establish a connection via ADB.
     * 
     * @return True if the connection was successfully opened, false otherwise.
     */
    public boolean openConnection() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "devices");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("device")) {
                    connectionOpen = true;
                    return true;
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error while establishing connection via ADB", e);
        }
        return false;
    }
}
