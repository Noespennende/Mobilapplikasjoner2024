package com.movielist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movielist.model.AllMedia
import com.movielist.model.ApiAllMediaResponse
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.ApiShowSeason
import com.movielist.model.ApiShowSeasonEpisode
import com.movielist.networking.ApiConfig
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class ApiViewModel() : ViewModel() {
    private val _mediaData = MutableLiveData<List<AllMedia>>()
    val mediaData: LiveData<List<AllMedia>> get() = _mediaData as LiveData<List<AllMedia>>

    private val _movieData = MutableLiveData<ApiMovieResponse?>()
    val movieData: LiveData<ApiMovieResponse> get() = _movieData as LiveData<ApiMovieResponse>

    private val _showData = MutableLiveData<ApiShowResponse?>()
    val showData: LiveData<ApiShowResponse> get() = _showData as LiveData<ApiShowResponse>

    private val _showSeasonData = MutableLiveData<ApiShowSeason?>()
    val showSeasonData: LiveData<ApiShowSeason> get() = _showSeasonData as LiveData<ApiShowSeason>

    private val _showEpisodeData = MutableLiveData<ApiShowSeasonEpisode?>()
    val showEpisodeData: LiveData<ApiShowSeasonEpisode> get() = _showEpisodeData as LiveData<ApiShowSeasonEpisode>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    var errorMessage: String = ""
        private set

    fun getAllMedia() {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getAllMedia()

        Log.d("ApiViewModel", "Starting API call to fetch media")

        client.enqueue(object : Callback<ApiAllMediaResponse> {

            override fun onResponse(
                call: Call<ApiAllMediaResponse>,
                response: Response<ApiAllMediaResponse>
            )
            {
                Log.d("ApiViewModel", "Raw getAllMedia response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response received: $responseBody")

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }

                val mediaList = responseBody.results

                // sørger for at bare data med mediaType 'tv' og 'movie' hentes ut
                val filteredResults = mediaList?.filter {
                    it?.mediaType == "tv" || it?.mediaType == "movie"
                }?.filterNotNull()

                _isLoading.value = false
                _mediaData.postValue(filteredResults ?: emptyList())
                Log.d("ApiViewModel", "please be right: $filteredResults")

            }

            override fun onFailure(call: Call<ApiAllMediaResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()

                Log.e("ApiViewModel", "API getAllMedia call failed", t)

            }
        })
    }

    fun getMovie(movieId: String) {
        _isLoading.value = true
        _isError.value = false

        Log.d("ApiVIEWMODel", "getMovie Called")
        val client = ApiConfig.getApiService().getMovie(movieId)

        client.enqueue(object : Callback<ApiMovieResponse> {

            override fun onResponse(
                call: Call<ApiMovieResponse?>,
                response: Response<ApiMovieResponse?>
            ) {
                Log.d("ApiViewModel", "Raw getMovie response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response getMovie received: $responseBody")

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }
                _isLoading.value = false
                _movieData.postValue(responseBody)
            }

            override fun onFailure(
                call: Call<ApiMovieResponse?>,
                t: Throwable
            ) {
                 onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getMovie call failed", t)}
        })
    }

    fun getShow(seriesId: String) {
        _isLoading.value = true
        _isError.value = false

        Log.d("ApiVIEWMODel", "getShow Called")
        val client = ApiConfig.getApiService().getShow(seriesId)

        client.enqueue(object : Callback<ApiShowResponse> {

            override fun onResponse(
                call: Call<ApiShowResponse?>,
                response: Response<ApiShowResponse?>
            ) {
                Log.d("ApiViewModel", "Raw getShow response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response getShow received: $responseBody")

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }
                _isLoading.value = false
                _showData.postValue(responseBody)
            }

            override fun onFailure(
                call: Call<ApiShowResponse?>,
                t: Throwable
            ) {
                onError(t.message)
                t.printStackTrace()

                Log.e("ApiViewModel", "API getShow call failed", t)}
        })
    }

    fun getShowSeason(seriesId: Int, seasonNumber: Int) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getShowSeason(seriesId, seasonNumber)

        client.enqueue(object : Callback<ApiShowSeason> {

            override fun onResponse(
                call: Call<ApiShowSeason?>,
                response: Response<ApiShowSeason?>
            ) {
                Log.d("ApiViewModel", "Raw getShowSeason response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response getShowSeason received: $responseBody")

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }
                _isLoading.value = false
                _showSeasonData.postValue(responseBody)
            }

            override fun onFailure(
                call: Call<ApiShowSeason?>,
                t: Throwable
            ) {
                onError(t.message)
                t.printStackTrace()

                Log.e("ApiViewModel", "API getShowSeason call failed", t)}
        })
    }

    fun getShowEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getShowEpisode(seriesId, seasonNumber, episodeNumber)

        client.enqueue(object : Callback<ApiShowSeasonEpisode> {

            override fun onResponse(
                call: Call<ApiShowSeasonEpisode?>,
                response: Response<ApiShowSeasonEpisode?>
            ) {
                Log.d("ApiViewModel", "Raw getShowEpisode response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response getShowEpisode received: $responseBody")

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }
                _isLoading.value = false
                _showEpisodeData.postValue(responseBody)
            }

            override fun onFailure(
                call: Call<ApiShowSeasonEpisode?>,
                t: Throwable
            ) {
                onError(t.message)
                t.printStackTrace()

                Log.e("ApiViewModel", "API getShowEpisode call failed", t)}
        })
    }

    private fun onError(inputMessage: String?) {

        val message = if (inputMessage.isNullOrBlank() or inputMessage.isNullOrEmpty()) "Unknown Error"
        else inputMessage

        errorMessage = StringBuilder("ERROR: ")
            .append("$message some data may not displayed properly").toString()

        _isError.value = true
        _isLoading.value = false
    }
}


