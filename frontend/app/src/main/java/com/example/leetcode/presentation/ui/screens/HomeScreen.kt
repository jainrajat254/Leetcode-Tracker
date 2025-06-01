package com.example.leetcode.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.leetcode.domain.model.StreakContent
import com.example.leetcode.presentation.ui.navigation.BottomNavBar
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.CommonTabRow
import com.example.leetcode.presentation.ui.screens.components.CommonTopBar
import com.example.leetcode.presentation.ui.screens.components.EmptyState
import com.example.leetcode.presentation.ui.screens.components.EnhancedPullToRefresh
import com.example.leetcode.presentation.ui.screens.components.LoadingScreen
import com.example.leetcode.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    vm: HomeViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        vm.loadDataIfNotLoaded() // Ensure that data is loaded if not loaded
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar(navController = navController) },
        content = {
            StreakScreen(vm = vm, navController = navController)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StreakScreen(vm: HomeViewModel, navController: NavController) {
    val isLoading by vm.isLoading.collectAsState()
    val languages = remember { listOf("Java", "C++") }
    val pagerState = rememberPagerState { languages.size }
    val coroutineScope = rememberCoroutineScope()

    EnhancedPullToRefresh(
        isRefreshing = isLoading,
        onRefresh = { vm.refreshAll() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CommonTopBar(title = "Streak")

            CommonTabRow(
                tabs = languages,
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                StudentStreak(language = languages[page], vm = vm, navController = navController)
            }
        }
    }
}

@Composable
fun StudentStreak(language: String, vm: HomeViewModel, navController: NavController) {
    val streakDataMap by vm.streakDataMap.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    val streaks by remember(language, streakDataMap) {
        derivedStateOf { streakDataMap[language] ?: emptyList() }
    }
    when {
        isLoading -> LoadingScreen() // Display loading indicator
        streaks.isEmpty() -> EmptyState("No streak data available") // Empty state when no data is found
        else -> StreakList(streaks = streaks, navController = navController) // Display streak list
    }
}


@Composable
fun StreakList(streaks: List<StreakContent>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, bottom = 80.dp, top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(streaks) { streak ->
            StreakListItem(
                name = streak.name,
                username = streak.username,
                isActive = streak.submittedToday,
                userAvatar = streak.userAvatar,
                onClick = { navController.navigate(Routes.OtherProfile.createRoute(streak.username)) }
            )
        }
    }
}

@Composable
fun StreakListItem(
    name: String,
    username: String,
    isActive: Boolean,
    userAvatar: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userAvatar,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@$username",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFF65E26A) else Color(0xFFE45D5D))
        )
    }
}


