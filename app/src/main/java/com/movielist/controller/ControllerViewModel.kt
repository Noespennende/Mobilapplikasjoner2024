package com.movielist.controller

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.movielist.model.ListItem
import com.movielist.model.User
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import kotlinx.coroutines.flow.StateFlow

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

}

