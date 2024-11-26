package com.movielist.model

import android.util.Log
import com.google.firebase.Timestamp
import com.movielist.data.FirebaseTimestampAdapter
import com.movielist.data.UUIDAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Calendar
import java.util.UUID

data class Review(
    val reviewID: String = UUID.randomUUID().toString(),
    val score: Int,
    val reviewerID: String,
    val productionID: String,
    val reviewBody: String,
    val postDate: Calendar = Calendar.getInstance(),
    var likes: Int = 0
) {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "reviewID" to reviewID,
            "score" to score,
            "reviewerID" to reviewerID,
            "productionID" to productionID,
            "reviewBody" to reviewBody,
            "postDate" to Timestamp(postDate.time),
            "likes" to likes
        )
    }
}


data class ReviewDTO(
    val reviewID: String, //UUID = UUID.randomUUID(),
    val score: Int,
    val reviewerID: String,
    val productionID: String,
    val reviewBody: String,
    val postDate: Calendar = Calendar.getInstance(),
    var likes: Int = 0,
    val reviewerUserName: String,
    val reviewerProfileImage: String?,
    val productionPosterUrl: String?,
    val productionTitle: String,
    val productionReleaseDate: Calendar,
    val productionType: ProductionType,
)


