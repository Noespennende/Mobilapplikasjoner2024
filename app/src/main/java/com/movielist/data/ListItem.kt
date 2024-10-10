package com.movielist.data

data class ListItem (
    var currentEpisode: Int = 0,
    var score: Int = 0,
    val show: Show
)