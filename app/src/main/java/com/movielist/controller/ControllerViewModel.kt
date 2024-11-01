package com.movielist.controller

import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.movielist.data.addCurrentlyWatchingShow
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.User
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class ControllerViewModel(
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
        authViewModel.checkUserStatus() // Call authentication status check
    }

    // This function is for testing purposes - DELETE LATER
    fun addToShowTest() {
        val loggedInUserId = currentFirebaseUser.value?.uid
        if (loggedInUserId != null) {
            val newListItem = ListItem(
                currentEpisode = 4,
                score = 5,
                production = Movie(
                    imdbID = "tt2096673",
                    title = "Inside Out",
                    description = "When 11-year-old Riley moves to a new city, her Emotions team up to help her through the transition. Joy, Fear, Anger, Disgust and Sadness work together, but when Joy and Sadness get lost, they must journey through unfamiliar places to get back home",
                    genre = listOf("Animation", "Family", "Drama", "Comedy"),
                    releaseDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2015)
                        set(Calendar.MONTH, Calendar.JUNE) // Remember that months are 0-indexed
                        set(Calendar.DAY_OF_MONTH, 17)
                    },
                    lengthMinutes = 120,
                    actors = listOf(),
                    rating = 7,
                    reviews = listOf("reviewid0300", "reviewid0431"),
                    posterUrl = "https://image.tmdb.org/t/p/w500/2H1TmgdfNtsKlU9jKdeNyYL5y8T.jpg"
                )
            )

            addCurrentlyWatchingShow(
                userID = loggedInUserId,
                listItem = newListItem,
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


    private val _friendsWatchedList = MutableStateFlow<List<ListItem>>(emptyList())
    val friendsWatchedList: StateFlow<List<ListItem>> get() = _friendsWatchedList

    private val _friendsJustWatchedLoading = MutableLiveData<Boolean>(true)
    val friendsJustWatchedLoading: LiveData<Boolean> get() = _friendsJustWatchedLoading



    init {
        // Lytt etter endringer i loggedInUser og kall getProductionsFromFriendsWatchedList når den er oppdatert
        viewModelScope.launch {
            loggedInUser.collect { user ->
                if (user != null) {
                    getProductionsFromFriendsWatchedList()
                }
            }
        }
    }


    private fun getProductionsFromFriendsWatchedList() {
        _friendsJustWatchedLoading.value = true

        viewModelScope.launch {
            userViewModel.getUsersFriends { friendsList ->
                val recentFriendsWatched = mutableListOf<ListItem>()

                if (friendsList.isNotEmpty()) {

                    if(friendsList.size > 2) {
                        for (friend in friendsList) {
                            friend.completedShows.lastOrNull()?.let { recentFriendsWatched.add(it) }
                        }
                    } else {
                        for (friend in friendsList) {
                            friend.completedShows.takeLast(2).forEach { recentFriendsWatched.add(it) }
                        }
                    }
                }

                // Oppdater StateFlow
                _friendsWatchedList.value = recentFriendsWatched
                _friendsJustWatchedLoading.value = false
            }
        }
    }



}
