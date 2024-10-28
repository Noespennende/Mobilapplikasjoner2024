package com.movielist.model

import java.util.Calendar

data class Show (
    val title: String,
    val length: Int,
    val imageID: Int,
    val releaseDate: Calendar,
    val imageDescription: String
)

