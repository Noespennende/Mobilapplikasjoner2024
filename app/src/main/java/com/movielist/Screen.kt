package com.movielist

import com.google.common.io.Files.append

sealed class Screen (
    val route: String
) {
    object HomeScreen : Screen("home_screen")
    object ListScreen : Screen("list_screen")
    object SearchScreen : Screen("search_screen")
    object ReviewsScreen : Screen("reviews_screen")
    object ProfileScreen : Screen("profile_screen")
    object LoginScreen : Screen("login_screen")

    fun withArguments(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}