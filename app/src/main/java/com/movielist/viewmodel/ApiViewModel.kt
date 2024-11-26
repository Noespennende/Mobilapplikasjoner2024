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
import com.movielist.model.ApiShowSeason
import com.movielist.model.ApiShowSeasonEpisode
import com.movielist.model.MovieResponse
import com.movielist.model.ShowResponse
import com.movielist.model.VideoResult
import com.movielist.networking.ApiConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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

    private val _movieData = MutableStateFlow<ApiMovieResponse?>(null)
    val movieData: StateFlow<ApiMovieResponse?> get() = _movieData

    private val _showData = MutableStateFlow<ApiShowResponse?>(null)
    val showData: StateFlow<ApiShowResponse?> get() = _showData

    private val _showSeasonData = MutableStateFlow<ApiShowSeason?>(null)
    val showSeasonData: StateFlow<ApiShowSeason?> get() = _showSeasonData

    private val _showEpisodeData = MutableStateFlow<ApiShowSeasonEpisode?>(null)
    val showEpisodeData: StateFlow<ApiShowSeasonEpisode?> get() = _showEpisodeData

    private val _movieCreditData = MutableStateFlow<ApiMovieCreditResponse?>(null)
    val movieCreditData: StateFlow<ApiMovieCreditResponse?> get() = _movieCreditData

    private val _showCreditData = MutableStateFlow<ApiShowCreditResponse?>(null)
    val showCreditData: StateFlow<ApiShowCreditResponse?> get() = _showCreditData

    private val _movieVideoData = MutableStateFlow<List<VideoResult>>(emptyList())
    val movieVideoData: StateFlow<List<VideoResult>> get() = _movieVideoData

    private val _showVideoData = MutableStateFlow<List<VideoResult>>(emptyList())
    val showVideoData: StateFlow<List<VideoResult>> get() = _showVideoData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    var errorMessage: String = ""
        private set

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
                Log.d("ApiViewModel", "Raw searchMulti response: $response")

                val responseBody = response.body()

                Log.d("ApiViewModel", "Response received for searchMulti: $responseBody")

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
                Log.d("ApiViewModel", "Filtered search results: $filteredResults")
            }

            override fun onFailure(call: Call<ApiAllMediaResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
                Log.e("ApiViewModel", "API searchMulti call failed", t)
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
                //_mediaData.value = mediaList
            }
            override fun onFailure(call: Call<ApiAllMediaResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
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
                    _movieData.value = responseBody
                }

                override fun onFailure(
                    call: Call<ApiMovieResponse?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getMovie call failed", t)
                }
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
                _showData.value = responseBody
            }

            override fun onFailure(
                call: Call<ApiShowResponse?>,
                t: Throwable
            ) {
                onError(t.message)
                t.printStackTrace()

                Log.e("ApiViewModel", "API getShow call failed", t)
            }
        })
    }

    fun getShowSeason(seriesId: String, seasonNumber: String) {

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
                    _showSeasonData.value = responseBody
                }

                override fun onFailure(
                    call: Call<ApiShowSeason?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getShowSeason call failed", t)
                }
            })
        }

    fun getShowEpisode(seriesId: String, seasonNumber: String, episodeNumber: String) {

            _isLoading.value = true
            _isError.value = false

            val client =
                ApiConfig.getApiService().getShowEpisode(seriesId, seasonNumber, episodeNumber)

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
                    _showEpisodeData.value = responseBody
                }

                override fun onFailure(
                    call: Call<ApiShowSeasonEpisode?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getShowEpisode call failed", t)
                }
            })
        }

    fun getMovieCredits(movieId: String) {

            _isLoading.value = true
            _isError.value = false

            Log.d("ApiVIEWMODel", "getMovieCredits Called")
            val client = ApiConfig.getApiService().getMovieCredits(movieId)

            client.enqueue(object : Callback<ApiMovieCreditResponse> {

                override fun onResponse(
                    call: Call<ApiMovieCreditResponse?>,
                    response: Response<ApiMovieCreditResponse?>
                ) {
                    Log.d("ApiViewModel", "Raw getMovieCredits response: $response")

                    val responseBody = response.body()

                    Log.d("ApiViewModel", "Response getMovieCredits received: $responseBody")

                    if (!response.isSuccessful || responseBody == null) {
                        onError("Data Processing Error")
                        return
                    }
                    _isLoading.value = false
                    _movieCreditData.value = responseBody
                }

                override fun onFailure(
                    call: Call<ApiMovieCreditResponse?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getMovieCredits call failed", t)
                }
            })
        }

    fun getShowCredits(seriesId: String) {

            _isLoading.value = true
            _isError.value = false

            Log.d("ApiVIEWMODel", "getShowCredits Called")
            val client = ApiConfig.getApiService().getShowCredits(seriesId)

            client.enqueue(object : Callback<ApiShowCreditResponse> {

                override fun onResponse(
                    call: Call<ApiShowCreditResponse?>,
                    response: Response<ApiShowCreditResponse?>
                ) {
                    Log.d("ApiViewModel", "Raw getShowCredits response: $response")

                    val responseBody = response.body()

                    Log.d("ApiViewModel", "Response getShowCredits received: $responseBody")

                    if (!response.isSuccessful || responseBody == null) {
                        onError("Data Processing Error")
                        return
                    }
                    _isLoading.value = false
                    _showCreditData.value = responseBody
                }

                override fun onFailure(
                    call: Call<ApiShowCreditResponse?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getShowCredits call failed", t)
                }
            })
        }

    fun getMovieVideo(movieId: String) {

            _isLoading.value = true
            _isError.value = false

            Log.d("ApiVIEWMODel", "getMovieVideo Called")
            val client = ApiConfig.getApiService().getMovieVideo(movieId)

            client.enqueue(object : Callback<ApiMediaVideoResponse> {

                override fun onResponse(
                    call: Call<ApiMediaVideoResponse?>,
                    response: Response<ApiMediaVideoResponse?>
                ) {
                    Log.d("ApiViewModel", "Raw getMovieVideo response: $response")

                    val responseBody = response.body()

                    Log.d("ApiViewModel", "Response getMovieVideo received: $responseBody")

                    if (!response.isSuccessful || responseBody == null) {
                        onError("Data Processing Error")
                        return
                    }

                    val movieList = responseBody.results

                    val filteredResults = movieList?.filter {
                        it?.type == "Trailer" && it?.name == "Official Trailer"
                    }.orEmpty()

                    _isLoading.value = false
                    _movieVideoData.value = filteredResults
                    Log.d("ApiViewModel", "Video please be right: $filteredResults")

                }

                override fun onFailure(
                    call: Call<ApiMediaVideoResponse?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getMovieVideo call failed", t)
                }
            })
        }

    fun getShowVideo(seriesId: String) {

            _isLoading.value = true
            _isError.value = false

            Log.d("ApiVIEWMODel", "getShowVideo Called")
            val client = ApiConfig.getApiService().getShowVideo(seriesId)

            client.enqueue(object : Callback<ApiMediaVideoResponse> {

                override fun onResponse(
                    call: Call<ApiMediaVideoResponse?>,
                    response: Response<ApiMediaVideoResponse?>
                ) {
                    Log.d("ApiViewModel", "Raw getShowVideo response: $response")

                    val responseBody = response.body()

                    Log.d("ApiViewModel", "Response getShowVideo received: $responseBody")

                    if (!response.isSuccessful || responseBody == null) {
                        onError("Data Processing Error")
                        return
                    }
                    val showList = responseBody.results

                    val filteredResults = showList?.filter {
                        it?.type == "Trailer" && it?.name == "Official Trailer"
                    }.orEmpty()

                    _isLoading.value = false
                    _showVideoData.value = filteredResults
                }

                override fun onFailure(
                    call: Call<ApiMediaVideoResponse?>,
                    t: Throwable
                ) {
                    onError(t.message)
                    t.printStackTrace()

                    Log.e("ApiViewModel", "API getShowVideo call failed", t)
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

    private val _movieDataTest = MutableStateFlow<MovieResponse?>(null)
    val movieDataTest: StateFlow<MovieResponse?> get() = _movieDataTest

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
                _movieDataTest.value = movieResponse

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

    suspend fun getMovieVideoData(movieId: String): List<VideoResult> {

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

    suspend fun getMovieCreditData(movieID: String): ApiMovieCreditResponse {

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

    private val _showDataTest = MutableStateFlow<ShowResponse?>(null)
    val showDataTest: StateFlow<ShowResponse?> get() = _showDataTest

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
                _showDataTest.value = showResponse

            } catch (e: Exception) {
                _isLoading.value = false
                _isError.value = true
                Log.e("ApiViewModel", "Error fetching movie details", e)
            }
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

    suspend fun getShowCreditData(showID: String): ApiShowCreditResponse {

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