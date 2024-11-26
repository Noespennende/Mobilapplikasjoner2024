package com.movielist.model

import com.movielist.R
import java.util.Calendar

data class Movie(
    override val imdbID: String = "",
    override val title: String = "",
    override val description: String = "",
    override val genre: List<String> = emptyList(),
    override val releaseDate: Calendar = Calendar.getInstance(),
    override val actors: List<String> = emptyList(), // Listen skal ikke endres i appen - data kommer fra API
    override val rating: Int? = null,
    override val reviews: List<String> = emptyList(), // Så bruker kan se anmeldelsen sin *umiddelbart*
    override val posterUrl: String? = null,
    override val trailerUrl: String = "",

    val lengthMinutes: Int? = null,

) : Production() {

    // Type skal ikke kunne forandres i konstruktør
    override val type: ProductionType = ProductionType.MOVIE


    override fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        // Legg til felt kun hvis de ikke er null
        imdbID.let { map["imdbID"] = it }
        title.let { map["title"] = it }
        description.let { map["description"] = it }
        genre.let { map["genre"] = it }
        releaseDate.let { map["releaseDate"] = it.timeInMillis }
        actors.let { map["actors"] = it }
        rating?.let { map["rating"] = it }
        reviews.let { map["reviews"] = it }
        posterUrl?.let { map["posterUrl"] = it }
        type.let { map["type"] = it }
        trailerUrl.let { map["trailerUrl"] = it }

        lengthMinutes?.let { map["lengthMinutes"] = it }

        return map
    }
}
