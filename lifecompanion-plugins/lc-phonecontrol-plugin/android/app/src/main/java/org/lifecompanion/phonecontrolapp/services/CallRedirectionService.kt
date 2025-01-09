package org.lifecompanion.phonecontrolapp.services

import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log

class CallRedirectionService : CallRedirectionService() {
    companion object {
        private const val TAG = "CallRedirectionService"
    }

    override fun onPlaceCall(handle: Uri, initialPhoneAccount: PhoneAccountHandle, allowInteractiveResponse: Boolean) {
        Log.i(TAG, "Outgoing call intercepted: ${handle.schemeSpecificPart}")

        // Forward the call directly
        redirectCall(handle, initialPhoneAccount, false)
    }
}
