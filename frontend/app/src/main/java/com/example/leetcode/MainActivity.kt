package com.example.leetcode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat.startActivity
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chat.models.User
import com.cometchat.chatuikit.calls.incomingcall.CometChatIncomingCall
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings
import com.example.leetcode.presentation.ui.navigation.App
import com.example.leetcode.utils.SharedPreferencesManager
import com.example.leetcode.presentation.ui.theme.LeetcodeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    private val appID = "2791408cfaed7852" // Replace with your App ID
    private val region = "in" // Replace with your App Region
    private val authKey = "6ee485430c4ba1c1b097defd2c2a5b04e90c0597" // Replace with your Auth Key or leave blank if you are authenticating using Auth Token

    private val uiKitSettings = UIKitSettings.UIKitSettingsBuilder()
        .setRegion(region)
        .setAppId(appID)
        .setAuthKey(authKey)
        .subscribePresenceForAllUsers()
        .build()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        CometChatUIKit.init(this, uiKitSettings, object : CometChat.CallbackListener<String?>() {
//            override fun onSuccess(successString: String?) {
//
//                Log.d(TAG, "Initialization completed successfully")
//
//                loginUser()
//            }
//
//            override fun onError(e: CometChatException?) {}
//        })
//    }

    private fun loginUser() {
        CometChatUIKit.login("cometchat-uid-1", object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User) {

                // Launch Conversation List + Message View (Split-Screen Style)
                startActivity(Intent(this@MainActivity, ConversationActivity::class.java))
            }

            override fun onError(e: CometChatException) {
                // Handle login failure (e.g. show error message or retry)
                Log.e("Login", "Login failed: ${e.message}")
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesManager.init(this)
        enableEdgeToEdge()
        if (!CometChat.isInitialized()) {
            val uiKitSettings = UIKitSettings.UIKitSettingsBuilder()
                .setRegion(region)
                .setAppId(appID)
                .setAuthKey(authKey)
                .subscribePresenceForAllUsers()
                .build()

            CometChatUIKit.init(this, uiKitSettings, object : CometChat.CallbackListener<String?>() {
                override fun onSuccess(successString: String?) {
                    Log.d(TAG, "Initialization completed successfully")
                }

                override fun onError(e: CometChatException?) {
                    Log.e(TAG, "Initialization failed: ${e?.message}")
                }
            })
        }

        setContent {
            LeetcodeTheme {
                App()
            }
        }
    }
}
