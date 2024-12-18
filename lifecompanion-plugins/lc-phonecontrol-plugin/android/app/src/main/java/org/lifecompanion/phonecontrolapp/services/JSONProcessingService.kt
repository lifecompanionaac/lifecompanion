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
        private const val INPUT_DIR = "/data/local/tmp/lifecompanion/phonecontrol/input"
        private const val OUTPUT_DIR = "/data/local/tmp/lifecompanion/phonecontrol/output"
    }

    private var isCallActive = false
    private var watcherThread: Thread? = null
    private var pollingInterval = 1000L // Default polling interval in milliseconds

    override fun onCreate() {
        super.onCreate()
        startFileWatcher()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        watcherThread?.interrupt()
    }

    private fun startFileWatcher() {
        watcherThread = Thread {
            val inputDir = File(INPUT_DIR)
            if (!inputDir.exists()) inputDir.mkdirs()

            while (!Thread.currentThread().isInterrupted) {
                try {
                    val files = inputDir.listFiles { _, name -> name.endsWith(".json") }
                    files?.forEach { file ->
                        processFile(file)
                        file.delete()
                    }

                    Thread.sleep(pollingInterval)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing files: ${e.message}")
                }
            }
        }.apply { start() }
    }

    private fun processFile(file: File) {
        try {
            val content = FileInputStream(file).bufferedReader().use { it.readText() }
            val json = JSONObject(content)

            if (!validateJson(json)) {
                Log.e(TAG, "Invalid JSON: ${file.name}")
                return
            }

            routeJsonToService(json)

            if (json.has("request_id")) {
                writeResponse(json.optString("request_id"), createResponseData(json))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing file: ${file.name}", e)
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
        val outputDir = File(OUTPUT_DIR)
        if (!outputDir.exists()) outputDir.mkdirs()

        val responseFile = File(outputDir, "$requestId.json")
        FileOutputStream(responseFile).use { it.write(responseData.toString().toByteArray()) }
    }

    private fun createResponseData(request: JSONObject): JSONObject {
        return JSONObject().apply {
            put("request_id", request.optString("request_id"))
            put("status", "success")
            put("message", "Processed ${request.optString("type")} request.")
        }
    }

    fun setCallActive(active: Boolean) {
        isCallActive = active
        pollingInterval = if (active) 100 else 1000
    }
}
