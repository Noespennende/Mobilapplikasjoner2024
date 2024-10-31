package com.movielist.networking

import com.movielist.model.ApiResponse
import com.movielist.data.SeriesDetailsResponse
import com.movielist.data.ShowResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Hvilket endpoint vi vil hente data fra (det etter BASE_URL/)
    @GET("titles?startYear=2010&list=top_rated_english_250")
    fun getMovies(@Query("key") key: String = ApiConfig.API_KEY): Call<ApiResponse>

    @GET("titles?startYear=2010&list=most_pop_series")
    fun getShows(): Call<ShowResponse>

    //@GET("https://moviesdatabase.p.rapidapi.com/titles/series/")
    //fun getSerie(): Call<MovieResponse>

    @GET("titles/series/{seriesId}")
    fun getSeriesDetails(@Path("seriesId") seriesId: String): Call<SeriesDetailsResponse>


    /*
Eksempel kode nedenunder, mulig det ikke er nødvending med @Query callene? Spørs hvor mye som må endres, kommer tilbake hit
package com.dimaswisodewo.weatherapp.networking

import com.dimaswisodewo.weatherapp.model.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    // Get current weather data
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") key: String = ApiConfig.API_KEY,
        @Query("q") city: String,
        @Query("aqi") aqi: String = "no"
    ): Call<CurrentWeatherResponse>
}
     */
}