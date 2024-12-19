package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class SystemService : Service() {

    companion object {
        private const val TAG = "SystemService"
    }

    private val outputDirPath: String by lazy { File(filesDir, "output").absolutePath }

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
            "adjust_volume" -> adjustVolume(data.optString("mode"), data.optString("request_id") ?: null)
            "connection_status" -> checkConnectionStatus()
            else -> Log.e(TAG, "Unknown system subtype: $subtype")
        }

        return START_NOT_STICKY
    }

    private fun adjustVolume(mode: String, requestId: String?) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (mode) {
            "increase" -> audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
            "decrease" -> audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
            else -> {
                Log.e(TAG, "Invalid volume adjustment mode: $mode")
                return
            }
        }
        Log.i(TAG, "Volume adjusted: $mode")

        // Write response if requestId is provided
        if (requestId != null) {
            val response = JSONObject().apply {
                put("request_id", requestId)
                put("status", "success")
                put("action", "adjust_volume")
                put("mode", mode)
            }
            writeResponse(requestId, response)
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

    private fun checkConnectionStatus() {
        Log.i(TAG, "Checking connection status")
        // Connection status logic can be added here
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
