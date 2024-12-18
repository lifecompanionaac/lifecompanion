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
import android.util.Log
import org.json.JSONObject

class CallService : Service() {

    companion object {
        private const val TAG = "CallService"
    }

    private var currentCall: Call? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val jsonString = intent.getStringExtra("json") ?: return START_NOT_STICKY
        val json = JSONObject(jsonString)
        val subtype = json.optString("subtype")
        val data = json.optJSONObject("data")

        if (data == null) {
            Log.e(TAG, "No data provided for call subtype: $subtype")
            return START_NOT_STICKY
        }

        when (subtype) {
            "make_call" -> startCall(data.optString("phone_number"), data.optBoolean("speaker", false))
            "hang_up" -> endCall()
            "numpad_input" -> sendDtmf(data.optString("dtmf"))
            else -> Log.e(TAG, "Unknown call subtype: $subtype")
        }

        return START_NOT_STICKY
    }

    private fun startCall(phoneNumber: String, speaker: Boolean) {
        JSONProcessingService().setCallActive(true)
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
        JSONProcessingService().setCallActive(false)
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
