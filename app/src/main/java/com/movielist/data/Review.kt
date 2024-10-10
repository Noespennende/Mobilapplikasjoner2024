package com.movielist.data

import java.util.Calendar
import java.util.UUID

data class Review (
    val reviewId: UUID = UUID.randomUUID(),
    val score: Int,
    val reviewer: User,
    val show: Show,
    val reviewBody: String,
    val postDate: Calendar = Calendar.getInstance(),
    var likes: Int = 0
)