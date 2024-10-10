package com.movielist.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movielist.data.ListItem
import com.movielist.data.Show
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.White
import com.movielist.ui.theme.*
import androidx.compose.runtime.LaunchedEffect
import com.movielist.MyApi
import com.movielist.data.Movie
import com.movielist.data.MovieResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.movielist.R
import com.movielist.data.CombinedData
import com.movielist.data.PrimaryImage
import com.movielist.data.ReleaseYear
import com.movielist.data.SeriesDetailsResponse
import com.movielist.data.ShowResponse
import com.movielist.data.TitleText
import com.movielist.data.TitleType
import kotlinx.coroutines.*
import retrofit2.http.GET
import kotlin.Int
import kotlin.String


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

//TODO(
//
//- !!! Implementere funksjonene til FrontPage -> fokusere på dette"
//- Implementere api call for bare serier og? Slik som i getMovies"
//- API implementasjon episode detaljer for når man g
//- Ordne så alle filer 'kommer overens' etter endringer relatert til API"
//- Forstørre API-et? Sette limit på 50 (10 er automatisk satt), muligens heller ordne det i BETA levering"
//- Flytte API call ut i egen fil (mappe) andre filer heller kalle på APIet
//)

@Composable
fun FrontPage () {

    // Metoden som henter filmer/shows fra APIet - ORDNET!! Henter både filmer og serier

    // Metode som viser filmer i "currenctly watching" - I produksjon


    val combinedMediaList = remember { mutableStateOf<List<CombinedData>>(emptyList()) }

    LaunchedEffect(Unit) {
        getAllMedia { media ->
            combinedMediaList.value = media // Update the state with fetched media
            Log.i(TAG, "Both shows and movies ${combinedMediaList.value}")
        }
    }

    // Metode som viser de mest populære filmene og seriene blant brukere på platformen (api fetch -> ??? Flere 'listetyper' som ga tom lister)
    // FOR MEST POPULÆRE SERIER Bruke 'most_pop_series' med limit på 10
    // FOR MEST POPULÆRE FILMER Bruke 'top_rated_english_250' med limit på 10 og sjekk at "isSeries": false og "isEpisode": false. most_pop_movies og top_rated_250 returnerte tomme lister


    //Temporary code: DELETE THIS CODE
    /*
    val listItemList = mutableListOf<ListItem>()
    for (i in 0..12) {
        listItemList.add(
            ListItem(
                currentEpisode = i,
                score = Random.nextInt(0, 10),
                show =  Show(
                    title = "Silo",
                    length = 12,
                    imageID = R.drawable.silo,
                    imageDescription = "Silo TV Show",
                    releaseDate = Calendar.getInstance()
                )
            )
        )
    }

    val showList = mutableListOf<Show>()

    for (i in 0..12) {
        showList.add(
            Show(
            title = "Silo",
            length = 12,
            imageID = R.drawable.silo,
            imageDescription = "Silo TV Show",
            releaseDate = Calendar.getInstance()
            )
        )
    }

    val reviewList = mutableListOf<Review>()
    val user = User(
        userName = "User Userson",
        profileImageID = R.drawable.profilepicture,
        completedShows = listItemList,
        wantToWatchShows = listItemList,
        droppedShows = listItemList,
        currentlyWatchingShows = listItemList
    )
    for (i in 0..6) {
        reviewList.add(
            Review(
                score = Random.nextInt(0, 10), //<- TEMP CODE: PUT IN REAL CODE
                reviewer = user,
                show = listItemList[1].show,
                reviewBody = "It’s reasonably well-made, and visually compelling," +
                        "but it’s ultimately too derivative, and obvious in its thematic execution," +
                        "to recommend..",
                postDate = Calendar.getInstance(),
                likes = Random.nextInt(0, 100) //<- TEMP CODE: PUT IN REAL CODE
            )
        )
    }
    */
    //^^^KODEN OVENFOR ER MIDLERTIDIG. SLETT DEN.^^^^

    //Front page graphics
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Front page content
        item {
            CurrentlyWatchingScroller(listOfShows = combinedMediaList.value)
        }

        item {
            PopularShowsAndMovies(listOfShows = combinedMediaList.value)
        }
        item {
           // YourFriendsJustWatched(listItemList)
        }

        item {
            //Top reviews this week:
            //ReviewsSection(
             //   reviewList = reviewList,
                //header = "Top reviews this week"
           // )
        }

        item {
            /*Adds empty space the size of the bottom nav bar to ensure content don't dissapear
            behind it*/
            Spacer(modifier = Modifier.height(bottomNavBarHeight))
        }

    }

}


@Composable
fun CurrentlyWatchingScroller (
    listOfShows: List<CombinedData>
    //listOfShows: List<ListItem>
) {

    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(start = horizontalPadding, end = 0.dp)
    ) {
        items (listOfShows.size) {i ->
            CurrentlyWatchingCard(
                imageId = listOfShows[i].primaryImage?.id,
                imageURL = listOfShows[i].primaryImage?.url,
                //imageDescription = listOfShows[i].primaryImage?.caption?.plainText.toString(),
                title = listOfShows[i].titleText?.text ?: "Title",
                showLength = listOfShows[i].showLength,
                episodesWatched = listOfShows[i].currentEpisode)
            }
    }
}

