package org.lifecompanion.plugin.phonecontrol.controller;

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
        } catch (Exception e) {
            LOGGER.error("Error adjusting volume", e);
        }
    }
}
