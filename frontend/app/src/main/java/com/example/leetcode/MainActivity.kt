package com.example.leetcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.leetcode.presentation.ui.navigation.App
import com.example.leetcode.utils.SharedPreferencesManager
import com.example.leetcode.presentation.ui.theme.LeetcodeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesManager.init(this)
        enableEdgeToEdge()
        setContent {
            LeetcodeTheme {
                App()
            }
        }
    }
}
