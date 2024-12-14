package org.lifecompanion.plugin.phonecontrol.controller;

import org.lifecompanion.plugin.phonecontrol.server.PhoneCommunicationProtocol;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContactsController handles all contact-related operations.
 */
public class ContactsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactsController.class);
    private final PhoneCommunicationProtocol communicationProtocol;

    /**
     * Constructor to initialize the ContactsController with a communication protocol.
     *
     * @param communicationProtocol The protocol used for communication with the phone.
     */
    public ContactsController(PhoneCommunicationProtocol communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

    /**
     * Requests the contact list from the phone.
     */
    public void getContacts() {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "contacts");
            json.put("subtype", "get_contacts");
            json.put("data", new JSONObject());

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Requested contact list from the phone.");
        } catch (Exception e) {
            LOGGER.error("Error requesting contact list", e);
        }
    }

    /**
     * Updates a specific contact on the phone.
     *
     * @param contactName  The name of the contact.
     * @param phoneNumber  The phone number of the contact.
     */
    public void updateContact(String contactName, String phoneNumber) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender", "pc");
            json.put("type", "contacts");
            json.put("subtype", "update_contact");

            JSONObject data = new JSONObject();
            data.put("name", contactName);
            data.put("phone_number", phoneNumber);
            json.put("data", data);

            String jsonWithTimestamp = communicationProtocol.addTimestamp(json.toString());
            communicationProtocol.send(jsonWithTimestamp);

            LOGGER.info("Updated contact: {} ({})", contactName, phoneNumber);
        } catch (Exception e) {
            LOGGER.error("Error updating contact", e);
        }
    }
}
