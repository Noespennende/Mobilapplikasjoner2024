package com.movielist


sealed class Screen (
    val route: String
) {
    object HomeScreen : Screen("home_screen")
    object ListScreen : Screen("list_screen")
    object SearchScreen : Screen("search_screen")
    object ReviewsScreen : Screen("reviews_screen")
    object ProfileScreen : Screen("profile_screen")
    object LoginScreen : Screen("login_screen")
    object CreateUserScreen : Screen("createUser_screen")
    object ProductionScreen : Screen("production_screen")
    object ReviewScreen : Screen("review_screen")
    object ComparisonScreen: Screen("comparison_screen")
    object SettingsScreen: Screen("Settings_Screen")
    object WriteReviewScreen: Screen("WriteReview_Screen")


    fun withArguments(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}