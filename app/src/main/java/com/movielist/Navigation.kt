package com.movielist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movielist.composables.BottomNavBar
import com.movielist.composables.BottomNavbarAndMobileIconsBackground
import com.movielist.composables.TopNav
import com.movielist.controller.ControllerViewModel
import com.movielist.model.NavbarOptions
import com.movielist.model.Screens
import com.movielist.model.User
import com.movielist.screens.ComparisonScreen
import com.movielist.screens.CreateUserScreen
import com.movielist.screens.HomeScreen
import com.movielist.screens.ListScreen
import com.movielist.screens.LoginPage
import com.movielist.screens.ProductionScreen
import com.movielist.screens.ProfilePage
import com.movielist.screens.ReviewsScreen
import com.movielist.screens.ReviewScreen
import com.movielist.screens.SearchPage
import com.movielist.screens.SettingsScreen
import com.movielist.screens.WriteReviewScreen


@Composable
fun Navigation (controllerViewModel: ControllerViewModel, localStorage: DataStore<Preferences>) {
    //Nav controller
    val navController = rememberNavController()
    controllerViewModel.checkUserStatus()
    val isLoggedIn by controllerViewModel.isLoggedIn
    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()
    var aNavButtonIsActive by remember { mutableStateOf<Boolean>(true) }
    var activeNavButton by remember { mutableStateOf(NavbarOptions.HOME) }
    var currentScreen by remember { mutableStateOf("Home") }

    val handleScreenNameChange: (screen: Screens) -> Unit = {screen ->

        if (screen == Screens.WRITEREVIEW){
            currentScreen = "Write review"
        } else if (screen == Screens.LOGINN){
            currentScreen = "Login"
        } else if (screen == Screens.CREATEUSER) {
            currentScreen = "Create user"
        }
        else {
            currentScreen = screen.toString().lowercase().replaceFirstChar { char ->
                char.uppercase()
            }
        }
    }

    val handleNavButtonClick: (navOption: NavbarOptions) -> Unit = {navOption ->
        activeNavButton = navOption
        if(navOption == NavbarOptions.HOME){
            navController.navigate(Screen.HomeScreen.route)
        } else if (navOption == NavbarOptions.LIST){
            navController.navigate(Screen.ListScreen.withArguments(loggedInUser?.id.toString()))
        } else if (navOption == NavbarOptions.REVIEW){
            navController.navigate(Screen.ReviewsScreen.route)
        } else if (navOption == NavbarOptions.PROFILE){
            navController.navigate(Screen.ProfileScreen.withArguments(loggedInUser?.id.toString()))
        }else if (navOption == NavbarOptions.SEARCH){
            navController.navigate(Screen.SearchScreen.route)
        }
    }

    val handleTopNavProfileClick: () -> Unit = {
        if (loggedInUser != null){
            val user = loggedInUser as User
            handleScreenNameChange(Screens.PROFILE)
            navController.navigate(Screen.ProfileScreen.withArguments(user.id))
        }
    }

    val handleTopNavSettingsClick: () -> Unit = {
        if (loggedInUser != null) {
            handleScreenNameChange(Screens.SETTINGS)
            navController.navigate(Screen.SettingsScreen.withArguments())
        }
    }

    val handleLogoClick: () -> Unit = {
        handleScreenNameChange(Screens.HOME)
        navController.navigate(Screen.HomeScreen.withArguments())
    }

    val startScreen =
        if (!isLoggedIn) {
            Screen.LoginScreen.route
        }
        else {
            Screen.HomeScreen.route
        }

    NavHost(
        navController = navController,
        startDestination = startScreen
    ) {
        composable(
            route = Screen.LoginScreen.route // Legger til LoginScreen
        ) {
            LoginPage(controllerViewModel, navController)
            aNavButtonIsActive = false
            activeNavButton = NavbarOptions.NONE
            handleScreenNameChange(Screens.LOGINN)

        }
        composable(
            route = Screen.HomeScreen.withArguments()
        ) {
            HomeScreen(controllerViewModel, navController)
            aNavButtonIsActive = true
            handleScreenNameChange(Screens.HOME)
            activeNavButton = NavbarOptions.HOME
        }
        composable(
            route = Screen.ListScreen.withArguments() + "/{userID}",
            arguments = listOf(
                navArgument("userID") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            ListScreen(
                controllerViewModel,
                navController,
                userID = entry.arguments?.getString("userID")
            )
            handleScreenNameChange(Screens.LIST)
            if(entry.arguments?.getString("userID") == loggedInUser?.id.toString())
            {
                aNavButtonIsActive = true
                activeNavButton = NavbarOptions.LIST
            } else {
                aNavButtonIsActive = false
                activeNavButton = NavbarOptions.NONE
            }

        }
        composable(
            route = Screen.SearchScreen.withArguments()
        ) {
            SearchPage(controllerViewModel, navController)
            aNavButtonIsActive = true
            activeNavButton = NavbarOptions.SEARCH
            handleScreenNameChange(Screens.SEARCH)
        }
        composable(
            route = Screen.ReviewsScreen.withArguments()
        ) {
            ReviewsScreen(controllerViewModel, navController)
            handleScreenNameChange(Screens.REVIEWS)
            aNavButtonIsActive = true
            activeNavButton = NavbarOptions.REVIEW
        }
        composable(
            route = Screen.ProfileScreen.withArguments() + "/{userID}",
            arguments = listOf(
                navArgument("userID") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            ProfilePage(
                controllerViewModel,
                navController,
                userID = entry.arguments?.getString("userID")
            )
            handleScreenNameChange(Screens.PROFILE)
            if(entry.arguments?.getString("userID") == loggedInUser?.id.toString())
            {
                aNavButtonIsActive = true
                activeNavButton = NavbarOptions.PROFILE
            } else {
                aNavButtonIsActive = false
                activeNavButton = NavbarOptions.NONE
            }

        }
        composable(
            route = Screen.CreateUserScreen.withArguments()
        ) {
            CreateUserScreen(controllerViewModel, navController)
            aNavButtonIsActive = false
            handleScreenNameChange(Screens.CREATEUSER)
            activeNavButton = NavbarOptions.NONE
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
                navController,
                controllerViewModel,
                productionID = entry.arguments?.getString("productionID"),
                productionType = entry.arguments?.getString("productionType")
            )
            handleScreenNameChange(Screens.PRODUCTION)
            aNavButtonIsActive = false
            activeNavButton = NavbarOptions.NONE
        }
        composable(
            route = Screen.ReviewScreen.withArguments() + "/{reviewID}",
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
                reviewID = entry.arguments?.getString("reviewID")
            )
            handleScreenNameChange(Screens.REVIEW)
            aNavButtonIsActive = false
            activeNavButton = NavbarOptions.NONE
        }
        composable(
            route = Screen.SettingsScreen.withArguments() + "",
            arguments = listOf(
                navArgument("") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        )
        { entry ->
            SettingsScreen(
                controllerViewModel,
                navController,
                localStorage
            )
            handleScreenNameChange(Screens.SETTINGS)
            aNavButtonIsActive = false
            activeNavButton = NavbarOptions.NONE
        }
        composable(
            route = Screen.ComparisonScreen.withArguments() + "/{userToCompareToID:}",
            arguments = listOf(
                navArgument("userToCompareToID:") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        )
        { entry ->
            ComparisonScreen(
                controllerViewModel,
                navController,
                userToCompareToID = entry.arguments?.getString("userToCompareToID:")
            )
            handleScreenNameChange(Screens.COMPARISON)
            aNavButtonIsActive = false
            activeNavButton = NavbarOptions.NONE
        }
        composable(
            route = Screen.WriteReviewScreen.withArguments() + "/{productionID}/{productionType}",
            arguments = listOf(
                navArgument("userToCompareToID:") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        )
        { entry ->
            WriteReviewScreen(
                controllerViewModel,
                navController,
                productionID = entry.arguments?.getString("productionID"),
                productionType = entry.arguments?.getString("productionType")
            )
            handleScreenNameChange(Screens.WRITEREVIEW)
            aNavButtonIsActive = false
            activeNavButton = NavbarOptions.NONE
        }
    }

    //Navbar graphics
    if (isLoggedIn) {
        TopNav(
            loggedInUser = loggedInUser,
            CurrentScreen = currentScreen,
            handleProfileClick = handleTopNavProfileClick,
            handleLogoClick = handleLogoClick,
            handleSettingsClick = handleTopNavSettingsClick
        )
        BottomNavbarAndMobileIconsBackground()
        BottomNavBar(
            activeNavButton = activeNavButton,
            handleNavButtonClick = handleNavButtonClick)
    }
}
