package org.lifecompanion.plugin.phonecontrol.server;

import org.json.JSONObject;
import org.json.JSONException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * PhoneCommunicationProtocol is an interface representing the blueprint for different types of communication protocols
 * between the PC plugin and the phone. It defines methods to send and receive data, manage connections, and validate data.
 */
public interface PhoneCommunicationProtocol {

    /**
     * Send a JSON object to the phone.
     * 
     * @param data A JSON-formatted string to be sent to the phone.
     */
    void send(String data);

    /**
     * Receive a JSON object from the phone.
     * 
     * @return A JSON-formatted string received from the phone.
     */
    String receive();

    /**
     * Close the communication connection between the PC and the phone.
     */
    void close();

    /**
     * Check if the communication connection is currently open.
     * 
     * @return True if the connection is open, false otherwise.
     */
    boolean isOpen();

    /**
     * Check if a given JSON object is valid according to the defined communication schema.
     * 
     * @param data A JSON-formatted string to be validated.
     * @return True if the data is valid, false otherwise.
     */
    default public boolean isValid(String data) {
        if (data == null || !data.startsWith("{") || !data.endsWith("}")) {
            return false;
        }

        try {
            JSONObject json = new JSONObject(data);

            // Validate presence of required fields
            if (!json.has("sender") || !json.has("type") || !json.has("subtype") || !json.has("data")) {
                return false;
            }

            // Validate sender field
            String sender = json.getString("sender");

            if (!sender.equals("pc") && !sender.equals("phone")) {
                return false;
            }

            // Validate type field
            String type = json.getString("type");

            if (!type.equals("call") && !type.equals("sms") && !type.equals("contacts") && !type.equals("system")) {
                return false;
            }

            // Validate subtype field based on type
            String subtype = json.getString("subtype");

            switch (type) {
                case "call":
                    if (!subtype.equals("make_call") && !subtype.equals("hang_up") && !subtype.equals("numpad_input") && !subtype.equals("call_messagerie")) {
                        return false;
                    }

                    break;
                case "sms":
                    if (!subtype.equals("send_sms") && !subtype.equals("receive_sms") && !subtype.equals("get_sms_conversations") && !subtype.equals("get_conversation_messages")) {
                        return false;
                    }

                    break;
                case "contacts":
                    if (!subtype.equals("get_contacts") && !subtype.equals("update_contact")) {
                        return false;
                    }

                    break;
                case "system":
                    if (!subtype.equals("adjust_volume") && !subtype.equals("connection_status")) {
                        return false;
                    }

                    break;
                default:
                    return false;
            }

            // Additional validation for specific fields ?
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Adds a timestamp to a given JSON object.
     * 
     * @param data The JSON object to which a timestamp will be added.
     * @return The updated JSON object with a timestamp field added.
     */
    default public String addTimestamp(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
            json.put("timestamp", timestamp);

            return json.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
        }
    }
}
