package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Intent
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class JSONProcessingService : Service() {
    companion object {
        private const val TAG = "LC-JSONProcessingService"
        private const val CHANNEL_ID = "LC_JSON_PROCESSING_CHANNEL"
        private const val NOTIFICATION_ID = 1
    }

    private val outputDirPath: String by lazy { File(filesDir, "output").absolutePath }

    private val intentChannel = Channel<Intent>(Channel.UNLIMITED)
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()

        val outputDir = File(outputDirPath)

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // Start foreground service with a notification
        Notify.createNotificationChannel(
            name = "JSON Processing Channel", 
            channelId = CHANNEL_ID, 
            context = this
        )

        val notification = Notify.createNotification(
            title = "JSON Processing Service Running",
            channelId = CHANNEL_ID,
            context = this
        )
        startForeground(NOTIFICATION_ID, notification)

        // Launch a coroutine to process intents from the channel
        serviceScope.launch {
            processQueue()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.let {
            serviceScope.launch {
                intentChannel.send(it)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        serviceJob.cancel()  // Cancel all running coroutines
    }

    private suspend fun processQueue() {
        for (intent in intentChannel) {
            val encodedData = intent.getStringExtra("extra_data")

            if (encodedData != null) {
                val data = String(android.util.Base64.decode(encodedData, android.util.Base64.DEFAULT))
                processJsonData(data)
            }
        }
    }

    private fun processJsonData(data: String) {
        try {
            val json = JSONObject(data)

            if (!validateJson(json)) {
                Log.e(TAG, "Invalid JSON data : $data")

                return
            }

            routeJsonToService(json)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing data", e)
        }
    }

    private fun validateJson(json: JSONObject): Boolean {
        return json.has("sender") && json.has("type") && json.has("subtype") && json.has("data")
    }

    private fun routeJsonToService(json: JSONObject) {
        when (json.optString("type")) {
            "call" -> startService(Intent(this, CallService::class.java).apply { putExtra("json", json.toString()) })
            "sms" -> startService(Intent(this, SMSService::class.java).apply { putExtra("json", json.toString()) })
            "system" -> startService(Intent(this, SystemService::class.java).apply { putExtra("json", json.toString()) })

            else -> Log.e(TAG, "Unknown type ${json.optString("type")}")
        }
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
}
