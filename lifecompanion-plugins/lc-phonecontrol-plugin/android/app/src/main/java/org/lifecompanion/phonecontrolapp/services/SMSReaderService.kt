package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.IBinder
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import android.util.Base64
import org.lifecompanion.phonecontrolapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SMSReaderService : Service() {

    private lateinit var smsChannelName: String
    private val smsChannelId = "smsChannel"
    private lateinit var db: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        smsChannelName = getString(R.string.sms_channel_name)
        db = getSharedPreferences("read_db", MODE_PRIVATE)
        Notify.createNotificationChannel(smsChannelName, smsChannelId, this)
    }

    /**
     * Start when the service is called by the ADB command. <br>
     * Get the parameters from the ADB command and call the right method.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, Notify.createNotification(smsChannelName, smsChannelId, this))
        Log.d("SMSReaderService", "SMSReader Service started")

        // Get parameters from the ADB command
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val start = intent.getIntExtra("start", 0)
        val end = intent.getIntExtra("end", 5)

        // If they are a phoneNumber, we read SMS
        if (phoneNumber != null) {
            getSMS(phoneNumber, start, end)
        } else {
            getConversations(start, end)
        }

        Log.d("SMSReaderService", "SMSReader Service ended")
        return START_NOT_STICKY
    }

    /**
     * Read the SMS from the phoneNumber and log them in the console.
     *
     * @param phoneNumber The phone number of the contact
     * @param start The index of the first message to read
     * @param end The index of the last message to read
     */
    private fun getSMS(phoneNumber: String, start: Int, end: Int) {
        Log.d("SMSReaderService", "Read SMS from $phoneNumber")

        val uri = Uri.parse("content://sms")
        val projection = arrayOf("_id", "address", "body", "date", "type", "read")
        val selection = "address = ? OR address = ?"
        val selectionArgs = arrayOf(phoneNumber, phoneNumber.replace("+33", "0"))
        val limit = start + end
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, "date DESC LIMIT $limit")
        var count = 0
        
        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(Telephony.Sms._ID)
            val addressIndex = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndexOrThrow(Telephony.Sms.DATE)
            val typeIndex = it.getColumnIndexOrThrow(Telephony.Sms.TYPE)
            val readIndex = it.getColumnIndexOrThrow(Telephony.Sms.READ)

            // Check the first message if it's unread and save to the database
            if (it.moveToFirst()) {
                val firstReadStatus = it.getInt(readIndex)
                if (firstReadStatus == 0) {
                    val firstId = it.getString(idIndex)
                    db.edit().putString(phoneNumber, firstId).apply()
                }
            }
            it.moveToPosition(-1) // Reset cursor position

            while (it.moveToNext() && count < end) {
                if (count >= start) { // We start to log
                    // --------- Get values ---------
                    var address = it.getString(addressIndex) // Phone number
                    val body = it.getString(bodyIndex) // Message body
                    val type = it.getInt(typeIndex) // Message type (Sent=2 or Received=1)
                    val dateMillis = it.getLong(dateIndex)

                    // --------- Convert values ---------
                    if (address.startsWith("0")) {
                        address = "+33" + address.substring(1)
                    }
                    val body64 = encodeToBase64(body)
                    val isSendByMe = if (type == 2) "true" else "false"
                    val date = SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date(dateMillis))
                    val contactName = getContactName(phoneNumber)

                    // --------- Send values (by log) ---------
                    val smsInfo = "$address|$contactName|$body64|$date|$isSendByMe"
                    Log.i("SMSReaderServiceSMS", smsInfo)
                }
                count++
            }
        }
        // Log a guardrail indicating there are not enough messages to send
        Log.i("SMSReaderServiceSMS", "+33000000000")
    }

    /**
     * Read the conversations and log them in the console.
     * Read all sms, and select the start-end unique phone numbers.
     *
     * @param start The index of the first conversation to read
     * @param end The index of the last conversation to read
     */
    private fun getConversations(start: Int, end: Int) {
        Log.d("SMSReaderService", "getConversations called")

        val uri = Uri.parse("content://sms")
        val projection = arrayOf("_id", "address", "body", "read", "type")
        val cursor = contentResolver.query(uri, projection, null, null, "date DESC")
        var count = 0
        val uniqueAddresses = HashSet<String>()

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(Telephony.Sms._ID)
            val addressIndex = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val typeIndex = it.getColumnIndexOrThrow(Telephony.Sms.TYPE)
            val readIndex = it.getColumnIndexOrThrow(Telephony.Sms.READ)

            while (it.moveToNext() && count < end - start) {
                // Get the phoneNumber
                var phoneNumber = it.getString(addressIndex)
                if (phoneNumber.startsWith("0")) {
                    phoneNumber = "+33" + phoneNumber.substring(1)
                }

                // Try to add it, return false is already in list
                val isAdded = uniqueAddresses.add(phoneNumber)

                if (isAdded && uniqueAddresses.size > start) { // We start to log
                    // --------- Get values ---------
                    val body = it.getString(bodyIndex) // Message body
                    val isRead = it.getInt(readIndex) // Message read status (unread=0, read=1)
                    val type = it.getInt(typeIndex) // Message type (Sent=2 or Received=1)

                    // --------- Convert values ----------
                    val isSendByMe = if (type == 2) "true" else "false"
                    var isSeen = if (isRead == 1) "true" else "false"
                    val contactName = getContactName(phoneNumber)
                    val body64 = encodeToBase64(body)

                    // Check if the conversation is marked as read in the db
                    // Update it if they are a new message
                    if (db.contains(phoneNumber)) {
                        val storedId = db.getString(phoneNumber, "")
                        val lastId = it.getString(idIndex)

                        // Compare the message id with the stored id
                        // If the last message is the same, the conversation is seen
                        if (storedId.equals(lastId)) {
                            isSeen = "true"
                        } else { // We clear the stored conversation, it's a new sms
                            db.edit().remove(phoneNumber).apply()
                        }
                    }

                    // --------- Send values (by log) ---------
                    val convInfo = "$phoneNumber|$contactName|$body64|$isSeen|$isSendByMe"
                    Log.i("SMSReaderServiceConv", convInfo)
                    count++
                }
            }
        }
        // Log a guardrail indicating there are not enough messages to send
        Log.i("SMSReaderServiceConv", "+33000000000")
    }

    /**
     * Search if a contact exists for the given phoneNumber.
     *
     * @param phoneNumber The phone number to search
     * @return The contact name if exists, else the phoneNumber
     */
    private fun getContactName(phoneNumber: String): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }
        return phoneNumber
    }

    /**
     * Encode the input string to base64
     * @param input The string to encode
     * @return The encoded string
     */
    private fun encodeToBase64(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val encodedString = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return encodedString
    }

    /**
     * This method is necessary to respect the interface Service, but we don't use it here.
     * it's return always null because clients are not allowed to bind to this service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
