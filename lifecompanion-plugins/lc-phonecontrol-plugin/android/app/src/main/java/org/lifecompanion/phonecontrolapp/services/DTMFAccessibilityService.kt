package org.lifecompanion.phonecontrolapp.services

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class DTMFAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "LC-DTMFAccessibilityService"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) { }

    private fun isInCallScreen(node: AccessibilityNodeInfo): Boolean {
        // Check for specific elements that indicate the call screen
        return node.packageName?.contains("dialer", ignoreCase = true) == true
    }

    private fun isKeypadOpen(node: AccessibilityNodeInfo): Boolean {
        val keypadElements = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "*", "#")

        return keypadElements.all { element ->
            node.findAccessibilityNodeInfosByText(element).isNotEmpty()
        }
    }

    private fun openKeypad(node: AccessibilityNodeInfo) {
        // Find and click the button to open the keypad
        val keypadButtonTexts = listOf("Keypad", "Clavier")

        for (text in keypadButtonTexts) {
            val keypadButton = node.findAccessibilityNodeInfosByText(text).firstOrNull()

            if (keypadButton != null) {
                keypadButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.i(TAG, "Keypad opened with text $text")

                return
            }
        }

        Log.i(TAG, "Keypad button not found")
    }

    fun pressKeypadButton(buttonText: String) {
        val rootNode = rootInActiveWindow ?: return

        if (isInCallScreen(rootNode)) {
            if (!isKeypadOpen(rootNode)) {
                openKeypad(rootNode)
            }

            searchAndClick(rootNode, buttonText)
        }
    }

    private fun searchAndClick(node: AccessibilityNodeInfo, buttonText: String) {
        if (node.text?.toString() == buttonText && node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.i(TAG, "Clicked button $buttonText")

            return
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { searchAndClick(it, buttonText) }
        }
    }

    override fun onInterrupt() {
        Log.i(TAG, "Accessibility service interrupted")
    }
}
