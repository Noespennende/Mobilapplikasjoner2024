package com.movielist.controller

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.movielist.data.FirestoreRepository
import com.movielist.data.LocalStorageKeys
import com.movielist.data.createDataStore
import com.movielist.model.AllMedia
import com.movielist.model.ApiMovieResponse
import com.movielist.model.ApiProductionResponse
import com.movielist.model.ApiShowResponse
import com.movielist.model.FollowStatus
import com.movielist.model.ListItem
import com.movielist.model.ListOptions
import com.movielist.model.Movie
import com.movielist.model.MovieResponse
import com.movielist.model.PostNotification
import com.movielist.model.Production
import com.movielist.model.ProductionType
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.ShowResponse
import com.movielist.model.SearchSortOptions
import com.movielist.model.ShowSortOptions
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.viewmodel.ApiViewModel
import com.movielist.viewmodel.AuthViewModel
import com.movielist.viewmodel.ReviewViewModel
import com.movielist.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log


class ControllerViewModel(
    private val userViewModel: UserViewModel,
    private val authViewModel: AuthViewModel,
    private val apiViewModel: ApiViewModel,
    private val reviewViewModel: ReviewViewModel
) : ViewModel() {

    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    /* USER LOGIC */

    val currentFirebaseUser: StateFlow<FirebaseUser?> = authViewModel.currentUser

    val isLoggedIn: State<Boolean> = authViewModel.isLoggedIn
    val loggedInUser: StateFlow<User?> = userViewModel.loggedInUser
    val otherUser: StateFlow<User?> = userViewModel.otherUser

    fun setLoggedInUser(uid: String) {
        userViewModel.setLoggedInUser(uid)
    }

    fun logOutUser() {
        authViewModel.logOut()
    }

    private val _filteredMediaData = MutableStateFlow<List<Production>>(emptyList())
    val filteredMediaData: StateFlow<List<Production>> get() = _filteredMediaData

    private val _searchResult = MutableStateFlow<List<Production>>(emptyList())
    val searchResults: StateFlow<List<Production>> get() = _searchResult

    private val _displayedList = MutableStateFlow<List<ListItem>>(emptyList())
    val displayedList: StateFlow<List<ListItem>> = _displayedList

    fun updateDisplayedList(activeCategory: ListOptions, activeSortOption: ShowSortOptions) {
        val user = loggedInUser.value
        if (user == null) {
            _displayedList.value = emptyList()
            return
        }

        val list = when (activeCategory) {
            ListOptions.WATCHING -> user.currentlyWatchingCollection
            ListOptions.COMPLETED -> user.completedCollection
            ListOptions.WANTTOWATCH -> user.wantToWatchCollection
            ListOptions.DROPPED -> user.droppedCollection
            else -> emptyList()
        }

        _displayedList.value = when (activeSortOption) {
            ShowSortOptions.MOVIESANDSHOWS -> list
            ShowSortOptions.MOVIES -> list.filter { it.production.type == ProductionType.MOVIE }
            ShowSortOptions.SHOWS -> list.filter { it.production.type == ProductionType.TVSHOW }
        }
    }


    fun getPopularMoviesAndShows() {
        apiViewModel.getAllMedia()

        apiViewModel.mediaData.observeForever { mediaList ->

            val convertedResults = mediaList.map { media ->
                if (media.mediaType.equals("movie", ignoreCase = true)) {
                    convertToMovie(media)
                } else {
                    convertToTVShow(media)
                }
            }

            _filteredMediaData.value = convertedResults
                .sortedByDescending { it.rating }
                .take(10)
        }
    }

    private val _userSearchResults = MutableStateFlow<List<User>>(emptyList())
    val userSearchResults: StateFlow<List<User>> = _userSearchResults

    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                val users = userViewModel.fetchUsersFromFirebase(query)
                _userSearchResults.value = users
            } catch (e: Exception) {
                _userSearchResults.value = emptyList()
            }
        }
    }

    private val _profileOwner = MutableStateFlow<User?>(null)
    val profileOwner: StateFlow<User?> get() = _profileOwner

    private val _profileBelongsToLoggedInUser = MutableStateFlow(false)
    val profileBelongsToLoggedInUser: StateFlow<Boolean> get() = _profileBelongsToLoggedInUser

    suspend fun loadProfileOwner(userID: String) {
        Log.d("Profile", "Loading profile for userID: $userID")
        _profileOwner.value = if (userID == loggedInUser.value?.id) {

            _profileBelongsToLoggedInUser.value = true
            Log.d("Profile", "Profile owner is the logged-in user.")


            loggedInUser.value

        } else {

            _profileBelongsToLoggedInUser.value = false
            setOtherUser(userID)
            val other = otherUser.firstOrNull { it?.id == userID }
            Log.d("Profile", "Found other user: ${other?.userName}")
            other
        }

    }

    fun determineFollowStatus(): FollowStatus {
        val currentUser = loggedInUser.value
        val profileUser = _profileOwner.value

        return if (currentUser != null && profileUser != null) {
            val followingList = currentUser.followingList ?: emptyList()

            if (profileUser.id in followingList) {
                FollowStatus.FOLLOWING
            } else {
                FollowStatus.NOTFOLLOWING
            }
        } else {
            FollowStatus.NOTFOLLOWING
        }
    }

    fun getUserFollowingCount(): Int {
        val user = loggedInUser.value
        return user?.followingList?.size ?: 0
    }

    fun getUsersFollowingMeCount(): Int {
        val user = loggedInUser.value
        return user?.followingMeList?.size ?: 0
    }

    fun getMovieInListsCount(): Int {
        val user = loggedInUser.value

        if (user == null) {
            Log.d("MovieLists", "User is null, returning count as 0")
            return 0
        }
        val uniqueMovieTitles = mutableSetOf<String>()

        user.completedCollection.filter { it.production.type == ProductionType.MOVIE }
            .forEach { uniqueMovieTitles.add(it.production.title) }
        user.favoriteCollection.filter { it.production.type == ProductionType.MOVIE }
            .forEach { uniqueMovieTitles.add(it.production.title) }
        user.currentlyWatchingCollection.filter { it.production.type == ProductionType.MOVIE }
            .forEach { uniqueMovieTitles.add(it.production.title) }
        user.wantToWatchCollection.filter { it.production.type == ProductionType.MOVIE }
            .forEach { uniqueMovieTitles.add(it.production.title) }
        user.droppedCollection.filter { it.production.type == ProductionType.MOVIE }
            .forEach { uniqueMovieTitles.add(it.production.title) }

        return uniqueMovieTitles.size

    }

    fun getFilteredUserProductions(showSortOption: ShowSortOptions): List<ListItem> {
        val user = loggedInUser.value

        if (user == null) {
            Log.d("Controller", "User data is not loaded yet.")
            return emptyList()
        }

        val allProductions = (user.completedCollection +
                user.wantToWatchCollection +
                user.currentlyWatchingCollection +
                user.favoriteCollection +
                user.droppedCollection)


        return when (showSortOption) {
            ShowSortOptions.MOVIESANDSHOWS -> allProductions
            ShowSortOptions.MOVIES -> allProductions.filter { it.production.type == ProductionType.MOVIE }
            ShowSortOptions.SHOWS -> allProductions.filter { it.production.type == ProductionType.TVSHOW }
        }
    }

    fun getShowsInListsCount(): Int {
        val user = loggedInUser.value
        if (user == null) {
            Log.d("MovieLists", "User is null, returning count as 0")
            return 0
        }
        val uniqueShowTitles = mutableSetOf<String>()

        user.completedCollection.filter { it.production.type == ProductionType.TVSHOW }
            .forEach { uniqueShowTitles.add(it.production.title) }
        user.favoriteCollection.filter { it.production.type == ProductionType.TVSHOW }
            .forEach { uniqueShowTitles.add(it.production.title) }
        user.currentlyWatchingCollection.filter { it.production.type == ProductionType.TVSHOW }
            .forEach { uniqueShowTitles.add(it.production.title) }
        user.wantToWatchCollection.filter { it.production.type == ProductionType.TVSHOW }
            .forEach { uniqueShowTitles.add(it.production.title) }
        user.droppedCollection.filter { it.production.type == ProductionType.TVSHOW }
            .forEach { uniqueShowTitles.add(it.production.title) }


        return uniqueShowTitles.size
        }


    fun addUserToFollowerList(otherUser: User){
        val user = loggedInUser.value

        if (user != null) {
            Log.d("FollowerList", "Attempting to add user ${otherUser.id} to ${user.id}'s following list.")
            val updatedOtherUserFollowerList = otherUser.followingMeList.toMutableList()
            val updatedFollowerList = user.followingList.toMutableList().apply {

                if (!contains(otherUser.id)) {
                    add(otherUser.id)
                    updatedOtherUserFollowerList.add(user.id)
                    Log.d("FollowerList", "User ${otherUser.id} successfully added to ${user.id}'s following list.")
                } else {
                    Log.d("FollowerList", "User ${otherUser.id} is already in the following list.")
                }
            }
            val updatedOtherUser = otherUser.copy(followingMeList = updatedOtherUserFollowerList)
            val updatedUser = user.copy(followingList = updatedFollowerList)

            userViewModel.updateFollowingForUser(updatedUser, updatedOtherUser)


            Log.d("FollowerList", "Updated USER following list after addition: ${updatedFollowerList}")
            Log.d("FollowerList", "\nUpdated OTHERUSER followingList after addition: ${updatedOtherUserFollowerList}")
        }
    }

    fun removeUserFromFollowerList(otherUser: User) {
        val user = loggedInUser.value

        if (user != null) {
            Log.d("FollowerList", "Attempting to remove user ${otherUser.id} from ${user.id}'s following list.")


            val updatedFollowerList = user.followingList.toMutableList().apply {
                removeAll { it == otherUser.id }
            }

            val updatedUser = user.copy(followingList = updatedFollowerList)

            userViewModel.updateLoggedInUser(updatedUser)

            Log.d("FollowerList", "Updated USER following list after removal: ${updatedFollowerList}")
        }
    }

    fun editUserBio(newBio: String) {
        _profileOwner.value?.let { user ->
            user.bio = newBio

            loggedInUser.value?.id?.let { userId ->
                val myUpdates = mapOf("bio" to newBio)

                firestoreRepository.updateUserField(userId, myUpdates)

            }
        }
    }

    fun editUserGender(newGender: String){
        _profileOwner.value?.let { user ->
            user.gender = newGender

            loggedInUser.value?.id?.let { userId ->
                val myUpdates = mapOf("gender" to newGender)

                firestoreRepository.updateUserField(userId, myUpdates)

            }
        }
    }

    fun editUserWebsite(newWebsite: String){
        _profileOwner.value?.let { user ->
            user.website = newWebsite

            loggedInUser.value?.id?.let { userId ->
                val myUpdates = mapOf("website" to newWebsite)

                firestoreRepository.updateUserField(userId, myUpdates)

            }
        }
    }

    fun editUserLocation(newLocation: String){
        _profileOwner.value?.let { user ->
            user.location = newLocation

            loggedInUser.value?.id?.let { userId ->
                val myUpdates = mapOf("location" to newLocation)

                firestoreRepository.updateUserField(userId, myUpdates)

            }
        }
    }

    fun getSharedProductions(comparingUser: User): Map<ListItem, ListItem> {

        val loggedInUserProductions = loggedInUser.value?.getAllMoviesAndShows2()
        val comparingUserProductions = comparingUser?.getAllMoviesAndShows2()

        if (loggedInUserProductions != null) {
            Log.d("Profile", "we're in: ${loggedInUserProductions.count()} + ${comparingUserProductions?.count()}")
            val sharedProductions: Map<ListItem, ListItem> = loggedInUserProductions.associateWith { loggedInUserProduction ->
                comparingUserProductions?.find { it.production.imdbID == loggedInUserProduction.production.imdbID }
            }.filterValues { it != null } as Map<ListItem, ListItem>

            return sharedProductions
        }

        return emptyMap()
    }


    fun getUniqueProductions(
        comparingUser: User,
        sharedProductions: Map<ListItem, ListItem>
    ): Pair<List<ListItem>, List<ListItem>> {
        val loggedInUserProductions = loggedInUser.value?.getAllMoviesAndShows2() ?: emptyList()
        val comparingUserProductions = comparingUser.getAllMoviesAndShows2()

        // Hent kun de unike elementene som IKKE er i sharedShowsAndMovies
        val loggedInUserShared = sharedProductions.keys
        val comparingUserShared = sharedProductions.values
        val uniqueToLoggedInUser = loggedInUserProductions.filter { it !in comparingUserProductions && it !in loggedInUserShared }
        val uniqueToComparisonUser = comparingUserProductions.filter { it !in loggedInUserProductions && it !in comparingUserShared }

        return Pair(uniqueToLoggedInUser, uniqueToComparisonUser)
    }


    fun searchMedia(query: String, sortOptions: SearchSortOptions) {
        apiViewModel.searchMulti(query)

        if (sortOptions == SearchSortOptions.USER) {
            viewModelScope.launch {
                userViewModel.fetchUsersFromFirebase(query)
            }
        }

        viewModelScope.launch {
            try {
                apiViewModel.searchResults.collect { searchResultsList ->
                    val convertedSearchResults = when (sortOptions) {
                        SearchSortOptions.MOVIESANDSHOWS -> {
                            searchResultsList.map { media ->
                                if (media.mediaType.equals("movie", ignoreCase = true)) {
                                    convertToMovie(media)
                                } else {
                                    convertToTVShow(media)
                                }
                            }
                        }
                        SearchSortOptions.MOVIE -> {
                            searchResultsList.filter { it.mediaType.equals("movie", ignoreCase = true) }
                                .map { convertToMovie(it) }
                        }
                        SearchSortOptions.SHOW -> {
                            searchResultsList.filter { it.mediaType.equals("tv", ignoreCase = true) }
                                .map { convertToTVShow(it) }
                        }
                        SearchSortOptions.USER -> {
                            userViewModel.searchResults.collect { userResults ->
                                userResults.filter { it.userName.contains(query, ignoreCase = true) }
                            }
                        }
                        else -> emptyList()
                    }

                    _searchResult.value = convertedSearchResults.sortedBy { it.title }

                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error searching media: ${e.message}")
            }
        }
    }



    fun searchMultibleMedia(query: String) {
        apiViewModel.searchMulti(query)

        viewModelScope.launch {
            try {
                apiViewModel.searchResults.collect { searchResultsList ->

                    val convertedSearchResults = searchResultsList.map { media ->
                        if (media.mediaType.equals("movie", ignoreCase = true)) {
                            convertToMovie(media)
                        } else {
                            convertToTVShow(media)
                        }
                    }

                    _searchResult.value = convertedSearchResults
                    Log.d("ControllerViewModel", "Search results updated: $convertedSearchResults")
                }
            } catch (e: Exception) {
                Log.e("ControllerViewModel", "Error collecting search results", e)
            }
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
            posterUrl = "https://image.tmdb.org/t/p/w500/"+media.posterPath,

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

    fun getShow(seriesId: String) {
        Log.d("ControllerViewModel", "getShow called")
        apiViewModel.getShow(seriesId)
    }

    fun getShowSeason(seriesId: String, seasonNumber: String) {
        Log.d("ControllerViewModel", "getShowSeason called")
        apiViewModel.getShowSeason(seriesId, seasonNumber)
    }

    fun getShowEpisode(seriesId: String, seasonNumber: String, episodeNumber: String) {
        Log.d("ControllerViewModel", "getShowEpisode called")
        apiViewModel.getShowEpisode(seriesId, seasonNumber, episodeNumber)
    }

    fun getMovieCredits(movieId: String) {
        Log.d("ControllerViewModel", "getMovieCredits called")
        apiViewModel.getMovieCredits(movieId)
    }

    fun getShowCredits(seriesId: String) {
        Log.d("ControllerViewModel", "getShowCredits called")
        apiViewModel.getShowCredits(seriesId)
    }

    fun getMovieVideo(movieId: String) {
        Log.d("ControllerViewModel", "getMovieVideo called")
        apiViewModel.getMovieVideo(movieId)
    }

    fun getShowVideo(seriesId: String) {
        Log.d("ControllerViewModel", "getShowVideo called")
        apiViewModel.getShowVideo(seriesId)
    }

    private val _singleProductionData = MutableStateFlow<Production?>(null)
    val singleProductionData: MutableStateFlow<Production?> get() = _singleProductionData

    fun nullifySingleProductionData() {
        _singleProductionData.value = null
    }

    private suspend fun fetchAdditionalMovieData(movieId: String): Pair<List<String>, String?> {
        return try {
            val credits = fetchMovieCredits(movieId)
            val videoKey = fetchMovieVideo(movieId)
            val trailerUrl = videoKey?.let { "https://www.youtube.com/watch?v=$it" }
            Pair(credits, trailerUrl)
        } catch (e: Exception) {
            Log.e("ControllerViewModel", "Error fetching movie data for movieId: $movieId", e)
            Pair(emptyList(), null)
        }
    }

    private suspend fun fetchAdditionalShowData(seriesId: String): Pair<List<String>, String?> {
        return try {
            val credits = fetchShowCredits(seriesId)
            val videoKey = fetchShowVideo(seriesId)
            val trailerUrl = videoKey?.let { "https://www.youtube.com/watch?v=$it" }
            Pair(credits, trailerUrl)
        } catch (e: Exception) {
            Log.e("ControllerViewModel", "Error fetching show data for seriesId: $seriesId", e)
            Pair(emptyList(), null)
        }
    }

    private suspend fun fetchMovieCredits(movieId: String): List<String> {
        apiViewModel.getMovieCredits(movieId)
        return apiViewModel.movieCreditData.firstOrNull()?.cast?.map { it.name.orEmpty() } ?: emptyList()
    }

    private suspend fun fetchMovieVideo(movieId: String): String? {
        apiViewModel.getMovieVideo(movieId)
        return apiViewModel.movieVideoData.firstOrNull()
            ?.firstOrNull { it.type == "Trailer" && it.name == "Official Trailer" }
            ?.key
    }

    private suspend fun fetchShowCredits(seriesId: String): List<String> {
        apiViewModel.getShowCredits(seriesId)
        return apiViewModel.showCreditData.firstOrNull()?.cast?.map { it.name.orEmpty() } ?: emptyList()
    }

    private suspend fun fetchShowVideo(seriesId: String): String? {
        apiViewModel.getShowVideo(seriesId)
        return apiViewModel.showVideoData.firstOrNull()
            ?.firstOrNull { it.type == "Trailer" && it.name == "Official Trailer" }
            ?.key
    }

    fun setMovieById(id: String) {
        Log.d("Controller", "setMovieById called with id: $id")

        viewModelScope.launch {

            try {
                nullifySingleProductionData()
                apiViewModel.getMovieDetails(id)

                apiViewModel.movieDataTest.collect { movieResponse ->
                    val production = movieResponse?.let { convertResponseToProduction(it) }
                    Log.d("Controller", "Observed production: $production")
                    _singleProductionData.update { production }
                }

            } catch(e: Exception) {
                Log.e("Controller", "Error collecting Movie data", e)
            }
        }
    }

    fun setTVShowById(id: String) {
        Log.d("Controller", "getTVShowById called with id: $id")


        viewModelScope.launch {

            try {
                nullifySingleProductionData()
                apiViewModel.getShowDetails(id)
                apiViewModel.showDataTest.collect { showResponse ->
                    val production = showResponse?.let { convertResponseToProduction(it) }
                    Log.d("Controller", "Observed production: $production")
                    if (production != null) {
                        Log.d("Controller", "${production.trailerUrl}")
                    }
                    _singleProductionData.update { production }
                }
            } catch (e: Exception) {
                Log.e("Controller", "Error collecting TV show data", e)
            }
        }
    }

    private fun convertResponseToProduction(result: ApiProductionResponse): Production {
        return when (result) {

            is MovieResponse -> {

                val credits = result.movieCreditData.cast?.map { it.name.orEmpty() } ?: emptyList()
                val videoKey = result.movieVideoData?.firstOrNull { it.name == "Official Trailer" }?.key
                val trailerUrl = videoKey?.let { "https://www.youtube.com/watch?v=$it" }

                Log.d("Cott", "${result.movieData.title} + ${videoKey} + ${trailerUrl}")
                convertApiMovieResponseToMovie(result.movieData, credits, trailerUrl)
            }

            is ShowResponse -> {

                val credits = result.showCreditData.cast?.map { it.name.orEmpty() } ?: emptyList()
                val videoKey = result.showVideoData?.firstOrNull {
                    it?.name?.contains("Official Trailer", ignoreCase = true) == true }?.key
                val trailerUrl = videoKey?.let { "https://www.youtube.com/watch?v=$it" }

                Log.d("Cott", "${result.showData.name} + ${videoKey} + ${trailerUrl}")

                convertApiShowResponseToTVShow(result.showData, credits, trailerUrl)
            }
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

    private fun convertApiMovieResponseToMovie(
        result: ApiMovieResponse,
        actors: List<String>,
        trailerUrl: String?
    ): Movie {

        return Movie(
            imdbID = result.id.toString(),
            title = result.title.orEmpty(),
            description = result.overview.orEmpty(),
            posterUrl = "https://image.tmdb.org/t/p/w500" + result.posterPath,
            genre = result.genres?.map { it.name.orEmpty() } ?: emptyList(),
            releaseDate = convertStringToCalendar(result.releaseDate) ?: Calendar.getInstance(),
            actors = actors,
            rating = result.voteAverage?.toInt(),
            trailerUrl = trailerUrl.toString(),
            lengthMinutes = result.runtime
        )
    }

    private fun convertApiShowResponseToTVShow(
        result: ApiShowResponse,
        actors: List<String>,
        trailerUrl: String?
    ): TVShow {
        return TVShow(
            imdbID = result.id.toString(),
            title = result.name.orEmpty(),
            description = result.overview.orEmpty(),
            posterUrl = "https://image.tmdb.org/t/p/w500" + result.posterPath,
            genre = result.genres?.map { it?.name.orEmpty() } ?: emptyList(),
            releaseDate = convertStringToCalendar(result.firstAirDate) ?: Calendar.getInstance(),
            actors = actors,
            trailerUrl = trailerUrl.toString(),
            episodes  = (1..(result.numberOfEpisodes ?: 1)).map { it.toString() },
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
            if(movie.production.type == ProductionType.MOVIE){
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
            if(show.production.type != ProductionType.MOVIE){
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

        loggedInUser.value?.followingList?.forEach { friend ->
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

        val loggedInUserCollection = getWantToWatchList()?.filter { it.production.type == ProductionType.MOVIE } ?: emptyList()
        val otherUserCollection = otherUser.value?.wantToWatchCollection?.filter { it.production.type == ProductionType.MOVIE } ?: emptyList()

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

        val loggedInUserCollection = getCompletedShows()?.filter { it.production.type == ProductionType.MOVIE } ?: emptyList()
        val otherUserCollection = otherUser.value?.completedCollection?.filter { it.production.type == ProductionType.MOVIE } ?: emptyList()

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

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type == ProductionType.MOVIE } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type == ProductionType.MOVIE } ?: emptyList()

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

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type != ProductionType.MOVIE } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type != ProductionType.MOVIE } ?: emptyList()

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

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type != ProductionType.MOVIE } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type != ProductionType.MOVIE } ?: emptyList()

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

        val loggedInUserCollection = getFavoritesList()?.filter { it.production.type != ProductionType.MOVIE } ?: emptyList()
        val otherUserCollection = otherUser.value?.favoriteCollection?.filter { it.production.type != ProductionType.MOVIE } ?: emptyList()

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

    fun handleEpisodeCountChange(
        listItem: ListItem,
        watchedEpisodeCount: Int,
        isPlus: Boolean,
        onMoveToCollection: () -> Unit)  {

        viewModelScope.launch {

            val sourceCollection = findListItemCollection(listItem)

            if (sourceCollection == null) {

                Log.d("ControllerViewModel", "sourceCollection is RemoveFromList for listItem")
                return@launch
            }


            val production = listItem.production
            val user = loggedInUser.value
            val userID = user?.id
            val targetCollection = "completedCollection"
            val ToWatching = "currentlyWatchingCollection"

            if (userID != null) {
                when (production) {
                    is TVShow -> {
                        Log.d("ControllerViewModel", "sourceCollection: $sourceCollection")
                        val productionTotalEpisode = production.episodes.size

                        val productionIsComplete = watchedEpisodeCount == productionTotalEpisode

                        userViewModel.updateCurrentEpisodeInCollection(
                            sourceCollection,
                            listItem,
                            watchedEpisodeCount
                        )

                        if (productionIsComplete) {

                            userViewModel.addOrMoveToUsersCollection(
                                userID,
                                listItem,
                                sourceCollection,
                                targetCollection,
                                onSuccess = {
                                    onMoveToCollection()
                                }
                            )

                            onMoveToCollection()
                        } else {

                            if (sourceCollection == targetCollection) {
                                userViewModel.addOrMoveToUsersCollection(
                                    userID,
                                    listItem,
                                    sourceCollection,
                                    ToWatching,
                                    onSuccess = {
                                        onMoveToCollection()
                                    }
                                )

                                onMoveToCollection()
                            }

                            if (sourceCollection == "wantToWatchCollection"
                                || (sourceCollection == "droppedCollection" && isPlus)) {

                                userViewModel.addOrMoveToUsersCollection(
                                    userID,
                                    listItem,
                                    sourceCollection,
                                    ToWatching,
                                    onSuccess = {
                                        onMoveToCollection()
                                    }
                                )

                                onMoveToCollection()
                            }

                        }
                    }

                    is Movie -> {
                        val productionTotalEpisode = 1

                        val productionIsComplete = watchedEpisodeCount == productionTotalEpisode

                        userViewModel.updateCurrentEpisodeInCollection(
                            sourceCollection,
                            listItem,
                            watchedEpisodeCount
                        )

                        if (productionIsComplete) {

                            userViewModel.addOrMoveToUsersCollection(
                                userID,
                                listItem,
                                sourceCollection,
                                targetCollection,
                                onSuccess = {
                                    onMoveToCollection()
                                }
                            )
                        } else {

                            if (sourceCollection == targetCollection) {
                                userViewModel.addOrMoveToUsersCollection(
                                    userID,
                                    listItem,
                                    sourceCollection,
                                    ToWatching,
                                    onSuccess = {
                                        onMoveToCollection()
                                    }
                                )
                            }

                            if (sourceCollection == "wantToWatchCollection" || sourceCollection == "droppedCollection") {

                                userViewModel.addOrMoveToUsersCollection(
                                    userID,
                                    listItem,
                                    sourceCollection,
                                    ToWatching,
                                    onSuccess = {
                                        onMoveToCollection()
                                    }
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    fun handleListItemScoreChange(listItem: ListItem, score: Int)  {

        viewModelScope.launch {

            val sourceCollection = findListItemCollection(listItem)

            if (sourceCollection == null) {

                Log.d("ControllerViewModel", "sourceCollection is RemoveFromList for listItem")
                return@launch
            }

            val user = loggedInUser.value
            val userID = user?.id


            if (userID != null) {
                userViewModel.updateScoreInCollection(sourceCollection, listItem, score)
            }
        }
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

    fun addOrRemoveFromUsersFavorites(userID: String, listItem: ListItem, isFavorite: Boolean) {

        userViewModel.addOrRemoveFromUsersFavorites(userID, listItem, isFavorite)
    }

    fun findListItemCollection(listItem: ListItem): String? {

        val user = loggedInUser.value

        var sourceCollection: String? = null;
        if (user != null) {
            sourceCollection = when {
                user.currentlyWatchingCollection.contains(listItem) -> "currentlyWatchingCollection"
                user.wantToWatchCollection.contains(listItem) -> "wantToWatchCollection"
                user.droppedCollection.contains(listItem) -> "droppedCollection"
                user.completedCollection.contains(listItem) -> "completedCollection"
                else -> null
            }
        }

        return sourceCollection

    }

    fun findProductionInUsersCollection(productionID: String): ListItem? {

        val user = loggedInUser.value

        var listItem: ListItem? = null

        if (user != null) {
            listItem =
                user.currentlyWatchingCollection.find { it.production.imdbID == productionID }
                    ?: user.wantToWatchCollection.find { it.production.imdbID == productionID }
                    ?: user.droppedCollection.find { it.production.imdbID == productionID }
                    ?: user.completedCollection.find { it.production.imdbID == productionID }
        }

        return listItem
    }

    fun addOrMoveToUsersCollection(productionID: String, targetCollection: String) {

        val userID = loggedInUser.value?.id
        val user = loggedInUser.value

        if (userID.isNullOrEmpty() || user == null) {
            Log.e("UserViewModel", "UserID or user data is null.")
            return
        }

        // Finn riktig listItem i de forskjellige samlingene
        var listItem = findProductionInUsersCollection(productionID)

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

                Log.d("UserViewModel", "$targetCollection")
            }

            Log.e("UserViewModel", "List item with productionID: $productionID not found in any collection.")
        }

        if (sourceCollection == targetCollection) {
            Log.e("UserViewModel", "source and target collection is the same")
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

    private val _reviewDTOs = MutableStateFlow<List<ReviewDTO>>(emptyList())
    val reviewDTOs: StateFlow<List<ReviewDTO>> get() = _reviewDTOs

    fun nullifyReviewDTOs() {
        _reviewDTOs.value = emptyList()
    }

    fun nullifySingleReviewDTOData() {
        _singleReviewDTOData.value = null
    }


    private val _reviewTopAllTime = MutableStateFlow<List<ReviewDTO>>(emptyList())
    val reviewTopAllTime: StateFlow<List<ReviewDTO>> get() = _reviewTopAllTime

    private val _reviewTopMonth = MutableStateFlow<List<ReviewDTO>>(emptyList())
    val reviewTopMonth: StateFlow<List<ReviewDTO>> get() = _reviewTopMonth

    private val _reviewTopWeek = MutableStateFlow<List<ReviewDTO>>(emptyList())
    val reviewTopWeek: StateFlow<List<ReviewDTO>> get() = _reviewTopWeek

    // Prøver paginering
    suspend fun getTop10ReviewsAllTime() { // List<ReviewDTO>
        val reviewDTOList: MutableList<ReviewDTO> = mutableListOf()
        var lastVisible: Any? = null
        var hasMoreData = true


        while (hasMoreData) {

            val (reviews, hasMore) = reviewViewModel.getReviewsAllTime(pageSize = 10, lastVisible = lastVisible)

            for (review in reviews) {
                val (collectionType, _, _) = reviewViewModel.splitReviewID(review.reviewID)

                val user = userViewModel.getUser(review.reviewerID)
                if (user != null) {
                    val production = when (collectionType) {
                        "RMOV" -> getMovieByIdAsync(review.productionID)
                        "RTV" -> getTVShowByIdAsync(review.productionID)
                        else -> null
                    }
                    val reviewDTO = production?.let { reviewViewModel.createReviewDTO(review, user, it) }
                    reviewDTO?.let { reviewDTOList.add(it) }
                }
            }

            hasMoreData = hasMore
            lastVisible = reviews.lastOrNull()?.postDate
        }

        _reviewTopAllTime.value = reviewDTOList
            .sortedByDescending { it.likes }
            .take(10)
    }

    
    suspend fun getTop10ReviewsPastWeek() { //: List<ReviewDTO>
        val reviewDTOList: MutableList<ReviewDTO> = mutableListOf()

        // Hent anmeldelsene denne uken asynkront
        val reviewObjects = reviewViewModel.getReviewsFromPastWeek()

        for (review in reviewObjects) {

            val (collectionType, _, _) = reviewViewModel.splitReviewID(review.reviewID)

            val user = userViewModel.getUser(review.reviewerID)
            if (user != null) {

                val production = when (collectionType) {
                    "RMOV" -> getMovieByIdAsync(review.productionID)
                    "RTV" -> getTVShowByIdAsync(review.productionID)
                    else -> break
                }
                val reviewDTO = production?.let { reviewViewModel.createReviewDTO(review, user, it) }
                reviewDTO?.let { reviewDTOList.add(it) }
            }
        }

        Log.d("DEBUG", "GetTop10ReviewsPastWeek: " + reviewDTOList.toString())
        _reviewTopWeek.value = reviewDTOList
            .sortedByDescending { it.score }
            .take(10)
    }



    suspend fun getTop10ReviewsThisMonth() { // : List<ReviewDTO>
        val reviewDTOList: MutableList<ReviewDTO> = mutableListOf()

        val reviewObjects = reviewViewModel.getReviewsFromThisMonth()

        for (review in reviewObjects) {

            val (collectionType, _, _) = reviewViewModel.splitReviewID(review.reviewID)

            val user = userViewModel.getUser(review.reviewerID)
            if (user != null) {

                val production = when (collectionType) {
                    "RMOV" -> getMovieByIdAsync(review.productionID)
                    "RTV" -> getTVShowByIdAsync(review.productionID)
                    else -> break
                }
                val reviewDTO = production?.let { reviewViewModel.createReviewDTO(review, user, it) }
                reviewDTO?.let { reviewDTOList.add(it) }
            }
        }

        _reviewTopMonth.value = reviewDTOList
            .sortedByDescending { it.likes }
            .take(10)
    }

    fun getReviewById(reviewID: String, productionType: ProductionType, productionID: String) {
        viewModelScope.launch {
            try {

                Log.d("problem", "CVM " + productionType.name)
                val reviewObject = requireNotNull(reviewViewModel.getReviewByID(reviewID, productionType, productionID)) {

                    _singleReviewDTOData.value = null
                    return@launch
                }


                val production = when (productionType) {
                    ProductionType.MOVIE -> getMovieByIdAsync(productionID)
                    ProductionType.TVSHOW -> getTVShowByIdAsync(productionID)
                }
                if (production == null) {
                    Log.d("GetReviews", "Production data is null, aborting.")
                    return@launch
                }

                val reviewer =
                    async {
                    userViewModel.getUser(reviewObject.reviewerID)
                        ?: User(id = "fallbackReviewer", email = "default@email.com", userName = "Anonymous")
                    }.await()

                val reviewDTO = reviewViewModel.createReviewDTO(reviewObject, reviewer, production)

                Log.d("Review", "her er jeg getReviewById - $reviewID $productionID")
                _singleReviewDTOData.value = reviewDTO

            } catch (exception: Exception) {

                Log.d("Controller-getReviewById", "Failed to fetch reviews: $exception")
                _reviewDTOs.value = emptyList()
            }
        }
    }

    fun getReviewByProduction(productionID: String, productionType: String) {

        viewModelScope.launch {
            try {
                Log.d("DEBUG", "Fetching reviews for ProductionID: $productionID, Type: $productionType")

                // Hent anmeldelser fra Firestore
                val reviewObjects = withContext(Dispatchers.IO) {
                    reviewViewModel.getReviewsByProduction(productionID, productionType)
                }

                Log.d("DEBUG", "Fetched ${reviewObjects.size} reviews for ProductionType: $productionType")

                // Hent produksjonsdata asynkront
                val production = withContext(Dispatchers.IO) {
                    when (productionType) {
                        "MOVIE" -> getMovieByIdAsync(productionID)
                        "TVSHOW" -> getTVShowByIdAsync(productionID)
                        else -> null
                    }
                }

                if (production == null) {
                    Log.d("DEBUG", "Production data is null, aborting.")
                    _reviewDTOs.value = emptyList()
                    return@launch
                }

                // Hent brukere for hver reviewerID parallelt
                val reviewers = reviewObjects.map { review ->
                    async(Dispatchers.IO) {
                        review.reviewerID to (userViewModel.getUser(review.reviewerID)
                            ?: User(id = "fallbackReviewer", email = "default@email.com", userName = "Anonymous"))
                    }
                }.awaitAll().toMap() // Vent på alle oppgaver og konverter til Map

                // Opprett ReviewDTO-er ved å matche reviewerID med brukere fra "reviewers"
                val reviewDTOs = reviewObjects.mapNotNull { review ->
                    val reviewer = reviewers[review.reviewerID]
                    reviewer?.let { reviewViewModel.createReviewDTO(review, it, production) }
                }

                _reviewDTOs.value = reviewDTOs
                Log.d("DEBUG", "Updated StateFlow with ${reviewDTOs.size} reviews")
            } catch (exception: Exception) {
                Log.e("DEBUG", "Failed to fetch reviews: $exception", exception)
                _reviewDTOs.value = emptyList()
            }
        }
    }


    fun loadReviewData(reviewID: String?) {
        viewModelScope.launch {

            nullifySingleReviewDTOData()
            nullifySingleProductionData()

            val (collectionType, productionID, _) =
                if (reviewID != null) reviewViewModel.splitReviewID(reviewID)
                else Triple(null, null, null)

            var production: Production? = null;
            when (collectionType) {
                "RMOV" -> productionID?.let { production = getMovieByIdAsync(it) }
                "RTV" -> productionID?.let { production = getTVShowByIdAsync(it) }
                else -> Log.d("problem", "feil")
            }

            if (!reviewID.isNullOrEmpty()) {
                production?.let {
                        Log.d("Review", "Yuhuu" + (production?.imdbID ?: "production null"))

                    getReviewById(reviewID, it.type, it.imdbID)
                    Log.d("Review", "loadReviewData $reviewID ${it.type} ${it.imdbID}")

                    Log.d("Review", "Yuhuu" + it.imdbID)
                }
            }
        }
    }

    suspend fun getUsersReviews(user: User): List<ReviewDTO> {

        val userReviews = user.myReviews

        return getReviewsByUser(userReviews, user.id)
    }

    suspend fun getLoggedInUsersFriendsReviews(): List<ReviewDTO> {
        val friendsReviewsDTO: MutableList<ReviewDTO> = mutableListOf()

        val friends = userViewModel.getUsersFriends()

        for (friend in friends) {

            val friendReviews = getUsersReviews(friend)

            friendsReviewsDTO.addAll(friendReviews)
        }

        return friendsReviewsDTO
    }


    private suspend fun getReviewsByUser(reviewIDs: List<String>, userID: String): List<ReviewDTO> {
        val reviewDTOList: MutableList<ReviewDTO> = mutableListOf()

        return suspendCoroutine { continuation ->
            viewModelScope.launch {

                val deferredReviews = reviewIDs.map { reviewID ->
                    async {
                        Log.d("tester", "reviewID: $reviewID")
                        val (collectionType, productionID, uuid) = reviewViewModel.splitReviewID(reviewID)
                        Log.d("tester", "collectionType $reviewID : $collectionType")

                        val collectionID = try {
                            when (collectionType) {
                                "RMOV" -> "movieReviews"
                                "RTV" -> "tvShowReviews"
                                else -> throw IllegalArgumentException("Invalid collectionType: $collectionType")
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.e("Firestore", "Invalid collection type for reviewID: $reviewID")
                            return@async null
                        }

                        val user = userViewModel.getUser(userID)

                        val reviewObjects = reviewViewModel.getReviewsByUser(collectionID, productionID, userID)

                        reviewObjects.mapNotNull { reviewObject ->
                            var reviewDTO: ReviewDTO? = null

                            val production = when (collectionType) {
                                "RMOV" -> async { getMovieByIdAsync(productionID) }
                                "RTV" -> async { getTVShowByIdAsync(productionID) }
                                else -> null
                            }

                            production?.let { deferredProduction ->

                                Log.d("tester", "Calling await for productionID: $productionID")
                                val prod = deferredProduction.await()
                                Log.d("tester", "Completed await for productionID: $productionID")

                                Log.d("tester", "prod: " + prod.toString())

                                if (prod != null) {
                                    reviewDTO = user?.let {
                                        reviewViewModel.createReviewDTO(reviewObject, it, prod)
                                    }
                                } else {
                                    Log.e("tester", "Prod is null for productionID: $productionID")
                                }
                            }
                            reviewDTO
                        }
                    }
                }

                // Vent på at alle asynkrone operasjoner skal være ferdige og samle resultatene
                val results = deferredReviews.awaitAll()

                Log.d("tester", "Results received: ${results.size}")
                results.forEach {

                    Log.d("tester", "Result: $it")
                    if (it != null) {
                        reviewDTOList.addAll(it)
                    }
                }

                continuation.resume(reviewDTOList)
            }
        }
    }


    suspend fun getMovieByIdAsync(id: String): Production? {
        Log.d("ViewModel", "getMovieById called with id: $id")

        // Start API-kallet for å hente filmen
        val result = apiViewModel.getMovieDetailsTry(id)

        // Vent på at movieData skal oppdateres
        return try {
            // Vent på at movieData skal inneholde et resultat
            val movieResponse = result.getOrNull()

            // Hvis movieResponse er null, returner null
            val production = movieResponse?.let { convertResponseToProduction(it) }

            Log.d("Controller", "Returning production: $production")
            production

        } catch (e: Exception) {
            Log.e("Controller", "Error fetching movie data", e)
            null
        }
    }

    suspend fun getTVShowByIdAsync(id: String): Production? {
        Log.d("Controller", "getTVShowByIdAsync called with id: $id")

        val result = apiViewModel.getShowDetailsTry(id)

        return try {

            val showResponse = result.getOrNull()

            val production = showResponse?.let { convertResponseToProduction(it) }
            Log.d("DEBUG", "Converted response to production: $production")

            production

        } catch (e: Exception) {
            Log.e("Controller_getTVShowByIdAsync", "Error fetching show data", e)
            null
        }
    }

    fun publishReview(
        production: Production,
        reviewText: String,
        reviewScore: Int,
        onSuccess: () -> Unit
    ) {

        val userID = loggedInUser.value?.id
        if (userID != null) {

            val collection = when (production.type) {
                ProductionType.MOVIE -> "movieReviews"
                ProductionType.TVSHOW -> "tvShowReviews"
                else -> { "" }
            }

            val reviewID = generateReviewID(production.type, production.imdbID)

            val review = reviewID?.let {
                Review(it, score = reviewScore, userID, production.imdbID, reviewBody = reviewText)
            }

           if (review != null) {
               val reviewMap = review.toMap()

               userViewModel.publishReview(collection, production.imdbID, reviewID, reviewMap,
                   onSuccess = {
                       onSuccess()
                   } )
           }
        }
    }

    private fun generateReviewID(collectionType: ProductionType, productionID: String): String? {

        val collectionID = when (collectionType) {
            ProductionType.MOVIE -> "RMOV"
            ProductionType.TVSHOW -> "RTV"
            else -> { "" }
        }

       return "${collectionID}_${productionID}_${UUID.randomUUID()}"
    }

    private val _snackBarStatus = MutableStateFlow<Status?>(null)
    val snackBarStatus: StateFlow<Status?> = _snackBarStatus


    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return file.toUri()
    }


    private fun deleteFile(file: File) {
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                Log.d("FileDelete", "File deleted successfully")
            } else {
                Log.e("FileDelete", "Failed to delete file")
            }
        } else {
            Log.e("FileDelete", "File does not exist")
        }
    }

    fun handleAlbumPickProfileImage(imageUri: Uri) {

        viewModelScope.launch {
            try {

                userViewModel.updateProfileImage(imageUri)

                _snackBarStatus.value = Status.Success
            } catch (e: Exception) {
                _snackBarStatus.value = Status.Error(e.message ?: "Noe gikk galt")
            }
        }
    }

    fun handleCapturedProfileImage(context: Context, image: Bitmap) {

        viewModelScope.launch {

            val imageUri = withContext(Dispatchers.IO) {
                bitmapToUri(context, image)
            }

            try {
                userViewModel.updateProfileImage(imageUri)

                _snackBarStatus.value = Status.Success
            } catch (e: Exception) {
                _snackBarStatus.value = Status.Error(e.message ?: "Noe gikk galt")
            } finally {

                imageUri.path?.let {
                    withContext(Dispatchers.IO) {
                        deleteFile(File(it))
                    }
                }
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackBarStatus.value = null
    }

    sealed class Status {
        object Success : Status()
        data class Error(val message: String) : Status()
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

    @Composable
    fun checkForNewFollowers () {
        val context = LocalContext.current
        val dataStore = createDataStore(context)
        var outdatedLocalData by remember { mutableStateOf(false) }

        //Read follower count from local storage
        val localStorageFollowerCount by dataStore.data
            .map { preferences ->
                preferences[LocalStorageKeys().followerCount] ?: 0
            }
            .collectAsState(initial = 0)

        LaunchedEffect(Unit) {
            while(true){
                val currentLocalFollowerCount = localStorageFollowerCount
                val firebaseFollowerCount = getUserFollowingCount()

                //Check to see if the follower count has changed since last loop
                if (currentLocalFollowerCount < firebaseFollowerCount) {
                    val amountOfFollowers = firebaseFollowerCount - localStorageFollowerCount

                    //Post notofication
                    PostNotification(
                        context = context,
                        contentTitle = "New follower" + if (amountOfFollowers > 1) "s" else "",
                        contentText = "You have ${amountOfFollowers} new follower" + if (amountOfFollowers > 1) "s" else "",
                        importance = NotificationManager.IMPORTANCE_LOW
                    )
                    outdatedLocalData = true
                } else if (
                    currentLocalFollowerCount > firebaseFollowerCount
                ) {
                    outdatedLocalData = true
                }

                //Write updated follower count to local storage
                if (outdatedLocalData) {
                    //Store updated follower count locally
                    dataStore.edit { preferences ->
                        preferences[LocalStorageKeys().followerCount] = firebaseFollowerCount
                    }
                    outdatedLocalData = false
                }

                //Delay for 30 minutes before next check

                delay(5 * 60 * 1000L)
            }
        }
    }
}
