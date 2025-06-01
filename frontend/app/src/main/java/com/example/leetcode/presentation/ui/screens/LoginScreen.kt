package com.example.leetcode.presentation.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.leetcode.R
import com.example.leetcode.domain.model.LoginCredentials
import com.example.leetcode.presentation.viewmodel.AuthViewModel
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.AuthButton
import com.example.leetcode.presentation.ui.screens.components.AuthNavigationText
import com.example.leetcode.presentation.ui.screens.components.CustomLoader
import com.example.leetcode.presentation.ui.screens.components.CustomTextField
import com.example.leetcode.presentation.ui.screens.components.HeaderSection
import com.example.leetcode.presentation.ui.screens.components.LoadingScreen
import com.example.leetcode.presentation.ui.screens.components.PasswordTextField
import com.example.leetcode.utils.ResultState
import com.example.leetcode.utils.SharedPreferencesManager
import retrofit2.HttpException

@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    sharedPreferencesManager: SharedPreferencesManager,
) {
    val context = LocalContext.current

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isFormValid by derivedStateOf { username.isNotBlank() && password.isNotBlank() }

    var isDialog by rememberSaveable { mutableStateOf(false) }
    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is ResultState.Success -> {
                isDialog = false
                val loginData = (loginState as ResultState.Success).data
                sharedPreferencesManager.saveUser(loginResponse = loginData)
                Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.Home.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }

            is ResultState.Error -> {
                isDialog = false
                Log.d("AUTH ERROR", (loginState as ResultState.Error).error.message.toString())
                val error = (loginState as? ResultState.Error)?.error?.let { e ->
                    (e as? HttpException)?.response()?.errorBody()?.string()
                } ?: "Some error occurred"
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

            ResultState.Idle -> isDialog = false
            ResultState.Loading -> isDialog = true
        }
    }

    if (isDialog) {
        LoadingScreen()
    } else {

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderSection(
                    title = "Welcome Back",
                    subtitle = "Please sign in to continue"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Username",
                        leadingIconRes = R.drawable.baseline_person_24
                    )

                    PasswordTextField(
                        label = "Password",
                        password = password,
                        onPasswordChange = { password = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AuthButton(
                    text = "Sign In",
                    enabled = isFormValid,
                    onClick = {
                        val user = LoginCredentials(username.trim(), password.trim())
                        authViewModel.loginUser(user = user)
                    }
                )

                AuthNavigationText(
                    text = "Don't have an account? ",
                    buttonText = "Register Now",
                    onClick = { navController.navigate(Routes.Register.route) }
                )
            }
        }
    }
}