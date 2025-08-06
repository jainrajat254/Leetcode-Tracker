package com.example.leetcode

import android.content.Intent
import com.cometchat.chat.core.Call
import com.cometchat.chat.core.CometChat
import android.app.Application
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp
import kotlin.jvm.java

@HiltAndroidApp
class BaseApplication : Application() {
    companion object {
        private val LISTENER_ID = "${BaseApplication::class.java.simpleName}${System.currentTimeMillis()}"
        var currentCall: Call? = null // Store call globally
    }

    override fun onCreate() {
        super.onCreate()
        CometChat.addCallListener(LISTENER_ID, object : CometChat.CallListener() {
            override fun onIncomingCallReceived(call: Call) {
                currentCall = call // Store globally
                val intent = Intent(this@BaseApplication, IncomingCallActivity::class.java)
                intent.putExtra("sessionId", call.sessionId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }



            override fun onOutgoingCallAccepted(call: Call) {
                // To be implemented
            }

            override fun onOutgoingCallRejected(call: Call) {
                // To be implemented
            }

            override fun onIncomingCallCancelled(call: Call) {
                // To be implemented
            }
        })
    }
}