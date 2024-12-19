package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.TelecomManager
import java.io.File
import java.io.FileOutputStream
import android.util.Log
import org.json.JSONObject
import org.lifecompanion.phonecontrolapp.services.CallStateListener

class CallService : Service(), CallStateListener {

    companion object {
        private const val TAG = "CallService"
    }

    private val outputDirPath: String by lazy { File(filesDir, "output").absolutePath }

    private var isCallActive = false
    private var isCallIncoming = false
    private var currentCall: Call? = null
    private var phoneNumber: String? = null

    private fun setCallActive(active: Boolean) {
        isCallActive = active
        JSONProcessingService().setPollingInterval(if (active) 100 else 1000)
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

        CallWatcher.callStateListener = this

        when (subtype) {
            "make_call" -> startCall(data.optString("phone_number"), data.optBoolean("speaker", false))
            "hang_up" -> endCall()
            "numpad_input" -> sendDtmf(data.optString("dtmf"))
            "get_call_status" -> getCallStatus(requestId)
            else -> Log.e(TAG, "Unknown call subtype: $subtype")
        }

        return START_NOT_STICKY
    }

    override fun onCallStateChanged(call: Call?, isIncoming: Boolean, isActive: Boolean, phoneNumber: String?) {
        currentCall = call
        isCallIncoming = isIncoming
        isCallActive = isActive
        this.phoneNumber = phoneNumber

        Log.i(TAG, "Call state updated: isIncoming=$isIncoming, isActive=$isActive")
    }

    private fun startCall(phoneNumber: String, speaker: Boolean) {
        setCallActive(true)
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val bundle = Bundle().apply { putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, speaker) }
        val uri = Uri.fromParts("tel", phoneNumber, null)

        try {
            telecomManager.placeCall(uri, bundle)
            Log.i(TAG, "Call initiated to $phoneNumber with speaker: $speaker")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission to make calls not granted", e)
        }
    }

    private fun endCall() {
        setCallActive(false)
        currentCall?.disconnect()
        Log.i(TAG, "Call ended")
    }

    private fun sendDtmf(dtmf: String) {
        if (currentCall == null) {
            Log.e(TAG, "No active call to send DTMF tones")
            return
        }

        try {
            for (tone in dtmf) {
                val dtmfTone = tone.toString()[0]
                currentCall?.playDtmfTone(dtmfTone)
                Thread.sleep(500) // Pause between tones to ensure proper transmission
                currentCall?.stopDtmfTone()
            }
            Log.i(TAG, "DTMF tones sent: $dtmf")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending DTMF tones", e)
        }
    }

    private fun getCallStatus(requestId: String) {
        val callStatus = if (isCallActive) "active" else "inactive"
        var incomingCallStatus = if (isCallIncoming) "incoming" else "none"
        val status = JSONObject().apply {
            put("call_status", callStatus)
            put("incoming_call_status", incomingCallStatus)
            if (isCallIncoming) put("phone_number", phoneNumber)
        }
        val response = JSONObject().apply {
            put("sender", "phone")
            put("type", "call")
            put("subtype", "get_call_status")
            put("request_id", requestId)
            put("data", status)
        }

        writeResponse(requestId, response)
    }

    private fun writeResponse(requestId: String, responseData: JSONObject) {
        val outputDir = File(outputDirPath)
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
