package org.lifecompanion.phonecontrolapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.lifecompanion.phonecontrolapp.services.Notify
import org.lifecompanion.phonecontrolapp.services.DTMFAccessibilityService

class MainActivity : Activity() {
    private val PERMISSION_REQUEST_CODE = 14122004
    private val TAG = "MainActivity"
    private val outputDirPath: String by lazy { File(filesDir, "output").absolutePath }
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 5 * 60 * 1000L // 5 minutes in milliseconds
    private val fileAgeLimit = 2 * 60 * 1000L // 2 minutes in milliseconds
    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Set the main view

        // Initialize notification channel
        Notify.createNotificationChannel(
            name = "JSON Processing Service",
            channelId = "json_service_channel",
            context = this
        )

        // Check and request permissions
        checkPermissions()

        // Disable battery optimizations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as android.os.PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                disableBatteryOptimizations(packageName)
            }
        }

        // Check and prompt to enable Accessibility Service
        promptEnableAccessibilityService()

        // clean the files/output directory
        File(outputDirPath).deleteRecursively()
        executor.scheduleAtFixedRate(checkAndDeleteOldFiles, 0, checkInterval, TimeUnit.MILLISECONDS)
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
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
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
                Log.i(TAG, "All permissions granted")
            } else {
                Log.w(TAG, "Required permissions denied by user, exiting app...")
                finish() // Close the app if permissions are not granted
            }
        }
    }

    private fun disableBatteryOptimizations(packageName: String) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse(packageName)
        startActivity(intent)
    }

    private val checkAndDeleteOldFiles = Runnable {
        val currentTime = System.currentTimeMillis()
        File(outputDirPath).listFiles()?.forEach { file ->
            if (currentTime - file.lastModified() > fileAgeLimit) {
                file.delete()
            }
        }
    }

    private fun isAccessibilityServiceEnabled(service: Class<*>): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = enabledServices.split(":")
        val serviceName = componentName.flattenToString().replace(packageName, service.name)
        return colonSplitter.any { it.equals(serviceName, ignoreCase = true) }
    }
    
    private fun promptEnableAccessibilityService() {
        if (!isAccessibilityServiceEnabled(DTMFAccessibilityService::class.java)) {
            AlertDialog.Builder(this)
                .setTitle("Activer le service d'accessibilité")
                .setMessage("Cette application nécessite le service d'accessibilité pour émuler les entrées du clavier pendant les appels. Veuillez l'activer dans les paramètres d'accessibilité. Vous devrez peut-être le faire à chaque redémarrage.")
                .setPositiveButton("Ouvrir les paramètres") { _, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("Déjà fait", null)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
