package com.movielist

import retrofit2.Call
import retrofit2.http.GET
import com.movielist.data.Show
import com.movielist.data.Movie
import com.movielist.data.MovieResponse
import com.movielist.data.SeriesDetailsResponse
import com.movielist.data.ShowResponse
import retrofit2.http.Path


interface MyApi {

    // Hvilket endpoint vi vil hente data fra (det etter BASE_URL/)
    @GET("titles?startYear=2010&list=top_rated_english_250")
    fun getMovies(): Call<MovieResponse>

    @GET("titles?startYear=2010&list=most_pop_series")
    fun getShows(): Call<ShowResponse>

    @GET("https://moviesdatabase.p.rapidapi.com/titles/series/")
    fun getSerie(): Call<MovieResponse>

     @GET("titles/series/{seriesId}")
    fun getSeriesDetails(@Path("seriesId") seriesId: String): Call<SeriesDetailsResponse>
}