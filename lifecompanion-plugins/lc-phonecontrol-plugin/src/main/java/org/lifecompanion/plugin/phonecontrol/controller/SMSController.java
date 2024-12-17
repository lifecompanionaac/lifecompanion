package org.lifecompanion.plugin.phonecontrol.controller;

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

    /**
     * Retrieves SMS conversations from the phone.
     */
    public void getConversations() {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "get_sms_conversations");
            json.put("data", new JSONObject());

            String jsonWithTimestamp = GlobalState.INSTANCE.getCommunicationProtocol().addTimestamp(json.toString());
            GlobalState.INSTANCE.getCommunicationProtocol().send(jsonWithTimestamp);

            LOGGER.info("Requested SMS conversations.");
        } catch (Exception e) {
            LOGGER.error("Error requesting SMS conversations", e);
        }
    }

    /**
     * Fetches all messages from a specific conversation.
     *
     * @param contactNumber The phone number or contact identifier.
     */
    public void getMessagesFromConversation(String contactNumber) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "get_conversation_messages");

            JSONObject data = new JSONObject();
            data.put("contact_number", contactNumber);
            json.put("data", data);

            String jsonWithTimestamp = GlobalState.INSTANCE.getCommunicationProtocol().addTimestamp(json.toString());
            GlobalState.INSTANCE.getCommunicationProtocol().send(jsonWithTimestamp);

            LOGGER.info("Requested messages from conversation with {}.", contactNumber);
        } catch (Exception e) {
            LOGGER.error("Error requesting messages from conversation", e);
        }
    }
}
