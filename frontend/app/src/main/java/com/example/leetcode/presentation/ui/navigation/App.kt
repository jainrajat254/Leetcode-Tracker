package com.example.leetcode.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.leetcode.presentation.ui.screens.ChangePasswordScreen
import com.example.leetcode.presentation.ui.screens.EditProfileScreen
import com.example.leetcode.presentation.ui.screens.HomeScreen
import com.example.leetcode.presentation.ui.screens.LeaderboardScreen
import com.example.leetcode.presentation.ui.screens.LoginScreen
import com.example.leetcode.presentation.ui.screens.OtherProfileScreen
import com.example.leetcode.presentation.ui.screens.RegisterScreen
import com.example.leetcode.presentation.ui.screens.SplashScreen
import com.example.leetcode.presentation.ui.screens.StatsScreen
import com.example.leetcode.presentation.ui.screens.UserProfileScreen
import com.example.leetcode.presentation.viewmodel.AuthViewModel
import com.example.leetcode.presentation.viewmodel.HomeViewModel
import com.example.leetcode.presentation.viewmodel.LeaderBoardViewModel
import com.example.leetcode.presentation.viewmodel.SettingsViewModel
import com.example.leetcode.presentation.viewmodel.StatsViewModel
import com.example.leetcode.presentation.viewmodel.UserViewModel
import com.example.leetcode.utils.SharedPreferencesManager

@Composable
fun App() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val statsViewModel: StatsViewModel = hiltViewModel()
    val leaderboardViewModel: LeaderBoardViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val sharedPreferences = SharedPreferencesManager

    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(navController = navController, vm = authViewModel)
        }
        composable(Routes.Login.route) {
            LoginScreen(
                navController = navController,
                sharedPreferencesManager = sharedPreferences,
                authViewModel = authViewModel
            )
        }
        composable(Routes.Register.route) {
            RegisterScreen(
                navController = navController,
                modifier = Modifier,
                authViewModel = authViewModel
            )
        }
        composable(Routes.Profile.route) {
            UserProfileScreen(
                navController = navController,
                userViewModel = userViewModel,
                onLogoutClicked = {
                    sharedPreferences.clearUserData()
                    userViewModel.clear()
                    leaderboardViewModel.clear()
                    settingsViewModel.clear()
                    authViewModel.clear()
                    homeViewModel.clear()
                    statsViewModel.clear()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Home.route) {
            HomeScreen(vm = homeViewModel, navController = navController)
        }
        composable(Routes.Leaderboard.route) {
            LeaderboardScreen(navController = navController, vm = leaderboardViewModel)
        }
        composable(Routes.Stats.route) {
            StatsScreen(navController = navController, vm = statsViewModel, modifier = Modifier)
        }
        composable(Routes.EditProfileScreen.route) {
            EditProfileScreen(
                navController = navController,
                vm = settingsViewModel,
                authViewModel = authViewModel,
                sharedPreferences = sharedPreferences
            )
        }
        composable(Routes.ChangePassword.route) {
            ChangePasswordScreen(
                navController = navController,
                vm = settingsViewModel,
                sharedPreferencesManager = sharedPreferences
            )
        }
        composable(
            route = Routes.OtherProfile.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            OtherProfileScreen(
                navController = navController,
                vm = userViewModel,
                username = username
            )
        }
    }
}