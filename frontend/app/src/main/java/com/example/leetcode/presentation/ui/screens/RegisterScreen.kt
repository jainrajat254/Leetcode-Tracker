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
import androidx.navigation.NavController
import com.example.leetcode.R
import com.example.leetcode.domain.model.UserData
import com.example.leetcode.presentation.viewmodel.AuthViewModel
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.AuthButton
import com.example.leetcode.presentation.ui.screens.components.CustomLoader
import com.example.leetcode.presentation.ui.screens.components.CustomTextField
import com.example.leetcode.presentation.ui.screens.components.HeaderSection
import com.example.leetcode.presentation.ui.screens.components.LanguageDropDownMenu
import com.example.leetcode.presentation.ui.screens.components.PasswordTextField
import com.example.leetcode.presentation.ui.screens.components.YearDropDownMenu
import com.example.leetcode.utils.ResultState

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("First Year") }
    var selectedLanguage by rememberSaveable { mutableStateOf("Java") }
    val isFormValid by derivedStateOf { name.isNotBlank() || username.isNotBlank() || password.isNotBlank() }

    val context = LocalContext.current

    var isDialog by rememberSaveable { mutableStateOf(false) }
    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is ResultState.Success -> {
                isDialog = false
                Toast.makeText(
                    context, "Registration Successful\nLog in to continue", Toast.LENGTH_SHORT
                ).show()
                navController.navigate(Routes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            is ResultState.Error -> {
                Log.d("AUTH ERROR", (registerState as ResultState.Error).error.message.toString())
                Toast.makeText(context, "some error occurred, please try again", Toast.LENGTH_SHORT)
                    .show()
            }

            ResultState.Idle -> isDialog = false
            ResultState.Loading -> isDialog = true
        }
    }

    if (isDialog) {
        CustomLoader(text = "Registering.....")
    } else {

        Scaffold(containerColor = MaterialTheme.colorScheme.background, content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderSection(title = "Create Account")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        leadingIconRes = R.drawable.baseline_person_24
                    )

                    LanguageDropDownMenu(
                        selectedLanguage = selectedLanguage,
                        onLanguageSelected = { selectedLanguage = it },
                    )

                    YearDropDownMenu(selectedYear = year, onYearSelected = { year = it })

                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Username",
                        leadingIconRes = R.drawable.baseline_alternate_email_24
                    )

                    PasswordTextField(label = "Password",
                        password = password,
                        onPasswordChange = { password = it })

                }

                Spacer(modifier = Modifier.height(24.dp))

                AuthButton(
                    text = if (isDialog) "Registering..." else "Register",
                    enabled = isFormValid,
                    onClick = {
                        when {
                            password.length < 6 -> {
                                Toast.makeText(
                                    context,
                                    "Password must be at least 6 characters long.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                val user = UserData(
                                    name.trim(),
                                    selectedLanguage,
                                    year,
                                    username.trim(),
                                    password.trim()
                                )
                                authViewModel.registerUser(
                                    user,
                                )
                            }
                        }
                    },
                )
            }
        })
    }
}
