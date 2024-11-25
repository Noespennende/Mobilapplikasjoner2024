package com.movielist.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.movielist.Screen
import com.movielist.composables.FavoriteButton
import com.movielist.composables.LineDevider
import com.movielist.composables.ProgressBar
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.ProductionImage
import com.movielist.composables.ProductionSortSelectButton
import com.movielist.composables.RatingSlider
import com.movielist.composables.TopScreensNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.Episode
import com.movielist.model.ListItem
import com.movielist.model.ListOptions
import com.movielist.model.Movie
import com.movielist.model.ShowSortOptions
import com.movielist.model.TVShow
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.topNavBaHeight
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import java.util.Calendar


@Composable
fun ListScreen (controllerViewModel: ControllerViewModel, navController: NavHostController, userID: String?)
{
    val listOwnderID by remember {mutableStateOf<String?>(userID)} /*<- ID til brukeren som eier listen*/
    val isLoggedInUser by remember { mutableStateOf(true) }

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()

    val profileOwner by controllerViewModel.profileOwner.collectAsState()

    val profileBelongsToLoggedInUser by controllerViewModel.profileBelongsToLoggedInUser.collectAsState(true)

    // Innlogget bruker sin favorite-kolleksjon
    val favoriteCollection: List<ListItem> = profileOwner?.favoriteCollection ?: emptyList()

    // Innlogget bruker sin currentlyWatching-kolleksjon
    val completedCollection: List<ListItem> = profileOwner?.completedCollection ?: emptyList()

    // Innlogget bruker sin wantToWatch-kolleksjon
    val wantToWatchCollection: List<ListItem> = profileOwner?.wantToWatchCollection ?: emptyList()

    // Innlogget bruker sin favorite-kolleksjon
    val droppedCollection: List<ListItem> = profileOwner?.droppedCollection ?: emptyList()

    // Innlogget bruker sin currentlyWatching-kolleksjon
    val currentlyWatchingCollection: List<ListItem> = profileOwner?.currentlyWatchingCollection ?: emptyList()

    var activeCategory by remember { mutableStateOf(ListOptions.WATCHING) }

    var activeSortOption by remember { mutableStateOf<ShowSortOptions>(ShowSortOptions.MOVIESANDSHOWS) } /*<- Current production sorting: Movies and shows, movies, shows*/

    val displayedList = when (activeCategory) {
        ListOptions.WATCHING -> currentlyWatchingCollection
        ListOptions.COMPLETED -> completedCollection
        ListOptions.WANTTOWATCH -> wantToWatchCollection
        ListOptions.DROPPED -> droppedCollection
        ListOptions.REMOVEFROMLIST -> TODO()
    }

    val handleProductionClick: (productionID: String, productionType: String) -> Unit = {productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType))
    }

    val handleSortingChange: (sortOption: ShowSortOptions) -> Unit = {sortOption ->
        activeSortOption = sortOption
        //Kontroller funksjon for å håndtere sorting
    }

    val handleListItemRatingsChange: (listItem: ListItem, score: Int) -> Unit = { listItem, score ->
        //Kontroller kall her:

        Log.d("ControllerViewModel", listItem.score.toString())
        Log.d("ControllerViewModel", score.toString())
        controllerViewModel.handleListItemScoreChange(listItem, score)
        Log.d("ControllerViewModel", listItem.score.toString())
        Log.d("ControllerViewModel", listItem.score.toString())
    }

    val handleListItemFavoriteClick: (listItem: ListItem, favorited: Boolean) -> Unit = { listItem, favorited ->

        val loggedInUserID = loggedInUser?.id
        if (loggedInUserID != null) {
            controllerViewModel.addOrRemoveFromUsersFavorites(loggedInUserID, listItem, favorited)
        }
    }

    val handleCompareUserListsClick: () -> Unit = {
        navController.navigate(Screen.ComparisonScreen.withArguments(listOwnderID.toString()))
    }



    var refreshState by remember { mutableIntStateOf(0) }

    val handleEpisodeCountChange: (listItem: ListItem, episodeCount: Int, isPlus: Boolean)  -> Unit = { listItem, episodeCount, isPlus ->

        listItem.currentEpisode = episodeCount
        controllerViewModel.handleEpisodeCountChange(listItem, episodeCount, isPlus,
            onMoveToCollection = {
                refreshState++
            })

    }

    LaunchedEffect(refreshState) {
        if (userID != null) {
            controllerViewModel.loadProfileOwner(userID)
        }
    }


    /*
        Her kan man da da lage sjekk:
        om isLoggedInUser == true -> hent loggedInUser lister
        om isLoggedInUser == false -> hent annen brukers lister

        !! OBS !!
        Om vi henter en annen brukers lister,
        må denne brukeren ha blitt satt i userViewModel på et tidspunkt, med userViewModel.setOtherUser().

    * */

    //Graphics




    LaunchedEffect(Unit) {
        if (userID != null) {
            controllerViewModel.loadProfileOwner(userID)
        }
    }

    //List
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsAndNavBarBackgroundHeight + topNavBaHeight,
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = bottomNavBarHeight+20.dp
        ),
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            ListPageList(
                loggedInUsersList = profileBelongsToLoggedInUser,
                listItemList = displayedList,
                handleProductionImageClick = handleProductionClick,
                handleListItemRatingChange = handleListItemRatingsChange,
                handleListItemFavoriteClick = handleListItemFavoriteClick,
                handleCompareUserClick = handleCompareUserListsClick,
                handleEpisodeCountChange = handleEpisodeCountChange,
            )
        }
    }

    TopNavBarListPage(
        activeCategory = activeCategory,
        onCategoryChange = { newCategory -> activeCategory = newCategory },
        watchedListCount = currentlyWatchingCollection.size,
        completedListCount = completedCollection.size,
        wantToWatchListCount = wantToWatchCollection.size,
        droppedListCount = droppedCollection.size,
        handleSortChange = handleSortingChange
    )
}

