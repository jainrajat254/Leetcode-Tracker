package com.example.leetcode.presentation.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.leetcode.R
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.viewmodel.AuthViewModel
import com.example.leetcode.utils.SharedPreferencesManager
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(vm: AuthViewModel, navController: NavController) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        content = { _ ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(32.dp)
                )
            }
        }
    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(1500)
        val isLoggedIn = SharedPreferencesManager.isLoggedIn()
        val username = SharedPreferencesManager.getUsername()

        if (!isLoggedIn || username.isNullOrBlank()) {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }

        vm.isValidUser(username) { result ->
            val message = result["message"] ?: result["error"]

            // Use message to determine if the user is valid
            val isValid = message == "User Found"

            if (isValid) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                SharedPreferencesManager.clearUserData()
                Toast.makeText(context, message ?: "Invalid username", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}
