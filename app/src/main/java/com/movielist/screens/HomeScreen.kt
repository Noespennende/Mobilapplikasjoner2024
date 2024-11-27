package com.movielist.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.movielist.R
import com.movielist.Screen
import com.movielist.composables.LineDevider
import com.movielist.composables.LoadingCircle
import com.movielist.composables.ProductionListSidesroller
import com.movielist.composables.ProfileImage
import com.movielist.composables.ProgressBar
import com.movielist.composables.RatingSlider
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.ProductionImage
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.ProductionType
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.*
import java.util.Calendar
import kotlin.math.log
import kotlin.random.Random

@Composable
fun HomeScreen(controllerViewModel: ControllerViewModel, navController: NavController) {

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()
    val currentlyWatchingCollection: List<ListItem> =
        loggedInUser?.currentlyWatchingCollection ?: emptyList()
    val friendsWatchedList by controllerViewModel.friendsWatchedList.collectAsState()

    val popularMoviesAndShows by controllerViewModel.filteredMediaData.collectAsState()

    val handleProductionButtonClick: (showID: String, productionType: ProductionType)
    -> Unit = { showID, productionType ->
        Log.d("Test", "$showID $productionType")
        navController.navigate(Screen.ProductionScreen.withArguments(showID, productionType.name))
    }

    val handleReviewLikeButtonClick: (reviewID: String, productionType: ProductionType) -> Unit = { reviewID, productionType ->
        //Kontroller funksjon for å håndtere en review like hær
    }

    val handleProfilePictureClick: (profileID: String) -> Unit = { profileID ->
        navController.navigate(Screen.ProfileScreen.withArguments(profileID))
    }

    val handleReviewClick: (reviewID: String) -> Unit = { reviewID ->
        navController.navigate(Screen.ReviewScreen.withArguments(reviewID))
    }

    val handleUserRatingChange: (listItem: ListItem, score: Int) -> Unit = {listItem, score ->
        //Kontroller funksjon for å oppdatere ratingen for det gitte list itemet

        controllerViewModel.handleListItemScoreChange(listItem, score)

    }

    val handleMarkAsWatched: (listItem: ListItem, watchedEpisodesCount: Int, showLength: Int)
    -> Unit = { listItem, watchedEpisodesCount, showLength ->

        Log.d("ControllerViewModel", listItem.currentEpisode.toString())

        var newCurrentEpisode: Int = watchedEpisodesCount
        if(watchedEpisodesCount < showLength) {
            newCurrentEpisode = ++listItem.currentEpisode
        }

        Log.d("ControllerViewModel", newCurrentEpisode.toString())
        controllerViewModel.handleEpisodeCountChange(
            listItem,
            newCurrentEpisode,
            true,
            onMoveToCollection = {})

    }

    val top10ReviewsListPastWeek by controllerViewModel.reviewTopWeek.collectAsState(emptyList())



    // Oppdateres/hentes hver gang Homescreen laster
    // TODO: Hent når det har gått 24 timer siden sist henting i stedet (Oppdater Unit til noe annet)
    LaunchedEffect(Unit) {
        controllerViewModel.getTop10ReviewsPastWeek()

        controllerViewModel.getPopularMoviesAndShows()

        Log.d("DEBUG", "UI: " + top10ReviewsListPastWeek.toString())
    }


    // Front page graphics

    if (loggedInUser == null) {
        LoadingCircle()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Front page content
            item {
                CurrentlyWatchingScroller(
                    listOfShows = currentlyWatchingCollection,
                    onImageClick = handleProductionButtonClick,
                    handleRatingChange = handleUserRatingChange,
                    handleMarkAsWatched = handleMarkAsWatched
                )
            }


            item {

                ProductionListSidesroller(
                    header = "Popular shows and movies",
                    listOfShows = popularMoviesAndShows,
                    handleImageClick = handleProductionButtonClick,
                    textModifier = Modifier
                        .padding(vertical = 10.dp, horizontal = LocalConstraints.current.mainContentHorizontalPadding),
                    modifier = Modifier
                        .padding(top = verticalPadding)

                )
            }

            item {

                TheUsersYouFollowJustWatched(
                    listOfShows = friendsWatchedList.toMutableList(),
                    handleShowButtonClick = handleProductionButtonClick
                )


            }

            item {

                ReviewsSection(
                    reviewList = top10ReviewsListPastWeek,
                    header = "Top reviews this week",
                    handleLikeClick = handleReviewLikeButtonClick,
                    handleProductionImageClick = handleProductionButtonClick,
                    handleProfilePictureClick = handleProfilePictureClick,
                    handleReviewClick = handleReviewClick,
                    loadingIfListEmpty = true,
                    loggedInUser = loggedInUser?: User("","","")
                )

            }

            item {
                /*Adds empty space the size of the bottom nav bar to ensure content don't dissapear
                behind it*/
                Spacer(modifier = Modifier.height(bottomNavBarHeight))
            }

        }
    }
}


