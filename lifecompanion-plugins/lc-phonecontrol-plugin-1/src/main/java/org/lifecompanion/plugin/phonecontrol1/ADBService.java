package org.lifecompanion.plugin.phonecontrol1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Etudiants IUT Vannes : GUERNY Baptiste, HASCOËT Anthony,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public enum ADBService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ADBService.class);
    private String adbPath;
    private String deviceSerialNumber;

    /**
     * Start the ADB server. <br>
     * Execute the command shell <code>adb start-server</code>
     */
    public void startADB(String adbPath) {
        this.adbPath = adbPath;

        LOGGER.info("Starting ADB");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "start-server");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            LOGGER.error("Error starting ADB", e);
        }
    }

    /**
     * Stop the ADB server. <br>
     * Execute the command shell <code>adb kill-server</code>
     */
    public void stopADB() {
        LOGGER.info("Stopping ADB");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "kill-server");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            LOGGER.error("Error stopping ADB", e);
        }
    }

    //== DEVICES =====================================================================================

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
            Process process = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "settings", "get", "secure", "bluetooth_name").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            deviceName = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("Error getting devices names", e);
        }
        return deviceName;
    }

    //== INSTALL & START APP ==========================================================================

    /**
     * Install the application on the device
     *
     * @param deviceSerialNumber Serial number of the device
     * @param apkPath Path of the APK file
     * @return true if the application is installed successfully, false otherwise
     */
    public boolean installApp(String deviceSerialNumber, String apkPath) {
        LOGGER.info("Installing app");
        boolean ret = false;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "install", apkPath);
            Process process = processBuilder.start();
            process.waitFor();
            if (isAppInstalled(deviceSerialNumber)) {
                ret = true;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error installing app", e);
        }
        return ret;
    }

    /**
     * Check if the application is installed on the device with the following command : <br>
     * <code>adb -s < deviceSerialNumber > shell pm list packages org.lifecompanion</code>
     *
     * @param deviceSerialNumber Serial number of the device
     * @return true if the application is installed, false otherwise
     */
    public boolean isAppInstalled(String deviceSerialNumber) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell", "pm", "list",
                    "packages", "org.lifecompanion");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null && line.equals("package:org.lifecompanion")) {
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
        this.deviceSerialNumber = deviceSerialNumber;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start", "-n", "org.lifecompanion/.MainActivity");
            Process startServiceProcess = processBuilder.start();
            startServiceProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error starting app", e);
        }
    }

    //== CALLS =======================================================================================

    /**
     * Get The status call of the device :
     * 0 : No call
     * 1 : In call
     * PhoneNumber : Incoming call
     *
     * @return Call status
     */
    public String getCallStatus() {
        LOGGER.info("Getting call status");
        String callStatus = "0"; // Default status: No call
        int nbCallStateFound = 0; // For better performances, we limit to search 2 callState

        try {
            Process process = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "dumpsys", "telephony.registry").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            Pattern callStatePattern = Pattern.compile("mCallState=(\\d+)"); // Search for the call state
            Pattern phoneNumberPattern = Pattern.compile("mCallIncomingNumber=(.+)"); // Search for the incoming phone number

            String line;
            while ((line = reader.readLine()) != null && nbCallStateFound <= 2) {
                Matcher callStateMatcher = callStatePattern.matcher(line);
                Matcher phoneNumberMatcher = phoneNumberPattern.matcher(line);

                if (callStateMatcher.find()) {
                    String callState = callStateMatcher.group(1);
                    nbCallStateFound += 1;
                    if (callState.equals("2")) {
                        callStatus = "1"; // In call
                        break;
                    }
                }

                if (phoneNumberMatcher.find()) {
                    String phoneNumber = phoneNumberMatcher.group(1);
                    if (!phoneNumber.isEmpty()) {
                        callStatus = phoneNumber; // Incoming call
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error getting call status", e);
        }

        LOGGER.info("Call status: {}", callStatus);
        return callStatus;
    }

    /**
     * Pick up the incoming call.
     *
     * @param speakerOn True if the speaker should be on, false otherwise
     */
    public void pickUp(boolean speakerOn) {
        LOGGER.info("Picking up");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start-foreground-service", "-n", "org.lifecompanion/.services.CallService",
                    "--ez", "speaker", "" + speakerOn);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error picking up", e);
        }
    }

    /**
     * Hang up an incoming call, or end a call.
     */
    public void hangUp() {
        LOGGER.info("Hanging up");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start-foreground-service", "-n", "org.lifecompanion/.services.CallService", "--ez", "stop",
                    "True");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error hanging up", e);
        }
    }

    /**
     * Call a specific phone number.
     *
     * @param phoneNumber Phone number to call
     * @param speakerOn True if the speaker should be on, false otherwise
     */
    public void call(String phoneNumber, boolean speakerOn) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        LOGGER.info("Calling {}", phoneNumber);
        phoneNumber = phoneNumber.replace(" ", "");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start-foreground-service", "-n", "org.lifecompanion/.services.CallService",
                    "--es", "phoneNumber", phoneNumber, "--ez", "speaker", "" + speakerOn);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error calling", e);
        }
    }

    //== GET CONV & SMS ==============================================================================

    /**
     * Get the list of recent conversations. <br>
     * Can return a size <= indexMax - indexMin. <br>
     * Return format -> ArrayList of String :
     * <code>"phoneNumber|phoneNumberOrContactName|lastMessage|lastMessageDate|isSeen"</code>
     *
     * @param indexMin Index of the first conversation to return
     * @param indexMax Index of the last conversation to return
     * @return ArrayList of conversations
     */
    public ArrayList<String> getConvList(int indexMin, int indexMax) {
        if (indexMin < 0) {
            throw new IllegalArgumentException("Index min cannot be negative");
        }
        if (indexMax < indexMin) {
            throw new IllegalArgumentException("Index max cannot be lower than index min");
        }

        LOGGER.info("Fetch list of conv from {} to {}", indexMin, indexMax);
        ArrayList<String> ret = new ArrayList<>();
        try {
            // Logcat
            Process logcatProcess = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "logcat", "-v",
                    "raw", "SMSReaderServiceConv:I", "*:S").start();

            // Call the android app
            Process serviceProcess = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start-foreground-service", "-n", "org.lifecompanion/.services.SMSReaderService",
                    "--ei", "start", "" + indexMin, "--ei", "end", "" + indexMax).start();
            serviceProcess.waitFor();

            // Read the logcat output
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            reader.readLine(); // Ignore the first line
            String line;
            while ((line = reader.readLine()) != null
                    && ret.size() < indexMax - indexMin
                    && !line.equals("+33000000000")) {
                ret.add(line);
            }

            logcatProcess.destroy();
            reader.close();

            // Clear the logcat buffer
            new ProcessBuilder(adbPath, "logcat", "-c").start();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error getting conv list", e);
        }

        return ret;
    }

    /**
     * Get the list of SMS of a conversation. <br>
     * Can return a size <= indexMax - indexMin. <br>
     * Return format -> ArrayList of String :
     * "phoneNumber|phoneNumberOrContactName|message|date|isSendByMe"
     *
     * @param phoneNumber Phone number of the conversation
     * @param indexMin Index of the first conversation to return
     * @param indexMax Index of the last conversation to return
     * @return ArrayList of SMS
     */
    public ArrayList<String> getSMSList(String phoneNumber, int indexMin, int indexMax) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (indexMin < 0) {
            throw new IllegalArgumentException("Index min cannot be negative");
        }
        if (indexMax < indexMin) {
            throw new IllegalArgumentException("Index max cannot be lower than index min");
        }

        LOGGER.info("Fetch list of sms of {} from {} to {}", phoneNumber, indexMin, indexMax);
        ArrayList<String> ret = new ArrayList<>();
        try {
            // Logcat
            Process logcatProcess = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "logcat", "-v",
                    "raw", "SMSReaderServiceSMS:I", "*:S").start();

            // Call the android app
            Process serviceProcess = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start-foreground-service", "-n", "org.lifecompanion/.services.SMSReaderService",
                    "--es", "phoneNumber", phoneNumber,
                    "--ei", "start", "" + indexMin, "--ei", "end", "" + indexMax).start();
            serviceProcess.waitFor();

            // Read the logcat output
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null && ret.size() < indexMax - indexMin
                    && !line.equals("+33000000000")) {
                if (line.startsWith(phoneNumber)) {
                    ret.add(line);
                }
            }

            logcatProcess.destroy();
            reader.close();

            // Clear the logcat buffer
            new ProcessBuilder(adbPath, "logcat", "-c").start();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error getting sms list", e);
        }

        return ret;
    }

    //== SEND SMS ===================================================================================

    /**
     * Sends an SMS to the specified phone number from the device with the given serial number.
     * The message is encoded in Base64 for better comprehension in command shell.
     *
     * @param phoneNumber        Phone number to which the SMS is to be sent
     * @param message            The message to be sent as an SMS
     * @return int               Returns 1 if the SMS was sent successfully, 0 otherwise
     */
    public int sendSMS(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        LOGGER.info("Sending sms to {} : {}", phoneNumber, message);
        int lastValidationLog = 0;

        try {
            // Logcat
            Process logcatProcess = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "logcat", "-v", "raw",
                    "SmsSenderServiceCallBack:I", "*:S").start();

            // Call the android app
            String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes());
            Process process = new ProcessBuilder(adbPath, "-s", deviceSerialNumber, "shell",
                    "am", "start-foreground-service", "-n", "org.lifecompanion/.services.SMSSenderService",
                    "--es", "phoneNumber", phoneNumber, "--es", "message", encodedMessage).start();
            process.waitFor();

            // Read the logcat output
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("SMS sent successfully")) {
                    LOGGER.info("SMS sent successfully");
                    lastValidationLog = 1;

                    break;
                } else if (line.contains("SMS not send")) {
                    LOGGER.info("SMS not send");
                    lastValidationLog = 0;
                    break;
                }
            }
            logcatProcess.destroy();
            reader.close();

            // Clear the logcat buffer
            new ProcessBuilder(adbPath, "logcat", "-c").start();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error sending sms", e);
        }
        return lastValidationLog;
    }
}
