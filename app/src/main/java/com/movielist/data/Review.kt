package com.movielist.data

import java.util.Calendar

data class Review (
    val score: Int,
    val reviewer: User,
    val show: Show,
    val reviewBody: String,
    val postDate: Calendar,
    var likes: Int

)