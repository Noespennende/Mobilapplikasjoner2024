package com.movielist.networking

import com.movielist.model.ApiResponse
import com.movielist.model.MovieResponse
//import com.movielist.data.SeriesDetailsResponse
//import com.movielist.data.ShowResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Hvilket endpoint vi vil hente data fra (det etter BASE_URL/)
    @GET("trending/all/week?language=en-US")
    //fun getMovies(@Query("api_key") key: String = ApiConfig.API_KEY): Call<ApiResponse>
    fun getAllMedia(@Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiResponse>


    @GET("movie/{movie_id}")
    fun getMovieById(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"
    ): Call<MovieResponse>
}