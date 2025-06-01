package com.example.leetcode.presentation.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.leetcode.R
import com.example.leetcode.domain.model.EditDetails
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.CustomTextField
import com.example.leetcode.presentation.ui.screens.components.LanguageDropDownMenu
import com.example.leetcode.presentation.ui.screens.components.YearDropDownMenu
import com.example.leetcode.presentation.viewmodel.AuthViewModel
import com.example.leetcode.presentation.viewmodel.SettingsViewModel
import com.example.leetcode.utils.ResultState
import com.example.leetcode.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    vm: SettingsViewModel,
    authViewModel: AuthViewModel,
    sharedPreferences: SharedPreferencesManager,
) {
    val user = sharedPreferences.getUser()

    val id by remember { mutableStateOf(user?.id ?: "") }
    var name by remember { mutableStateOf(user?.name ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var selectedYear by remember { mutableStateOf(user?.year ?: "Java") }
    var selectedLanguage by remember { mutableStateOf(user?.selectedLanguage ?: "") }
    val context = LocalContext.current

    var isDialog by rememberSaveable { mutableStateOf(false) }
    val editDetailsState by vm.editDetailsState.collectAsState()

    LaunchedEffect(editDetailsState) {
        when (editDetailsState) {
            is ResultState.Loading -> {
                isDialog = true
            }

            is ResultState.Success -> {
                isDialog = false
                val newData = (editDetailsState as ResultState.Success).data
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                sharedPreferences.saveUserDetails(details = newData)
                vm.resetEditDetailsState()
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Home.route) { inclusive = true }
                }
            }

            is ResultState.Error -> {
                isDialog = false
                val message = (editDetailsState as ResultState.Error).error.message
                    ?: "Some error occurred. Please try again."
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                vm.resetEditDetailsState()
            }

            ResultState.Loading -> isDialog = true
            ResultState.Idle -> isDialog = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Making it more compact
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    leadingIconRes = R.drawable.baseline_person_24
                )

                CustomTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    leadingIconRes = R.drawable.baseline_alternate_email_24
                )

                YearDropDownMenu(
                    selectedYear = selectedYear,
                    onYearSelected = { selectedYear = it }
                )

                LanguageDropDownMenu(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )

                Button(
                    onClick = {
                        authViewModel.isValidUser(username) { result ->
                            val message = result["message"] ?: result["error"]
                            val isValid = result["status"] == "true"

                            if (isValid || username == user?.username) { // Allow current username reuse
                                val data =
                                    EditDetails(name, username, selectedYear, selectedLanguage)
                                Log.d("Edit Details", data.toString())
                                vm.editDetails(id = id, data = data)
                            } else {
                                Toast.makeText(
                                    context,
                                    message ?: "Username not available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}
