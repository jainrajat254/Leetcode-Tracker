package com.example.leetcode.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.leetcode.presentation.ui.navigation.BottomNavBar
import com.example.leetcode.presentation.ui.navigation.Routes
import com.example.leetcode.presentation.ui.screens.components.CommonTabRow
import com.example.leetcode.presentation.ui.screens.components.CommonTopBar
import com.example.leetcode.presentation.ui.screens.components.EmptyState
import com.example.leetcode.presentation.ui.screens.components.EnhancedPullToRefresh
import com.example.leetcode.presentation.ui.screens.components.FilterBottomSheet
import com.example.leetcode.presentation.ui.screens.components.FilterFAB
import com.example.leetcode.presentation.ui.screens.components.LoadingScreen
import com.example.leetcode.presentation.viewmodel.StatsViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    vm: StatsViewModel,
    navController: NavController,
) {

    LaunchedEffect(Unit) {
        vm.loadDataIfNotLoaded() // Ensure that data is loaded if not loaded
    }

    var showFilterDialog by remember { mutableStateOf(false) } // Move state here

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        content = {
            UserStats(
                modifier = modifier,
                vm = vm,
                navController = navController,
                showFilterDialog
            ) {
                showFilterDialog = it
            }
        },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FilterFAB(onClick = { showFilterDialog = true })
        },
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserStats(
    modifier: Modifier = Modifier,
    vm: StatsViewModel,
    navController: NavController,
    showFilterDialog: Boolean,
    setShowFilterDialog: (Boolean) -> Unit, // Add setter
) {
    val pagerState = rememberPagerState { 2 }
    val languages = remember { listOf("Java", "C++") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 72.dp),
        verticalArrangement = Arrangement.Top
    ) {
        CommonTopBar(title = "Stats")

        CommonTabRow(
            tabs = languages,
            selectedIndex = pagerState.currentPage,
            onTabSelected = { index -> coroutineScope.launch { pagerState.animateScrollToPage(index) } }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            UserStatsContent(
                language = languages[page],
                vm = vm,
                navController = navController,
                showFilterDialog = showFilterDialog,
                setShowFilterDialog = setShowFilterDialog // Pass it down
            )
        }
    }
}


@Composable
private fun UserStatsContent(
    language: String,
    vm: StatsViewModel,
    navController: NavController,
    showFilterDialog: Boolean,
    setShowFilterDialog: (Boolean) -> Unit,
) {
    val streakDataMap by vm.statsDataMap.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    val stats by remember(language, streakDataMap) {
        derivedStateOf { streakDataMap[language] ?: emptyList() }
    }

    var selectedYear by remember { mutableStateOf("All") }
    var minQuestions by remember { mutableStateOf("") }

    val years = listOf("All") + stats.map { it.year }.distinct()

    val filteredList = stats.filter { stat ->
        val matchesYear = selectedYear == "All" || stat.year == selectedYear
        val matchesQuestionCount = minQuestions.toIntOrNull()?.let {
            stat.totalSolved >= it
        } ?: true
        matchesYear && matchesQuestionCount
    }

    if (showFilterDialog) {
        FilterBottomSheet(
            years = years,
            selectedYear = selectedYear,
            onYearSelected = { selectedYear = it },
            showActive = false,
            minQuestions = minQuestions,
            onMinQuestionsChange = { minQuestions = it },
            onDismiss = { setShowFilterDialog(false) }
        )
    }

    EnhancedPullToRefresh(
        isRefreshing = isLoading,
        onRefresh = { vm.refreshAll() }
    ) {
        when {
            isLoading -> LoadingScreen()
            filteredList.isEmpty() -> EmptyState(message = "No stats available")

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { stat ->
                        StatsListItem(
                            name = stat.name,
                            username = stat.username,
                            streakMap = stat.submissionCalendar.take(5).reversed(),
                            score = stat.totalSolved,
                            onClick = {
                                navController.navigate(
                                    Routes.OtherProfile.createRoute(stat.username)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun StatsListItem(
    name: String,
    username: String,
    streakMap: List<Boolean>,
    score: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@$username",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Light
                    )
                )
            }

            Row(
                modifier = Modifier.weight(2f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                streakMap.take(5).forEach { active ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (active) Color(0xFF65E26A) else Color(0xFFE45D5D),
                                shape = CircleShape
                            )
                    )
                }
            }

            Text(
                text = score.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}