@Composable
fun CurrentlyWatchingCard (
    imageId: String?,
    imageURL: String?,
    imageDescription: String = "Image not available",
    title: String,
    showLength: Int?,
    episodesWatched: Int?,
    modifier: Modifier = Modifier

    ) {

    val allShowLength = if (showLength == null || showLength == 0 || showLength == 1) 1 else showLength // allShowLength lagd slik at filmer også får "episodenr"


    var watchedEpisodesCount by remember {
        mutableIntStateOf(episodesWatched ?: 0)
    }

    var buttonText by remember {
        mutableStateOf(generateButtonText(watchedEpisodesCount, allShowLength))
    }

    //Card container
    Card (
        modifier = modifier
            .width(350.dp),
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Gray)

    ) {
        //card content
        Column(
            modifier = Modifier
                .height(265.dp + topPhoneIconsBackgroundHeight)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = (topPhoneIconsBackgroundHeight + 10.dp),
                    bottom = 10.dp
                )
        )
        {
            //Main image
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageURL ?: R.drawable.noimage
                ),
                contentDescription = imageDescription,
                contentScale = ContentScale.Fit, // Byttet fra .Crop til .Fit da bildene var for store, se nærmere på fiks
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            //Content under image
            Column(
                modifier = Modifier
                    .fillMaxSize()
            )
            {
                //Title and episodes watched
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    //Title
                    Text(
                        title,
                        style = TextStyle(
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = weightRegular
                        )
                    )
                    //Episodes watched
                    Text (
                        "Ep $watchedEpisodesCount of $allShowLength",
                        style = TextStyle(
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = weightLight
                        )
                    )
                }

                //Progress bar
                ProgressBar(currentNumber = watchedEpisodesCount, endNumber = allShowLength)

                //Mark as watched button
                Button(
                    onClick = {
                        //Button onclick function
                        allShowLength.let { length ->
                            if (watchedEpisodesCount < allShowLength) {
                                watchedEpisodesCount++
                            }
                        }

                        buttonText = generateButtonText(watchedEpisodesCount, allShowLength)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Purple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                ) {
                    //Button text
                    Text(
                        buttonText,
                        fontSize = headerSize,
                        fontWeight = weightRegular,
                        color = DarkGray
                    )
                }
            }


                }
            }
        }

@Composable
fun PopularShowsAndMovies(
    listOfShows: List<CombinedData>
//listOfShows: List<Show>
)
{
    Log.i("PopularShows", "Received ${listOfShows.size} shows/movies")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = verticalPadding)
    ) {
        //Header
        Text(
            "Popular shows and movies",
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightBold,
            color = White,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = horizontalPadding)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = horizontalPadding, end = 0.dp)
        ) {
            items(listOfShows.size) { i ->
                ShowImage(
                    imageID = listOfShows[i].primaryImage?.id,
                    imageURL = listOfShows[i].primaryImage?.url,
                    imageDescription = "Image for ${listOfShows[i].titleText?.text}"

                )
            }
        }


    }
}


        @Composable
        fun YourFriendsJustWatched(
            listOfShows: List<ListItem>
        ) {
            //Container collumn
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = verticalPadding)
            ) {
                //Header
                Text(
                    "Your friends just watched",
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    color = White,
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = horizontalPadding)
                )
                //Content
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    contentPadding = PaddingValues(start = horizontalPadding, end = 0.dp)
                ) {
                    items(listOfShows.size) { i ->
                        //Info for each show
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            ShowImage(
                                //imageID = listOfShows[i].show.primaryImage?.url,
                                //imageDescription = listOfShows[i].show.primaryImage?.caption.toString()
                            )
                            //Friend Info
                            // Skal være med
                            /*
                    FriendsWatchedInfo(
                        profileImageID = R.drawable.profilepicture,
                        profileName = "User Userson", //TEMP DELETE THIS
                        episodesWatched = i,
                        showLenght = listOfShows[i].show.length,
                        score = listOfShows[i].score
                    )
                     */
                        }


                    }
                }

            }
        }

        @Composable
        fun FriendsWatchedInfo(
            profileImageID: Int,
            profileName: String,
            episodesWatched: Int,
            showLenght: Int,
            score: Int = 0
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                ProfileImage(
                    imageID = profileImageID,
                    userName = profileName
                )
                //Episode Count and Score
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "Ep $episodesWatched of $showLenght",
                        color = White,
                        fontFamily = fontFamily,
                        fontWeight = weightLight,
                        fontSize = 12.sp
                    )
                    ScoreGraphics(
                        score = score
                    )
                }
            }

        }

        //Utility Functions
        fun generateButtonText(
            episodesWatched: Int,
            showLenght: Int?
        )
                : String {
            if (episodesWatched + 1 == showLenght) {
                return "Mark as completed"
            } else if (episodesWatched == showLenght) {
                return "Add a rating"
            } else {
                return "Mark episode ${episodesWatched + 1} as watched"
            }

        }



