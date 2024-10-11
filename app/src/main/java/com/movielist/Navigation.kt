package com.movielist

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.movielist.composables.BottomNavBar
import com.movielist.composables.BottomNavbarAndMobileIconsBackground
import com.movielist.composables.FrontPage
import com.movielist.composables.ListPage
import com.movielist.composables.ProfilePage
import com.movielist.composables.ReviewPage
import com.movielist.composables.SearchPage


@Composable
fun Navigation (){
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
            ListPage()
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