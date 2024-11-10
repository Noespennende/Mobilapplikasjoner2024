package com.movielist.model

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
)


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
    val productionType: String,
)