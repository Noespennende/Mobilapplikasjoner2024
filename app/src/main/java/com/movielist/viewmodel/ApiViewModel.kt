package com.movielist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.protobuf.Api
import com.movielist.model.ApiResponse
import com.movielist.model.MovieResponse
import com.movielist.networking.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class ApiViewModel() : ViewModel() {
    private val _mediaData = MutableLiveData<ApiResponse?>()
    val mediaData: LiveData<ApiResponse> get() = _mediaData as LiveData<ApiResponse>

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

        client.enqueue(object : Callback<ApiResponse> {

            override fun onResponse(
                call: Call<ApiResponse>,
                //response: Response<ApiResponse>
                response: Response<ApiResponse>

            )
            {
                Log.d("ApiViewModel", "Raw response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response received: $responseBody")


                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }
                _isLoading.value = false
                _mediaData.postValue(responseBody)
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()

                Log.e("ApiViewModel", "API call failed", t)

            }
        })
    }

    private val _movieData = MutableStateFlow<MovieResponse?>(null)
    val movieData: StateFlow<MovieResponse?> get() = _movieData

    fun getMovieById(movieId: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getMovieById(movieId) // Dette bør returnere Call<MovieResponse>

        client.enqueue(object : Callback<MovieResponse> {

            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                val responseBody = response.body()

                if (!response.isSuccessful || responseBody == null) {
                    _isLoading.value = false
                    _isError.value = true
                    return
                }
                _isLoading.value = false
                _movieData.value = responseBody // Sett data i StateFlow
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                t.printStackTrace()
                Log.e("ApiViewModel", "API call failed", t)
            }
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

