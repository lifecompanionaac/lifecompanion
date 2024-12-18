package org.lifecompanion.plugin.phonecontrol.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    private int smsUnreadCount;
    private String callDuration;
    private String phoneNumberOrContactName;

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
    private Set<Consumer<Integer>> validationSendSMSCallback;

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

    public String getCallDuration() {
        return callDuration;
    }

    public String getPhoneNumberOrContactName() {
        return phoneNumberOrContactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        GlobalState.INSTANCE.setPluginProperties((PhoneControlPluginProperties) configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class));
        this.currentPhoneControlPluginProperties = GlobalState.INSTANCE.getPluginProperties();
        resetVariables();

        // Start app on phone if needed
        String deviceSerialNumber = currentPhoneControlPluginProperties.deviceProperty().get();
        PhoneCommunicationManager.INSTANCE.startApp(deviceSerialNumber);

        // Set up the interval of refresh
        this.durationInterval = currentPhoneControlPluginProperties.durationInternalProperty().get();
        this.refreshTimer = new Timer(true);
        this.refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LOGGER.info("Refreshing phone control plugin");
                refreshCallStatus();
                refreshConvListExec();

                // Refresh only if we are not in call, if we have a selected conversation and if we are at the top of the list
                if (phoneNumber != null && !onCall && smsIndexMin == 0) {
                    refreshSMSList();
                }
            }
        }, 500, durationInterval);

        // Lists KeyOptions
        // Find every conv list cells
        Map<GridComponentI, List<ConversationListKeyOption>> convKeys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(ConversationListKeyOption.class, configuration, convKeys, null);
        convKeys.values().stream().flatMap(List::stream).distinct().forEach(convCells::add);
        setLoadingConvList();
        refreshConvList();  // First load of conversations

        // Find every sms list cells
        Map<GridComponentI, List<SMSListKeyOption>> smsKeys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(SMSListKeyOption.class, configuration, smsKeys, null);
        smsKeys.values().stream().flatMap(List::stream).distinct().forEach(smsCells::add);
        setLoadingSMSList();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        GlobalState.INSTANCE.setPluginProperties(null);
        this.currentPhoneControlPluginProperties = null;
        resetVariables();

        // Removing callbacks
        this.callEnterCallback = null;
        this.callEndedCallback = null;

        // Stopping timer
        this.refreshTimer.cancel();
        this.refreshTimer = null;

        // Lists KeyOptions
        convCells.clear();
        smsCells.clear();
    }

    private void resetVariables() {
        this.smsUnreadCount = 0;
        this.callDuration = "00:00:00";
        this.phoneNumberOrContactName = "--.--.--.--.--";
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
            inputFolder = "/adb/platform-tools-latest-win.zip";
            adbFileName += ".exe";
        } else if (systemType == SystemType.UNIX) {
            inputFolder = "/adb/platform-tools-latest-linux.zip";
        } else {
            LOGGER.error("Unsupported system type");

            return null;
        }

        File adbZip = new File(dataDirectory + File.separator + "platform-tools.zip");
        File adbFolder = new File(dataDirectory + File.separator + "platform-tools");

        if (adbFolder.exists()) {
            try {
                LOGGER.info("ADB folder exists, trying to get the latest version.");
                String url = systemType == SystemType.WINDOWS ? 
                    "https://dl.google.com/android/repository/platform-tools-latest-windows.zip" : 
                    "https://dl.google.com/android/repository/platform-tools-latest-linux.zip";

                try (InputStream in = URI.create(url).toURL().openStream()) {
                    Files.copy(in, adbZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                IOUtils.unzipInto(adbZip, adbFolder, null);
                adbZip.delete();
            } catch (Exception e) {
                LOGGER.error("Failed to download the latest ADB version.", e);
            }
        } else {
            installAdbFromInputFolder(inputFolder, adbZip, adbFolder);
        }

        File adb = new File(dataDirectory + File.separator + "platform-tools" + File.separator + adbFileName);
        adb.setExecutable(true);
        
        return adb;
    }

    private void installAdbFromInputFolder(String inputFolder, File adbZip, File adbFolder) {
        try {
            if (inputFolder != null) {
                LOGGER.info("Installing ADB from input folder.");
                InputStream is = PhoneControlPlugin.class.getResourceAsStream(inputFolder);
                FileOutputStream fos = new FileOutputStream(adbZip);
                IOUtils.copyStream(is, fos);
                IOUtils.unzipInto(adbZip, adbFolder, null);
                adbZip.delete();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to install ADB from input folder.", e);
        }
    }

    /**
     * Refresh the call status and do actions if needed. <br>
     * - Incoming call : launch the event "OnCallEnter" <br>
     * - No call but "onCall" isn't update : update "onCall" and launch the event "OnCallEnded" <br>
     */
    public void refreshCallStatus() {
        String callStateOrPhoneNumber = CallController.INSTANCE.getCallStatus();

        if (callStateOrPhoneNumber.equals("0")) {  // No call on the phone
            if (this.onCall) {
                this.onCall = false;
                callEndedCallback.run();
            }
        } else if (callStateOrPhoneNumber.equals("1")) {  // Current call on the phone
            this.onCall = true;
        } else {  // Incoming call
            this.onCall = true;
            this.phoneNumberOrContactName = callStateOrPhoneNumber;
            UseVariableController.INSTANCE.requestVariablesUpdate();  // Update variables (to show the phone number)
            callEnterCallback.run();  // Launch event "OnCallEnter"
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
            int duration = 0;

            while (onCall) {
                try {
                    Thread.sleep(1000);
                    duration++;
                    this.callDuration = convertTime(duration);
                    UseVariableController.INSTANCE.requestVariablesUpdate();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Call duration monitoring interrupted", e);
                } catch (Exception e) {
                    LOGGER.error("error while monitoring call duration");
                }
            }

            this.callDuration = "00:00:00";
        });
    }

    /**
     * Call the current selected conversation (phone number)
     */
    public void callContact() {
        boolean speakerOn = currentPhoneControlPluginProperties.speakerOnProperty().get();
        CallController.INSTANCE.call(this.phoneNumber, speakerOn);
        this.onCall = true;
        this.timer();
    }

    /**
     * Pick up the phone and update "onCall" to true
     */
    public void pickUp() {
        boolean speakerOn = currentPhoneControlPluginProperties.speakerOnProperty().get();
        CallController.INSTANCE.pickUp(speakerOn);
        this.onCall = true;
        this.timer();
    }

    /**
     * Hang up the phone :
     * - update "onCall" to false
     * - launch the event "OnCallEnded"
     */
    public void hangUp() {
        CallController.INSTANCE.hangUp();
        this.onCall = false;
        callEndedCallback.run();
    }

    /** To handle if the list is already refreshing */
    private boolean convIsRefreshing = false;

    /**
     * Refresh the content of conversations cells. <br>
     * If the list is already refreshing, don't refresh.
     */
    public void refreshConvList() {
        if (!convIsRefreshing) {
            convIsRefreshing = true;
            refreshConvListExec();
            convIsRefreshing = false;
        }
    }

    /**
     * Refresh the content of conversations cells. <br>
     * - Only one execution at a time
     */
    private void refreshConvListExec() {
        LOGGER.info("Loading conversations...");

        int convIndexMax = convIndexMin + convCells.size();
        int unreadConvCount = 0;

        // Get conv
        ArrayList<String> convStr = SMSController.INSTANCE.getConvList(convIndexMin, convIndexMax);
        ConversationListContent emptyContent = new ConversationListContent(null, null, null, true, false);
        boolean emptyConvSet = false;  // To detect the first empty conv

        if (convStr.isEmpty()) {
            for (final ConversationListKeyOption cell : convCells) {
                Platform.runLater(() -> cell.convProperty().set(emptyContent));
            }
        } else {
            // Analyse each convStrings
            for (int i = 0; i < convCells.size(); i++) {
                ConversationListKeyOption cell = this.convCells.get(i);  // Get the cell

                if (i < convStr.size()) {
                    String[] convData = convStr.get(i).split("\\|");  // Split data
                    byte[] decodedBytes = Base64.getDecoder().decode(convData[2]);  // Get message body
                    String message = new String(decodedBytes);
                    boolean isSeen = Boolean.parseBoolean(convData[3]);
                    boolean isSendByMe = Boolean.parseBoolean(convData[4]);

                    if (!isSeen) {
                        unreadConvCount++;
                    }

                    ConversationListContent cellContent = new ConversationListContent(convData[0], convData[1], message, isSeen, isSendByMe);
                    Platform.runLater(() -> cell.convProperty().set(cellContent));
                    topConvReached = false;
                } else {
                    if (!emptyConvSet) {  // Fill the first empty cell with an alert message
                        Platform.runLater(() -> cell.convProperty().set(CONV_END_MESSAGE));
                        emptyConvSet = true;
                    } else {  // Fill cell with empty data
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

        LOGGER.info("{} Conversations loaded", convStr.size());
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
        LOGGER.info("Loading sms...");
        int smsIndexMax = smsIndexMin + smsCells.size();

        // Get conv
        ArrayList<String> smsStr = SMSController.INSTANCE.getSMSList(this.phoneNumber, smsIndexMin, smsIndexMax);
        SMSListContent emptyContent = new SMSListContent(null, null, null, null, false);
        boolean emptyMessageSet = false;  // To detect the first empty message

        if (smsStr.isEmpty()) {
            for (final SMSListKeyOption cell : smsCells) {
                Platform.runLater(() -> cell.smsProperty().set(emptyContent));
            }
        } else {
            int smsStrSize = smsStr.size();

            for (int i = 0; i < smsCells.size(); i++) {
                SMSListKeyOption cell = this.smsCells.get(smsCells.size() - 1 - i);  // Start from the end of smsCells

                if (i < smsStrSize) {
                    String[] smsData = smsStr.get(i).split("\\|");  // Split data
                    String message = new String(Base64.getDecoder().decode(smsData[2]));
                    boolean sendByMe = Boolean.parseBoolean(smsData[4]);

                    SMSListContent cellContent = new SMSListContent(smsData[0], smsData[1], message, smsData[3], sendByMe);
                    Platform.runLater(() -> cell.smsProperty().set(cellContent));
                    topSmsReached = false;
                } else {
                    if (!emptyMessageSet) {  // Fill the first empty cell with an alert message
                        Platform.runLater(() -> cell.smsProperty().set(SMS_END_MESSAGE));
                        emptyMessageSet = true;
                    } else {  // Fill cell with empty data
                        Platform.runLater(() -> cell.smsProperty().set(emptyContent));
                    }

                    topSmsReached = true;
                }
            }
        }

        LOGGER.info("{} SMS loaded", smsStr.size());
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
     * Send an SMS to the current selected conversation (phone number). <br>
     * - Get the message from the WritingStateController, <br>
     * - send the message, <br>
     * - then clear the text into the WritingStateController <br>
     * - and scroll down in the SMS list.
     */
    public void sendSMS() {
        String message = WritingStateController.INSTANCE.currentTextProperty().get();
        int val = SMSController.INSTANCE.sendSMS(this.phoneNumber, message);
        validationSendSMSCallback.forEach((callback) -> callback.accept(val));
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
        this.smsIndexMin = 0;
    }

    public void upConvIndex() {
        if (this.convIndexMin != 0) {  // Si c'est déja à 0 on charge pas
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

    public void addValidationSendSMSCallback(Consumer<Integer> validationSendCallback) {
        this.validationSendSMSCallback.add(validationSendCallback);
    }

    public void removeValidationSendSMSCallback(Consumer<Integer> validationSendCallback) {
        this.validationSendSMSCallback.remove(validationSendCallback);
    }

    /**
     * Select a conversation
     * @param phoneNumber the phone number of the conversation
     * @param phoneNumberOrContactName the phone number or the contact name of the conversation
     */
    public void selectConv(String phoneNumber, String phoneNumberOrContactName) {
        phoneNumber = phoneNumber.replace(" ", "");

        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+33" + phoneNumber.substring(1);
        }

        this.phoneNumber = phoneNumber;
        this.phoneNumberOrContactName = phoneNumberOrContactName;
        UseVariableController.INSTANCE.requestVariablesUpdate();
        this.smsIndexMin = 0;
        setLoadingSMSList();
        refreshSMSList();
    }

    /**
     * Unselect the current selected conversation
     */
    public void unselectConv() {
        LOGGER.info("Unselecting conversation");
        this.phoneNumber = null;
        this.phoneNumberOrContactName = "--.--.--.--.--";
        UseVariableController.INSTANCE.requestVariablesUpdate();
        setLoadingConvList();
        refreshConvList();
    }
}
