package com.movielist.api

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import com.movielist.MyApi
import com.movielist.data.CombinedData
import com.movielist.data.MovieResponse
import com.movielist.data.PrimaryImage
import com.movielist.data.SeriesDetailsResponse
import com.movielist.data.ShowResponse
import com.movielist.data.TitleText
import com.movielist.data.TitleType
import com.movielist.data.APIMovie
import com.movielist.data.APIShow
import com.movielist.ui.theme.*
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class apiFetch {

    private val BASE_URL ="https://moviesdatabase.p.rapidapi.com/"
    private val API_KEY = "09f23523ebmshad9f7b2ebe7b44bp1ecd5bjsn35bb315b63a3" // api key, må være med! Har med autentisering å gjøre
    private val API_HOST = "moviesdatabase.p.rapidapi.com" // host link, må være med! Har med autentisering å gjøre, lik for alle
    private val TAG: String = "CHECK_RESPONSE" // Skriv inn i LogCat for å se output fra api

    // Autentiserer API nøkkelen. Gis i rapidAPI sin code snippet (tror man måtte opprette bruker for å få den)
    private val apiKeyInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .addHeader("x-rapidapi-key", API_KEY)
            .addHeader("x-rapidapi-host", API_HOST)
            .build()
        chain.proceed(request)
    }

    // Oppretter en OkHttpClient med apiKeyInterceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .build()

    // Oppretter en Retrofit instans for å gjøre et API call
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Henter størrelsen (antall episoder) til en serie
    private fun getShowDetails(seriesId: String, onResult: (Int) -> Unit) {
        val api = retrofit.create(MyApi::class.java)

        api.getSeriesDetails(seriesId).enqueue(object : Callback<SeriesDetailsResponse> {
            override fun onResponse(call: Call<SeriesDetailsResponse>, response: Response<SeriesDetailsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { seriesResponse ->
                        val showLength = seriesResponse.results.size
                        onResult(showLength)
                    }
                } else {
                    Log.i(TAG, "Failed with response code: ${response.code()}")
                    onResult(0)
                }
            }

            override fun onFailure(call: Call<SeriesDetailsResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
                onResult(0)
            }
        })
    }

    // Henter både filmer og serier (shows) i en felles liste
    private fun getAllMedia(onShowsFetched: (List<CombinedData>) -> Unit) {
        val api = retrofit.create(MyApi::class.java)

        api.getShows().enqueue(object : Callback<ShowResponse> {
            override fun onResponse(call: Call<ShowResponse>, response: Response<ShowResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { showResponse ->
                        val combinedDataList = mutableListOf<CombinedData>()

                        // Henter shows (serier)
                        for (show in showResponse.results) {
                            // Henter også antall episoder her via getShowDetails og Id-en til serien
                            getShowDetails(show.id) { totalEpisodes ->
                                combinedDataList.add(
                                    CombinedData(
                                        _id = show._id ?: "",
                                        id = show.id ?: "",
                                        primaryImage = PrimaryImage(
                                            id = show.primaryImage?.id ?: "",
                                            url = show.primaryImage?.url ?: "",
                                            width = show.primaryImage?.width ?: 200,
                                            height = show.primaryImage?.height ?: 250
                                        ),
                                        titleType = TitleType(show.titleType?.isSeries == false, show.titleType?.isEpisode == false),
                                        titleText = TitleText(show.titleText?.text ?: "No Title"),
                                        originalTitleText = show.originalTitleText?.let { TitleText(it.text) },
                                        showLength = totalEpisodes, // Use the totalEpisodes value here
                                        totalEpisodes = totalEpisodes,
                                        currentEpisode = show.currentEpisode
                                    )
                                )

                                // Sjekker at alle seriene er blitt hentet
                                if (combinedDataList.size == showResponse.results.size) {
                                    getMovies(combinedDataList, onShowsFetched) // hvis alle serier er hentet, blir funksjonen for å hente filmene kjørt
                                }
                            }
                        }
                    }
                } else {
                    Log.i(TAG, "Failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    // Henter filmer og legger de til i combinedDataList
    private fun getMovies(combinedDataList: MutableList<CombinedData>, onMediaFetched: (List<CombinedData>) -> Unit) {
        val api = retrofit.create(MyApi::class.java)

        // Fetch movies
        api.getMovies().enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { movieResponse ->
                        for (movie in movieResponse.results) {
                            combinedDataList.add(
                                CombinedData(
                                    _id = movie._id ?: "",
                                    id = movie.id ?: "",
                                    primaryImage = PrimaryImage(
                                        id = movie.primaryImage?.id ?: "",
                                        url = movie.primaryImage?.url ?: "",
                                        width = movie.primaryImage?.width ?: 200,
                                        height = movie.primaryImage?.height ?: 250
                                    ),
                                    titleType = TitleType(movie.titleType?.isSeries == false, movie.titleType?.isEpisode == false),
                                    titleText = TitleText(movie.titleText?.text ?: "No Title"),
                                    originalTitleText = movie.originalTitleText?.let { TitleText(it.text) },
                                    showLength = 0,
                                    totalEpisodes = 0,
                                    currentEpisode = null
                                )
                            )
                        }

                        // Etter filmer er hentet blir de lagt til i felleslisten med serier
                        onMediaFetched(combinedDataList)
                    }
                } else {
                    Log.i(TAG, "Failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }
/*
   val combinedMediaList = remember { mutableStateOf<List<CombinedData>>(emptyList()) }

    LaunchedEffect(Unit) {
        getAllMedia { media ->
            combinedMediaList.value = media // Update the state with fetched media
            Log.i(TAG, "Both shows and movies ${combinedMediaList.value}")
        }
    }
 */

}