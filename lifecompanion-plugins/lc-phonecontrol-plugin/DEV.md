# `lc-phonecontrol-plugin`
## DEV documentation

This plugin implements a server to connect to an Android phone and control it from LifeCompanion.  
The server is able to work with ADB and Bluetooth.

### JSON schema specification
The plugin and the phone communicate via the server through JSON messages.  
The JSON format is structured to contain metadata about the sender, the type of transmission, subtype, and the data itself. This allows the server to properly interpret what action is required, be it sending an SMS, making a call, or handling volume settings. Below, we outline all possible fields, their meanings, and examples.
```json
{
  "sender": "<pc|phone>",
  "type": "<call|sms|contacts|system>",
  "subtype": "<specific subtype>",
  "data": {
    "<object containing>": "<specific information>"
  }
}
```
- **sender**: Indicates which entity is initiating the transmission.
  - Values: `"pc"` or `"phone"`
- **type**: Represents the type of transmission.
  - Values: `"call"`, `"sms"`, `"contacts"`, `"system"`
- **subtype**: Provides more specific information related to the type.
  - Subtype values vary depending on `type`.
- **data**: Contains a JSON object relevant to the action.

#### Type : **call**
- **Subtypes** :
  1. **"make_call"**
     - Description : Initiates a call to a specified number.
     - Data example :
       ```json
       {
         "phone_number": "+123456789"
       }
       ```
  2. **"hang_up"**
     - Description : Ends an active call.
     - Data example : `{}` (Empty data)
  3. **"numpad_input"**
     - Description : Sends DTMF (Dual-Tone Multi-Frequency) input, used for interacting with automated systems.
     - Data example :
       ```json
       {
         "dtmf": "1234"
       }
       ```
  4. **"call_messagerie"**
     - Description : Calls the voicemail service.
     - Data example : `{}` (Empty data)

#### Type : **sms**
- **Subtypes** :
  1. **"send_sms"**
     - Description: Sends an SMS to a specified recipient.
     - Data example :
       ```json
       {
         "recipient": "+123456789",
         "message": "Hello, how are you?",
         "timestamp": "2024-12-06T14:30:00Z"
       }
       ```
  2. **"receive_sms"**
     - Description : Receives an SMS from a contact.
     - Data example :
       ```json
       {
         "sender": "+123456789",
         "message": "I'm good, thanks!",
         "timestamp": "2024-12-06T14:32:00Z"
       }
       ```
  3. **"get_sms_conversations"**
     - Description : Requests a list of SMS conversations.
     - Data example : `{}` (Empty data)
  4. **"get_conversation_messages"**
     - Description : Gets all messages from a specific conversation.
     - Data example :
       ```json
       {
         "contact_number": "+123456789"
       }
       ```

#### Type : **contacts**
- **Subtypes** :
  1. **"get_contacts"**
     - Description : Requests the contact list from the phone.
     - Data example : `{}` (Empty data)
  2. **"update_contact"**
     - Description : Updates a specific contact.
     - Data example :
       ```json
       {
         "contact_id": "123",
         "name": "John Doe",
         "phone_number": "+987654321"
       }
       ```

#### Type : **system**
- **Subtypes** :
  1. **"adjust_volume"**
     - Description : Adjusts the phone's volume.
     - Data example :
       ```json
       {
         "level": 5,  // Volume level (0-10)
         "mode": "increase"  // Values can be "increase", "decrease", or "set"
       }
       ```
  2. **"connection_status"**
     - Description : Checks if the connection is active.
     - Data example : `{}` (Empty data)

#### Example JSON objects
1. **Sending an SMS**
   ```json
   {
     "sender": "pc",
     "type": "sms",
     "subtype": "send_sms",
     "data": {
       "recipient": "+123456789",
       "message": "Hello, this is a test message",
       "timestamp": "2024-12-06T14:30:00Z"
     }
   }
   ```
2. **Receiving contacts list**
   ```json
   {
     "sender": "phone",
     "type": "contacts",
     "subtype": "get_contacts",
     "data": {}
   }
   ```
3. **Adjusting volume**

   ```json
   {
     "sender": "pc",
     "type": "system",
     "subtype": "adjust_volume",
     "data": {
       "level": 3,
       "mode": "decrease"
     }
   }
   ```
4. **Making a call**
   ```json
   {
     "sender": "pc",
     "type": "call",
     "subtype": "make_call",
     "data": {
       "phone_number": "+123456789"
     }
   }
   ```

#### Validation notes
To ensure robustness, the data must be validated :
- **Field presence** : All fields (`sender`, `type`, `subtype`, `data`) must be present.
- **Correct values** : Values like `sender`, `type`, and `subtype` should be limited to pre-defined values.
- **Data format** : Ensure that phone numbers are in valid international formats, timestamps follow ISO 8601, and volume levels are within 0-10.
