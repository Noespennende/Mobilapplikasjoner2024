package com.movielist.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movielist.model.ListItem
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel

class ControllerViewModel (
    private val userViewModel: UserViewModel,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    /* USER LOGIC */

    val loggedInUser = userViewModel.loggedInUser
    val currentUser = authViewModel.currentUser

    fun setLoggedInUser(uid: String) {
        userViewModel.setLoggedInUser(uid)
    }

    fun setOtherUser(uid: String) {
        userViewModel.setOtherUser(uid)
    }

    fun checkUserStatus() {
        authViewModel.checkUserStatus() // Kall autentiseringstatus
    }

    fun getLoggedInUsersFavoriteCollection(): List<ListItem> {
        return userViewModel.getLoggedInUsersFavoriteCollection()
    }

    fun getLoggedInUsersCompletedCollection(): List<ListItem> {
        return userViewModel.getLoggedInUsersCompletedCollection()
    }

    fun getLoggedInUsersWantToWatchCollection(): List<ListItem> {
        return userViewModel.getLoggedInUsersWantToWatchCollection()
    }
    fun getLoggedInUsersDroppedCollection(): List<ListItem> {
        return userViewModel.getLoggedInUsersDroppedCollection()
    }

    fun getLoggedInUsersCurrentlyWatchingCollection(): List<ListItem> {
        return userViewModel.getLoggedInUsersCurrentlyWatchingCollection()
    }


}

