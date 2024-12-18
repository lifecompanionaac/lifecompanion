package org.lifecompanion.plugin.phonecontrol.controller;

import java.util.UUID;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SystemController handles all system-related operations.
 * It is also the main controller to instanciate the communication protocol.
 */
public enum SystemController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

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

            GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString());

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
            String uuid = UUID.randomUUID().toString();
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "system");
            json.put("subtype", "connection_status");
            json.put("request_id", uuid);
            json.put("data", new JSONObject());

            String status = GlobalState.INSTANCE.getCommunicationProtocol().send(json.toString(), uuid);

            LOGGER.info("Requested connection status.");
        } catch (Exception e) {
            LOGGER.error("Error requesting connection status", e);
        }
    }
}
