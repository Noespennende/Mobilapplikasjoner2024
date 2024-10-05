package com.movielist

import retrofit2.Call
import retrofit2.http.GET
import com.movielist.data.Show
import com.movielist.data.Movie
import com.movielist.data.MovieResponse


interface MyApi {

    // Hvilket endpoint vi vil hente data fra (det etter BASE_URL/)
    @GET("titles")
    fun getTitles(): Call<MovieResponse>
}