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
import com.movielist.controller.ControllerViewModel

class MainActivity : ComponentActivity() {

    // Må initalisere de andre viewModels-ene i MainActivity og sende inn i controllerViewModel
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var controllerViewModel: ControllerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initializes Firebase
        enableEdgeToEdge()

        controllerViewModel = ControllerViewModel(userViewModel, authViewModel)

        controllerViewModel.checkUserStatus()

        setContent {

            val firebaseUser by controllerViewModel.currentFirebaseUser.collectAsState()


            //val testUser = "LVE5ZfTvycg09HX11rdcIsW0rVf2"

            LaunchedEffect(firebaseUser) {
                if (firebaseUser != null) {
                    controllerViewModel.setLoggedInUser(firebaseUser!!.uid)

                    //controllerViewModel.addToShowTest()
                }

            }


            Background()

            //FirebaseTesting()
            Navigation(controllerViewModel)

            TopMobileIconsBackground()



        }
    }
}

