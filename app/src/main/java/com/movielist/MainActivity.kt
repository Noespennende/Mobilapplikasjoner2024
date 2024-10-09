package com.movielist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import backend.AuthViewModel
import com.google.firebase.FirebaseApp
import com.movielist.composables.*
import com.movielist.data.Movie
import com.movielist.data.Production
import java.util.Calendar

class MainActivity : ComponentActivity() {

    /*
    Må ha felles instans av authViewModel når vi har navigasjon klar
    Så sendes authViewModel inn i hvert komponent.
    */ 
    //private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initializes Firebase
        enableEdgeToEdge()
        setContent {
            //Background color
            Background()

            //main content
            FirebaseTesting()
            //ProfilePage()
            //FrontPage()
            //ListPage()


            //menu backgrounds
            TopMobileIconsBackground()
            BottomNavbarAndMobileIconsBackground()

            //Bottom nav bar
            BottomNavBar()

        }
    }
}

