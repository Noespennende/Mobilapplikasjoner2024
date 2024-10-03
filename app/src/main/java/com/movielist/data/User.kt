package com.movielist.data

import java.util.UUID

data class User (
    val id: UUID = UUID.randomUUID(),
    val userName: String,
    val email: String,
    val friendList: List<User> = emptyList(),
    val myReviews: List<Review> = emptyList(),
    val favoriteCollection: List<ListItem>,
    val profileImageID: Int,
    val completedShows: List<ListItem>,
    val wantToWatchShows: List<ListItem>,
    val droppedShows: List<ListItem>,
    val currentlyWatchingShows: List<ListItem>
)

fun createNewUser(
    userName: String,
    email: String,
    friendList: List<User> = emptyList(),
    myReviews: List<Review> = emptyList(),
    favoriteCollection: List<ListItem> = emptyList(),
    profileImageID: Int,
    completedShows: List<ListItem> = emptyList(),
    wantToWatchShows: List<ListItem> = emptyList(),
    droppedShows: List<ListItem> = emptyList(),
    currentlyWatchingShows: List<ListItem> = emptyList()
): User {
    return User(
        userName = userName,
        email = email,
        friendList = friendList,
        myReviews = myReviews, 
        favoriteCollection = favoriteCollection,
        profileImageID = profileImageID,
        completedShows = completedShows,
        wantToWatchShows = wantToWatchShows,
        droppedShows = droppedShows,
        currentlyWatchingShows = currentlyWatchingShows
    )
}


