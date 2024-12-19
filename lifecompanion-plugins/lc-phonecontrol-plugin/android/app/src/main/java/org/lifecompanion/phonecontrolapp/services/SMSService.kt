package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.Build
import android.provider.Telephony
import android.provider.ContactsContract
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
            "get_sms_conversations" -> getSMSConversations(data, requestId)
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

    private fun getSMSConversations(data: JSONObject, requestId: String?) {
        val resolver: ContentResolver = contentResolver
        val uri = Uri.parse("content://sms")
        val projection = arrayOf("address", "body", "date", "read", "type")
        val cursor = resolver.query(uri, projection, null, null, "date DESC")
    
        val conversations = JSONArray()
        val uniqueContacts = mutableSetOf<String>()
    
        val convIndexMin = data.optInt("conv_index_min", 0)
        val convIndexMax = data.optInt("conv_index_max", Int.MAX_VALUE)
    
        cursor?.use {
            var index = 0

            while (it.moveToNext()) {
                if (index < convIndexMin) {
                    index++

                    continue
                }

                if (index > convIndexMax) {
                    break
                }
    
                val address = it.getString(it.getColumnIndexOrThrow("address"))
                val contactName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address))
                    val projection2 = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    val cursor2 = contentResolver.query(contactUri, projection2, null, null, null)
                    val name = if (cursor2 != null && cursor2.moveToFirst()) {
                        cursor2.getString(cursor2.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                    } else {
                        address
                    }
                    cursor2?.close()
                    name
                } else {
                    address
                }
    
                if (uniqueContacts.add(address)) {
                    val body = it.getString(it.getColumnIndexOrThrow("body"))
                    val dateMillis = it.getLong(it.getColumnIndexOrThrow("date"))
                    val read = it.getInt(it.getColumnIndexOrThrow("read")) == 1
                    val type = it.getInt(it.getColumnIndexOrThrow("type"))
                    val isSentByMe = type == 2
    
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date(dateMillis))
                    val conversation = JSONObject().apply {
                        put("phone_number", address)
                        put("contact_name", contactName)
                        put("last_message", Base64.encodeToString(body.toByteArray(), Base64.NO_WRAP))
                        put("timestamp", date)
                        put("is_read", read)
                        put("is_sent_by_me", isSentByMe)
                    }
                    conversations.put(conversation)
                }

                index++
            }
        }

        Log.i(TAG, "Retrieved ${conversations.length()} conversations")

        if (requestId != null) {
            val response = JSONObject().apply {
                put("sender", "phone")
                put("type", "sms")
                put("subtype", "get_sms_conversations")
                put("request_id", requestId)
                put("data", conversations)
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
        val msgIndexMin = data.optInt("msg_index_min", 0)
        val msgIndexMax = data.optInt("msg_index_max", Int.MAX_VALUE)

        cursor?.use {
            var index = 0
            while (it.moveToNext()) {
                if (index < msgIndexMin) {
                    index++
                    continue
                }
                if (index > msgIndexMax) break
    
                val address = it.getString(it.getColumnIndexOrThrow("address"))
                val body = it.getString(it.getColumnIndexOrThrow("body"))
                val dateMillis = it.getLong(it.getColumnIndexOrThrow("date"))
                val read = it.getInt(it.getColumnIndexOrThrow("read")) == 1
                val type = it.getInt(it.getColumnIndexOrThrow("type"))
                val isSentByMe = type == Telephony.Sms.MESSAGE_TYPE_SENT

                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date(dateMillis))
                val contactName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address))
                    val projection2 = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    val cursor2 = contentResolver.query(contactUri, projection2, null, null, null)
                    val name = if (cursor2 != null && cursor2.moveToFirst()) {
                        cursor2.getString(cursor2.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                    } else {
                        address
                    }
                    cursor2?.close()
                    name
                } else {
                    address
                }

                val message = JSONObject().apply {
                    put("phone_number", address)
                    put("contact_name", contactName)
                    put("message", Base64.encodeToString(body.toByteArray(), Base64.NO_WRAP))
                    put("timestamp", date)
                    put("is_read", read)
                    put("is_sent_by_me", isSentByMe)
                }
                messages.put(message)
    
                index++
            }
        }

        Log.i(TAG, "Retrieved ${messages.length()} messages for contact: $contactNumber")

        if (requestId != null) {
            val response = JSONObject().apply {
                put("sender", "phone")
                put("type", "sms")
                put("subtype", "get_conversation_messages")
                put("request_id", requestId)
                put("data", messages)
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
