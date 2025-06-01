package com.example.leetcode.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.leetcode.domain.model.EditPassword
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.PasswordTextField
import com.example.leetcode.presentation.viewmodel.SettingsViewModel
import com.example.leetcode.utils.ResultState
import com.example.leetcode.utils.SharedPreferencesManager
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    sharedPreferencesManager: SharedPreferencesManager,
    vm: SettingsViewModel,
    navController: NavController,
) {
    val user = sharedPreferencesManager.getUser()
    val id by remember { mutableStateOf(user?.id ?: "") }

    var password by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val editPasswordState by vm.editPasswordState.collectAsState()

    LaunchedEffect(editPasswordState) {
        when (editPasswordState) {
            is ResultState.Loading -> {
                isDialog = true
            }

            is ResultState.Success -> {
                isDialog = false
                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Home.route) { inclusive = true }
                }
            }

            is ResultState.Error -> {
                isDialog = false
                val error = (editPasswordState as? ResultState.Error)?.error?.let { e ->
                    (e as? HttpException)?.response()?.errorBody()?.string()
                } ?: "Password changed successfully"
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Home.route) { inclusive = true }
                }
            }

            ResultState.Idle -> {
                isDialog = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                label = "Current Password",
                password = password,
                onPasswordChange = { password = it }
            )
            PasswordTextField(
                label = "New Password",
                password = newPassword,
                onPasswordChange = { newPassword = it }
            )
            PasswordTextField(
                label = "Confirm New Password",
                password = confirmNewPassword,
                onPasswordChange = { confirmNewPassword = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = {
                    errorMessage = null

                    when {
                        password.isBlank() -> errorMessage = "Please enter the current password."
                        newPassword.length < 6 -> errorMessage =
                            "Password must be at least 6 characters long."

                        newPassword == password -> errorMessage =
                            "New password cannot be the same as the current password."

                        newPassword != confirmNewPassword -> errorMessage =
                            "Passwords do not match."

                        else -> {
                            val data = EditPassword(password, newPassword)
                            vm.editPassword(id = id, data = data)
                        }
                    }
                },
                enabled = !isDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isDialog) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Change Password", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

