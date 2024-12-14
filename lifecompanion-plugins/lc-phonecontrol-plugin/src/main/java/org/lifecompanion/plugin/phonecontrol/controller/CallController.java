package org.lifecompanion.plugin.phonecontrol.controller;

import org.lifecompanion.plugin.phonecontrol.server.PhoneCommunicationProtocol;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CallController handles all call-related operations.
 */
public class CallController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallController.class);
    private final PhoneCommunicationProtocol communicationProtocol;

    /**
     * Constructor to initialize the CallController with a communication protocol.
     *
     * @param communicationProtocol The protocol used for communication with the phone.
     */
    public CallController(PhoneCommunicationProtocol communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

    /**
     * Initiates a phone call to the specified number.
     *
     * @param phoneNumber The phone number to call.
     */
    public void makeCall(String phoneNumber) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "make_call");

            JSONObject data = new JSONObject();
            data.put("phone_number", phoneNumber);
            json.put("data", data);

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Initiated call to {}.", phoneNumber);
        } catch (Exception e) {
            LOGGER.error("Error initiating call", e);
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

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Sent hang-up command.");
        } catch (Exception e) {
            LOGGER.error("Error sending hang-up command", e);
        }
    }

    /**
     * Calls the voicemail service.
     */
    public void callVoicemail() {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "call");
            json.put("subtype", "call_messagerie");
            json.put("data", new JSONObject());

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Called voicemail service.");
        } catch (Exception e) {
            LOGGER.error("Error calling voicemail", e);
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

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Sent DTMF input: {}.", dtmf);
        } catch (Exception e) {
            LOGGER.error("Error sending DTMF input", e);
        }
    }
}
