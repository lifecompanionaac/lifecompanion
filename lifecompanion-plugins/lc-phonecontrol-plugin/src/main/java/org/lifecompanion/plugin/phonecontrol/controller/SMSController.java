package org.lifecompanion.plugin.phonecontrol.controller;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;
import org.lifecompanion.controller.textcomponent.WritingStateController;
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
    public int sendSMS(String recipient, String message) {
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "send_sms");
            json.put("request_id", uuid);

            JSONObject data = new JSONObject();
            data.put("recipient", recipient);
            data.put("message", message);
            json.put("data", data);

            String result = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);

            LOGGER.info("SMS sent to {}: {}", recipient, message);

            // TODO (get something meaningful from the result)
            return 0;
        } catch (Exception e) {
            LOGGER.error("Error sending SMS", e);
        }

        return -1;
    }

    public void sendSMS() {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "send_sms");

            JSONObject data = new JSONObject();
            data.put("recipient", ConnexionController.INSTANCE.getPhoneNumber());
            data.put("message", WritingStateController.INSTANCE.currentTextProperty().get());
            json.put("data", data);

            GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString());

            LOGGER.info("SMS sent to {}: {}", ConnexionController.INSTANCE.getPhoneNumber(), WritingStateController.INSTANCE.currentTextProperty().get());
        } catch (Exception e) {
            LOGGER.error("Error sending SMS", e);
        }
    }

    /**
     * Retrieves SMS conversations from the phone.
     */
    public ArrayList<JSONObject> getConvList(int convIndexMin, int convIndexMax) {
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "get_sms_conversations");
            json.put("request_id", uuid);

            JSONObject data = new JSONObject();
            data.put("conv_index_min", convIndexMin);
            data.put("conv_index_max", convIndexMax);
            json.put("data", data);

            String result = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);
            LOGGER.info("Requested SMS conversations.");
            JSONObject resultJson = new JSONObject(result);
            ArrayList<JSONObject> conversations = new ArrayList<>();

            if (resultJson.getJSONArray("data") != null && resultJson.getJSONArray("data").length() >= 0) {
                for (int i = 0; i < resultJson.getJSONArray("data").length(); i++) {
                    conversations.add(resultJson.getJSONArray("data").getJSONObject(i));
                }
            }

            return conversations;
        } catch (Exception e) {
            LOGGER.error("Error requesting SMS conversations", e);
        }

        return null;
    }

    /**
     * Fetches all messages from a specific conversation.
     *
     * @param phoneNumber The phone number or contact identifier.
     */
    public ArrayList<JSONObject> getSMSList(String phoneNumber, int msgIndexMin, int msgIndexMax) {
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "sms");
            json.put("subtype", "get_conversation_messages");
            json.put("request_id", uuid);

            JSONObject data = new JSONObject();
            data.put("contact_number", phoneNumber);
            data.put("msg_index_min", msgIndexMin);
            data.put("msg_index_max", msgIndexMax);
            json.put("data", data);

            String result = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);
            LOGGER.info("Requested messages from conversation with {}.", phoneNumber);

            JSONObject resultJson = new JSONObject(result);
            ArrayList<JSONObject> messages = new ArrayList<>();

            if (resultJson.getJSONArray("data") != null && resultJson.getJSONArray("data").length() >= 0) {
                for (int i = 0; i < resultJson.getJSONArray("data").length(); i++) {
                    messages.add(resultJson.getJSONArray("data").getJSONObject(i));
                }
            }

            return messages;
        } catch (Exception e) {
            LOGGER.error("Error requesting messages from conversation", e);
        }

        return null;
    }
}
