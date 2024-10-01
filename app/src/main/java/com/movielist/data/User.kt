package com.movielist.data

import java.util.UUID

data class User (
    val id: UUID = UUID.randomUUID(),
    val userName: String,
    val profileImageID: Int,
    val completedShows: List<ListItem>,
    val wantToWatchShows: List<ListItem>,
    val droppedShows: List<ListItem>,
    val currentlyWatchingShows: List<ListItem>
)
