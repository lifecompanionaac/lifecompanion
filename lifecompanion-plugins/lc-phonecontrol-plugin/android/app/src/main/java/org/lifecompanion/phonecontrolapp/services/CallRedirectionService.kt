package org.lifecompanion.phonecontrolapp.services

import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log

class CallRedirectionService : CallRedirectionService() {
    companion object {
        private const val TAG = "LC-CallRedirectionService"
    }

    override fun onPlaceCall(handle: Uri, initialPhoneAccount: PhoneAccountHandle, allowInteractiveResponse: Boolean) {
        // Forward the call directly
        redirectCall(handle, initialPhoneAccount, false)
    }
}
