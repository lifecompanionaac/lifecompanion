package org.lifecompanion.phonecontrolapp.services
import android.telecom.Call

interface CallStateListener {
    fun onCallStateChanged(call: Call?, isIncoming: Boolean, isActive: Boolean, phoneNumber: String?)
}
