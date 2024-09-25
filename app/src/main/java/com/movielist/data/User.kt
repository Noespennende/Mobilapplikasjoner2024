package com.movielist.data

data class User (
    val userName: String,
    val profileImageID: Int,
    val completedShows: List<Show>,
    val wantToWatchShows: List<Show>,
    val droppedShows: List<Show>,
    val currentlyWatchingShows: List<Show>
)
