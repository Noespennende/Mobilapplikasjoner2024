package com.movielist.model

import com.movielist.R
import java.util.Calendar

data class TVShow(
    override val imdbID: String = "",
    override val title: String = "",
    override val description: String = "",
    override val genre: List<String> = emptyList(),
    override val releaseDate: Calendar = Calendar.getInstance(),
    override val actors: List<String> = emptyList(), // Listen skal ikke endres i appen - data kommer fra API
    override val rating: Int? = null,
    override val posterUrl: String? = null,
    override val trailerUrl: String = "",

    val episodes: List<String> = emptyList(), // Listen skal ikke endres i appen - data kommer fra API
    val seasons: List<String> = emptyList()
) : Production() {

    // Type skal ikke kunne forandres i konstruktør
    override val type: ProductionType = ProductionType.TVSHOW

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
        posterUrl?.let { map["posterUrl"] = it }
        type.let { map["type"] = it }
        trailerUrl.let { map["trailerUrl"] = it }

        episodes.let { map["episodes"] = it }
        seasons.let { map["seasons"] = it }

        return map
    }
}
