package com.movielist.model

import com.movielist.R
import java.util.Calendar

data class Episode(
    override val imdbID: String = "",
    override val title: String = "",
    override val description: String = "",
    override val genre: List<String> = emptyList(),
    override val releaseDate: Calendar = Calendar.getInstance(),
    override val actors: List<String> = emptyList(), // Listen skal ikke endres i appen - data kommer fra API
    override val rating: Int? = null,
    override val reviews: List<String> = emptyList(), // Så bruker kan se anmeldelsen sin *umiddelbart*
    override val posterUrl: String? = null,
    override val type: String = "Episode",

    val lengthMinutes: Int? = null,
    val seasonNr: Int = 0,
    val episodeNr: Int = 0,
) : Production()
