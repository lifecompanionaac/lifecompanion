package org.lifecompanion.plugin.phonecontrol.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.plugin.phonecontrol.controller.GlobalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdbCommunicationProtocol is an implementation of PhoneCommunicationProtocol using ADB (Android Debug Bridge).
 * It handles sending and receiving JSON data to/from an Android device via ADB.
 */
public class AdbCommunicationProtocol implements PhoneCommunicationProtocol {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdbCommunicationProtocol.class.getName());
    private File adb;
    private String adbPath;
    private boolean connectionOpen;

    /**
     * Constructor for AdbCommunicationProtocol.
     */
    public AdbCommunicationProtocol() {
        this.adb = GlobalState.INSTANCE.getDataDirectory();
        this.adbPath = this.adb.getPath();
        this.connectionOpen = false;
    }

    public File getAdbPath() {
        return adb;
    }

    @Override
    public void send(String data) {
        if (!isOpen()) {
            LOGGER.warn("Connection is not open. Unable to send data.");

            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "echo", data, ">", "/data/local/tmp/phonecontrol/input.json");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error while sending data via ADB", e);
        }
    }
    
    @Override
    public String receive() {
        if (!isOpen()) {
            LOGGER.warn("Connection is not open. Unable to receive data.");

            return null;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "cat", "/data/local/tmp/phonecontrol/output.json");
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
            LOGGER.error("Error while receiving data via ADB", e);
        }

        return null;
    }

    @Override
    public void close() {
        connectionOpen = false;
        stopAdb();
    }

    @Override
    public boolean isOpen() {
        return connectionOpen;
    }

    private void startAdb() {
        LOGGER.info("Starting ADB");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "start-server");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            LOGGER.error("Error starting ADB", e);
        }
    }

    private void stopAdb() {
        LOGGER.info("Stopping ADB");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "kill-server");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            LOGGER.error("Error stopping ADB", e);
        }
    }

    /**
     * Establish a connection via ADB.
     * 
     * @return True if the connection was successfully opened, false otherwise.
     */
    public boolean openConnection() {
        startAdb();
        
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
            LOGGER.error("Error while establishing connection via ADB", e);
        }

        return false;
    }

    public String pollDirectoryViaAdb(String directoryPath, String adbPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "ls", directoryPath);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String fileName;

            while ((fileName = reader.readLine()) != null) {
                if (!fileName.trim().isEmpty()) {
                    // Retrieve the file content
                    ProcessBuilder pullBuilder = new ProcessBuilder(adbPath, "shell", "cat", directoryPath + "/" + fileName);
                    Process pullProcess = pullBuilder.start();
                    BufferedReader pullReader = new BufferedReader(new InputStreamReader(pullProcess.getInputStream()));

                    StringBuilder content = new StringBuilder();
                    String line;

                    while ((line = pullReader.readLine()) != null) {
                        content.append(line);
                    }

                    // Delete the file from the device
                    new ProcessBuilder(adbPath, "shell", "rm", directoryPath + "/" + fileName).start().waitFor();

                    return content.toString();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error polling directory via ADB: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Install the app on the selected device
     */
    public boolean installApk() {
        String deviceSerialNumber = GlobalState.INSTANCE.getDeviceSerialNumber();
        LOGGER.info("Installing app on phone...");

        if (deviceSerialNumber == null) {
            deviceSerialNumber = GlobalState.INSTANCE.getPluginProperties().deviceProperty().get();
        }

        File apkPath = new File(org.lifecompanion.util.IOUtils.getTempDir("apk") + File.separator + "lc-service.apk");
        IOUtils.createParentDirectoryIfNeeded(apkPath);

        try {
            FileOutputStream fos = new FileOutputStream(apkPath);
            InputStream is = ResourceHelper.getInputStreamForPath("/apk/lc-service.apk");
            IOUtils.copyStream(is, fos);
        } catch (Exception e) {
            LOGGER.error("Error while copying apk file", e);
        }

        boolean isInstalled = false;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "install", apkPath.getPath());
            Process process = processBuilder.start();
            process.waitFor();

            if (isAppInstalled(deviceSerialNumber)) {
                isInstalled = true;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error installing app", e);
        }

        if (isInstalled) {
            LOGGER.info("App installed");
            startApp(deviceSerialNumber); // To ask permission
        }

        return isInstalled;
    }

    /**
     * Get the list of connected devices with the command <code>adb devices</code>
     *
     * @return ArrayList of devices serial numbers
     */
    public ArrayList<String> getDevices() {
        ArrayList<String> devicesList = new ArrayList<>();

        try {
            Process process = new ProcessBuilder(adbPath, "devices").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            reader.readLine(); // Skip the first output line
            String line;

            while ((line = reader.readLine()) != null) { // Get connected devices
                String[] parts = line.split("\\s+");

                if (parts.length == 2 && parts[1].equals("device")) {
                    devicesList.add(parts[0]);
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error getting devices list", e);
        }

        return devicesList;
    }

    /**
     * Get the bluetooth device name by its serial number. <br>
     * The adb command used is <code>adb -s < deviceSerialNumber > shell settings get secure bluetooth_name</code>
     *
     * @param deviceSerialNumber Serial number of the device
     * @return Device name
     */
    public String getDeviceName(String deviceSerialNumber) {
        String deviceName = null;

        try {
            Process process = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell", "settings", "get", "secure", "bluetooth_name").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            deviceName = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("Error getting devices names", e);
        }

        return deviceName;
    }

    /**
     * Check if the application is installed on the device with the following command : <br>
     * <code>adb -s < deviceSerialNumber > shell pm list packages org.lifecompanion.phonecontrolapp</code>
     *
     * @param deviceSerialNumber Serial number of the device
     * @return true if the application is installed, false otherwise
     */
    public boolean isAppInstalled(String deviceSerialNumber) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell", "pm", "list", "packages", "org.lifecompanion.phonecontrolapp");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null && line.equals("package:org.lifecompanion.phonecontrolapp")) {
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error checking if app is installed", e);
        }

        return false;
    }

    /**
     * Start the application on the device.
     *
     * @param deviceSerialNumber Serial number of the device
     */
    public void startApp(String deviceSerialNumber) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell", "am", "start", "-n", "org.lifecompanion.phonecontrolapp/.MainActivity");
            Process startServiceProcess = processBuilder.start();
            startServiceProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error starting app", e);
        }
    }
}
