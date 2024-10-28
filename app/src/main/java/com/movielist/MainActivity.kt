package com.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import com.google.firebase.FirebaseApp
import com.movielist.composables.*

class MainActivity : ComponentActivity() {

    /*
    Må ha felles instans av authViewModel når vi har navigasjon klar
    Så sendes authViewModel inn i hvert komponent.
    */
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initializes Firebase
        enableEdgeToEdge()
        setContent {

            val firebaseUser by authViewModel.currentUser.collectAsState()
            authViewModel.checkUserStatus()

            val testUser = "LVE5ZfTvycg09HX11rdcIsW0rVf2"

            LaunchedEffect(testUser) {
                userViewModel.fetchLoggedInUser(testUser)
            }

            Background()

            Navigation(userViewModel)

            TopMobileIconsBackground()



        }
    }
}

