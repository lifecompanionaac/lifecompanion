package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import org.json.JSONObject

class CallService : Service() {
    companion object {
        private const val TAG = "LC-CallService"
    }

    private val outputDirPath: String by lazy { File(filesDir, "output").absolutePath }
    private lateinit var telephonyManager: TelephonyManager

    private var isCallActive = false
    private var isCallIncoming = false
    private var phoneNumber: String? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val jsonString = intent.getStringExtra("json") ?: return START_NOT_STICKY

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        val json = JSONObject(jsonString)
        val subtype = json.optString("subtype")
        val data = json.optJSONObject("data")
        val requestId = json.optString("request_id")

        if (data == null) {
            Log.e(TAG, "No data provided for call subtype $subtype")

            return START_NOT_STICKY
        }

        when (subtype) {
            "make_call" -> startCall(data.optString("phone_number"), data.optBoolean("speaker_on", false))
            "hang_up" -> endCall()
            "numpad_input" -> sendDtmf(data.optString("dtmf"))
            "get_call_status" -> getCallStatus(requestId)

            else -> Log.e(TAG, "Unknown call subtype $subtype")
        }

        return START_NOT_STICKY
    }

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    isCallIncoming = true
                    isCallActive = false
                    this@CallService.phoneNumber = phoneNumber
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    isCallActive = true
                    isCallIncoming = false
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    isCallActive = false
                    isCallIncoming = false
                    this@CallService.phoneNumber = null
                }
            }
        }
    }

    private fun startCall(phoneNumber: String, speaker_on: Boolean) {
        this.isCallActive = true
        this.phoneNumber = phoneNumber

        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val bundle = Bundle().apply { putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, speaker_on) }
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setMode(android.media.AudioManager.MODE_IN_CALL)

        var speakerDevice: AudioDeviceInfo? = null
        val devices = audioManager.availableCommunicationDevices

        for (device in devices) {
            if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                speakerDevice = device

                break
            }
        }

        speakerDevice?.let {
            if (speaker_on) {
                audioManager.setCommunicationDevice(it)
            } else {
                audioManager.clearCommunicationDevice()
            }
        }

        val uri = Uri.fromParts("tel", phoneNumber, null)

        try {
            telecomManager.placeCall(uri, bundle)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission to make calls not granted", e)
            isCallActive = false
            this.phoneNumber = null
        }
    }

    private fun endCall() {
        this.isCallActive = false
        this.phoneNumber = null

        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }

    private fun sendDtmf(dtmf: String) {
        val intent = Intent(this, DTMFAccessibilityService::class.java)
        startService(intent)

        DTMFAccessibilityServiceSingleton.instance?.pressKeypadButton(dtmf)
    }

    private fun getCallStatus(requestId: String) {
        val callStatus = if (isCallActive) {
            "active" 
        } else {
            "inactive"
        }

        var incomingCallStatus = if (isCallIncoming) {
            "incoming" 
        } else {
            "none"
        }

        val status = JSONObject().apply {
            put("call_status", callStatus)
            put("incoming_call_status", incomingCallStatus)

            if (isCallIncoming) {
                put("phone_number", phoneNumber)
            }
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

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val responseFile = File(outputDir, "$requestId.json")

        try {
            FileOutputStream(responseFile).use { it.write(responseData.toString().toByteArray()) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write response for requestId $requestId", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
