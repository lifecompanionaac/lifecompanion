package org.lifecompanion.plugin.phonecontrol.controller;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SMSController handles all SMS-related operations.
 */
public enum SMSController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSController.class);

    /**
     * Sends an SMS to the specified recipient.
     *
     * @param recipient The phone number of the recipient.
     * @param message   The message content.
     */
    public void sendSMS(String recipient, String message) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "send_sms");

            JSONObject data = new JSONObject();
            data.put("recipient", recipient);
            data.put("message", message);
            json.put("data", data);

            String jsonWithTimestamp = GlobalState.INSTANCE.getCommunicationProtocol().addTimestamp(json.toString());
            GlobalState.INSTANCE.getCommunicationProtocol().send(jsonWithTimestamp);

            LOGGER.info("SMS sent to {}: {}", recipient, message);
        } catch (Exception e) {
            LOGGER.error("Error sending SMS", e);
        }
    }

    public int requestSendSMS() {
        // TODO : callback (poll)
        return 0;
    }

    public void sendSMS() {
        // TODO
        // global selected contact
    }

    /**
     * Retrieves SMS conversations from the phone.
     */
    public void getConvList(int convIndexMin, int convIndexMax) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "get_sms_conversations");
            
            JSONObject data = new JSONObject();
            data.put("conv_index_min", convIndexMin);
            data.put("conv_index_max", convIndexMax);
            json.put("data", data);

            String jsonWithTimestamp = GlobalState.INSTANCE.getCommunicationProtocol().addTimestamp(json.toString());
            GlobalState.INSTANCE.getCommunicationProtocol().send(jsonWithTimestamp);

            LOGGER.info("Requested SMS conversations.");
        } catch (Exception e) {
            LOGGER.error("Error requesting SMS conversations", e);
        }
    }

    public ArrayList<String> requestGetConvList() {
        // TODO : callback (poll)
        return new ArrayList<>();
    }

    /**
     * Fetches all messages from a specific conversation.
     *
     * @param phoneNumber The phone number or contact identifier.
     */
    public void getSMSList(String phoneNumber, int msgIndexMin, int msgIndexMax) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "get_conversation_messages");

            JSONObject data = new JSONObject();
            data.put("contact_number", phoneNumber);
            data.put("msg_index_min", msgIndexMin);
            data.put("msg_index_max", msgIndexMax);
            json.put("data", data);

            String jsonWithTimestamp = GlobalState.INSTANCE.getCommunicationProtocol().addTimestamp(json.toString());
            GlobalState.INSTANCE.getCommunicationProtocol().send(jsonWithTimestamp);

            LOGGER.info("Requested messages from conversation with {}.", phoneNumber);
        } catch (Exception e) {
            LOGGER.error("Error requesting messages from conversation", e);
        }
    }

    public ArrayList<String> requestGetSMSList() {
        // TODO : callback (poll)
        return new ArrayList<>();
    }

    public void addUnreadCountUpdateCallback(Consumer<Integer> unreadCountUpdatedCallback) {
        // TODO
    }

    public void removeUnreadCountUpdateCallback(Consumer<Integer> unreadCountUpdatedCallback) {
        // TODO
    }

    public void addValidationSendSMSCallback(Consumer<Integer> validationSendSMSCallback) {
        // TODO
    }

    public void removeValidationSendSMSCallback(Consumer<Integer> validationSendSMSCallback) {
        // TODO
    }
}
