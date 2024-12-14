package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.util.Base64
import org.lifecompanion.phonecontrolapp.R

class SMSSenderService : Service() {
    private lateinit var smsChannelName: String
    private val smsChannelId = "smsChannel"

    private val sentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                android.app.Activity.RESULT_OK -> Log.i("SmsSenderServiceCallBack", "SMS sent successfully")
                else -> Log.i("SmsSenderServiceCallBack", "SMS not send")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        smsChannelName = getString(R.string.sms_channel_name)
        Notify.createNotificationChannel(smsChannelName, smsChannelId, this)

        // Register broadcast receivers
        registerReceiver(sentReceiver, IntentFilter("SMS_SENT"))
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister broadcast receivers
        unregisterReceiver(sentReceiver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, Notify.createNotification(smsChannelName, smsChannelId, this))
        Log.d("SmsSenderService", "SMSSender Service started")

        // Get parameters from the ADB command
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val message = intent.getStringExtra("message")

        if (!phoneNumber.isNullOrBlank() && !message.isNullOrBlank()) {
            sendSMS(phoneNumber, message)
        }

        Log.d("SmsSenderService", "SMSSender Service ended")

        return START_NOT_STICKY
    }

    private fun sendSMS(phoneNumber: String, messageEncode: String) {
        try {
            val message = String(Base64.decode(messageEncode, Base64.DEFAULT))
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message)
            val numParts = parts.size

            val sentIntents = ArrayList<PendingIntent?>()

            for (i in 0 until numParts) {
                val sentIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent("SMS_SENT"),
                    PendingIntent.FLAG_IMMUTABLE
                )
                sentIntents.add(sentIntent)
            }

            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, null)
            Log.d("SmsSenderService", "SMS sent to $phoneNumber: $message")
        } catch (e: Exception) {
            Log.e("SmsSenderService", "Error sending SMS: ${e.message}")
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
