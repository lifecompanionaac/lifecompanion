package org.lifecompanion.plugin.phonecontrol.controller;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.json.JSONObject;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.plugin.phonecontrol.PhoneCommunicationManager;
import org.lifecompanion.plugin.phonecontrol.PhoneControlPlugin;
import org.lifecompanion.plugin.phonecontrol.PhoneControlPluginProperties;
import org.lifecompanion.plugin.phonecontrol.keyoption.ConversationListKeyOption;
import org.lifecompanion.plugin.phonecontrol.keyoption.SMSListKeyOption;
import org.lifecompanion.plugin.phonecontrol.model.ConversationListContent;
import org.lifecompanion.plugin.phonecontrol.model.SMSListContent;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;

/**
 * Main Controller for the whole app.
 * Handles the initialization of the communication protocol and the controllers.
 */
public enum ConnexionController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnexionController.class);

    private Timer refreshTimer;
    private PhoneControlPluginProperties currentPhoneControlPluginProperties;

    public static final String VAR_SMS_UNREAD = "SMSUnread";
    public static final String VAR_CALL_DURATION = "CallDuration";
    public static final String VAR_PHONE_NUMBER_OR_CONTACT_NAME = "PhoneNumberOrContactName";
    public static final String VAR_PHONE_NAME = "PhoneName";

    private int smsUnreadCount;
    private int callDurationInt;
    private String callDuration;
    private String phoneNumberOrContactName;
    private String phoneName;

    public static final ConversationListContent CONV_LOADING = new ConversationListContent();
    public static final ConversationListContent CONV_NOT_CONNECTED = new ConversationListContent();
    public static final ConversationListContent CONV_END_MESSAGE = new ConversationListContent();
    public static final SMSListContent SMS_LOADING = new SMSListContent();
    public static final SMSListContent SMS_NOT_CONNECTED = new SMSListContent();
    public static final SMSListContent SMS_END_MESSAGE = new SMSListContent();

    private boolean onCall;
    private String phoneNumber;
    private List<ConversationListKeyOption> convCells;
    private List<SMSListKeyOption> smsCells;
    private int convIndexMin;
    private int smsIndexMin;
    private int durationInterval;
    private boolean topConvReached;
    private boolean topSmsReached;

    private Runnable callEnterCallback;
    private Runnable callEndedCallback;
    private Set<Consumer<Integer>> unreadCountUpdateCallback;
    private Set<Consumer<Boolean>> validationSendSMSCallback;

    /** To handle if the list is already refreshing */
    private boolean convIsRefreshing = false;

    public void startController() {
        this.convCells = new ArrayList<>();
        this.smsCells = new ArrayList<>();
        this.unreadCountUpdateCallback = new HashSet<>(5);
        this.validationSendSMSCallback = new HashSet<>(1);
    }

    public double getDurationInterval() {
        return durationInterval;
    }

    public int getSmsUnread() {
        return smsUnreadCount;
    }

    public int getCallDurationInt() {
        return callDurationInt;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public String getPhoneNumberOrContactName() {
        return phoneNumberOrContactName;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void installFonts() {
        if (isFontInstalled("Segoe UI Emoji")) {
            return;
        }

        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File fontsDir = new File(tempDir, "fonts");
        File fontsZip = new File(fontsDir, "fonts.zip");

        if (!fontsDir.exists()) {
            fontsDir.mkdirs();
        }   

        try {
            InputStream is = PhoneControlPlugin.class.getResourceAsStream("/fonts/fonts.zip");
            FileOutputStream fos = new FileOutputStream(fontsZip);
            IOUtils.copyStream(is, fos);
        } catch (Exception e) {
            LOGGER.error("Failed to copy the fonts.zip file to the temp directory", e);
        }

        try {
            IOUtils.unzipInto(fontsZip, fontsDir, null);
        } catch (Exception e) {
            LOGGER.error("Failed to unzip the fonts.zip file", e);
        }

        try {
            if (SystemType.current() == SystemType.MAC) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("sh", "-c", "cp -R " + fontsDir.getAbsolutePath() + "/*.ttf /Library/Fonts/");
                    pb.start();
                } catch (Exception e) {
                    LOGGER.error("Failed to install the fonts on mac", e);
                }
            } else if (SystemType.current() == SystemType.UNIX) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("sh", "-c", "cp -R " + fontsDir.getAbsolutePath() + "/*.ttf /usr/share/fonts/");
                    pb.start();
                } catch (Exception e) {
                    LOGGER.error("Failed to install the fonts on linux", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to install the fonts", e);
        }

        fontsZip.delete();
        fontsDir.delete();
    }

    private boolean isFontInstalled(String fontName) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        for (String name : fontNames) {
            if (name.equalsIgnoreCase(fontName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        GlobalState.INSTANCE.setPluginProperties((PhoneControlPluginProperties) configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class));
        this.currentPhoneControlPluginProperties = GlobalState.INSTANCE.getPluginProperties();
        resetVariables();

        // Start app on phone if needed
        String deviceSerialNumber = currentPhoneControlPluginProperties.deviceProperty().get();
        PhoneCommunicationManager.INSTANCE.startApp(deviceSerialNumber);

        this.phoneName = PhoneCommunicationManager.INSTANCE.getDeviceName(deviceSerialNumber);

        // Set up the interval of refresh
        this.durationInterval = Math.max(currentPhoneControlPluginProperties.durationInternalProperty().get(), 1000);
        this.refreshTimer = new Timer(true);
        this.refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshCallStatus();
                refreshConvListExec();

                // Refresh only if we are not in call, if we have a selected conversation and if we are at the top of the list
                if (phoneNumber != null && !onCall && smsIndexMin == 0) {
                    refreshSMSList();
                }
            }
        }, 500, durationInterval);

        // Lists KeyOptions, find every conv list cells
        Map<GridComponentI, List<ConversationListKeyOption>> convKeys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(ConversationListKeyOption.class, configuration, convKeys, null);
        convKeys.values().stream().flatMap(List::stream).distinct().forEach(convCells::add);
        setLoadingConvList();
        // First load of conversations
        refreshConvList();

        // Find every sms list cells
        Map<GridComponentI, List<SMSListKeyOption>> smsKeys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(SMSListKeyOption.class, configuration, smsKeys, null);
        smsKeys.values().stream().flatMap(List::stream).distinct().forEach(smsCells::add);
        setLoadingSMSList();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.callEnterCallback = null;
        this.callEndedCallback = null;

        this.refreshTimer.cancel();
        this.refreshTimer = null;
        
        // Lists KeyOptions
        convCells.clear();
        smsCells.clear();

        resetVariables();
        this.currentPhoneControlPluginProperties = null;
        GlobalState.INSTANCE.setPluginProperties(null);
    }

    private void resetVariables() {
        this.smsUnreadCount = 0;
        this.callDurationInt = -1;
        this.callDuration = "00:00:00";
        this.phoneNumberOrContactName = "--.--.--.--.--";
        this.phoneName = "";
        this.phoneNumber = null;
        this.onCall = false;
        this.convIndexMin = 0;
        this.smsIndexMin = 0;
        this.topConvReached = false;
        this.topSmsReached = false;
    }
    
    public File installAdb() {
        File dataDirectory = GlobalState.INSTANCE.getDataDirectory();
        String inputFolder = null;
        String adbFileName = "adb";
        SystemType systemType = SystemType.current();

        if (systemType == SystemType.WINDOWS) {
            inputFolder = "/adb/platform-tools-latest-windows.zip";
            adbFileName += ".exe";
        } else if (systemType == SystemType.UNIX) {
            inputFolder = "/adb/platform-tools-latest-linux.zip";
        } else if (systemType == SystemType.MAC) {
            inputFolder = "/adb/platform-tools-latest-macos.zip";
        } else {
            LOGGER.error("Unsupported system type");

            return null;
        }

        File adbZip = new File(dataDirectory + File.separator + "platform-tools.zip");
        File adbFolder = new File(dataDirectory + File.separator + "platform-tools");

        installAdbFromInputFolder(inputFolder, adbZip, adbFolder.getParentFile());

        File adb = new File(dataDirectory + File.separator + "platform-tools" + File.separator + adbFileName);
        adb.setExecutable(true);

        return adb;
    }

    private void installAdbFromInputFolder(String inputFolder, File adbZip, File adbFolder) {
        try {
            if (inputFolder != null) {
                InputStream is = PhoneControlPlugin.class.getResourceAsStream(inputFolder);
                FileOutputStream fos = new FileOutputStream(adbZip);
                IOUtils.copyStream(is, fos);
                IOUtils.unzipInto(adbZip, adbFolder, null);
                adbZip.delete();
            }
        } catch (java.io.FileNotFoundException e) {
            // Normal case, happens when the user changed modes : Configuration -> Usage -> Configuration -> Usage
            if (!e.getMessage().contains("Le processus ne peut pas accéder au fichier car ce fichier est utilisé par un autre processus")) {
                LOGGER.error("Failed to install ADB from input folder", e);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to install ADB from input folder", e);
        }
    }

    /**
     * Refresh the call status and do actions if needed.
     * - Incoming call : launch the event "OnCallEnter"
     * - No call but "onCall" isn't update : update "onCall" and launch the event "OnCallEnded"
     */
    public void refreshCallStatus() {
        // failsafe, sometimes there's a race condition where the phone doesn't told just yet that we're in a call, and the plugin quits to Menu while the call is still ongoing
        // this is due to the line that follows : "callEndedCallback.run();"
        // waits for 10 seconds before considering the call as potentially ended
        if (this.callDurationInt >= 0 && this.callDurationInt < 10) {
            return;
        } else if (this.callDurationInt == -1) {
            try {
                // let potentially 2 requests arrive
                Thread.sleep(2 * durationInterval);

                if (this.callDurationInt != -1) {
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                return;
            }
        }

        JSONObject callState = CallController.INSTANCE.getCallStatus();

        if (callState == null) {
            this.onCall = false;

            return;
        }

        String callStatus = callState.getString("call_status");
        String incomingCallStatus = callState.getString("incoming_call_status");

        if (callStatus.equals("inactive")) {
            // No call on the phone
            if (this.onCall && callDurationInt == -1) {
                this.onCall = false;
                callEndedCallback.run();
            }
        } else if (callStatus.equals("active")) {
            // Current call on the phone
            this.onCall = true;
        }
        
        if (incomingCallStatus.equals("incoming")) {
            // Incoming call
            this.onCall = true;
            this.phoneNumberOrContactName = callState.getString("phone_number");
            // Update variables (to show the phone number)
            UseVariableController.INSTANCE.requestVariablesUpdate();
            // Launch event "OnCallEnter"
            callEnterCallback.run();
        }
    }

    public String convertTime(int duration) {
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void timer() {
        CompletableFuture.runAsync(() -> {
            this.callDurationInt = 0;

            while (onCall) {
                try {
                    Thread.sleep(1000);
                    this.callDurationInt++;
                    this.callDuration = convertTime(this.callDurationInt);
                    UseVariableController.INSTANCE.requestVariablesUpdate();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOGGER.error("Error while monitoring call duration", e);
                }
            }

            this.callDurationInt = -1;
            this.callDuration = "00:00:00";
        });
    }

    /**
     * Call the current selected conversation (phone number)
     */
    public void callContact() {
        this.onCall = true;
        this.timer();
        boolean speakerOn = currentPhoneControlPluginProperties.speakerOnProperty().get();
        CallController.INSTANCE.call(this.phoneNumber, speakerOn);
    }

    /**
     * Pick up the phone and update "onCall" to true
     */
    public void pickUp() {
        this.onCall = true;
        this.timer();
        boolean speakerOn = currentPhoneControlPluginProperties.speakerOnProperty().get();
        CallController.INSTANCE.pickUp(speakerOn);
    }

    /**
     * Hang up the phone :
     * - update "onCall" to false
     * - launch the event "OnCallEnded"
     */
    public void hangUp() {
        this.onCall = false;
        CallController.INSTANCE.hangUp();
        callEndedCallback.run();
    }

    /**
     * Refresh the content of conversations cells.
     * If the list is already refreshing, don't refresh.
     */
    public void refreshConvList() {
        if (!convIsRefreshing && !onCall) {
            convIsRefreshing = true;
            refreshConvListExec();
            convIsRefreshing = false;
        }
    }

    /**
     * Refresh the content of conversations cells. Only one execution at a time
     */
    private void refreshConvListExec() {
        int convIndexMax = convIndexMin + convCells.size();
        int unreadConvCount = 0;

        // Get conv
        ArrayList<JSONObject> convObj = SMSController.INSTANCE.getConvList(convIndexMin, convIndexMax);
        ConversationListContent emptyContent = new ConversationListContent(null, null, null, true, false);
        // To detect the first empty conv
        boolean emptyConvSet = false;

        if (convObj == null || convObj.isEmpty()) {
            for (final ConversationListKeyOption cell : convCells) {
                Platform.runLater(() -> cell.convProperty().set(emptyContent));
            }
        } else {
            for (int i = 0; i < convCells.size(); i++) {
                // Get the cell
                ConversationListKeyOption cell = this.convCells.get(i);

                if (i < convObj.size()) {
                    JSONObject jsonObject = convObj.get(i);
                    String phoneNumber = jsonObject.getString("phone_number");
                    String contactName = jsonObject.getString("contact_name");
                    byte[] decodedBytes = Base64.getDecoder().decode(jsonObject.getString("last_message"));
                    String message = new String(decodedBytes);
                    boolean isSeen = jsonObject.getBoolean("is_read");
                    boolean isSendByMe = jsonObject.getBoolean("is_sent_by_me");

                    if (!isSeen) {
                        unreadConvCount++;
                    }

                    ConversationListContent cellContent = new ConversationListContent(phoneNumber, contactName, message, isSeen, isSendByMe);
                    Platform.runLater(() -> cell.convProperty().set(cellContent));
                    topConvReached = false;
                } else {
                    if (!emptyConvSet) {
                        // Fill the first empty cell with an alert message
                        Platform.runLater(() -> cell.convProperty().set(CONV_END_MESSAGE));
                        emptyConvSet = true;
                    } else {
                        // Fill cell with empty data
                        Platform.runLater(() -> cell.convProperty().set(emptyContent));
                    }

                    topConvReached = true;
                }
            }
        }

        // Update variables
        this.smsUnreadCount = unreadConvCount;
        UseVariableController.INSTANCE.requestVariablesUpdate();
        final int finalUnreadConvCount = unreadConvCount;
        unreadCountUpdateCallback.forEach((callback) -> callback.accept(finalUnreadConvCount));
    }

    /**
     * For each conversations cells, set the content to "loading".
     */
    private void setLoadingConvList() {
        for (final ConversationListKeyOption cell : convCells) {
            Platform.runLater(() -> cell.convProperty().set(CONV_LOADING));
        }
    }

    /**
     * Refresh the content of conversations cells.
     */
    public void refreshSMSList() {
        if (onCall) {
            return;
        }

        int smsIndexMax = smsIndexMin + smsCells.size();

        // Get conv
        ArrayList<JSONObject> smsObj = SMSController.INSTANCE.getSMSList(this.phoneNumber, smsIndexMin, smsIndexMax);
        SMSListContent emptyContent = new SMSListContent(null, null, null, null, false);
        // To detect the first empty message
        boolean emptyMessageSet = false;

        if (smsObj == null || smsObj.isEmpty()) {
            for (final SMSListKeyOption cell : smsCells) {
                Platform.runLater(() -> cell.smsProperty().set(emptyContent));
            }
        } else {
            int smsObjSize = smsObj.size();

            for (int i = 0; i < smsCells.size(); i++) {
                // Start from the end of smsCells
                SMSListKeyOption cell = this.smsCells.get(smsCells.size() - 1 - i);

                if (i < smsObjSize) {
                    JSONObject jsonObject = smsObj.get(i);
                    String phoneNumber = jsonObject.getString("phone_number");
                    String contactName = jsonObject.getString("contact_name");
                    byte[] decodedBytes = Base64.getDecoder().decode(jsonObject.getString("message"));
                    String message = new String(decodedBytes);
                    String timestamp = jsonObject.getString("timestamp");
                    boolean isRead = jsonObject.getBoolean("is_read");
                    boolean isSendByMe = jsonObject.getBoolean("is_sent_by_me");

                    SMSListContent cellContent = new SMSListContent(phoneNumber, contactName, message, timestamp, isSendByMe);
                    Platform.runLater(() -> cell.smsProperty().set(cellContent));
                    topSmsReached = false;
                } else {
                    if (!emptyMessageSet) {
                        // Fill the first empty cell with an alert message
                        Platform.runLater(() -> cell.smsProperty().set(SMS_END_MESSAGE));
                        emptyMessageSet = true;
                    } else {
                        // Fill cell with empty data
                        Platform.runLater(() -> cell.smsProperty().set(emptyContent));
                    }

                    topSmsReached = true;
                }
            }
        }

    }

    /**
     * For each conversations cells, set the content to "loading".
     */
    private void setLoadingSMSList() {
        for (final SMSListKeyOption cell : smsCells) {
            Platform.runLater(() -> cell.smsProperty().set(SMS_LOADING));
        }
    }

    /**
     * Send an SMS to the current selected conversation (phone number).
     * - Get the message from the WritingStateController,
     * - send the message,
     * - then clear the text into the WritingStateController
     * - and scroll down in the SMS list.
     */
    public void sendSMS() {
        String message = WritingStateController.INSTANCE.currentTextProperty().get();
        boolean val = SMSController.INSTANCE.sendSMS(this.phoneNumber, message);
        validationSendSMSCallback.forEach((callback) -> callback.accept(val));
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
        this.smsIndexMin = 0;
    }

    public void upConvIndex() {
        if (this.convIndexMin != 0) {
            // Si c'est déja à 0 on charge pas
            if (this.convIndexMin > convCells.size()) {
                this.convIndexMin -= convCells.size();
            } else {
                this.convIndexMin = 0;
            }

            setLoadingConvList();
            refreshConvList();
        }
    }

    public void downConvIndex() {
        if (!topConvReached) {
            this.convIndexMin += convCells.size();
            setLoadingConvList();
            refreshConvList();
        }
    }

    public void upSmsIndex() {
        if (!topSmsReached) {
            this.smsIndexMin++;
            refreshSMSList();
        }
    }

    public void downSmsIndex() {
        if (this.smsIndexMin > 0) {
            this.smsIndexMin--;
            refreshSMSList();
        }
    }

    public void addCallEnterCallback(final Runnable callback) {
        this.callEnterCallback = callback;
    }

    public void addCallEndedCallback(final Runnable callback) {
        this.callEndedCallback = callback;
    }

    public void addUnreadCountUpdateCallback(Consumer<Integer> unreadCountUpdatedCallback) {
        this.unreadCountUpdateCallback.add(unreadCountUpdatedCallback);
    }

    public void removeUnreadCountUpdateCallback(Consumer<Integer> unreadCountUpdatedCallback) {
        this.unreadCountUpdateCallback.remove(unreadCountUpdatedCallback);
    }

    public void addValidationSendSMSCallback(Consumer<Boolean> validationSendCallback) {
        this.validationSendSMSCallback.add(validationSendCallback);
    }

    public void removeValidationSendSMSCallback(Consumer<Boolean> validationSendCallback) {
        this.validationSendSMSCallback.remove(validationSendCallback);
    }

    /**
     * Select a conversation
     * @param phoneNumber the phone number of the conversation
     * @param phoneNumberOrContactName the phone number or the contact name of the conversation
     * @param call if selectConv is called to make a call
     */
    public void selectConv(String phoneNumber, String phoneNumberOrContactName, boolean call) {
        phoneNumber = phoneNumber.replace(" ", "");

        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+33" + phoneNumber.substring(1);
        }

        this.phoneNumber = phoneNumber;
        this.phoneNumberOrContactName = phoneNumberOrContactName;
        UseVariableController.INSTANCE.requestVariablesUpdate();
        this.smsIndexMin = 0;
        if (!call) {
            setLoadingSMSList();
            refreshSMSList();
        }
    }

    /**
     * Unselect the current selected conversation
     */
    public void unselectConv() {
        this.phoneNumber = null;
        this.phoneNumberOrContactName = "--.--.--.--.--";
        UseVariableController.INSTANCE.requestVariablesUpdate();
        setLoadingConvList();
        refreshConvList();
    }
}
