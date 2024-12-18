package org.lifecompanion.plugin.phonecontrol.controller;

import java.util.UUID;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CallController handles all call-related operations.
 */
public enum CallController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(CallController.class);

    /**
     * Initiates a phone call to the specified number.
     *
     * @param phoneNumber The phone number to call.
     */
    public void call(String phoneNumber, boolean speakerOn) {
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "make_call");
            json.put("request_id", uuid);

            JSONObject data = new JSONObject();
            data.put("phone_number", phoneNumber);
            data.put("speaker_on", speakerOn);
            json.put("data", data);

            String success = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);

            LOGGER.info("Initiated call to {}.", phoneNumber);
        } catch (Exception e) {
            LOGGER.error("Error initiating call", e);
        }
    }

    public void callContact() {
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "make_call");
            json.put("request_id", uuid);

            JSONObject data = new JSONObject();
            data.put("phone_number", ConnexionController.INSTANCE.getPhoneNumber());
            data.put("speaker_on", GlobalState.INSTANCE.getPluginProperties().speakerOnProperty().get());
            json.put("data", data);

            String success = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);

            LOGGER.info("Initiated call to {}.", ConnexionController.INSTANCE.getPhoneNumber());
        } catch (Exception e) {
            LOGGER.error("Error initiating call", e);
        }
    }

    public void pickUp(boolean speakerOn) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "pickup");

            JSONObject data = new JSONObject();
            data.put("speaker_on", speakerOn);
            json.put("data", data);

            GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString());

            LOGGER.info("Picked up call.");
        } catch (Exception e) {
            LOGGER.error("Error picking up call", e);
        }
    }

    /**
     * Hangs up the current call.
     */
    public void hangUp() {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "hang_up");
            json.put("data", new JSONObject());

            GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString());

            LOGGER.info("Sent hang-up command.");
        } catch (Exception e) {
            LOGGER.error("Error sending hang-up command", e);
        }
    }

    /**
     * Sends DTMF (Dual-Tone Multi-Frequency) input during a call.
     *
     * @param dtmf The DTMF input sequence.
     */
    public void sendDtmf(String dtmf) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "numpad_input");

            JSONObject data = new JSONObject();
            data.put("dtmf", dtmf);
            json.put("data", data);

            GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString());

            LOGGER.info("Sent DTMF input: {}.", dtmf);
        } catch (Exception e) {
            LOGGER.error("Error sending DTMF input", e);
        }
    }

    public String getCallStatus() {
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "get_call_status");
            json.put("request_id", uuid);
            json.put("data", new JSONObject());

            String status = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);

            LOGGER.info("Requested call status.");

            return status;
        } catch (Exception e) {
            LOGGER.error("Error requesting call status", e);
        }

        return null;
    }
}
