package com.movielist.networking

import com.movielist.model.ApiAllMediaResponse
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.ApiShowSeason
import com.movielist.model.ApiShowSeasonEpisode
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
    fun getAllMedia(@Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiAllMediaResponse>

    @GET("movie/{movie_id}?language=en-US")
    fun getMovie(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiMovieResponse>

    @GET("tv/{series_id}?language=en-USS")
    fun getShow(
        @Path("series_id") seriesId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiShowResponse>

    @GET("tv/{series_id}/season/{season_number}?language=en-US")
    fun getShowSeason(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiShowSeason>

    @GET("tv/{series_id}/season/{season_number}/episode/{episode_number}?language=en-US")
    fun getShowEpisode(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiShowSeasonEpisode>

}