@Composable
fun TopNavBarListPage(
    activeCategory: ListOptions,
    onCategoryChange: (ListOptions) -> Unit,
    handleSortChange: (ShowSortOptions) -> Unit,
    watchedListCount: Int,
    completedListCount: Int,
    wantToWatchListCount: Int,
    droppedListCount: Int

){

    Box(
        modifier = Modifier.wrapContentSize()
    ){
        TopScreensNavbarBackground()
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(top = topNavBarContentStart)
        ) {
            ProductionSortSelectButton(
                handleSortChange = handleSortChange
            )
            ListCategoryOptions(
                activeCategory = activeCategory,
                onCategoryChange = onCategoryChange,
                watchedListCount = watchedListCount,
                completedListCount = completedListCount,
                wantToWatchListCount = wantToWatchListCount,
                droppedListCount = droppedListCount
            )
        }
    }
}


@Composable
fun ListCategoryOptions (
    activeCategory: ListOptions,
    onCategoryChange: (ListOptions) -> Unit,
    activeButtonColor: Color = LocalColor.current.primary,
    inactiveButtonColor: Color = if(isAppInDarkTheme())LocalColor.current.quaternary else LocalColor.current.primaryLight,
    watchedListCount: Int,
    completedListCount: Int,
    wantToWatchListCount: Int,
    droppedListCount: Int,

    ){

    var watchingButtonColor = if (activeCategory == ListOptions.WATCHING) activeButtonColor else inactiveButtonColor
    var completedButtonColor = if (activeCategory == ListOptions.COMPLETED) activeButtonColor else inactiveButtonColor
    var wantToWatchButtonColor = if (activeCategory == ListOptions.WANTTOWATCH) activeButtonColor else inactiveButtonColor
    var droppedButtonColor = if (activeCategory == ListOptions.DROPPED) activeButtonColor else inactiveButtonColor


    //Graphics
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = horizontalPadding)
    ){
        item {
            //Watching
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable { onCategoryChange(ListOptions.WATCHING) }
                    .background(color = watchingButtonColor, shape = RoundedCornerShape(5.dp))
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text("Watching ($watchedListCount)", fontSize = paragraphSize, fontWeight = weightBold, color = LocalColor.current.background)
            }
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable { onCategoryChange(ListOptions.COMPLETED) }
                    .background(color = completedButtonColor, shape = RoundedCornerShape(5.dp))
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text("Completed ($completedListCount)", fontSize = paragraphSize, fontWeight = weightBold, color = LocalColor.current.background)
            }
        }

        item {
            //Want to watch
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable { onCategoryChange(ListOptions.WANTTOWATCH) }
                    .background(color = wantToWatchButtonColor, shape = RoundedCornerShape(5.dp))
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Want to watch ($wantToWatchListCount)",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }

        item {
            //Dropped
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable { onCategoryChange(ListOptions.DROPPED) }
                    .background(color = droppedButtonColor, shape = RoundedCornerShape(5.dp))
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Dropped ($droppedListCount)",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = LocalColor.current.background
                )
            }
        }
    }

}

