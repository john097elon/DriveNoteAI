package com.drivenote.app.ui.navigation

import android.net.Uri
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.drivenote.app.ui.detail.DetailScreen
import com.drivenote.app.ui.detail.DetailViewModel
import com.drivenote.app.ui.filter.FilterScreen
import com.drivenote.app.ui.filter.FilterViewModel
import com.drivenote.app.ui.home.HomeScreen
import com.drivenote.app.ui.home.HomeViewModel
import com.drivenote.app.ui.search.SearchScreen
import com.drivenote.app.ui.search.SearchViewModel
import com.drivenote.app.ui.settings.SettingsScreen
import com.drivenote.app.ui.settings.SettingsViewModel
import com.drivenote.app.ui.voice.VoiceScreen

@Composable
fun DriveNoteNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val rootScreens = listOf(Screen.Home, Screen.Search, Screen.Settings)

    Scaffold(
        bottomBar = {
            if (currentDestination?.route?.startsWith("detail/") != true &&
                currentDestination?.route?.startsWith("filter") != true &&
                currentDestination?.route?.startsWith("voice") != true
            ) {
                NavigationBar {
                    rootScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination.isTopLevelDestination(screen),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(bottomLabel(screen).first().toString()) },
                            label = { Text(bottomLabel(screen)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onRecordClick = viewModel::toggleRecording,
                    onOpenVoiceConversation = { navController.navigate(Screen.Voice.route) },
                    onNoteClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                    onCategorySelected = viewModel::selectCategory,
                    onOpenFilter = { category ->
                        navController.navigate(Screen.Filter.createRoute(category?.label))
                    },
                    onSyncClick = viewModel::syncNow,
                    onClearError = viewModel::clearError
                )
            }

            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SearchScreen(
                    uiState = uiState,
                    onQueryChange = viewModel::onQueryChange,
                    onNoteClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
                )
            }

            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SettingsScreen(
                    uiState = uiState,
                    onEndpointChanged = viewModel::setEndpoint,
                    onAuthTokenChanged = viewModel::setAuthToken,
                    onSaveEndpoint = viewModel::saveEndpoint,
                    onSyncNow = viewModel::syncNow,
                    onClearStatus = viewModel::clearStatus
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.StringType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId").orEmpty()
                val viewModel: DetailViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(noteId) { viewModel.load(noteId) }
                DetailScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onDelete = { viewModel.delete { navController.popBackStack() } },
                    onCategoryChange = viewModel::updateCategory,
                    onAddTag = viewModel::addTag,
                    onRemoveTag = viewModel::removeTag,
                    onSave = viewModel::save,
                    onClearError = viewModel::clearError
                )
            }

            composable(
                route = Screen.Filter.route,
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                val viewModel: FilterViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(category) { viewModel.load(category?.let(Uri::decode)) }
                FilterScreen(
                    uiState = uiState,
                    onCategorySelect = viewModel::select,
                    onBack = { navController.popBackStack() },
                    onNoteClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
                )
            }

            composable(Screen.Voice.route) {
                VoiceScreen()
            }
        }
    }
}

private fun bottomLabel(screen: Screen): String {
    return when (screen) {
        Screen.Home -> "홈"
        Screen.Search -> "검색"
        Screen.Settings -> "설정"
        else -> ""
    }
}

private fun NavDestination?.isTopLevelDestination(screen: Screen): Boolean {
    return this?.hierarchy?.any { it.route == screen.route } == true
}
