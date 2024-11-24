package com.movielist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import com.google.firebase.FirebaseApp
import com.movielist.composables.*
import com.movielist.controller.ControllerViewModel
import com.movielist.viewmodel.ApiViewModel
import com.movielist.viewmodel.ReviewViewModel

class MainActivity : ComponentActivity() {

    // Må initalisere de andre viewModels-ene i MainActivity og sende inn i controllerViewModel
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val apiViewModel : ApiViewModel by viewModels()
    private val reviewViewModel : ReviewViewModel by viewModels()

    //Notification permission request launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){}

    private lateinit var controllerViewModel: ControllerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initializes Firebase
        enableEdgeToEdge()

        //Check if user has granted permission for Notifications, ask user to do so if not
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        controllerViewModel = ControllerViewModel(userViewModel, authViewModel, apiViewModel, reviewViewModel)

        controllerViewModel.checkUserStatus()

        setContent {

            val firebaseUser by controllerViewModel.currentFirebaseUser.collectAsState()

            LaunchedEffect(firebaseUser) {
                if (firebaseUser != null) {
                    controllerViewModel.setLoggedInUser(firebaseUser!!.uid)

                
                }

            }


            Background()

            //FirebaseTesting(controllerViewModel)
            Navigation(controllerViewModel)

        }
    }
}

