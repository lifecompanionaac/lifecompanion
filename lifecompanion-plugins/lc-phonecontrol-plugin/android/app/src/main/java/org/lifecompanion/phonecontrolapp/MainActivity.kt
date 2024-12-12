package org.lifecompanion.phonecontrolapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    private val PERMISSION_REQUEST_CODE = 14122004

    /**
     * Called when the activity is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Show the main view
        checkPermissions()
    }

    /**
     * Ask to the user the permissions needed by the app.
     * Show an android popup which ask the user to grant the permissions.
     */
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.FOREGROUND_SERVICE
        )

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Show the popup
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
                return
            }
        }
    }

    /**
     * Called when the user has granted or denied the permissions.
     * If the user has denied the permissions, close the app.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if the result is related to the permissions asked
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // The user has denied the permissions
                    return
                }
            }
            finish()
        }
    }

}
