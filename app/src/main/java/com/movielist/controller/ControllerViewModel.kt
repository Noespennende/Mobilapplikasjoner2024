package com.movielist.controller

import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.movielist.data.addCurrentlyWatchingShow
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.Review
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.viewmodel.ApiViewModel
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar


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

    private val _singleProductionData = MutableLiveData<Production?>()
    val singleProductionData: LiveData<Production?> get() = _singleProductionData

    fun nullifySingleProductionData() {
        _singleProductionData.value = null
    }

    fun getMovieById(id: String) {
        Log.d("ViewModel", "getMovieById called with id: $id")
        apiViewModel.getMovie(id)

        // Opprett en lokal observer
        val oneTimeObserver = object : Observer<ApiMovieResponse?> {
            override fun onChanged(movieResponse: ApiMovieResponse?) {
                val production = movieResponse?.let { convertResponseToProduction(it) }
                Log.d("ViewModel", "Observed production: $production")
                _singleProductionData.value = production

                // Fjern observeren etter første oppdatering
                apiViewModel.movieData.removeObserver(this)
            }
        }

        // Legg til den lokale observeren for en enkel oppdatering
        apiViewModel.movieData.observeForever(oneTimeObserver)
    }

    fun getTVShowById(id: String) {
        Log.d("ViewModel", "getTVShowById called with id: $id")
        apiViewModel.getShow(id)

        // Opprett en lokal observer
        val oneTimeObserver = object : Observer<ApiShowResponse?> {
            override fun onChanged(showResponse: ApiShowResponse?) {
                val production = showResponse?.let { convertResponseToProduction(it) }
                Log.d("ViewModel", "Observed production: $production")
                _singleProductionData.value = production

                // Fjern observeren etter første oppdatering
                apiViewModel.showData.removeObserver(this)
            }
        }

        // Legg til den lokale observeren for en enkel oppdatering
        apiViewModel.showData.observeForever(oneTimeObserver)
    }


    private fun convertResponseToProduction(result: Any): Production? {
        return when (result) {
           is ApiMovieResponse -> Movie(
                imdbID = result.id.toString(),
                title = result.title.orEmpty(),
                description = result.overview.orEmpty(),
                //genre = result.genreIds.orEmpty(),
                //releaseDate = result.releaseDate ?: Calendar.getInstance(),
                //actors = result.actors.orEmpty(),
                //rating = result.rating,
                //reviews = result.reviews.orEmpty(),
                posterUrl = result.posterPath,
                //trailerUrl = result.trailerUrl.orEmpty(),
                //lengthMinutes = result.lengthMinutes
            )
            is ApiShowResponse -> TVShow(
                imdbID = result.id.toString(),
                title = result.name.toString(),
                description = result.overview.orEmpty(),
                //genre = result.genre.orEmpty(),
                //releaseDate = result.releaseDate ?: Calendar.getInstance(),
                //actors = result.actors.orEmpty(),
                //rating = result.rating,
                //reviews = result.reviews.orEmpty(),
                posterUrl = result.posterPath,
                //trailerUrl = result.trailerUrl.orEmpty(),
                //episodes = result.episodes.orEmpty(),
                //seasons = result.seasons.orEmpty()
            )
            else -> null // Hvis det ikke er en film eller tv-show, returner null
        }
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

    fun getAllReviewsWrittenByLoggedInUser(): List<Review>{
        return loggedInUser.value?.myReviews ?: emptyList()
    }

    fun getTopTenReviewsWrittenByLoggedInUser(): List<Review> {
        return getAllReviewsWrittenByLoggedInUser().sortedByDescending { it.likes }.take(10)
    }
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
            userViewModel.getUsersFriends { friendsList ->
                val recentFriendsWatched = mutableListOf<ListItem>()

                if (friendsList.isNotEmpty()) {

                    if(friendsList.size > 2) {
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
                _friendsJustWatchedLoading.value = false
            }
        }
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
