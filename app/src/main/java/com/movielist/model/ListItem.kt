package com.movielist.model

import android.util.Log
import com.movielist.data.FirebaseTimestampAdapter
import com.movielist.data.UUIDAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Calendar
import java.util.UUID

data class ListItem (
    var id: String = UUID.randomUUID().toString(),
    var currentEpisode: Int = 0,
    var score: Int = 0,
    val production: Production = Movie(),
    var lastUpdated: Calendar = Calendar.getInstance(),
    var loggedInUsersFavorite: Boolean = false,
) {


    fun toMap(): Map<String, Any> {

        val moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(Production::class.java, "type")
                    .withSubtype(Movie::class.java, "Movie")
                    .withSubtype(TVShow::class.java, "TVShow")
                    .withSubtype(Episode::class.java, "Episode")
            )
            .add(FirebaseTimestampAdapter())  // Adapter for Firestore Timestamps
            .add(UUIDAdapter())  // Adapter for UUID
            .addLast(KotlinJsonAdapterFactory())  // For Kotlin-klasser
            .build()
        // Konverter ListItem til JSON-streng
        val jsonAdapter = moshi.adapter(ListItem::class.java)
        val json = jsonAdapter.toJson(this)

        Log.d("ListItemToJson", "Converted ListItem to JSON: $json")

        // Definer typen Map<String, Any> eksplisitt ved å bruke Types.newParameterizedType
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        val mapAdapter = moshi.adapter<Map<String, Any>>(mapType)

        // Konverter JSON-streng til Map<String, Any>
        val map: Map<String, Any> = mapAdapter.fromJson(json) ?: emptyMap()

        return map
    }

}