package org.lifecompanion.phonecontrolapp.services

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import org.lifecompanion.phonecontrolapp.services.CallStateListener

class CallWatcher : InCallService() {
    companion object {
        private const val TAG = "CallWatcher"
        var callStateListener: CallStateListener? = null
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.i(TAG, "Call added: ${call.details.handle}")
        call.registerCallback(callStateCallback)
        notifyStateChange(call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.i(TAG, "Call removed")
        call.unregisterCallback(callStateCallback)
        notifyStateChange(null)
    }

    private val callStateCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            notifyStateChange(call)
        }
    }

    private fun getPhoneNumber(call: Call?): String? {
        return call?.details?.handle?.schemeSpecificPart
    }

    private fun notifyStateChange(call: Call?) {
        val isIncoming = call?.details?.state == Call.STATE_RINGING
        val isActive = call?.details?.state == Call.STATE_ACTIVE
        val phoneNumber = getPhoneNumber(call)
        callStateListener?.onCallStateChanged(call, isIncoming, isActive, phoneNumber)
    }
}
