package com.movielist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.movielist.composables.BottomNavBar
import com.movielist.composables.BottomNavbarAndMobileIconsBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.screens.CreateUserScreen
import com.movielist.screens.FrontPage
import com.movielist.screens.ListScreen
import com.movielist.screens.LoginPage
import com.movielist.screens.ProductionScreen
import com.movielist.screens.ProfilePage
import com.movielist.screens.ReviewPage
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

            Screen.ProductionScreen.route
            /*
            Screen.HomeScreen.route
            */



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
            FrontPage(controllerViewModel)
        }
        composable(
            route = Screen.ListScreen.withArguments()
        ) {
            ListScreen(controllerViewModel)
        }
        composable(
            route = Screen.SearchScreen.withArguments()
        ) {
            SearchPage()
        }
        composable(
            route = Screen.ReviewsScreen.withArguments()
        ) {
            ReviewPage()
        }
        composable(
            route = Screen.ProfileScreen.withArguments()
        ) {
            ProfilePage(controllerViewModel)
        }
        composable(
            route = Screen.CreateUserScreen.withArguments()
        ) {
            CreateUserScreen(controllerViewModel, navController)
        }
        composable(
            route = Screen.ProductionScreen.withArguments()
        ) {
            ProductionScreen(controllerViewModel)
        }
    }

    //Navbar graphics
    if (isLoggedIn) {
        BottomNavbarAndMobileIconsBackground()
        BottomNavBar(navController = navController)
    }
}