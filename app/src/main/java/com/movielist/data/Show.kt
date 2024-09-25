package com.movielist.data

data class Show (
    val title: String,
    val length: Int,
    val imageID: Int,
    val currentEpisode: Int = 0,
    val imageDescription: String
)

