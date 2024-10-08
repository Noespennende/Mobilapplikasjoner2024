package com.movielist.data

import java.util.Calendar

abstract class Production {
    abstract val imdbID: String
    abstract val title: String
    abstract val description: String
    abstract val genre: String
    abstract val releaseDate: Calendar
    abstract val actors: List<String>
    abstract val rating: Int
    abstract val reviews: List<String>
    abstract val posterUrl: Int
}