@Composable
fun CurrentlyWatchingScroller (
    listOfShows: List<ListItem>,
    onImageClick: (showID: String, productionType: ProductionType) -> Unit,
    handleRatingChange: (listItem: ListItem, score: Int) -> Unit,
    handleMarkAsWatched: (listItem: ListItem, watchedEpisodesCount: Int, showLength: Int)  -> Unit
) {

    //TEMP KODE: FLYTT UT
// Funksjon som returnerer alle filmer og serier som ligger i den LOGGED INN brukeren sin Currently Watching liste.
// Funksjonen returnerer en liste med ListItem objekter og er sortert i henhold til hvilke som var sist oppdatert

    var allCurrentlyWatchingShows = mutableListOf<ListItem>()

    val testUser = remember {mutableStateOf<User?>(null)} // Usikker på om det er riktig bruker som skal hentes

    // Sjekker om bruker har en currentlyWatchingShows liste
    testUser.value?.currentlyWatchingCollection?.let { shows ->
        allCurrentlyWatchingShows.addAll(shows)
    }

    // Holder på oversikten over nyligste klikk
    val clickTimes by remember {mutableStateOf(mutableMapOf<String, Long>())}

    val mostRecentButtonClick: (listItem: ListItem, watchedEpisodesCount: Int, showLength: Int) -> Unit = { listItem, watchedEpisodesCount, showLength ->
        clickTimes[listItem.id] = System.currentTimeMillis() // Registerer når currentEpisode på watchedEpisodesCount oppdateres (knapp trykkes)

        Log.d("Controlelr", "Handle Mark As Watched is triggered")

        handleMarkAsWatched(listItem, watchedEpisodesCount, showLength)

        allCurrentlyWatchingShows = listOfShows.sortedByDescending { clickTimes[it.id] }.toMutableList()
    }

    //Graphics
    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(start = LocalConstraints.current.mainContentHorizontalPadding, end = 0.dp)
    ) {
        if (listOfShows.isEmpty()) {
            items (3) {

                LoadingCurrentlyWatchingCard()
            }
        } else {
            items(listOfShows.size) { i ->
                CurrentlyWatchingCard(
                    listItem = listOfShows[i],
                    showLength = when (listOfShows[i].production) {
                        is TVShow -> (listOfShows[i].production as TVShow).episodes.size // Returnerer antall episoder som Int
                        is Movie -> 1 // Returnerer lengden i minutter som Int
                        else -> 0 // En fallback-verdi hvis det ikke er en TvShow, Movie eller Episode
                    },
                    onMarkAsWatched = mostRecentButtonClick,// Registrerer når "Mark as Watched" er trykket
                    onImageClick = onImageClick,
                    handleRatingChange = handleRatingChange
                )
            }
        }



    }
}

@Composable
fun LoadingCurrentlyWatchingCard(
    modifier: Modifier = Modifier,
) {
    // Card container
    Card(
        modifier = modifier.width(350.dp),
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        colors = CardDefaults.cardColors(containerColor = LocalColor.current.backgroundLight)
    ) {
        // Card content
        Column(
            modifier = Modifier
                .height(265.dp + topPhoneIconsAndNavBarBackgroundHeight)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = (topPhoneIconsAndNavBarBackgroundHeight + 10.dp),
                    bottom = 10.dp
                )
        ) {
            // Main image - Last inn bilde fra URL eller bruk placeholder
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.noimage)
                    .placeholder(R.drawable.noimage) // placeholder når bildet lastes
                    .error(R.drawable.noimage) // vis samme placeholder ved feil
                    .build(),
                contentDescription = "test",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(LocalColor.current.background)
                    .padding(5.dp)
            )

        }
    }
}

