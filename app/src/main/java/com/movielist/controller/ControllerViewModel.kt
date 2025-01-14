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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

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

    fun logInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestoreRepository.logInWithEmailAndPassword(email, password, onSuccess, onFailure)
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




    fun updateReviewLikes(reviewID: String, productionType: String) {
        viewModelScope.launch {
            try {
                val userId = loggedInUser.value?.id
                val (collectionType, productionId, _) = reviewViewModel.splitReviewID(reviewID)
                val currentReviewMap = firestoreRepository.getReviewById(reviewID, collectionType, productionId)

                val likes = currentReviewMap?.get("likes") as Long

                val newLikes = likes + 1

                val collection = when(collectionType){
                    "RMOV" -> "movieReviews"
                    "RTV" -> "tvShowReviews"
                    else -> {""}
                }

                val isUpdated = userId?.let {
                    firestoreRepository.updateReview(reviewID, collection, productionId, newLikes,
                        it
                    )
                }
                if (isUpdated == true) {
                    val likedByUsers = currentReviewMap["likedByUsers"] as? List<String> ?: emptyList()
                    val updatedReview = singleReviewDTOData.value?.copy(
                        likes = newLikes,
                        likedByUsers = likedByUsers + userId
                    )
                    _singleReviewDTOData.value = updatedReview
                }
            } catch (e: Exception) {
                Log.e("ControllerViewModel", "Error updating review: $e")
            }
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

        _profileOwner.value = if (userID == loggedInUser.value?.id) {

            _profileBelongsToLoggedInUser.value = true

            loggedInUser.value

        } else {

            _profileBelongsToLoggedInUser.value = false
            setOtherUser(userID)
            val other = otherUser.firstOrNull { it?.id == userID }

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
        val user = loggedInUser.value ?: return 0

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
        val user = loggedInUser.value ?: return emptyList()

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

            val updatedOtherUserFollowerList = otherUser.followingMeList.toMutableList()
            val updatedFollowerList = user.followingList.toMutableList().apply {

                if (!contains(otherUser.id)) {
                    add(otherUser.id)
                    updatedOtherUserFollowerList.add(user.id)

                } else {

                }
            }
            val updatedOtherUser = otherUser.copy(followingMeList = updatedOtherUserFollowerList)
            val updatedUser = user.copy(followingList = updatedFollowerList)

            userViewModel.updateFollowingForUser(updatedUser, updatedOtherUser)

        }
    }

    fun removeUserFromFollowerList(otherUser: User) {
        val user = loggedInUser.value

        if (user != null) {

            val updatedFollowerList = user.followingList.toMutableList().apply {
                removeAll { it == otherUser.id }
            }

            val updatedUser = user.copy(followingList = updatedFollowerList)

            userViewModel.updateLoggedInUser(updatedUser)

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

    fun setOtherUser(uid: String) {
        userViewModel.setOtherUser(uid)
    }

    fun checkUserStatus() {
        authViewModel.checkUserStatus() // Kall autentiseringstatus
    }

    private fun convertResponseToProduction(result: ApiProductionResponse): Production {
        return when (result) {

            is MovieResponse -> {

                val credits = result.movieCreditData.cast?.map { it.name.orEmpty() } ?: emptyList()
                val videoKey = result.movieVideoData?.firstOrNull { it.name == "Official Trailer" }?.key
                val trailerUrl = videoKey?.let { "https://www.youtube.com/watch?v=$it" }

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

                _friendsWatchedList.value = recentFriendsWatched
            } catch (e: Exception) {

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

    fun addOrMoveToUsersCollection(production: Production, targetCollection: String) {

        val userID = loggedInUser.value?.id
        val user = loggedInUser.value

        if (userID.isNullOrEmpty() || user == null) {

            return
        }

        var listItem = findProductionInUsersCollection(production.imdbID)

        // Sjekk hvilken samling listItem tilhører (kilden)
        val sourceCollection = when {
            user.currentlyWatchingCollection.contains(listItem) -> "currentlyWatchingCollection"
            user.wantToWatchCollection.contains(listItem) -> "wantToWatchCollection"
            user.droppedCollection.contains(listItem) -> "droppedCollection"
            user.completedCollection.contains(listItem) -> "completedCollection"
            else -> null
        }

        if (listItem == null) {

            listItem = ListItem(production = production)

        }

        if (sourceCollection == targetCollection) {
            Log.e("UserViewModel", "source and target collection is the same")
        }

        userViewModel.addOrMoveToUsersCollection(userID, listItem, sourceCollection, targetCollection)
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

    private fun getReviewById(reviewID: String, productionType: ProductionType, productionID: String) {
        viewModelScope.launch {
            try {

                val reviewObject = requireNotNull(reviewViewModel.getReviewByID(reviewID)) {

                    _singleReviewDTOData.value = null
                    return@launch
                }


                val production = when (productionType) {
                    ProductionType.MOVIE -> getMovieByIdAsync(productionID)
                    ProductionType.TVSHOW -> getTVShowByIdAsync(productionID)
                }
                if (production == null) {
                    return@launch
                }

                val reviewer =
                    async {
                    userViewModel.getUser(reviewObject.reviewerID)
                        ?: User(id = "fallbackReviewer", email = "default@email.com", userName = "Anonymous")
                    }.await()

                val reviewDTO = reviewViewModel.createReviewDTO(reviewObject, reviewer, production)

                _singleReviewDTOData.value = reviewDTO

            } catch (exception: Exception) {

                _reviewDTOs.value = emptyList()
            }
        }
    }

    fun getReviewByProduction(productionID: String, productionType: String) {

        viewModelScope.launch {
            try {
                // Hent anmeldelser fra Firestore
                val reviewObjects = withContext(Dispatchers.IO) {
                    reviewViewModel.getReviewsByProduction(productionID, productionType)
                }


                // Hent produksjonsdata asynkront
                val production = withContext(Dispatchers.IO) {
                    when (productionType) {
                        "MOVIE" -> getMovieByIdAsync(productionID)
                        "TVSHOW" -> getTVShowByIdAsync(productionID)
                        else -> null
                    }
                }

                if (production == null) {

                    _reviewDTOs.value = emptyList()
                    return@launch
                }

                // Hent brukere for hver reviewerID parallelt
                val reviewers = reviewObjects.map { review ->
                    async(Dispatchers.IO) {
                        review.reviewerID to (userViewModel.getUser(review.reviewerID)
                            ?: User(id = "fallbackReviewer", email = "default@email.com", userName = "Anonymous"))
                    }
                }.awaitAll().toMap() // Konverter til en Map for enkel matching

                // Opprett ReviewDTO-er ved å matche reviewerID med brukere fra "reviewers"
                val reviewDTOs = reviewObjects.mapNotNull { review ->
                    val reviewer = reviewers[review.reviewerID]
                    reviewer?.let { reviewViewModel.createReviewDTO(review, it, production) }
                }

                _reviewDTOs.value = reviewDTOs

            } catch (exception: Exception) {

                _reviewDTOs.value = emptyList()
            }
        }
    }

    fun loadReviewData(reviewID: String?) {
        viewModelScope.launch {

            nullifySingleReviewDTOData()

            val (collectionType, productionID, _) =
                if (reviewID != null) reviewViewModel.splitReviewID(reviewID)
                else Triple(null, null, null)

            var production: Production? = null;
            when (collectionType) {
                "RMOV" -> productionID?.let { production = getMovieByIdAsync(it) }
                "RTV" -> productionID?.let { production = getTVShowByIdAsync(it) }
            }

            if (!reviewID.isNullOrEmpty()) {
                production?.let {

                    getReviewById(reviewID, it.type, it.imdbID)
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
                        val (collectionType, productionID, uuid) = reviewViewModel.splitReviewID(reviewID)

                        val collectionID = try {
                            when (collectionType) {
                                "RMOV" -> "movieReviews"
                                "RTV" -> "tvShowReviews"
                                else -> throw IllegalArgumentException("Invalid collectionType: $collectionType")
                            }
                        } catch (e: IllegalArgumentException) {
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

                                val prod = deferredProduction.await()

                                if (prod != null) {
                                    reviewDTO = user?.let {
                                        reviewViewModel.createReviewDTO(reviewObject, it, prod)
                                    }
                                } else {
                                    Log.e("ControllerViewModel", "Prod is null for productionID: $productionID")
                                }
                            }
                            reviewDTO
                        }
                    }
                }

                val results = deferredReviews.awaitAll()

                results.forEach {

                    if (it != null) {
                        reviewDTOList.addAll(it)
                    }
                }

                continuation.resume(reviewDTOList)
            }
        }
    }

    suspend fun getMovieByIdAsync(id: String): Production? {

        // Start API-kallet for å hente filmen
        val result = apiViewModel.getMovieDetailsTry(id)

        // Vent på at movieData skal oppdateres
        return try {
            // Vent på at movieData skal inneholde et resultat
            val movieResponse = result.getOrNull()

            // Hvis movieResponse er null, returner null
            val production = movieResponse?.let { convertResponseToProduction(it) }

            production

        } catch (e: Exception) {
            Log.e("Controller", "Error fetching movie data", e)
            null
        }
    }

    suspend fun getTVShowByIdAsync(id: String): Production? {

        val result = apiViewModel.getShowDetailsTry(id)

        return try {

            val showResponse = result.getOrNull()

            val production = showResponse?.let { convertResponseToProduction(it) }

            production

        } catch (e: Exception) {
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
            userViewModel.updateProfileImage(imageUri)
        }
    }

    fun handleCapturedProfileImage(context: Context, image: Bitmap) {

        viewModelScope.launch {

            val imageUri = withContext(Dispatchers.IO) {
                bitmapToUri(context, image)
            }

            try {
                userViewModel.updateProfileImage(imageUri)

            } finally {

                imageUri.path?.let {
                    withContext(Dispatchers.IO) {
                        deleteFile(File(it))
                    }
                }
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
