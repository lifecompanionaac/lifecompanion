package org.lifecompanion.plugin.phonecontrol.server;

public interface PhoneCommunicationProtocol {
    // The purpose of this interface is to be then implemented by AdbCommunicationProtocol and BluetoothCommunicationProtocol
    // The "server" only role is to pass data from the plugin to the phone, and vice versa
    // The data is a JSON object, and the plugin/android app is responsible for the serialization/deserialization of the data

    // Send a JSON object to the phone
    void send(String data);

    // Receive a JSON object from the phone
    String receive();

    // Close the connection
    void close();

    // Check if the connection is open
    boolean isOpen();

    // Check if the JSON object is valid
    boolean isValid(String data);
}
