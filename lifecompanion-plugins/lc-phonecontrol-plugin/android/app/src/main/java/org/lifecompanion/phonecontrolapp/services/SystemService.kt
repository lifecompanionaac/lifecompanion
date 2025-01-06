package org.lifecompanion.phonecontrolapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import org.json.JSONObject

class SystemService : Service() {
    companion object {
        private const val TAG = "SystemService"
    }

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
            "adjust_volume" -> adjustVolume(data.optString("mode"))
            else -> Log.e(TAG, "Unknown system subtype: $subtype")
        }

        return START_NOT_STICKY
    }

    private fun adjustVolume(mode: String) {
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
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
