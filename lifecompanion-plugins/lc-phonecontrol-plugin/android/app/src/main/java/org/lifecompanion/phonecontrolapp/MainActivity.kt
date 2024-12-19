package org.lifecompanion.phonecontrolapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.lifecompanion.phonecontrolapp.services.JSONProcessingService

class MainActivity : Activity() {

    private val PERMISSION_REQUEST_CODE = 14122004
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the main view

        // Check and request permissions
        checkPermissions()

        // Start the JSONProcessingService
        startJsonProcessingService()
    }

    /**
     * Checks if all required permissions are granted. If not, requests them.
     */
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.MANAGE_OWN_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * Handles the result of permission requests.
     * If permissions are denied, logs a warning and does not start the app functionality.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.i(TAG, "All permissions granted.")
                startJsonProcessingService()
            } else {
                Log.w(TAG, "Required permissions denied by user. Exiting app.")
                finish() // Close the app if permissions are not granted
            }
        }
    }

    /**
     * Starts the JSONProcessingService in the background to handle file watching.
     */
    private fun startJsonProcessingService() {
        try {
            val serviceIntent = Intent(this, JSONProcessingService::class.java)
            startService(serviceIntent)
            Log.i(TAG, "JSONProcessingService started successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start JSONProcessingService: ${e.message}", e)
        }
    }
}
