package org.lifecompanion.phonecontrolapp.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object DTMFAccessibilityServiceSingleton {
    var instance: DTMFAccessibilityService? = null
}

class DTMFAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "LC-DTMFAccessibilityService"

        // Known dialer package names, more may be needed depending of the default call app
        private val KNOWN_DIALER_PACKAGES: List<String> by lazy {
            getPackagesOfDialerApps().apply {
                val additionalPackages = listOf(
                    "com.google.android.dialer",
                    "com.android.dialer",
                    "com.samsung.android.incallui"
                )
                additionalPackages.forEach { pkg ->
                    if (!this.contains(pkg)) {
                        this.add(pkg)
                    }
                }
            }
        }

        // Common text or contentDescriptions for opening the dialpad
        private val DIALPAD_TOGGLE_KEYWORDS = listOf("Keypad", "Clavier", "Dial pad", "Show dial pad")

        private fun getPackagesOfDialerApps(): MutableList<String> {
            val packageNames = mutableListOf<String>()
            val context = DTMFAccessibilityServiceSingleton.instance?.applicationContext

            if (context != null) {
                val intent = Intent(Intent.ACTION_DIAL)
                val resolveInfos: List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)

                for (resolveInfo in resolveInfos) {
                    val activityInfo = resolveInfo.activityInfo
                    packageNames.add(activityInfo.applicationInfo.packageName)
                }
            } else {
                Log.e(TAG, "Context is null, cannot get dialer packages")
            }

            return packageNames
        }
    }

    // Keep a reference to the latest root node
    private var _rootNode: AccessibilityNodeInfo? = null

    override fun onCreate() {
        super.onCreate()
        DTMFAccessibilityServiceSingleton.instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        DTMFAccessibilityServiceSingleton.instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) {
            return
        }

        // Whenever there's a window content/state change, update our root node reference
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            _rootNode = rootInActiveWindow
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "DTMFAccessibilityService connected")
        DTMFAccessibilityServiceSingleton.instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val action = it.getStringExtra("action")
            val buttonText = it.getStringExtra("button_text")

            if (action == "press_keypad_button" && buttonText != null) {
                pressKeypadButton(buttonText)
            }
        }
        return START_NOT_STICKY
    }

    /**
     * Heuristically checks if the current screen belongs to a known dialer or in-call UI.
     */
    private fun isInCallScreen(rootNode: AccessibilityNodeInfo): Boolean {
        val pkg = rootNode.packageName?.toString() ?: return false

        return KNOWN_DIALER_PACKAGES.any { pkg.contains(it, ignoreCase = true) }
    }

    /**
     * Check if the dial pad is open by searching for all digits 0-9 plus * and # in the hierarchy.
     */
    private fun areAllDigitsVisible(rootNode: AccessibilityNodeInfo): Boolean {
        val required = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "*", "#")

        for (digit in required) {
            val nodesWithDigit = rootNode.findAccessibilityNodeInfosByText(digit)

            if (nodesWithDigit.isNullOrEmpty()) {
                return false
            }
        }

        return true
    }

    /**
     * Attempt to find a toggle button (by text or contentDescription) to open the dialpad.
     * If we already see digits on the screen, we skip toggling.
     */
    private fun openDialPadIfNeeded(rootNode: AccessibilityNodeInfo) {
        // If we already see the digits 0-9, #, * in the node tree, probably the dial pad is open
        val allDigitsPresent = areAllDigitsVisible(rootNode)

        if (allDigitsPresent) {
            Log.i(TAG, "Dial pad appears to be open already.")

            return
        }

        // Otherwise, BFS to find a dialpad toggle
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(rootNode)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            // Check text & contentDescription
            val textStr = node.text?.toString() ?: ""
            val descStr = node.contentDescription?.toString() ?: ""

            // If either text or contentDescription matches known keywords
            if (DIALPAD_TOGGLE_KEYWORDS.any { keyword ->
                    textStr.contains(keyword, ignoreCase = true) ||
                    descStr.contains(keyword, ignoreCase = true)
                }
            ) {
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.i(TAG, "Attempted to open dial pad by clicking: $textStr / $descStr")
                    Thread.sleep(300)

                    return
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }

        Log.w(TAG, "Could not find any dialpad toggle button in the current UI.")
    }

    /**
     * Checks if a dialer button's text or contentDescription is relevant for this digit.
     * e.g. digit= "2" matches "2", "2 ABC", "2,ABC", "2." ...
     */
    private fun isDialerButtonMatch(nodeText: String?, digit: String): Boolean {
        if (nodeText == null || nodeText.isEmpty()) {
            return false
        }

        val escapedDigit = Regex.escape(digit)
        val pattern = Regex("^$escapedDigit[\\s,]*(.*)?$", RegexOption.IGNORE_CASE)

        return nodeText.matches(pattern)
    }

    /**
     * Find and click a single digit (0-9, *, #).
     * Returns true if the digit was successfully clicked, false otherwise.
     */
    private fun pressKeyDigit(rootNode: AccessibilityNodeInfo, digit: String): Boolean {
        // BFS
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(rootNode)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()

            // Skip system UI or non-dialer packages
            val nodePackage = node.packageName?.toString() ?: ""
            if (!KNOWN_DIALER_PACKAGES.any { nodePackage.contains(it, ignoreCase = true) }) {
                // This node isn't from a recognized dialer package. Skip its subtree.
                continue
            }

            val textStr = node.text?.toString()
            val descStr = node.contentDescription?.toString()

            // If either text or contentDescription is a partial match
            if (isDialerButtonMatch(textStr, digit) || isDialerButtonMatch(descStr, digit)) {
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.i(TAG, "Clicked digit : $digit (found text='$textStr' desc='$descStr')")
                    Thread.sleep(300)

                    return true
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }

        return false
    }

    /**
     * External entry point:
     * 1) Check if we are in a known dialer UI
     * 2) Open the dial pad if needed
     * 3) Press the requested button
     */
    fun pressKeypadButton(dtmfString: String) {
        val rootNode = _rootNode ?: run {
            Log.w(TAG, "No root node available. Are we sure the dialer is in the foreground ?")

            return
        }

        if (!isInCallScreen(rootNode)) {
            Log.w(TAG, "We are not in a recognized in-call/dialer screen !")

            return
        }

        openDialPadIfNeeded(rootNode)

        if (dtmfString.matches(Regex("[0-9*#]"))) {
            var success = pressKeyDigit(rootNode, dtmfString)

            if (!success) {
                // We probably just opened the dial pad
                Thread.sleep(300)
                success = pressKeyDigit(rootNode, dtmfString)

                if (!success) {
                    Log.e(TAG, "Failed to press digit : $dtmfString")
                } else {
                    Log.i(TAG, "Successfully pressed digit : $dtmfString")
                }
            } else {
                Log.i(TAG, "Successfully pressed digit : $dtmfString")
            }
        } else {
            Log.e(TAG, "Invalid DTMF char : $dtmfString")
        }
    }

    override fun onInterrupt() {
        Log.i(TAG, "Accessibility service interrupted")
        DTMFAccessibilityServiceSingleton.instance = null
    }
}