@Composable
fun ListPageList (
    loggedInUsersList: Boolean,
    listItemList: List<ListItem>,
    handleProductionImageClick: (productionID: String, productionType: String) -> Unit,
    handleListItemRatingChange: (listItem: ListItem, score: Int) -> Unit,
    handleListItemFavoriteClick: (listItem: ListItem, favorite: Boolean) -> Unit,
    handleCompareUserClick: () -> Unit,
    handleEpisodeCountChange: (listItem: ListItem, episodeCount: Int, isPlus: Boolean)  -> Unit,
){
    //Graphics
    Column(
        modifier = Modifier
            .fillMaxSize()
    ){

        //Compare list button if logged in user is looking at another users list
        if(!loggedInUsersList){
            //wrapper to center the button to the middle of the screen
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ){
                //Compare list button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable {
                            handleCompareUserClick()
                        }
                        .background(
                            color = LocalColor.current.backgroundLight,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(150.dp)
                        .height(30.dp)
                ) {
                    Text(
                        "Compare to your list",
                        fontSize = paragraphSize,
                        fontWeight = weightBold,
                        color = LocalColor.current.background
                    )
                }
            }

        }

        //List items
        for (listItem in listItemList){
            ListPageListItem(
                listItem = listItem,
                loggedInUsersList = loggedInUsersList,
                handleProductionImageClick = handleProductionImageClick,
                handleListItemRatingChange = handleListItemRatingChange,
                handleFavoriteClick = handleListItemFavoriteClick,
                handleEpisodeCountChange = handleEpisodeCountChange,
            )
        }
    }

}

