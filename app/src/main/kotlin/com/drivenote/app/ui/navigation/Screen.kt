package com.drivenote.app.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Detail : Screen("detail/{noteId}") {
        fun createRoute(noteId: String): String = "detail/$noteId"
    }
    data object Filter : Screen("filter?category={category}") {
        fun createRoute(category: String?): String = "filter?category=${Uri.encode(category.orEmpty())}"
    }
}
