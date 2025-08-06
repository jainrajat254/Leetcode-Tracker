package com.example.leetcode.presentation.viewmodel

import android.R.attr.name
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chat.models.User
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings
import com.example.leetcode.domain.model.LoginCredentials
import com.example.leetcode.domain.model.LoginResponse
import com.example.leetcode.domain.model.UserData
import com.example.leetcode.domain.repository.AuthRepository
import com.example.leetcode.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository,application: Application) : AndroidViewModel(application) {


    init {
        // You should use your actual App ID, Auth Key, and Region here.
        // Consider getting these from a secure source like build.gradle or a constants file.
        val uiKitSettings = UIKitSettings.UIKitSettingsBuilder()
            .setAppId("2791408cfaed7852")
            .setRegion("in")
            .setAuthKey("6ee485430c4ba1c1b097defd2c2a5b04e90c0597")
            .build()

        CometChatUIKit.init(this.getApplication(), uiKitSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(s: String?) {
                // CometChat initialized successfully.
            }
            override fun onError(p0: CometChatException) {
                // Handle initialization errors.
            }
        })
    }

    private val _registerState = MutableStateFlow<ResultState<UserData>>(ResultState.Idle)
    val registerState: StateFlow<ResultState<UserData>> get() = _registerState

    private val _loginState = MutableStateFlow<ResultState<LoginResponse>>(ResultState.Idle)
    val loginState: StateFlow<ResultState<LoginResponse>> get() = _loginState

    fun registerUser(user: UserData) {
        _registerState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val registerResponse: UserData = authRepository.registerUser(user)
                _registerState.value = ResultState.Success(registerResponse)
            } catch (e: Exception) {
                _registerState.value = ResultState.Error(e)
            }
        }
    }

    fun loginUser(user: LoginCredentials) {
        _loginState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val loginResponse: LoginResponse = authRepository.loginUser(user)
                val uid = loginResponse.username

                // Step 1: Attempt to log the user into CometChat
                CometChatUIKit.login(uid, object : CometChat.CallbackListener<User>() {
                    override fun onSuccess(cometChatUser: User?) {
                        // CometChat login successful, proceed with your app's login success logic
                        _loginState.value = ResultState.Success(loginResponse)
                    }

                    override fun onError(e: CometChatException?) {
                        // Step 2: If login fails, check if it's because the user doesn't exist
                        if (e?.code == "ERR_UID_NOT_FOUND") {
                            // User not found, so we'll create the user and then log them in.
                            // We pass a new callback to handle the result of this creation/login attempt.
                            createAndLoginCometChatUser(uid, user.username, loginResponse, object : CometChat.CallbackListener<User>(){
                                override fun onSuccess(cometChatUser: User?) {
                                    // User successfully created and logged in via the secondary function
                                    _loginState.value = ResultState.Success(loginResponse)
                                }

                                override fun onError(createError: CometChatException?) {
                                    // Failed to create and login the CometChat user
                                    _loginState.value = ResultState.Error(createError ?: Exception("Failed to create and login CometChat user"))
                                }
                            })
                        } else {
                            // Other CometChat login error during the initial login attempt
                            _loginState.value = ResultState.Error(e ?: Exception("Unknown CometChat error during initial login"))
                        }
                    }
                })
            } catch (e: Exception) {
                _loginState.value = ResultState.Error(e)
            }
        }
    }

    fun isValidUser(username: String, onResult: (Map<String, String>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.isValidUser(username)
                onResult(response)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                onResult(mapOf("status" to "false", "error" to (errorBody ?: "Invalid username")))
            } catch (e: Exception) {
                onResult(mapOf("status" to "false", "error" to "Something went wrong"))
            }
        }
    }

    fun clear() {
        _loginState.value = ResultState.Idle
        _registerState.value = ResultState.Idle
    }

    private fun createAndLoginCometChatUser(
        uid: String,
        name: String,
        loginResponse: LoginResponse,
        callback: CometChat.CallbackListener<User>
    ) {
        val cometChatUser = User()
        cometChatUser.uid = uid
        cometChatUser.name = name

        // Create user with authKey parameter
        CometChat.createUser(cometChatUser, "6ee485430c4ba1c1b097defd2c2a5b04e90c0597", object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User?) {
                // User created successfully, now log them in
                CometChatUIKit.login(uid, object : CometChat.CallbackListener<User>() {
                    override fun onSuccess(cometChatUser: User?) {
                        Log.d("CometChatLogin", "Login successful for: ${cometChatUser?.uid}")
                        callback.onSuccess(cometChatUser)
                    }
                    override fun onError(e: CometChatException?) {
                        callback.onError(e)
                    }
                })
            }

            override fun onError(e: CometChatException?) {
                Log.e("CometChatLogin", "User creation failed: ${e?.message}")
                callback.onError(e)
            }
        })
    }

}