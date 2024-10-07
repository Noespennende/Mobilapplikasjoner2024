package com.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.movielist.composables.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initializes Firebase
        enableEdgeToEdge()
        setContent {
            //Background color
            Background()

            //main content
            //FirebaseTesting()
            ReviewPage()
            //FrontPage()

            //menu backgrounds
            TopMobileIconsBackground()
            BottomNavbarAndMobileIconsBackground()

            //Bottom nav bar
            BottomNavBar()

        }
    }
}

