package com.movielist

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.movielist.viewmodel.UserViewModel
import com.movielist.composables.BottomNavBar
import com.movielist.composables.BottomNavbarAndMobileIconsBackground
import com.movielist.screens.FrontPage
import com.movielist.screens.ListPage
import com.movielist.screens.ProfilePage
import com.movielist.screens.ReviewPage
import com.movielist.screens.SearchPage


@Composable
fun Navigation (userViewModel: UserViewModel){
    //Nav controller
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route) {
        composable(
            route = Screen.HomeScreen.withArguments()
        ) {
            FrontPage()
        }
        composable(
            route = Screen.ListScreen.withArguments()
        ) {
            ListPage(userViewModel)
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
            ProfilePage()
        }
    }

    //Navbar graphics
    BottomNavbarAndMobileIconsBackground()
    BottomNavBar(navController = navController)
}