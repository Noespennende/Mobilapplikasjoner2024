package com.movielist.controller

import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.movielist.data.addCurrentlyWatchingShow
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.User
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import kotlin.math.log

class ControllerViewModel (
    private val userViewModel: UserViewModel,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    /* USER LOGIC */

    val currentFirebaseUser: StateFlow<FirebaseUser?> = authViewModel.currentUser

    val isLoggedIn: State<Boolean> = authViewModel.isLoggedIn
    val loggedInUser: StateFlow<User?> = userViewModel.loggedInUser
    val otherUser: StateFlow<User?> = userViewModel.otherUser

    fun setLoggedInUser(uid: String) {
        userViewModel.setLoggedInUser(uid)
    }

    fun setOtherUser(uid: String) {
        userViewModel.setOtherUser(uid)
    }

    fun checkUserStatus() {
        authViewModel.checkUserStatus() // Kall autentiseringstatus
    }

    // Kall denne i MainActivity i LaunchedEffect for å teste
    // SLETTES SENERE
    fun addToShowTest() {

        val loggedInUserId = currentFirebaseUser.value?.uid
        if (loggedInUserId != null) {
            val newListItem = ListItem(
                currentEpisode = 1,
                score = 5,
                production = Movie(
                    imdbID = "tt1234567",
                    title = "New Movie",
                    description = "A great new movie.",
                    genre = listOf("Action", "Adventure"),
                    releaseDate = Calendar.getInstance(), // Bruk dagens dato
                    actors = listOf("Actor 1", "Actor 2"),
                    rating = 8,
                    reviews = listOf("Great movie!", "Must watch."),
                    posterUrl = "http://example.com/movie.jpg",
                    lengthMinutes = 120,
                    trailerUrl = "http://example.com/trailer.mp4"
                )
            )

            addCurrentlyWatchingShow(userID = loggedInUserId, listItem = newListItem,
                onSuccess = {
                    Log.d("Controller", "Show added to currently watching list successfully.")
                },
                onFailure = { errorMessage ->
                    Log.e("Controller", "Failed to add show: $errorMessage")
                }
            )
        } else {
            Log.w("Controller", "User is not logged in.")
        }
    }




}

