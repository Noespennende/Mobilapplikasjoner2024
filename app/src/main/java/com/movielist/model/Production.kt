package com.movielist.model

import java.util.Calendar

sealed class Production(
    open val imdbID: String = "",
    open val title: String = "",
    open val description: String = "",
    open val genre: List<String> = emptyList(),
    open val releaseDate: Calendar = Calendar.getInstance(),
    open val actors: List<String> = emptyList(),
    open val rating: Int? = null,
    open val reviews: List<String> = emptyList(),
    open val posterUrl: String? = null,
    open val trailerUrl: String? = null,

) {

    abstract val type: String

    abstract fun toMap(): Map<String, Any>
}
