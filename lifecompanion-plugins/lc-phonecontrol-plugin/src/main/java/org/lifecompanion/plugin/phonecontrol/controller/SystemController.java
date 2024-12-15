package org.lifecompanion.plugin.phonecontrol.controller;

import org.lifecompanion.plugin.phonecontrol.server.PhoneCommunicationProtocol;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SystemController handles all system-related operations.
 */
public class SystemController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);
    private final PhoneCommunicationProtocol communicationProtocol;

    /**
     * Constructor to initialize the SystemController with a communication protocol.
     *
     * @param communicationProtocol The protocol used for communication with the phone.
     */
    public SystemController(PhoneCommunicationProtocol communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

    /**
     * Adjusts the phone's volume.
     *
     * @param mode The adjustment mode ("increase" or "decrease").
     */
    public void adjustVolume(String mode) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "system");
            json.put("subtype", "adjust_volume");

            JSONObject data = new JSONObject();
            data.put("mode", mode);
            json.put("data", data);

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Adjusted volume: {}", mode);
        } catch (Exception e) {
            LOGGER.error("Error adjusting volume", e);
        }
    }

    /**
     * Checks the connection status with the phone.
     */
    public void checkConnectionStatus() {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "system");
            json.put("subtype", "connection_status");
            json.put("data", new JSONObject());

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Requested connection status.");
        } catch (Exception e) {
            LOGGER.error("Error requesting connection status", e);
        }
    }
}
