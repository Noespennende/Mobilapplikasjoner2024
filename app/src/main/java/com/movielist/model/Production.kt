package com.movielist.model

import java.util.Calendar

sealed class Production {

    abstract val imdbID: String
    abstract val title: String
    abstract val description: String
    abstract val genre: List<String>
    abstract val releaseDate: Calendar
    abstract val actors: List<String>  // Listen skal ikke endres i appen - data kommer fra API
    abstract val rating: Int?
    abstract val reviews: List<String> // Så bruker kan se anmeldelsen sin *umiddelbart*
    abstract val posterUrl: Int
    abstract val type: String

}
