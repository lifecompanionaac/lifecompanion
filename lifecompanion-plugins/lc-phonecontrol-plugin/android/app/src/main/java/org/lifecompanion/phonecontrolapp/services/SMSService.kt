package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Base64
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SMSService : Service() {

    companion object {
        private const val TAG = "SMSService"
        private const val OUTPUT_DIR = "/data/local/tmp/lifecompanion/phonecontrol/output"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val jsonString = intent.getStringExtra("json") ?: return START_NOT_STICKY
        val json = JSONObject(jsonString)
        val subtype = json.optString("subtype")
        val data = json.optJSONObject("data")
        val requestId = json.optString("request_id")

        if (data == null) {
            Log.e(TAG, "No data provided for call subtype: $subtype")
            return START_NOT_STICKY
        }

        when (subtype) {
            "send_sms" -> sendSMS(data, requestId)
            "receive_sms" -> receiveSMS(data, requestId)
            "get_sms_conversations" -> getSMSConversations(requestId)
            "get_conversation_messages" -> getConversationMessages(data, requestId)
            else -> Log.e(TAG, "Unknown SMS subtype: $subtype")
        }

        return START_NOT_STICKY
    }

    private fun sendSMS(data: JSONObject, requestId: String?) {
        val recipient = data.optString("recipient")
        val message = data.optString("message")

        if (recipient.isEmpty() || message.isEmpty()) {
            Log.e(TAG, "Recipient or message missing for send_sms")
            return
        }

        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(recipient, null, message, null, null)
            Log.i(TAG, "SMS sent to $recipient: $message")

            // Write success response if requestId is provided
            if (requestId != null) {
                val response = JSONObject().apply {
                    put("request_id", requestId)
                    put("status", "success")
                    put("action", "send_sms")
                }
                writeResponse(requestId, response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS to $recipient", e)
        }
    }

    private fun receiveSMS(data: JSONObject, requestId: String?) {
        val sender = data.optString("sender")
        val message = data.optString("message")
        val timestamp = data.optString("timestamp")

        Log.i(TAG, "Received SMS from $sender at $timestamp: $message")

        // Simulate logging the received SMS
        if (requestId != null) {
            val response = JSONObject().apply {
                put("request_id", requestId)
                put("status", "success")
                put("action", "receive_sms")
            }
            writeResponse(requestId, response)
        }
    }

    private fun getSMSConversations(requestId: String?) {
        val resolver: ContentResolver = contentResolver
        val uri = Uri.parse("content://sms")
        val projection = arrayOf("address", "body", "date", "read")
        val cursor = resolver.query(uri, projection, null, null, "date DESC")

        val conversations = JSONArray()
        val uniqueContacts = mutableSetOf<String>()

        cursor?.use {
            while (it.moveToNext()) {
                val address = it.getString(it.getColumnIndexOrThrow("address"))
                if (uniqueContacts.add(address)) {
                    val body = it.getString(it.getColumnIndexOrThrow("body"))
                    val dateMillis = it.getLong(it.getColumnIndexOrThrow("date"))
                    val read = it.getInt(it.getColumnIndexOrThrow("read")) == 1

                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date(dateMillis))
                    val conversation = JSONObject().apply {
                        put("contact", address)
                        put("last_message", body)
                        put("timestamp", date)
                        put("read", read)
                    }
                    conversations.put(conversation)
                }
            }
        }

        Log.i(TAG, "Retrieved ${conversations.length()} conversations")

        // Write response
        if (requestId != null) {
            val response = JSONObject().apply {
                put("request_id", requestId)
                put("status", "success")
                put("action", "get_sms_conversations")
                put("conversations", conversations)
            }
            writeResponse(requestId, response)
        }
    }

    private fun getConversationMessages(data: JSONObject, requestId: String?) {
        val contactNumber = data.optString("contact_number")
        if (contactNumber.isEmpty()) {
            Log.e(TAG, "Contact number missing for get_conversation_messages")
            return
        }

        val resolver: ContentResolver = contentResolver
        val uri = Uri.parse("content://sms")
        val projection = arrayOf("address", "body", "date", "type")
        val selection = "address = ?"
        val selectionArgs = arrayOf(contactNumber)
        val cursor = resolver.query(uri, projection, selection, selectionArgs, "date ASC")

        val messages = JSONArray()

        cursor?.use {
            while (it.moveToNext()) {
                val body = it.getString(it.getColumnIndexOrThrow("body"))
                val dateMillis = it.getLong(it.getColumnIndexOrThrow("date"))
                val type = it.getInt(it.getColumnIndexOrThrow("type"))

                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date(dateMillis))
                val isSent = type == Telephony.Sms.MESSAGE_TYPE_SENT

                val message = JSONObject().apply {
                    put("body", Base64.encodeToString(body.toByteArray(), Base64.NO_WRAP))
                    put("timestamp", date)
                    put("sent", isSent)
                }
                messages.put(message)
            }
        }

        Log.i(TAG, "Retrieved ${messages.length()} messages for contact: $contactNumber")

        // Write response
        if (requestId != null) {
            val response = JSONObject().apply {
                put("request_id", requestId)
                put("status", "success")
                put("action", "get_conversation_messages")
                put("messages", messages)
            }
            writeResponse(requestId, response)
        }
    }

    private fun writeResponse(requestId: String, responseData: JSONObject) {
        val outputDir = File(OUTPUT_DIR)
        if (!outputDir.exists()) outputDir.mkdirs()

        val responseFile = File(outputDir, "$requestId.json")
        try {
            FileOutputStream(responseFile).use { it.write(responseData.toString().toByteArray()) }
            Log.i(TAG, "Response written to file: $responseFile")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write response for requestId: $requestId", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
