package org.lifecompanion.plugin.phonecontrol.server;

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
    boolean isValid(String data);
}
