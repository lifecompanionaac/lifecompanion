package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Intent
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class JSONProcessingService : Service() {
    companion object {
        private const val TAG = "JSONProcessingService"
        private const val CHANNEL_ID = "json_service_channel"
        private const val NOTIFICATION_ID = 1
    }

    private val outputDirPath: String by lazy { File(filesDir, "output").absolutePath }

    override fun onCreate() {
        super.onCreate()

        val outputDir = File(outputDirPath)
        if (!outputDir.exists()) outputDir.mkdirs()

        // Start foreground service with a notification
        val notification = Notify.createNotification(
            title = "JSON Processing Service Running",
            channelId = CHANNEL_ID,
            context = this
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.getStringExtra("extra_data")?.let { encodedData ->
            val data = String(android.util.Base64.decode(encodedData, android.util.Base64.DEFAULT))
            processJsonData(data)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun processJsonData(data: String) {
        Log.i(TAG, "Processing data: $data")
        try {
            val json = JSONObject(data)
            Log.i(TAG, "Processing JSON: $json")

            if (!validateJson(json)) {
                Log.e(TAG, "Invalid JSON data")

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
            else -> Log.e(TAG, "Unknown type: ${json.optString("type")}")
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
            Log.i(TAG, "Response written to file: $responseFile")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write response for requestId: $requestId", e)
        }
    }
}
