package com.example.leetcode.presentation.ui.navigation

import okhttp3.Route

sealed class Routes(var route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Profile : Routes("profile")
    data object Home : Routes("home")

    data object Chat : Routes("chat")

    data object Stats : Routes("stats")
    data object Leaderboard : Routes("leaderboard")
    data object ChangePassword : Routes("change_password")
    data object EditProfileScreen : Routes("edit_profile_screen")
    data object OtherProfile : Routes("otherProfile/{username}") {
        fun createRoute(username: String) = "otherProfile/$username"
    }
}