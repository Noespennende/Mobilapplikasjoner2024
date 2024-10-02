package com.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.movielist.composables.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //Background color
            Background()

            //main content
            LoginPage()

            //menu backgrounds
            TopMobileIconsBackground()
            BottomNavbarAndMobileIconsBackground()

            //Bottom nav bar
            BottomNavBar()

        }
    }
}

