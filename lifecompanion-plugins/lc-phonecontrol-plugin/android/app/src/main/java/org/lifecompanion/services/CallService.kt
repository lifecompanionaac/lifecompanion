package org.lifecompanion.services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import org.lifecompanion.R

class CallService : Service() {

    private lateinit var callChannelName: String
    private val callChannelId = "callChannel"

    override fun onCreate() {
        super.onCreate()
        callChannelName = getString(R.string.call_channel_name)
        Notify.createNotificationChannel(callChannelName, callChannelId, this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, Notify.createNotification(callChannelName, callChannelId, this))
        Log.d("CallService", "Call Service started")

        // Get parameters from the ADB command
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val speaker = intent.getBooleanExtra("speaker", false)
        val stop = intent.getBooleanExtra("stop", false)

        if (phoneNumber!=null) {
            startCall(phoneNumber, speaker)
        } else if (stop) {
            stopCall()
        } else {
            answerCall()
        }

        Log.d("CallService", "Call Service ended")
        return START_NOT_STICKY
    }

    private fun answerCall() {
        val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        if (checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
            == PackageManager.PERMISSION_GRANTED) {
            telecomManager.acceptRingingCall()
        } else {
            Log.d("CallService", "Permission to answer call not granted")
        }
    }

    private fun startCall(phoneNumber: String, speaker: Boolean) {
        val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        if (checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {
            telecomManager.let {
                Log.d("CallService", "Call started with telecomManager")

                val extra = Bundle()
                if (speaker) {
                    Log.d("CallService", "WITH SPEAKERPHONE")
                    extra.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
                }
                val uri = Uri.fromParts("tel", phoneNumber, null)
                it.placeCall(uri, extra)
            }
        } else {
            Log.d("CallService", "Permission to start call not granted")
        }
    }

    private fun stopCall() {
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        if (checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
            == PackageManager.PERMISSION_GRANTED) {
            telecomManager.endCall()
        } else {
            Log.d("CallService", "Permission to end call not granted")
        }
    }

    /**
     * This method is necessary to respect the interface Service, but we don't use it here.
     * it's return always null because clients are not allowed to bind to this service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
