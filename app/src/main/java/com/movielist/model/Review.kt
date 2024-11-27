package com.movielist.model

import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.UUID

data class Review(
    val reviewID: String = UUID.randomUUID().toString(),
    val score: Int,
    val reviewerID: String,
    val productionID: String,
    val reviewBody: String,
    val postDate: Calendar = Calendar.getInstance(),
    var likes: Long = 0,
    var likedByUsers: List<String> = emptyList()
) {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "reviewID" to reviewID,
            "score" to score,
            "reviewerID" to reviewerID,
            "productionID" to productionID,
            "reviewBody" to reviewBody,
            "postDate" to Timestamp(postDate.time),
            "likes" to likes,
            "likedByUsers" to likedByUsers
        )
    }
}


data class ReviewDTO(
    val reviewID: String,
    val score: Int,
    val reviewerID: String,
    val productionID: String,
    val reviewBody: String,
    val postDate: Calendar = Calendar.getInstance(),
    var likes: Long = 0,
    val likedByUsers: List<String?> = emptyList(), // Lagrer hvem som har likt
    val reviewerUserName: String,
    val reviewerProfileImage: String?,
    val productionPosterUrl: String?,
    val productionTitle: String,
    val productionReleaseDate: Calendar,
    val productionType: ProductionType,
)


