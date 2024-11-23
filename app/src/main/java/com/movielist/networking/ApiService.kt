package com.movielist.networking

import com.movielist.model.ApiAllMediaResponse
import com.movielist.model.ApiMediaVideoResponse
import com.movielist.model.ApiMovieCreditResponse
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiShowCreditResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.ApiShowSeason
import com.movielist.model.ApiShowSeasonEpisode
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
        @Path("series_id") seriesId: String,
        @Path("season_number") seasonNumber: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiShowSeason>

    @GET("tv/{series_id}/season/{season_number}/episode/{episode_number}?language=en-US")
    fun getShowEpisode(
        @Path("series_id") seriesId: String,
        @Path("season_number") seasonNumber: String,
        @Path("episode_number") episodeNumber: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiShowSeasonEpisode>

    @GET("movie/{movie_id}/credits?language=en-US")
    fun getMovieCredits(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiMovieCreditResponse>

    @GET("tv/{series_id}/aggregate_credits?language=en-US")
    fun getShowCredits(
        @Path("series_id") seriesId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiShowCreditResponse>

    @GET("movie/{movie_id}/videos?language=en-US")
    fun getMovieVideo(
        @Path("movie_id") movieId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiMediaVideoResponse>

    @GET("tv/{series_id}/videos?language=en-US")
    fun getShowVideo(
        @Path("series_id") seriesId: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"): Call<ApiMediaVideoResponse>

    @GET("search/multi?language=en-US")
    fun searchMulti(
        @Query("query") query: String,
        @Header("Authorization") authHeader: String = "Bearer ${ApiConfig.ACCESS_TOKEN}"
    ): Call<ApiAllMediaResponse>
}