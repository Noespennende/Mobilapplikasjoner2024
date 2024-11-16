package com.movielist.controller

import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.movielist.composables.firestoreRepository
import com.movielist.data.FirebaseTimestampAdapter
import com.movielist.data.UUIDAdapter
import com.movielist.model.AllMedia
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiProductionResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.viewmodel.ApiViewModel
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random


class ControllerViewModel(
    private val userViewModel: UserViewModel,
    private val authViewModel: AuthViewModel,
    private val apiViewModel: ApiViewModel
) : ViewModel() {

    /* USER LOGIC */

    val currentFirebaseUser: StateFlow<FirebaseUser?> = authViewModel.currentUser

    val isLoggedIn: State<Boolean> = authViewModel.isLoggedIn
    val loggedInUser: StateFlow<User?> = userViewModel.loggedInUser
    val otherUser: StateFlow<User?> = userViewModel.otherUser

    fun setLoggedInUser(uid: String) {
        userViewModel.setLoggedInUser(uid)
    }

    private val _filteredMediaData = MutableLiveData<List<Production>>()
    val filteredMediaData: LiveData<List<Production>> get() = _filteredMediaData

    init {

        apiViewModel.mediaData.observeForever { mediaList ->

            val convertedResults = mediaList.map { media ->
                if (media.mediaType.equals("movie", ignoreCase = true)) {
                    convertToMovie(media)
                } else {
                    convertToTVShow(media)
                }
            }

            _filteredMediaData.postValue(convertedResults)
        }
    }

    private fun convertToMovie(media: AllMedia): Movie{
        return(Movie(
            imdbID = media.id.toString(),
            title = media.title.toString(),
            description = media.overview.toString(),
            genre = media.genres?.map { it.name.orEmpty() } ?: emptyList(),
            releaseDate = convertStringToCalendar(media.releaseDate) ?: Calendar.getInstance(),
            rating = media.voteAverage?.toInt(),
            posterUrl = "https://image.tmdb.org/t/p/w500/"+media.backdropPath,

        ))
    }

    private fun convertToTVShow(media: AllMedia): TVShow{
        return(TVShow(
            imdbID = media.id.toString(),
            title = media.name.orEmpty(),
            description = media.overview.orEmpty(),
            posterUrl = "https://image.tmdb.org/t/p/w500/"+media.posterPath,
            genre = media.genres?.map { it?.name.orEmpty() } ?: emptyList(),
            releaseDate = convertStringToCalendar(media.firstAirDate) ?: Calendar.getInstance(),

        )
        )
    }



    init {

        apiViewModel.mediaData.observeForever { mediaList ->
            Log.d("ControllerViewModel", "Updated media data: $mediaList")
        }
    }

    val test = apiViewModel.mediaData.value.toString()

    init {
        Log.d("ControllerViewModel", "Value of test: $test")
    }
    fun setOtherUser(uid: String) {
        userViewModel.setOtherUser(uid)
    }

    fun checkUserStatus() {
        authViewModel.checkUserStatus() // Kall autentiseringstatus
    }

    fun getAllMedia() {
        Log.d("ControllerViewModel", "getAllMedia called")
        apiViewModel.getAllMedia()
    }

    fun getMovie(movieId: String) {
        Log.d("ControllerViewModel", "getMovie called")
        apiViewModel.getMovie(movieId)
    }

    fun getShow(seriesId: Int) {
        Log.d("ControllerViewModel", "getShow called")
        //apiViewModel.getShow(seriesId)
    }

    fun getShowSeason(seriesId: Int, seasonNumber: Int) {
        Log.d("ControllerViewModel", "getShowSeason called")
        apiViewModel.getShowSeason(seriesId, seasonNumber)
    }

    fun getShowEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int) {
        Log.d("ControllerViewModel", "getShowEpisode called")
        apiViewModel.getShowEpisode(seriesId, seasonNumber, episodeNumber)
    }

    private val _singleProductionData = MutableStateFlow<Production?>(null)
    val singleProductionData: MutableStateFlow<Production?> get() = _singleProductionData

    fun nullifySingleProductionData() {
        _singleProductionData.value = null
    }

    fun getMovieById(id: String) {
        Log.d("ViewModel", "getMovieById called with id: $id")
        apiViewModel.getMovie(id)

        viewModelScope.launch {
            try {
                apiViewModel.movieData.collect { movieResponse ->
                    val production = movieResponse?.let { convertResponseToProduction(it) }
                    Log.d("Controller", "Observed production: $production")
                    _singleProductionData.update { production }
                }
            } catch(e: Exception) {
                Log.e("Controller", "Error collecting TV show data", e)
            }
        }
    }

    fun getTVShowById(id: String) {
        Log.d("Controller", "getTVShowById called with id: $id")
        apiViewModel.getShow(id)

        viewModelScope.launch {
            try {
                apiViewModel.showData.collect { showResponse ->
                    val production = showResponse?.let { convertResponseToProduction(it) }
                    Log.d("Controller", "Observed production: $production")
                    _singleProductionData.update { production }
                }
            } catch (e: Exception) {
                Log.e("Controller", "Error collecting TV show data", e)
            }
        }
    }

    private fun convertResponseToProduction(result: ApiProductionResponse): Production {
        return when (result) {
            is ApiMovieResponse -> convertApiMovieResponseToMovie(result)
            is ApiShowResponse -> convertApiShowResponseToTVShow(result)
            // Mangler ApiEpisodeResponse her
        }
    }

    private fun convertStringToCalendar(dateString: String?): Calendar? {
        if (dateString.isNullOrEmpty()) {
            return null
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Tilpass datoformatet ditt
        return try {
            val date = format.parse(dateString)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            calendar
        } catch (e: Exception) {
            null
        }
    }

    private fun convertApiMovieResponseToMovie(result: ApiMovieResponse): Movie {
        return Movie(
            imdbID = result.id.toString(),
            title = result.title.orEmpty(),
            description = result.overview.orEmpty(),
            posterUrl = "https://image.tmdb.org/t/p/w500" + result.posterPath,
            genre = result.genres?.map { it.name.orEmpty() } ?: emptyList(),
            releaseDate = convertStringToCalendar(result.releaseDate) ?: Calendar.getInstance(),
            // actors = // Trenger nytt API Kall -> /3/movie/{movie_id}/credits
            rating = result.voteAverage?.toInt(), // Forandres vel til interne ratings
            // reviews = Kommer når Firebase implementasjon er klart
            // trailerUrl = Trenger nytt API Kall -> /3/movie/{movie_id}/videos
            lengthMinutes = result.runtime
        )
    }

    private fun convertApiShowResponseToTVShow(result: ApiShowResponse): TVShow {
        return TVShow(
            imdbID = result.id.toString(),
            title = result.name.orEmpty(),
            description = result.overview.orEmpty(),
            posterUrl = "https://image.tmdb.org/t/p/w500" + result.posterPath,
            genre = result.genres?.map { it?.name.orEmpty() } ?: emptyList(),
            releaseDate = convertStringToCalendar(result.firstAirDate) ?: Calendar.getInstance(),
            // actors =  // Trenger nytt API Kall -> /3/tv/{series_id}/aggregate_credits
            rating = 0,
            // reviews = Kommer når Firebase implementasjon er klart
            // trailerUrl = Trenger nytt API Kall -> /3/movie/{movie_id}/videos
            episodes = listOf(result.numberOfEpisodes.toString()),
            // Må gjøres om til "numberOfEpisodes" og ikke liste, eller vi må gjøre Episode-kall for å få alle
            seasons = result.seasons?.map { it?.seasonNumber.toString() } ?: emptyList()
            // ^^ Seasons må nok forandres - er mer info om en sesong som kan være fint å ha?
        )
    }



    // This function is for testing purposes - DELETE LATER
    fun addToShowTest() {
        val loggedInUserId = currentFirebaseUser.value?.uid
        if (loggedInUserId != null) {
            val newListItem = ListItem(
                currentEpisode = 5,
                score = 7,
                production = TVShow(
                    imdbID = "tt3205802",
                    title = "How to Get Away with Murder",
                    description = "Annalise discovers there’s a surprise witness that threatens her case. Meanwhile, Connor tries to persuade the K3 to go along with a new plan. Elsewhere, a lie between Frank and Bonnie threatens their relationship as Annalise’s killer is finally revealed.",
                    genre = listOf("Crime", "Drama", "Mystery"),
                    releaseDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2014)
                        set(Calendar.MONTH, Calendar.SEPTEMBER) // Remember that months are 0-indexed
                        set(Calendar.DAY_OF_MONTH, 25)
                    },
                    episodes = listOf("101", "102", "103", "104", "201", "202", "203", "204"),
                    seasons = listOf("01", "02"),
                    actors = listOf(),
                    rating = 7,
                    reviews = listOf("reviewid0300", "reviewid0431"),
                    posterUrl = "https://image.tmdb.org/t/p/w500/bJs8Y6T88NcgksxA8UaVl4YX8p8.jpg"
                )
            )

            /*
            addCurrentlyWatchingShow(
                userID = loggedInUserId,
                listItem = newListItem,
                onSuccess = {
                    Log.d("Controller", "Show added to currently watching list successfully.")
                },
                onFailure = { errorMessage ->
                    Log.e("Controller", "Failed to add show: $errorMessage")
                }
            )
             */
        } else {
            Log.w("Controller", "User is not logged in.")
        }
    }

    fun getCurrentlyWatchingShows(): MutableList<ListItem>? {
        return loggedInUser.value?.currentlyWatchingCollection

    }

    fun getCompletedShows(): MutableList<ListItem>?{
        return loggedInUser.value?.completedCollection
    }

    fun getDroppedShows(): MutableList<ListItem>?{
        return loggedInUser.value?.droppedCollection
        }

    fun getWantToWatchList(): MutableList<ListItem>?{
        return loggedInUser.value?.wantToWatchCollection
    }

    fun getFavoritesList(): MutableList<ListItem>?{
        return loggedInUser.value?.favoriteCollection
    }

    /* // Nå er myReviews en liste med Review-IDer. Logikken må ta hensyn til å returnere ReviewDTO objekter, og ikke rene Review objekter
    fun getAllReviewsWrittenByLoggedInUser(): List<Review>{
        return loggedInUser.value?.myReviews ?: emptyList()
    }
    */

    /* // Nå er myReviews en liste med Review-IDer. Logikken må ta hensyn til å returnere ReviewDTO objekter, og ikke rene Review objekter
    fun getTopTenReviewsWrittenByLoggedInUser(): List<Review> {
        return getAllReviewsWrittenByLoggedInUser().sortedByDescending { it.likes }.take(10)
    }
    */

    //funksjon når allUsers er implementert

    /*fun getMostPopularReviewsLastMonth(): List<Review>{
        val lastMonth = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }

        val allUsers = getAllUsers()

        val reviews = allUsers.map{user -> user.reviews}

        return reviews.filter{review-> review.postDate.after(lastMonth)}.sortedByDescending{review -> review.likes}.take()
    }

    fun getTopTenReviewsLastMonth(): List<Review>{
        val AllReviews = getMostPopularReviewsLastMonth()

        return allReviews.take(10)
    }*/

    //fun productionReviewsAscending(production: Production): List<Review>{}


    fun genrePercentageMovie(): Map<String, Int>{
        val moviesWatched = (
                (loggedInUser.value?.wantToWatchCollection ?: emptyList()) +
                        (loggedInUser.value?.currentlyWatchingCollection ?: emptyList()) +
                        (loggedInUser.value?.completedCollection ?: emptyList()) +
                        (loggedInUser.value?.favoriteCollection ?: emptyList())
                )

        val mapOfGenres = mutableMapOf<String, Int>()
        var totalGenres = 0

        for (movie in moviesWatched){
            if(movie.production.type == "Movie"){
                movie.production.genre.forEach { genre->
                    mapOfGenres[genre] = mapOfGenres.getOrDefault(genre, 0) +1
                    totalGenres++
                }

            }
            

        }
        return if (totalGenres > 0) {
            mapOfGenres.mapValues { entry ->
                val prosent = (entry.value*100) / totalGenres
                prosent
            }

        }else{
            emptyMap()
        }
    }

    fun genrePercentageShows(): Map<String, Int>{
        val showsWatched = (
                (loggedInUser.value?.wantToWatchCollection ?: emptyList()) +
                        (loggedInUser.value?.currentlyWatchingCollection ?: emptyList()) +
                        (loggedInUser.value?.completedCollection ?: emptyList()) +
                        (loggedInUser.value?.favoriteCollection ?: emptyList())
                )

        val mapOfGenres = mutableMapOf<String, Int>()
        var totalShows = 0

        for (show in showsWatched){
            if(show.production.type != "movie"){
                show.production.genre.forEach { genre->
                    mapOfGenres[genre] = mapOfGenres.getOrDefault(genre, 0) +1
                    totalShows++
                }

            }
        }
        return mapOfGenres.mapValues { it ->
            val count = it.value
            if(totalShows > 0 ){
                (count*100)/  totalShows
            }else{
                0
            }

        }
    }

    fun getMostRecentProductionFromFriends(): List<ListItem> {
        val friendProductionsWatched = mutableListOf<ListItem>()

        loggedInUser.value?.friendList?.forEach { friend ->
            //friend.favoriteCollection.forEach { friendProductionsWatched.add(it) }
            //friend.completedCollection.forEach { friendProductionsWatched.add(it) }
            //friend.wantToWatchCollection.forEach { friendProductionsWatched.add(it) }
            //friend.droppedCollection.forEach { friendProductionsWatched.add(it) }
            //friend.currentlyWatchingCollection.forEach { friendProductionsWatched.add(it) }

        }

        return friendProductionsWatched.sortedByDescending { it.lastUpdated }.take(10)

    }

    fun getTopTenProductions(production: List<Production>): List<Production>{
        return production.filter { it.rating != null }.sortedByDescending { it.rating }.take(10)
    }

    /*
    fun getTenReviewsWithMostLikes(production: Production): List<String>{
        val theTimeNow = Calendar.getInstance()
        val sevenDaysFromNow = theTimeNow.add(Calendar.DAY_OF_MONTH, 7)

        return production.reviews.filter { it.postDate.after(sevenDaysFromNow) }

    }*/

    fun getCommonMoviesInWantToWatchList(): MutableList<ListItem> {
        val commonMoviesInWatchList = mutableListOf<ListItem>()

        val loggedInUserCollection = getWantToWatchList()?.filter { it.production.type == "movie" } ?: emptyList()
        val otherUserCollection = otherUser.value?.wantToWatchCollection?.filter { it.production.type == "movie" } ?: emptyList()

        val otherUserIds = otherUserCollection.map { it.id }.toSet()

        for (show in loggedInUserCollection) {
            for(otherShow in otherUserCollection)
                if (show.id == otherShow.id && commonMoviesInWatchList.none { it.id == show.id }) {
                commonMoviesInWatchList.add(show)
            }
        }

        return commonMoviesInWatchList
    }

    fun getCommonMoviesInCompletedList(): MutableList<ListItem> {
        val commonMoviesInCompletedList = mutableListOf<ListItem>()

        val loggedInUserCollection = getCompletedShows()?.filter { it.production.type == "movie" } ?: emptyList()
        val otherUserCollection = otherUser.value?.completedCollection?.filter { it.production.type == "movie" } ?: emptyList()

        for (show in loggedInUserCollection) {
            for (otherShow in otherUserCollection) {
                if (show.id == otherShow.id && commonMoviesInCompletedList.none { it.id == show.id }) {
                    commonMoviesInCompletedList.add(show)
                }
            }
        }
        return commonMoviesInCompletedList
    }


    fun getCommonFavoritesList(): MutableList<ListItem>{
        val commonMoviesInFavoritesList = mutableListOf<ListItem>()

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type == "movie" } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type == "movie" } ?: emptyList()

        for (show in loggedInUserCollection){
            for(otherShow in otherUserCollection){
                if (show.id == otherShow.id && commonMoviesInFavoritesList.none { it.id == show.id }){
                    commonMoviesInFavoritesList.add(show)
                }
            }
        }
        return commonMoviesInFavoritesList
    }

    fun getCommonCompletedShowsList() : MutableList<ListItem>{
        val commonShowsInCompletedList = mutableListOf<ListItem>()

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type != "movie" } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type != "movie" } ?: emptyList()

        for (show in loggedInUserCollection){
            for(otherShow in otherUserCollection){
                if (show.id == otherShow.id && commonShowsInCompletedList.none { it.id == show.id }){
                    commonShowsInCompletedList.add(show)
                }
            }
        }
        return commonShowsInCompletedList
    }

    fun getCommonWantToWatchShowsList() : MutableList<ListItem>{
        val commonShowsInWantToWatchList = mutableListOf<ListItem>()

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type != "movie" } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type != "movie" } ?: emptyList()

        for (show in loggedInUserCollection){
            for(otherShow in otherUserCollection){
                if (show.id == otherShow.id && commonShowsInWantToWatchList.none { it.id == show.id }){

                    commonShowsInWantToWatchList.add(show)
                }
            }
        }
        return commonShowsInWantToWatchList
    }

    fun getCommonFavoriteShowsList() : MutableList<ListItem>{
        val commonShowsInFavoritesList = mutableListOf<ListItem>()

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type != "movie" } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type != "movie" } ?: emptyList()

        for (show in loggedInUserCollection){
            for(otherShow in otherUserCollection){
                if (show.id == otherShow.id){
                    commonShowsInFavoritesList.add(show)
                }
            }
        }
        return commonShowsInFavoritesList
    }



    fun addProductionToWantToWatchList(production: Production) {
        val user = loggedInUser.value
        if (user != null) {
            val listItem = ListItem(
                currentEpisode = if (production is TVShow) 0 else -1,
                score = 0,
                production = production,
                lastUpdated = Calendar.getInstance()
            )
            user.wantToWatchCollection.add(listItem)
        } else {
            println("User is not logged in.")
        }
    }


    fun addProductionToCompletedShows(production: Production) {
        val user = loggedInUser.value
        if (user != null) {
            val listItem = ListItem(
                currentEpisode = if (production is TVShow) production.episodes.size else -1,
                score = 0,
                production = production,
                lastUpdated = Calendar.getInstance()
            )
            user.completedCollection.add(listItem)
        } else {
            println("User is not logged in.")
        }
    }


    fun addProductionToCurrentlyWatchingShows(production: Production) {
        val user = loggedInUser.value
        if (user != null) {
            val listItem = ListItem(
                currentEpisode = if (production is TVShow) 1 else -1,
                score = 0,
                production = production,
                lastUpdated = Calendar.getInstance()
            )
            user.currentlyWatchingCollection.add(listItem)
        } else {
            println("User is not logged in.")
        }
    }


    fun addProductionToDroppedShows(production: Production) {
        val user = loggedInUser.value
        if (user != null) {
            val listItem = ListItem(
                currentEpisode = if (production is TVShow) 0 else -1,
                score = 0,
                production = production,
                lastUpdated = Calendar.getInstance()
            )
            user.droppedCollection.add(listItem)
        } else {
            println("User is not logged in.")
        }
    }


    fun addProductionToFavoriteCollection(production: Production) {
        val user = loggedInUser.value
        if (user != null) {
            val listItem = ListItem(
                currentEpisode = if (production is TVShow) 0 else -1,
                score = 0,
                production = production,
                lastUpdated = Calendar.getInstance()
            )
            user.favoriteCollection.add(listItem)
        } else {
            println("User is not logged in.")
        }
    }

    fun getUsersFavoriteMovies(user: User?): List<ListItem> {

        if (user != null) {
            val favoriteMovies = mutableListOf<ListItem>()

            for (listItem in user.favoriteCollection) {
                if (listItem.production is Movie) {
                    favoriteMovies.add(listItem)
                }
            }
            return favoriteMovies.toList()
        } else {
            Log.d("FavoriteMovies", "getUsersFavoriteMovies: User is null")
        }

        return emptyList()
    }

    fun getUsersFavoriteTVShows(user: User?): List<ListItem> {

        if (user != null) {
            val favoriteTVShows = mutableListOf<ListItem>()

            for (listItem in user.favoriteCollection) {
                if (listItem.production is TVShow) {
                    favoriteTVShows.add(listItem)
                }
            }
            return favoriteTVShows.toList()
        } else {
            Log.d("FavoriteTVShows", "getUsersFavoriteTVShows: User is null")
        }

        return emptyList()
    }


    private val _friendsWatchedList = MutableStateFlow<List<ListItem>>(emptyList())
    val friendsWatchedList: StateFlow<List<ListItem>> get() = _friendsWatchedList

    private val _friendsJustWatchedLoading = MutableLiveData<Boolean>(true)
    val friendsJustWatchedLoading: LiveData<Boolean> get() = _friendsJustWatchedLoading



    init {
        // Lytt etter endringer i loggedInUser og kall getProductionsFromFriendsWatchedList når den er oppdatert
        viewModelScope.launch {
            loggedInUser.collect { user ->
                if (user != null) {
                    getProductionsFromFriendsWatchedList()
                }
            }
        }

    }


    private fun getProductionsFromFriendsWatchedList() {
        _friendsJustWatchedLoading.value = true

        viewModelScope.launch {
            try {
                // Bruk den suspenderende funksjonen for å hente vennene
                val friendsList = userViewModel.getUsersFriends()

                val recentFriendsWatched = mutableListOf<ListItem>()

                if (friendsList.isNotEmpty()) {
                    if (friendsList.size > 2) {
                        for (friend in friendsList) {
                            friend.completedCollection.lastOrNull()?.let { recentFriendsWatched.add(it) }
                        }
                    } else {
                        for (friend in friendsList) {
                            friend.completedCollection.takeLast(2).forEach { recentFriendsWatched.add(it) }
                        }
                    }
                }

                // Oppdater StateFlow
                _friendsWatchedList.value = recentFriendsWatched
            } catch (e: Exception) {
                // Håndter eventuelle feil her
                Log.e("UserViewModel", "Failed to fetch user's friends", e)
            } finally {
                _friendsJustWatchedLoading.value = false
            }
        }
    }

    fun addOrMoveToUsersCollection(productionID: String, targetCollection: String) {

        val userID = loggedInUser.value?.id
        val user = loggedInUser.value

        if (userID.isNullOrEmpty() || user == null) {
            Log.e("UserViewModel", "UserID or user data is null.")
            return
        }

        // Finn riktig listItem i de forskjellige samlingene
        var listItem = user.currentlyWatchingCollection.find { it.production.imdbID == productionID }
            ?: user.wantToWatchCollection.find { it.production.imdbID == productionID }
            ?: user.droppedCollection.find { it.production.imdbID == productionID }
            ?: user.completedCollection.find { it.production.imdbID == productionID }

        // Sjekk hvilken samling listItem tilhører (kilden)
        val sourceCollection = when {
            user.currentlyWatchingCollection.contains(listItem) -> "currentlyWatchingCollection"
            user.wantToWatchCollection.contains(listItem) -> "wantToWatchCollection"
            user.droppedCollection.contains(listItem) -> "droppedCollection"
            user.completedCollection.contains(listItem) -> "completedCollection"
            else -> null
        }

        if (listItem == null) {

            val productionData = singleProductionData.value
            if (productionData != null) {
                listItem = ListItem(production = productionData)
            }
            Log.e("UserViewModel", "List item with productionID: $productionID not found in any collection.")
        }

        if (sourceCollection == null || sourceCollection == targetCollection) {
            Log.e("UserViewModel", "Invalid source or target collection.")
        }

        if (listItem != null) {
            userViewModel.addOrMoveToUsersCollection(userID, listItem, sourceCollection, targetCollection)
        }
    }

    fun removeProductionFromCollections(productionID: String) {

        val userID = loggedInUser.value?.id
        val user = loggedInUser.value

        if (userID.isNullOrEmpty() || user == null) {
            Log.e("UserViewModel", "UserID or user data is null.")
            return
        }

        // Finn riktig listItem i de forskjellige samlingene
        var listItem = user.currentlyWatchingCollection.find { it.production.imdbID == productionID }
            ?: user.wantToWatchCollection.find { it.production.imdbID == productionID }
            ?: user.droppedCollection.find { it.production.imdbID == productionID }
            ?: user.completedCollection.find { it.production.imdbID == productionID }

        // Sjekk hvilken samling listItem tilhører (kilden)
        val sourceCollection = when {
            user.currentlyWatchingCollection.contains(listItem) -> "currentlyWatchingCollection"
            user.wantToWatchCollection.contains(listItem) -> "wantToWatchCollection"
            user.droppedCollection.contains(listItem) -> "droppedCollection"
            user.completedCollection.contains(listItem) -> "completedCollection"
            else -> null
        }

        if (sourceCollection == null) {
            Log.e("UserViewModel", "Invalid source collection.")
        }

        if (listItem != null) {
            userViewModel.removeProductionFromCollections(userID, listItem, sourceCollection)
        }
    }

    fun isInCurrentlyWatching(productionID: String) : Boolean {

        val user = loggedInUser.value
        val listItem = user?.currentlyWatchingCollection?.find { it.production.imdbID == productionID }

        return listItem != null
    }

    private val _singleReviewDTOData = MutableStateFlow<ReviewDTO?>(null)
    val singleReviewDTOData: MutableStateFlow<ReviewDTO?> get() = _singleReviewDTOData

    fun getReview() {
        // Logikk her som henter fra Firebase

        val reviewTemp = Review(
            score = Random.nextInt(0, 10), //<- TEMP CODE: PUT IN REAL CODE
            reviewerID = "userIDhere",
            productionID = "154423",
            reviewBody = "It’s reasonably well-made, and visually compelling," +
                    "but it’s ultimately too derivative, and obvious in its thematic execution," +
                    "to recommend..",
            postDate = Calendar.getInstance(),
            likes = Random.nextInt(0, 100) //<- TEMP CODE: PUT IN REAL CODE
        )

        val reviewerTemp = User(
            id = "test",
            email = "test@email.no",
            userName = "tempUser",
        )

        val productionTemp = Movie()

        val reviewDTO = createReviewDTO(reviewTemp, reviewerTemp, productionTemp)

        _singleReviewDTOData.update { reviewDTO }
    }

    private fun createReviewDTO(review: Review, reviewer: User, production: Production): ReviewDTO {
        return ReviewDTO(
            reviewID = review.reviewID,
            score = review.score,
            productionID = review.productionID,
            reviewerID = review.reviewerID,
            reviewBody = review.reviewBody,
            postDate = review.postDate,
            likes = review.likes,
            reviewerUserName = reviewer.userName,
            reviewerProfileImage = reviewer.profileImageID,
            productionPosterUrl = production.posterUrl,
            productionTitle = production.title,
            productionReleaseDate = production.releaseDate,
            productionType = production.type
        )
    }

    private val _reviewDTOs = MutableStateFlow<List<ReviewDTO>>(emptyList())
    val reviewDTOs: StateFlow<List<ReviewDTO>> get() = _reviewDTOs

    fun nullifyReviewDTOs() {
        _reviewDTOs.value = emptyList()
    }

    fun getReviewByProduction(productionID: String, productionType: String) {
        // Start som en suspenderende funksjon innenfor en coroutine
        viewModelScope.launch {
            try {
                // Hent anmeldelser fra Firestore
                val reviewsRaw = firestoreRepository.getReviewByProduction(productionID, productionType)

                val reviewsObjects = reviewsRaw.mapNotNull { convertReviewJsonToReviewObject(it) }

                val production = singleProductionData.value
                if (production == null) {
                    Log.d("GetReviews", "Production data is null, aborting.")
                    _reviewDTOs.value = emptyList()
                    return@launch
                }

                // Lager map for enklere oversikt over reviewerID mot User-objekt
                val reviewers = reviewsObjects.map { review ->
                    async {
                        review.reviewerID to (userViewModel.getUser(review.reviewerID)
                            ?: User(id = "fallbackReviewer", email = "default@email.com", userName = "Anonymous"))
                    }
                }.awaitAll().toMap() // Konverter til en Map for enkel matching

                // Opprett ReviewDTO-er ved å matche reviewerID med brukere fra "reviewers"
                val reviewDTOs = reviewsObjects.mapNotNull { review ->
                    val reviewer = reviewers[review.reviewerID]
                    reviewer?.let { createReviewDTO(review, it, production) }
                }

                _reviewDTOs.value = reviewDTOs
                Log.d("GetReviews", "Updated StateFlow with ${reviewDTOs.size} reviews")
            } catch (exception: Exception) {

                Log.d("GetReviews", "Failed to fetch reviews: $exception")
                _reviewDTOs.value = emptyList()
            }
        }
    }


    private fun convertReviewJsonToReviewObject(reviewJson: Map<String, Any>?): Review? {
        if (reviewJson == null) return null

        val moshi = Moshi.Builder()
            .add(FirebaseTimestampAdapter()) // Adapter for Firestore Timestamps
            .add(UUIDAdapter())              // Adapter for UUID
            .addLast(KotlinJsonAdapterFactory()) // For Kotlin-klasser
            .build()

        // Konverter Map til JSON-streng
        val jsonAdapter = moshi.adapter(Review::class.java)
        val json = mapToJson(reviewJson, moshi)

        // Deserialiser JSON-streng til Review-objekt
        return jsonToReview(json, jsonAdapter)
    }

    private fun mapToJson(map: Map<String, Any>, moshi: Moshi): String {
        return moshi.adapter(Map::class.java).toJson(map)
    }

    private fun jsonToReview(json: String, jsonAdapter: JsonAdapter<Review>): Review? {
        return jsonAdapter.fromJson(json)
    }

    fun createUserWithEmailAndPassword(
        username: String,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        authViewModel.createUserWithEmailAndPassword(username, email, password, onSuccess, onFailure)
    }

}
