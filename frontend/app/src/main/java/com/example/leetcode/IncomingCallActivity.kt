package com.example.leetcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.chat.core.Call
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chatuikit.calls.CometChatOngoingCallActivity
import com.cometchat.chatuikit.calls.incomingcall.CometChatIncomingCall
import com.example.leetcode.databinding.ActivityIncomingCallBinding

class IncomingCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomingCallBinding
    private lateinit var call: Call

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the call object directly from intent
        call = BaseApplication.currentCall ?: return
        binding.incomingCall.setCall(call)
    }
}