@Composable
fun ListPageListItem (
    listItem: ListItem,
    loggedInUsersList: Boolean,
    handleProductionImageClick: (productionID: String, productionType: String) -> Unit,
    handleListItemRatingChange: (listItem: ListItem, score: Int) -> Unit,
    handleFavoriteClick: (listItem: ListItem, favorite: Boolean) -> Unit,
    handleEpisodeCountChange: (listItem: ListItem, episodeCount: Int, isPlus: Boolean)  -> Unit,
){

    //Graphics logic
    var watchedEpisodesCount: Int by remember {
        mutableIntStateOf(listItem.currentEpisode)
    }

    var listItemRating by remember {
        mutableIntStateOf(listItem.score)
    }

    var listItemFavorite by remember { mutableStateOf(listItem.loggedInUsersFavorite) }

    var ratingsSliderIsVisible by remember { mutableStateOf(false) }

    var handleFavoriteClick: () -> Unit = {
        listItemFavorite = !listItemFavorite
        listItem.loggedInUsersFavorite = listItemFavorite
        handleFavoriteClick(listItem, listItemFavorite)
    }

    val handleListItemScoreChange: (rating: Int) -> Unit = {rating ->
        listItemRating = rating
        ratingsSliderIsVisible = false

        handleListItemRatingChange(listItem, rating)
    }

    val handleEpisodeCount = { isPlus: Boolean ->
        handleEpisodeCountChange(listItem, watchedEpisodesCount, isPlus)
    }



    // Passer på at watchedEpisodeCount oppdaterer seg for produksjonen
    // (Fikser bug hvor filmer f.eks har 5 som Ep of 1 (Ep 5 of 1),
    // selv om currentEpisode er 0 eller 1
    LaunchedEffect(listItem.currentEpisode) {
        watchedEpisodesCount = listItem.currentEpisode
    }

    //Graphics
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
    )
    {
        LineDevider()

        //List item
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProductionImage(
                imageID = listItem.production.posterUrl,
                imageDescription = listItem.production.title + " Poster",
                modifier = Modifier
                    .clickable {
                        handleProductionImageClick(listItem.production.imdbID, listItem.production.type)
                    }
            )
            //List Item information
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                //Show title
                Column (
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ){
                    //Title
                    Text(
                        text = listItem.production.title,
                        fontSize = headerSize,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        color = LocalColor.current.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    //ReleaseYear
                    Text(
                        text = "(${listItem.production.releaseDate.get(Calendar.YEAR)})",
                        fontSize = headerSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = LocalColor.current.secondary
                    )
                }

                //Pluss and minus buttons
                if (loggedInUsersList) {
                    // Favorite button
                    FavoriteButton(
                        favorited = listItemFavorite,
                        handleFavoriteClick = handleFavoriteClick
                    )
                    //+ and - buttons
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // Minus button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth(.5f)
                                .background(
                                    color = if(isAppInDarkTheme()) LocalColor.current.backgroundLight else LocalColor.current.primary,
                                    shape = RoundedCornerShape(
                                        topStart = 10.dp,
                                        bottomStart = 10.dp,
                                        topEnd = 0.dp,
                                        bottomEnd = 0.dp
                                    )
                                )
                                .clickable {
                                    //Button onclick function
                                    if (watchedEpisodesCount > 0) {
                                        watchedEpisodesCount--
                                        listItem.currentEpisode = watchedEpisodesCount

                                        handleEpisodeCount(false)

                                        // Log utskrift for å dobbeltsjekke at begge variablene oppdateres
                                        //Log.d("MinusBtn_VariableTest", "currentEpisode: " + listItem.currentEpisode.toString())
                                        //Log.d("MinusBtn_VariableTest", "watchedEpisodesCount: $watchedEpisodesCount")
                                    }
                                }
                        ) {
                            //Button text
                            Text(
                                text = "-",
                                fontSize = 20.sp,
                                fontWeight = weightBold,
                                color = if(isAppInDarkTheme())LocalColor.current.secondary else LocalColor.current.backgroundLight
                            )
                        }


                        // Plus button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth(1f)
                                .background(
                                    color = if(isAppInDarkTheme()) LocalColor.current.backgroundLight else LocalColor.current.primary,
                                    shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        bottomStart = 0.dp,
                                        topEnd = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                                .clickable {
                                    when (val production = listItem.production) {
                                        is TVShow -> {

                                            val productionTotalEpisodes = production.episodes.size;
                                            // For TV-serier: Sjekk om det er flere episoder igjen å se
                                            if (watchedEpisodesCount < productionTotalEpisodes) {
                                                watchedEpisodesCount++

                                                handleEpisodeCount(true)
                                            }

                                        }

                                        is Movie -> {
                                            // For filmer: Siden en film ikke har episoder, setter vi watchedEpisodesCount til 1
                                            if (watchedEpisodesCount == 0) {
                                                watchedEpisodesCount = 1

                                                handleEpisodeCount(true)
                                            }

                                        }

                                        is Episode -> TODO()
                                    }
                                }
                        ){
                            //Button text
                            Text(
                                text = "+",
                                fontSize = 20.sp,
                                fontWeight = weightBold,
                                color = if(isAppInDarkTheme())LocalColor.current.secondary else LocalColor.current.backgroundLight
                            )
                        }
                    }
                }

                // Episodes and score
                Column (
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Episode number and score
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        //Episode text
                        Text(
                            text = when (listItem.production) {
                                is TVShow -> {
                                    // For TV-serier: vis episodenummer og totalt antall episoder
                                    "Ep $watchedEpisodesCount of ${(listItem.production as TVShow).episodes.size}"
                                }
                                is Movie -> {
                                    //Log.d("ListScreen","watchedEpisodesCount: $watchedEpisodesCount")
                                    //Log.d("ListScreen","realEpisodesCount: ${listItem.currentEpisode}")


                                    "Ep $watchedEpisodesCount of 1"

                                }
                                else -> {
                                    "Ep $watchedEpisodesCount of 1"
                                }},
                            fontSize = headerSize,
                            fontWeight = weightLight,
                            fontFamily = fontFamily,
                            color = LocalColor.current.secondary
                        )
                        //Episode Rating
                        if(loggedInUsersList){
                            //Clickable score button
                            Box (
                                modifier = Modifier
                                    .width(90.dp)
                                    .wrapContentHeight()
                                    .clickable {
                                        ratingsSliderIsVisible = true
                                    }
                            )
                            {
                                RatingSlider(
                                    listItem = listItem,
                                    onValueChangeFinished = handleListItemScoreChange,
                                    visible = ratingsSliderIsVisible,
                                    rating = listItemRating
                                )
                                //Wrapper to align content to the right
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    //Ratings stars
                                    RatingsGraphics(
                                        color = LocalColor.current.primary,
                                        score = listItemRating,
                                        sizeMultiplier = 1.5f,
                                        loggedInUsersScore = loggedInUsersList
                                    )
                                }

                            }
                        } else {
                            //Ratings stars
                            RatingsGraphics(
                                color =  LocalColor.current.secondary,
                                score = listItemRating,
                                sizeMultiplier = 1.5f
                            )
                        }


                    }
                    ProgressBar(
                        currentNumber = watchedEpisodesCount,
                        endNumber = when (listItem.production) {
                            is TVShow -> (listItem.production as TVShow).episodes.size // Antall episoder for TV-serie
                            is Movie -> 1
                            else -> 0
                        },
                        foregroundColor = if(loggedInUsersList){LocalColor.current.primary}else{LocalColor.current.backgroundLight},
                        backgroundColor = if(loggedInUsersList){LocalColor.current.tertiary}else{LocalColor.current.backgroundLight},
                    )
                }
            }
        }
    }
}

