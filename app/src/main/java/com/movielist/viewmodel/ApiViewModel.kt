package com.movielist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movielist.model.AllMedia
import com.movielist.model.ApiAllMediaResponse
import com.movielist.model.ApiMediaVideoResponse
import com.movielist.model.ApiMovieCreditResponse
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiShowCreditResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.MovieResponse
import com.movielist.model.ShowResponse
import com.movielist.model.VideoResult
import com.movielist.networking.ApiConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ApiViewModel() : ViewModel() {

    private val _mediaData = MutableLiveData<List<AllMedia>>()
    val mediaData: LiveData<List<AllMedia>> get() = _mediaData as LiveData<List<AllMedia>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    var errorMessage: String = ""
        private set

    /** Multi **/

    private val _searchResults = MutableStateFlow<List<AllMedia>>(emptyList())
    val searchResults: StateFlow<List<AllMedia>> = _searchResults

    fun searchMulti(query: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().searchMulti(query)

        client.enqueue(object : Callback<ApiAllMediaResponse> {

            override fun onResponse(
                call: Call<ApiAllMediaResponse>,
                response: Response<ApiAllMediaResponse>
            ) {

                val responseBody = response.body()

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }

                val searchList = responseBody.results

                val filteredResults = searchList?.filter {
                    it?.mediaType == "tv" || it?.mediaType == "movie"
                }?.filterNotNull()

                _isLoading.value = false
                _searchResults.value = filteredResults ?: emptyList()
            }

            override fun onFailure(call: Call<ApiAllMediaResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
            }
        })
    }

    fun getAllMedia() {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getAllMedia()

        client.enqueue(object : Callback<ApiAllMediaResponse> {

            override fun onResponse(
                call: Call<ApiAllMediaResponse>,
                response: Response<ApiAllMediaResponse>
            ) {
                val responseBody = response.body()

                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }

                val mediaList = responseBody.results?.filter {
                    it?.mediaType == "tv" || it?.mediaType == "movie"
                }?.filterNotNull()

                _mediaData.postValue(mediaList ?: emptyList())
            }
            override fun onFailure(call: Call<ApiAllMediaResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
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

    /** Movies **/

    private val _movieData = MutableStateFlow<MovieResponse?>(null)
    val movieData: StateFlow<MovieResponse?> get() = _movieData

    fun getMovieDetails(movieID: String) {

        viewModelScope.launch {

            _isLoading.value = true
            _isError.value = false

            try {

                val movieDeferred = async { getMovieData(movieID) }
                val videoDeferred = async { getMovieVideoData(movieID) }
                val creditDeferred = async { getMovieCreditData(movieID) }

                // Vent på at kallene blir ferdig
                val movieData = movieDeferred.await()
                val movieVideoData = videoDeferred.await()
                val movieCreditData = creditDeferred.await()

                // Kombinerer resultatene i MovieResponse-typen
                val movieResponse = MovieResponse(movieData, movieVideoData, movieCreditData)

                _isLoading.value = false
                _movieData.value = movieResponse

            } catch (e: Exception) {
                _isLoading.value = false
                _isError.value = true
                Log.e("ApiViewModel", "Error fetching movie details", e)
            }
        }
    }

    private suspend fun getMovieData(movieId: String): ApiMovieResponse {

        return suspendCoroutine { cont ->
            val client = ApiConfig.getApiService().getMovie(movieId)

            client.enqueue(object : Callback<ApiMovieResponse> {

                override fun onResponse(call: Call<ApiMovieResponse>, response: Response<ApiMovieResponse>) {
                    if (response.isSuccessful) {
                        cont.resume(response.body() ?: ApiMovieResponse())
                    } else {
                        cont.resumeWithException(Exception("Failed to load movie data"))
                    }
                }

                override fun onFailure(call: Call<ApiMovieResponse>, t: Throwable) {
                    cont.resumeWithException(t)
                }
            })
        }
    }

    private suspend fun getMovieVideoData(movieId: String): List<VideoResult> {

        return suspendCoroutine { cont ->
            val client = ApiConfig.getApiService().getMovieVideo(movieId)

            client.enqueue(object : Callback<ApiMediaVideoResponse> {

                override fun onResponse(call: Call<ApiMediaVideoResponse>, response: Response<ApiMediaVideoResponse>) {
                    if (response.isSuccessful) {
                        val videoList = response.body()?.results?.filter {
                            it?.type == "Trailer" && it?.name == "Official Trailer"
                        } ?: emptyList()
                        cont.resume(videoList)
                    } else {
                        cont.resumeWithException(Exception("Failed to load movie video data"))
                    }
                }

                override fun onFailure(call: Call<ApiMediaVideoResponse>, t: Throwable) {
                    cont.resumeWithException(t)
                }
            })
        }
    }

    private suspend fun getMovieCreditData(movieID: String): ApiMovieCreditResponse {

        return suspendCoroutine { cont ->
            val client = ApiConfig.getApiService().getMovieCredits(movieID)

            client.enqueue(object : Callback<ApiMovieCreditResponse> {

                override fun onResponse(call: Call<ApiMovieCreditResponse>, response: Response<ApiMovieCreditResponse>) {
                    if (response.isSuccessful) {
                        // Hent cast-medlemmene
                        cont.resume(response.body() ?: ApiMovieCreditResponse())
                    } else {
                        cont.resumeWithException(Exception("Failed to load show credit data"))
                    }
                }

                override fun onFailure(call: Call<ApiMovieCreditResponse>, t: Throwable) {
                    cont.resumeWithException(t)
                }
            })
        }
    }


    /** Show **/

    private val _showData = MutableStateFlow<ShowResponse?>(null)
    val showData: StateFlow<ShowResponse?> get() = _showData

    suspend fun getShowDetails(showID: String) {

        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false

            try {
                val showDeferred = async { getShowData(showID) }
                val videoDeferred = async { getShowVideoData(showID) }
                val creditDeferred = async { getShowCreditData(showID) }

                // Venter på at alle kallene blir ferdige
                val showData = showDeferred.await()
                val showVideoData = videoDeferred.await()
                val showCreditData = creditDeferred.await()

                // Kombiner i ShowResponse-typen
                val showResponse = ShowResponse(showData, showVideoData, showCreditData)

                _isLoading.value = false
                _showData.value = showResponse

            } catch (e: Exception) {
                _isLoading.value = false
                _isError.value = true
                Log.e("ApiViewModel", "Error fetching movie details", e)
            }
        }
    }

    suspend fun getMovieDetailsTry(movieID: String): Result<MovieResponse> {

        return try {

            val movieDeferred = viewModelScope.async { getMovieData(movieID) }
            val videoDeferred = viewModelScope.async { getMovieVideoData(movieID) }
            val creditDeferred = viewModelScope.async { getMovieCreditData(movieID) }

            // Vent på at kallene blir ferdig
            val movieData = movieDeferred.await()
            val movieVideoData = videoDeferred.await()
            val movieCreditData = creditDeferred.await()

            // Kombinerer resultatene i MovieResponse-typen
            val movieResponse = MovieResponse(movieData, movieVideoData, movieCreditData)

            Result.success(movieResponse)

        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun getShowDetailsTry(showID: String): Result<ShowResponse> {

        return try {

            val showDeferred = viewModelScope.async { getShowData(showID) }
            val videoDeferred = viewModelScope.async { getShowVideoData(showID) }
            val creditDeferred = viewModelScope.async { getShowCreditData(showID) }

            val showData = showDeferred.await()
            val showVideoData = videoDeferred.await()
            val showCreditData = creditDeferred.await()

            val showResponse = ShowResponse(showData, showVideoData, showCreditData)

            Result.success(showResponse)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    private suspend fun getShowData(showID: String): ApiShowResponse {

        return suspendCoroutine { cont ->
            val client = ApiConfig.getApiService().getShow(showID)

            client.enqueue(object : Callback<ApiShowResponse> {

                override fun onResponse(call: Call<ApiShowResponse>, response: Response<ApiShowResponse>) {
                    if (response.isSuccessful) {
                        cont.resume(response.body() ?: ApiShowResponse())
                    } else {
                        cont.resumeWithException(Exception("Failed to load movie data"))
                    }
                }

                override fun onFailure(call: Call<ApiShowResponse>, t: Throwable) {
                    cont.resumeWithException(t)
                }
            })
        }
    }

    private suspend fun getShowVideoData(movieId: String): List<VideoResult> {

        return suspendCoroutine { cont ->
            val client = ApiConfig.getApiService().getShowVideo(movieId)

            client.enqueue(object : Callback<ApiMediaVideoResponse> {

                override fun onResponse(call: Call<ApiMediaVideoResponse>, response: Response<ApiMediaVideoResponse>) {
                    if (response.isSuccessful) {
                        val videoList = response.body()?.results?.filter {
                            it.type == "Trailer"  && it?.name?.contains("Official Trailer", ignoreCase = true) == true
                        } ?: emptyList()
                        Log.d("Controller", "Her er jeg " + videoList.toString())
                        cont.resume(videoList)
                    } else {
                        cont.resumeWithException(Exception("Failed to load movie video data"))
                    }
                }

                override fun onFailure(call: Call<ApiMediaVideoResponse>, t: Throwable) {
                    cont.resumeWithException(t)
                }
            })
        }
    }

    private suspend fun getShowCreditData(showID: String): ApiShowCreditResponse {

        return suspendCoroutine { cont ->
            val client = ApiConfig.getApiService().getShowCredits(showID)

            client.enqueue(object : Callback<ApiShowCreditResponse> {

                override fun onResponse(call: Call<ApiShowCreditResponse>, response: Response<ApiShowCreditResponse>) {
                    if (response.isSuccessful) {
                        // Hent cast-medlemmene
                        cont.resume(response.body() ?: ApiShowCreditResponse())
                    } else {
                        cont.resumeWithException(Exception("Failed to load show credit data"))
                    }
                }

                override fun onFailure(call: Call<ApiShowCreditResponse>, t: Throwable) {
                    cont.resumeWithException(t)
                }
            })
        }
    }

}