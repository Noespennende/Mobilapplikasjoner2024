package com.movielist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movielist.composables.BottomNavBar
import com.movielist.composables.BottomNavbarAndMobileIconsBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.screens.CreateUserScreen
import com.movielist.screens.HomeScreen
import com.movielist.screens.ListScreen
import com.movielist.screens.LoginPage
import com.movielist.screens.ProductionScreen
import com.movielist.screens.ProfilePage
import com.movielist.screens.ReviewPage
import com.movielist.screens.ReviewScreen
import com.movielist.screens.SearchPage


@Composable
fun Navigation (controllerViewModel: ControllerViewModel){
    //Nav controller
    val navController = rememberNavController()

    controllerViewModel.checkUserStatus()

    val isLoggedIn by controllerViewModel.isLoggedIn
    val startScreen =
        if (!isLoggedIn) {
            Screen.LoginScreen.route
        } else {
            // Screen.ReviewScreen.route
            Screen.HomeScreen.route

        }

    NavHost(
        navController = navController,
        startDestination = startScreen) {
        composable(
            route = Screen.LoginScreen.route // Legger til LoginScreen
        ) {
            LoginPage(controllerViewModel, navController)
        }
        composable(
            route = Screen.HomeScreen.withArguments()
        ) {
            HomeScreen(controllerViewModel, navController)
        }
        composable(
            route = Screen.ListScreen.withArguments()
        ) {
            ListScreen(controllerViewModel, navController)
        }
        composable(
            route = Screen.SearchScreen.withArguments()
        ) {
            SearchPage(controllerViewModel, navController)
        }
        composable(
            route = Screen.ReviewsScreen.withArguments()
        ) {
            ReviewPage(controllerViewModel, navController)
        }
        composable(
            route = Screen.ProfileScreen.withArguments()
        ) {
            ProfilePage(controllerViewModel, navController)
        }
        composable(
            route = Screen.CreateUserScreen.withArguments()
        ) {
            CreateUserScreen(controllerViewModel, navController)
        }
        composable(
            route = Screen.ProductionScreen.withArguments() + "/{productionID}/{productionType}",
            arguments = listOf(
                navArgument("productionID") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("productionType") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            ProductionScreen(
                controllerViewModel,
                productionID = entry.arguments?.getString("productionID"),
                productionType = entry.arguments?.getString("productionType"))
        }
        composable(
            route = Screen.ReviewScreen.withArguments(),
            arguments = listOf(
                navArgument("reviewID") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        )
            { entry ->
            ReviewScreen(
                controllerViewModel,
                navController,
                reviewID = entry.arguments?.getString("productionID"))
            }
    }

    //Navbar graphics
    if (isLoggedIn) {
        BottomNavbarAndMobileIconsBackground()
        BottomNavBar(navController = navController)
    }
}