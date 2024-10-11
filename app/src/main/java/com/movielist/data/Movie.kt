package com.movielist.data

import java.util.Calendar

data class Movie(
    override val imdbID: String,
    override val title: String,
    override val description: String,
    override val genre: String,
    override val releaseDate: Calendar,
    override val actors: List<String>, // Listen skal ikke endres i appen - data kommer fra API
    override val rating: Int,
    override val reviews: ArrayList<String>, // Så bruker kan se anmeldelsen sin *umiddelbart*
    override val posterUrl: Int,

    val lengthMinutes: Int,
    val trailerUrl: String
) : Production()
{

}
