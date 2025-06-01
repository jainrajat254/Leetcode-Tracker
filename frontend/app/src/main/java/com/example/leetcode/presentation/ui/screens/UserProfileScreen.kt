package com.example.leetcode.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.leetcode.R
import com.example.leetcode.presentation.ui.navigation.BottomNavBar
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.EnhancedPullToRefresh
import com.example.leetcode.presentation.ui.screens.components.LastThirtyDays
import com.example.leetcode.presentation.ui.screens.components.ProfileHeaderSection
import com.example.leetcode.presentation.ui.screens.components.QuestionStatsSection
import com.example.leetcode.presentation.ui.screens.components.SocialLinksSection
import com.example.leetcode.presentation.ui.screens.components.UserStatsSection
import com.example.leetcode.presentation.viewmodel.UserViewModel
import com.example.leetcode.utils.ResultState
import com.example.leetcode.utils.SharedPreferencesManager.getUser
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    navController: NavController,
    onLogoutClicked: () -> Unit,
) {
    Log.d("UserProfileScreen", "UserProfileScreen Composable called")

    val userResponse = remember { getUser() }
    val displayName = userResponse?.name ?: "User Name"
    val username = userResponse?.username ?: "username"
    val selectedLanguage by rememberUpdatedState(userResponse?.selectedLanguage ?: "Java")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userSocials by userViewModel.userSocials.collectAsState()
    val contestInfo by userViewModel.contestInfo.collectAsState()
    val solvedQuestions by userViewModel.solvedQuestions.collectAsState()
    val activityStreak by userViewModel.activityStreak.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    val isRefreshing =
        userSocials is ResultState.Loading ||
                contestInfo is ResultState.Loading ||
                solvedQuestions is ResultState.Loading ||
                activityStreak is ResultState.Loading ||
                userProfile is ResultState.Loading

    // Refresh logic
    val onRefresh = {
        userViewModel.getNameAndLanguage(username)
        userViewModel.getUserData(username)
    }

    // Fetch data once for this user
    LaunchedEffect(username) {
        if (userViewModel.userSocials.value is ResultState.Idle) {
            userViewModel.getUserData(username)
        }
    }

    EnhancedPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerContent = {
                EnhancedDrawerContent(
                    navController = navController,
                    displayName = displayName,
                    closeDrawer = { scope.launch { drawerState.close() } },
                    onLogoutClicked = onLogoutClicked
                )
            },
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Profile",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                bottomBar = { BottomNavBar(navController = navController) },
            )
            { paddingValues ->

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                        QuestionStatsSection(
                            solvedQuestions = (solvedQuestions as? ResultState.Success)?.data
                                ?: emptyList()
                        )
                    }
                    item {
                        LastThirtyDays(
                            activityStreak = (activityStreak as? ResultState.Success)?.data
                                ?: emptyList(),
                            modifier = Modifier
                        )
                    }


                    item {
                        UserStatsSection(
                            username = username,
                            primaryLanguage = selectedLanguage,
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
}


@Composable
private fun EnhancedDrawerContent(
    navController: NavController,
    displayName: String,
    closeDrawer: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp, top = 48.dp, bottom = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            DrawerItem(
                label = "Edit Profile",
                iconRes = R.drawable.baseline_person_24,
                onClick = {
                    closeDrawer()
                    navController.navigate(Routes.EditProfileScreen.route)
                }
            )

            DrawerItem(
                label = "Change Password",
                iconRes = R.drawable.baseline_lock_24,
                onClick = {
                    closeDrawer()
                    navController.navigate(Routes.ChangePassword.route)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.8.dp
            )

            DrawerItem(
                label = "Log Out",
                iconRes = R.drawable.baseline_logout_24,
                labelColor = MaterialTheme.colorScheme.error,
                onClick = {
                    onLogoutClicked()
                }
            )
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = labelColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(color = labelColor)
            )
        }
    }
}
