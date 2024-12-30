package org.lifecompanion.plugin.phonecontrol.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
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
    private String adbPath;
    private boolean connectionOpen;

    /**
     * Constructor for AdbCommunicationProtocol.
     */
    public AdbCommunicationProtocol(File adb) {
        this.adbPath = adb.getPath();
        this.connectionOpen = false;
    }

    @Override
    public void send(String data) {
        if (!isOpen()) {
            LOGGER.warn("Connection is not open. Unable to send data.");

            return;
        }

        try {
            // Encode the data to be passed as an intent extra
            String encodedData = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "am", "startservice",
                "-a", "org.lifecompanion.phonecontrolapp.services.JSONProcessingService",
                "--es", "extra_data", encodedData);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error while sending data via ADB", e);
        }
    }

    @Override
    public String send(String data, String requestId) {
        send(data);

        return receive(requestId);
    }

    private String receive(String requestId) {
        if (!isOpen()) {
            LOGGER.warn("Connection is not open. Unable to receive data.");

            return null;
        }

        try {
            String path = "/data/data/org.lifecompanion.phonecontrol/files/output/";
            ArrayList<String> processedFiles = new ArrayList<>();
            String content = null;

            while (content == null) {
                ArrayList<String> filePaths = pollDirectoryViaAdb(path);

                for (String filePath : filePaths) {
                    if (!processedFiles.contains(filePath)) {
                        ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "run-as", "org.lifecompanion.phonecontrol", "cat", filePath);
                        Process process = processBuilder.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        StringBuilder fileContent = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }

                        process.waitFor();
                        processedFiles.add(filePath);

                        try {
                            JSONObject jsonObject = new JSONObject(fileContent.toString());
                            if (jsonObject.getString("request_id").equals(requestId)) {
                                content = jsonObject.toString();
                                new ProcessBuilder(adbPath, "shell", "run-as", "org.lifecompanion.phonecontrol", "rm", filePath).start().waitFor();

                                break;
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Error while parsing JSON", e);
                        }
                    }
                }

                if (content == null) {
                    Thread.sleep(1000);
                }
            }

            return content;
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

    public ArrayList<String> pollDirectoryViaAdb(String directoryPath) {
        ArrayList<String> filePaths = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "shell", "ls", directoryPath);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String fileName;

            while ((fileName = reader.readLine()) != null) {
                if (!fileName.trim().isEmpty()) {
                    filePaths.add(directoryPath + "/" + fileName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error polling directory via ADB: " + e.getMessage(), e);
        }

        return filePaths;
    }

    /**
     * Install the app on the selected device
     */
    public boolean installApk() {
        String deviceSerialNumber = GlobalState.INSTANCE.getDeviceSerialNumber();
        LOGGER.info("Installing app on phone...");

        if (deviceSerialNumber == null) {
            if (GlobalState.INSTANCE.getPluginProperties() == null) {
                LOGGER.error("Plugin properties not set");

                return false;
            }

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
        String installedVersion = getInstalledAppVersion(deviceSerialNumber);
        String apkVersion = getApkVersion(apkPath);

        if (installedVersion != null && compareVersions(installedVersion, apkVersion) >= 0) {
            LOGGER.info("App is already installed with version " + installedVersion + " which is equal or newer than the APK version " + apkVersion);

            return true;
        }

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

    private String getInstalledAppVersion(String deviceSerialNumber) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell", "dumpsys", "package", "org.lifecompanion.service");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("versionName=")) {
                    return line.split("=")[1].trim();
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error getting installed app version", e);
        }

        return null;
    }
    
    private String getApkVersion(File apkPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("aapt", "dump", "badging", apkPath.getPath());
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("versionName=")) {
                    return line.split("'")[1].trim();
                }
            }

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error getting APK version", e);
        }

        return null;
    }
    
    private int compareVersions(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        for (int i = 0; i < Math.max(v1.length, v2.length); i++) {
            int num1 = i < v1.length ? Integer.parseInt(v1[i]) : 0;
            int num2 = i < v2.length ? Integer.parseInt(v2[i]) : 0;

            if (num1 != num2) {
                return num1 - num2;
            }
        }

        return 0;
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
