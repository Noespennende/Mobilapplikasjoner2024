package com.movielist

import retrofit2.Call
import retrofit2.http.GET
import com.movielist.data.Show

interface MyApi {

    @GET("comments")
    fun getTitles(): Call<List<Show>>
}