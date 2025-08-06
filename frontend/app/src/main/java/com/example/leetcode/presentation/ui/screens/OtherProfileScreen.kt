package com.example.leetcode.presentation.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.example.leetcode.ConversationActivity
import com.example.leetcode.presentation.ui.navigation.BottomNavBar
import com.example.leetcode.presentation.ui.screens.components.EnhancedPullToRefresh
import com.example.leetcode.presentation.ui.screens.components.LastThirtyDays
import com.example.leetcode.presentation.ui.screens.components.ProfileHeaderSection
import com.example.leetcode.presentation.ui.screens.components.QuestionStatsSection
import com.example.leetcode.presentation.ui.screens.components.SocialLinksSection
import com.example.leetcode.presentation.ui.screens.components.UserStatsSection
import com.example.leetcode.presentation.viewmodel.UserViewModel
import com.example.leetcode.utils.ResultState

@Composable
fun OtherProfileScreen(
    navController: NavHostController,
    vm: UserViewModel,
    username: String,
) {
    val userInfo by vm.userInfo.collectAsState()
    val userSocials by vm.userSocials.collectAsState()
    val contestInfo by vm.contestInfo.collectAsState()
    val solvedQuestions by vm.solvedQuestions.collectAsState()
    val activityStreak by vm.activityStreak.collectAsState()
    val userProfile by vm.userProfile.collectAsState()

    // Computed isRefreshing based on current loading states
    val isRefreshing = userInfo is ResultState.Loading ||
            userSocials is ResultState.Loading ||
            contestInfo is ResultState.Loading ||
            solvedQuestions is ResultState.Loading ||
            activityStreak is ResultState.Loading ||
            userProfile is ResultState.Loading

    // Refresh logic
    val onRefresh = {
        vm.getNameAndLanguage(username)
        vm.getUserData(username)
    }

    // Initial load when screen opens
    LaunchedEffect(username) {
        vm.getNameAndLanguage(username)
        vm.getUserData(username)
    }

    val nameAndLanguage = (userInfo as? ResultState.Success)?.data ?: listOf(username, "Java")
    val displayName = nameAndLanguage.getOrNull(0) ?: username
    val primaryLanguage = nameAndLanguage.getOrNull(1) ?: "Java"

    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar(navController = navController) },
    ) { paddingValues ->
        EnhancedPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    ProfileHeaderSection(
                        displayName = displayName,
                        username = username,
                        profilePhoto = (userProfile as? ResultState.Success)?.data?.userAvatar
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                if (CometChatUIKit.getLoggedInUser() != null) {
                                    val intent = Intent(context, ConversationActivity::class.java).apply {
                                        putExtra("uid", username)
                                        putExtra("name", displayName)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    // Handle the case where the current user is not logged into CometChat.
                                    // You can show a Toast message here.
                                }
                            }
                        ) {
                            Text("Message")
                        }
                    }
                }

                item {
                    QuestionStatsSection(
                        solvedQuestions = (solvedQuestions as? ResultState.Success)?.data
                            ?: emptyList()
                    )
                }

                item {
                    LastThirtyDays(
                        activityStreak = (activityStreak as? ResultState.Success)?.data
                            ?: emptyList()
                    )
                }

                item {
                    UserStatsSection(
                        username = username,
                        primaryLanguage = primaryLanguage,
                        questionsSolved = (solvedQuestions as? ResultState.Success)?.data
                            ?: emptyList(),
                        contestInfo = (contestInfo as? ResultState.Success)?.data
                    )
                }

                item {
                    SocialLinksSection(
                        username = username,
                        socials = (userSocials as? ResultState.Success)?.data
                    )
                }
            }
        }
    }
}

