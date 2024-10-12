package com.movielist.data

import java.util.Calendar
import java.util.UUID

data class ListItem (
    var id: String = UUID.randomUUID().toString(),
    var currentEpisode: Int = 0,
    var score: Int = 0,
    val production: Production = Movie(),
    var lastUpdated: Calendar = Calendar.getInstance(),
) {

}