@Composable
fun CurrentlyWatchingCard(
    listItem: ListItem,
    showLength: Int?,
    modifier: Modifier = Modifier,
    onMarkAsWatched: (listItem: ListItem, watchedEpisodesCount: Int, showLength: Int) -> Unit,
    onImageClick: (showID: String, productionType: ProductionType) -> Unit,
    handleRatingChange: (listItem: ListItem, score: Int) -> Unit
) {
    
    var watchedEpisodesCount: Int by remember {
        mutableIntStateOf(listItem.currentEpisode)
    }

    var buttonText by remember {
        mutableStateOf(generateButtonText(listItem.currentEpisode, showLength))
    }

    var ratingAdded by remember { mutableStateOf(false) }

    var ratingSliderVisible by remember { mutableStateOf(false) }

    val handleRatingChanged: (rating: Int) -> Unit = { rating ->

        handleRatingChange(listItem, rating)

        ratingSliderVisible = false
        ratingAdded = true
        buttonText = "Rating updated!"

    }

    // Card container
    Card(
        modifier = modifier.width(350.dp),
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        colors = CardDefaults.cardColors(containerColor = LocalColor.current.backgroundLight)
    ) {
        // Card content
        Column(
            modifier = Modifier
                .height(265.dp + topPhoneIconsAndNavBarBackgroundHeight)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = (topPhoneIconsAndNavBarBackgroundHeight + 10.dp),
                    bottom = 10.dp
                )
        ) {
            // Main image - Last inn bilde fra URL eller bruk placeholder
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listItem.production.posterUrl)
                    .placeholder(R.drawable.noimage) // placeholder når bildet lastes
                    .error(R.drawable.noimage) // vis samme placeholder ved feil
                    .build(),
                contentDescription = listItem.production.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        onImageClick(listItem.production.imdbID, listItem.production.type)
                    }
            )

            // Content under image
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Title and episodes watched
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title
                    Text(
                        listItem.production.title,
                        style = TextStyle(
                            color = LocalColor.current.secondary,
                            fontSize = 18.sp,
                            fontWeight = weightRegular
                        )
                    )
                    // Episodes watched
                    Text(
                        "Ep $watchedEpisodesCount of $showLength",
                        style = TextStyle(
                            color = LocalColor.current.secondary,
                            fontSize = 18.sp,
                            fontWeight = weightLight
                        )
                    )
                }

                // Progress bar
                ProgressBar(currentNumber = watchedEpisodesCount, endNumber = showLength!!)

                // Mark as watched button
                Button(
                    onClick = {
                        // Button onclick function


                        onMarkAsWatched(listItem, watchedEpisodesCount, showLength)

                        if (watchedEpisodesCount < showLength) {
                            watchedEpisodesCount++
                        } else {
                            ratingSliderVisible = true
                        }

                        if (!ratingAdded){
                            buttonText = generateButtonText(watchedEpisodesCount, showLength)
                        }

                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(LocalColor.current.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                ) {
                    Text(
                        buttonText,
                        fontSize = headerSize,
                        fontWeight = weightRegular,
                        color = LocalColor.current.background
                    )
                }
            }
        }
    }

    RatingSlider(
        rating = listItem.score,
        visible = ratingSliderVisible,
        onValueChangeFinished = handleRatingChanged
    )
}




@Composable
fun TheUsersYouFollowJustWatched (
    listOfShows: MutableList<ListItem>,
    handleShowButtonClick: (showID: String, productionType: ProductionType) -> Unit
) {
    val handleShowClick: (showID: String, productionType: ProductionType)
    -> Unit = { showID, productionType ->
        handleShowButtonClick(showID, productionType)
    }

    //Container collumn
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = verticalPadding)
    ) {
        //Header
        Text(
            "New updates from users you follow",
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightBold,
            color = LocalColor.current.secondary,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = LocalConstraints.current.mainContentHorizontalPadding)
        )
        //Content
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = LocalConstraints.current.mainContentHorizontalPadding, end = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
        ){
            if (listOfShows.isEmpty()) {
                item () {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text(
                            "There doesn't seem to be anything here :(",
                            fontFamily = fontFamily,
                            fontSize = paragraphSize,
                            fontWeight = weightRegular,
                            color = LocalColor.current.quaternary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .fillMaxWidth()
                        )
                    }

                }
            } else {
                items (listOfShows.size) {i ->
                    //Info for each show
                    Column (
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier
                            .clickable {
                                handleShowClick(listOfShows[i].production.imdbID, listOfShows[i].production.type)
                            }
                    ) {
                        ProductionImage(
                            imageID = listOfShows[i].production.posterUrl,
                            imageDescription = listOfShows[i].production.title + " Poster"
                        )
                        //Friend Info
                        FriendsWatchedInfo(
                            profileImageID = null, // Gir "standard" profilbilde
                            profileName = "User Userson", //TEMP DELETE THIS
                            episodesWatched = listOfShows[i].currentEpisode,
                            showLenght = when (listOfShows[i].production) {
                                is TVShow -> (listOfShows[i].production as TVShow).episodes.size // Returnerer antall episoder som Int
                                is Movie -> 1
                                else -> 0 // En fallback-verdi hvis det ikke er en TvShow, Movie eller Episode
                            },
                            score = listOfShows[i].score
                        )
                    }
                }
            }

        }

    }
}

@Composable
fun LoadingCard() {
    ProductionImage()
    Column (
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ){
        Text(
            text = "Placeholder",
            color = LocalColor.current.secondary,
            fontFamily = fontFamily,
            fontWeight = weightLight,
            fontSize = 12.sp
        )
    }
}

@Composable
fun FriendsWatchedInfo(
    profileImageID: String?,
    profileName: String,
    episodesWatched: Int,
    showLenght: Int?,
    score: Int = 0,

    ) {
    Row(
        horizontalArrangement =  Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImage(
            imageID = profileImageID,
            userName = profileName
        )
        //Episode Count and Score
        Column (
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ){
            Text(
                text = "Ep $episodesWatched of $showLenght",
                color = LocalColor.current.secondary,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = 12.sp
            )
            RatingsGraphics(
                score = score
            )
        }
    }

}

//Utility Functions
fun generateButtonText(
    episodesWatched: Int,
    showLenght: Int?)
        : String
{
    if (episodesWatched+1 == showLenght) {
        return "Mark as completed"
    } else if ( episodesWatched == showLenght){
        return "Add a rating"
    }
    else {
        return "Mark episode ${episodesWatched + 1} as watched"
    }

}