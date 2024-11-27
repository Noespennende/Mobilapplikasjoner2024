package com.movielist.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.R
import com.movielist.Screen
import com.movielist.composables.HamburgerButton
import com.movielist.composables.LineDevider
import com.movielist.composables.LineDividerVertical
import com.movielist.composables.ProductionImage
import com.movielist.composables.ProductionSortSelectButton
import com.movielist.composables.ProfileImage
import com.movielist.composables.ProgressBar
import com.movielist.composables.RatingSlider
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.TopScreensNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.ProductionType
import com.movielist.model.ShowSortOptions
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalConstraints
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.isAppInPortraitMode
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import java.util.Calendar

@Composable
fun ComparisonScreen (controllerViewModel: ControllerViewModel, navController: NavController, userToCompareToID: String?) {

    /*TEMP CODE DELETE*/

    var sharedShowsAndMoviesTemp = mutableMapOf<ListItem, ListItem>()
    var uniqueToLoggedInUserTemp: MutableList<ListItem> = mutableListOf()
    var uniqueToComparisonUserTemp: MutableList<ListItem> = mutableListOf()

    for (i in 1..5) {
        sharedShowsAndMoviesTemp.put(
            ListItem(
                production = Movie(
                    posterUrl = R.drawable.silo.toString(),
                    title = "Silo"
                )
            ),
            ListItem(
                production = Movie(
                    posterUrl = R.drawable.silo.toString(),
                    title = "Silo"
                )
            )
        )
        uniqueToLoggedInUserTemp.add(
            ListItem(
                production = Movie(
                    posterUrl = R.drawable.silo.toString(),
                    title = "Silo"
                )
            )
        )

        uniqueToComparisonUserTemp.add(
            ListItem(
                production = Movie(
                    posterUrl = R.drawable.silo.toString(),
                    title = "Silo"
                )
            )
        )
    }

    var comparisonUserTemp = User(
        userName = "Comparison user",
        email = "email@email.com",
        id = "tempID",
        profileImageID = R.drawable.profilepicture.toString()
    )

    var loggedInUserTemp = User(
        userName = "LoggedIn User",
        email = "email@email.com",
        id = "tempID",
        profileImageID = R.drawable.profilepicture.toString()
    )


    /*TEMP CODE DELETE ABOVE*/

    val comparisonUserID by remember { mutableStateOf(userToCompareToID) } /* <- ID til bruker som logged inn user skal sammenligne listen sin med */
    val loggedInUser by controllerViewModel.loggedInUser.collectAsState() /* <- LoggedInUser */
    val comparisonUser by controllerViewModel.profileOwner.collectAsState()/* <- Brukeren som matcher IDen til comparisonUserID*/

    val sharedShowsAndMovies = remember { mutableStateOf(emptyMap<ListItem, ListItem>()) }/* <- Map<ListItem, ListItem> med list items av alle filmer/serier som er å både logged inn user og comparison user sin liste.
    Keys tilhører logged in user og values tilhører comparison user. Pass på at Keyen og valuen er den samme produksjonen for begge brukerene. Hvis Key = Silo skal Value = Silo (bare for den andre brukeren også.)*/

    var uniqueToLoggedInUser = remember { mutableStateOf(emptyList<ListItem>()) }//comparisonUser?.let { loggedInUser.getUniqueShows(it) } ?: emptyList() /*<- Liste med list items av alle shows/movies som er unike til LoggedInUser*/
    var uniqueToComparisonUser = remember { mutableStateOf(emptyList<ListItem>()) }//comparisonUser?.getUniqueShows(loggedInUser) ?: emptyList() /*<- Liste med list items av alle shows/movies som er unike til ComparisonUser */

    var currentSortingOption by remember { mutableStateOf<ShowSortOptions>(ShowSortOptions.MOVIESANDSHOWS) }

    val handleSortChange: (sortOption: ShowSortOptions) -> Unit = {sortOption ->
        currentSortingOption = sortOption
        //Kontroller funksjon for å håndtere sorterings endring
    }

    val handleListItemRatingChange: (listItem: ListItem, score: Int) -> Unit  = { listItem, score ->

        controllerViewModel.handleListItemScoreChange(listItem, score)
    }

    val handleProfileImageClick: (userID: String) -> Unit = {userID ->
        navController.navigate(Screen.ProfileScreen.withArguments(userID))
    }

    val handleProductionImageClick: (productionID: String, productionType: ProductionType) -> Unit = {productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType.name))
    }

    LaunchedEffect(comparisonUser) {
        sharedShowsAndMovies.value = comparisonUser?.let { controllerViewModel.getSharedProductions(it) } ?: emptyMap()


        val (loggedInUserUnique, comparisonUserUnique) = comparisonUser?.let {
            controllerViewModel.getUniqueProductions(it, sharedShowsAndMovies.value)
        } ?: Pair(emptyList(), emptyList())

        uniqueToLoggedInUser.value = loggedInUserUnique
        uniqueToComparisonUser.value = comparisonUserUnique

    }


    //Graphics
    LazyColumn(
        contentPadding = PaddingValues(
            top = LocalConstraints.current.mainContentStart + 20.dp,
            start = LocalConstraints.current.mainContentHorizontalPadding,
            end = LocalConstraints.current.mainContentHorizontalPaddingAlternative,
            bottom =LocalConstraints.current.bottomUniversalNavbarHeight +20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp)
        ,
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            if(!isAppInPortraitMode()){
                comparisonUser?.let {
                    loggedInUser?.let { it1 ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(bottom = 10.dp)
                        ){
                            UserVsUserCard(
                                loggedInUser = it1,
                                comparisonUser = it,
                                handleProfileImageClick = handleProfileImageClick
                            )
                        }

                    }
                }
            }
        }
        //Shared Shows and movies
        item {
            ComparisonScreenHeader(GenerateHeaderText(currentSortingOption))
        }

        item {
            LineDevider()
        }
        items(sharedShowsAndMovies.value.entries.toList()) { entry ->
            ComparisonCard(
                listItemForLoggedInUser = entry.key,
                listItemForComparisonUser = entry.value,
                handleProductionImageClick = handleProductionImageClick,
                handleListItemRatingsChange = handleListItemRatingChange,
                modifier = Modifier
                    .padding(bottom = 15.dp)
            )

        }

        //Unique to logged in user
        item {
            ComparisonScreenHeader(
                headerText = "Unique to you",
                modifier = Modifier
                    .padding(top = 30.dp)
            )
        }
        item {
            LineDevider()
        }
        items (uniqueToLoggedInUser.value) { entry ->
            ComparisonCard(
                listItemForLoggedInUser = entry,
                listItemForComparisonUser = ListItem(),
                handleProductionImageClick = handleProductionImageClick,
                handleListItemRatingsChange = handleListItemRatingChange,
                modifier = Modifier
                    .padding(bottom = 15.dp)
            )
        }

        //Unique to comparison user
        item {
            ComparisonScreenHeader(
                headerText = "Unique to " + (comparisonUser?.userName ?: ""),
                modifier = Modifier
                    .padding(top = 30.dp)
            )
        }
        item {
            LineDevider()
        }
        items (uniqueToComparisonUser.value) { entry ->
            ComparisonCard(
                listItemForLoggedInUser = ListItem(),
                listItemForComparisonUser = entry,
                handleProductionImageClick = handleProductionImageClick,
                handleListItemRatingsChange = handleListItemRatingChange,
                modifier = Modifier
                    .padding(bottom = 15.dp)
            )
        }
    }

    if(isAppInPortraitMode()) {
        comparisonUser?.let {
            loggedInUser?.let { it1 ->
                TopNavBarComparisonScreen(
                    loggedInUser = it1,
                    comparisonUser = it,
                    handleSortChange = handleSortChange,
                    handleProfileImageClick = handleProfileImageClick
                )
            }
        }
    } else {
        TopNavigationComparisonScreenLandscape(
            handleSortChange = handleSortChange
        )
    }

}


@Composable
fun TopNavBarComparisonScreen(
    handleSortChange: (activeSortCategory: ShowSortOptions) -> Unit,
    loggedInUser: User,
    comparisonUser: User,
    handleProfileImageClick: (userID: String) -> Unit
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
            UserVsUserCard(
                loggedInUser = loggedInUser,
                comparisonUser = comparisonUser,
                handleProfileImageClick = handleProfileImageClick
            )
        }
    }
}

@Composable
fun UserVsUserCard(
    loggedInUser: User,
    comparisonUser: User,
    handleProfileImageClick: (userID: String) -> Unit
){
    //Users
    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //LoggedInUserCard
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
        ) {
            Text(
                text = loggedInUser.userName,
                fontFamily = fontFamily,
                fontSize = headerSize,
                fontWeight = weightBold,
                color = LocalColor.current.secondary,
            )
            ProfileImage(
                imageID = loggedInUser.profileImageID,
                userName = loggedInUser.userName,
                handleProfileImageClick = {handleProfileImageClick(loggedInUser.id)}
            )

        }

        //VerticalLineDevider
        LineDividerVertical(
            strokeWith = 9f,
            modifier = Modifier
                .height(35.dp)
        )

        //Comparison User
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            ProfileImage(
                imageID = comparisonUser.profileImageID,
                userName = comparisonUser.userName,
                handleProfileImageClick = {handleProfileImageClick(comparisonUser.id)}
            )
            Text(
                text = comparisonUser.userName,
                fontFamily = fontFamily,
                fontSize = headerSize,
                fontWeight = weightBold,
                color = LocalColor.current.secondary,
            )
        }
    }
}

@Composable
fun ComparisonScreenHeader (
    headerText: String,
    color: Color = LocalColor.current.secondary,
    modifier: Modifier = Modifier
){
    Text(
        text = headerText,
        fontFamily = fontFamily,
        fontWeight = weightBold,
        fontSize = headerSize,
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun ComparisonCard (
    listItemForLoggedInUser: ListItem,
    listItemForComparisonUser: ListItem,
    modifier: Modifier = Modifier,
    handleProductionImageClick: (productionID: String, productionType: ProductionType) -> Unit,
    handleListItemRatingsChange: (listItem: ListItem, rating: Int) -> Unit
){

    var productionLenght: Int = 1
    var productionImage = listItemForLoggedInUser.production.posterUrl
    var productionTitle = listItemForLoggedInUser.production.title
    var productionYear = listItemForLoggedInUser.production.releaseDate.get(Calendar.YEAR)
    var watchedEpisodesLoggedInUser = listItemForLoggedInUser.currentEpisode
    var watchedEpisodesComparisonUser = listItemForComparisonUser.currentEpisode

    if(productionImage == null){
        productionImage = listItemForComparisonUser.production.posterUrl
        productionTitle = listItemForComparisonUser.production.title
        productionYear = listItemForComparisonUser.production.releaseDate.get(Calendar.YEAR)

    }


    if(listItemForLoggedInUser.production is TVShow){
        productionLenght = listItemForLoggedInUser.production.episodes.size
    } else if (listItemForComparisonUser.production is TVShow){
        productionLenght = listItemForComparisonUser.production.episodes.size
    }




    var loggedInUserRating by remember { mutableStateOf(listItemForLoggedInUser.score) }

    var ratingsSliderVisible by remember { mutableStateOf(false) }

    val handleRatingsChange: (newRating: Int) -> Unit = { newRating ->
        ratingsSliderVisible = false
        loggedInUserRating = newRating

        handleListItemRatingsChange(listItemForLoggedInUser, newRating)
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        //Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ){
            Text(
                text = productionTitle,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = headerSize,
                color = LocalColor.current.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
            Text(
                text = "(" + productionYear + ")",
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                fontSize = headerSize,
                color = LocalColor.current.secondary,
                textAlign = TextAlign.Center,
            )
        }
        //Comparison Section
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ){
            //LoggedInUserInfo
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth(if(isAppInPortraitMode()) .33f else .40f)
                    .height(90.dp)
            ) {
                RatingsGraphics(
                    score = loggedInUserRating,
                    sizeMultiplier = 1.3f,
                    color = LocalColor.current.primary,
                    loggedInUsersScore = true,
                    modifier = Modifier.clickable {
                        ratingsSliderVisible = true
                    }
                )

                Text(
                    text = "Ep " + watchedEpisodesLoggedInUser + " of " + productionLenght,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    fontSize = paragraphSize,
                    color = LocalColor.current.quinary,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 6.dp )
                )
                ProgressBar(
                    currentNumber = watchedEpisodesLoggedInUser,
                    endNumber = productionLenght,
                    flip = true
                )
            }

            //Production Image
            ProductionImage(
                imageID = productionImage,
                imageDescription = listItemForLoggedInUser.production.title,
                sizeMultiplier = .7f,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clickable {
                        handleProductionImageClick(listItemForLoggedInUser.production.imdbID, listItemForLoggedInUser.production.type)
                    }
            )

            //Comparison user info
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                RatingsGraphics(
                    score = listItemForLoggedInUser.score,
                    sizeMultiplier = 1.3f,
                    color = if(isAppInDarkTheme()) LocalColor.current.secondary else LocalColor.current.quaternary,
                    loggedInUsersScore = false,
                )

                Text(
                    text = "Ep " + listItemForComparisonUser.score + " of " + productionLenght,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    fontSize = paragraphSize,
                    color = LocalColor.current.quinary,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 6.dp)
                )


                ProgressBar(
                    currentNumber = watchedEpisodesComparisonUser,
                    endNumber = productionLenght,
                    foregroundColor =  if(isAppInDarkTheme()) LocalColor.current.quaternary else LocalColor.current.quaternary,
                    backgroundColor = if(isAppInDarkTheme()) LocalColor.current.backgroundLight else LocalColor.current.backgroundLight
                )

            }
        }
    }

    RatingSlider(
        rating = loggedInUserRating,
        visible = ratingsSliderVisible,
        onValueChangeFinished = handleRatingsChange
    )
}

@Composable
fun TopNavigationComparisonScreenLandscape(
    handleSortChange: (activeSortCategory: ShowSortOptions) -> Unit
){

    var categoryDropDownExpanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = LocalConstraints.current.bottomUniversalNavbarHeight + 20.dp,
                end = 80.dp
            )
    ) {

        HamburgerButton(
            sizeMultiplier = 2.3f,
            handleHamburgerButtonClick = {
                categoryDropDownExpanded = !categoryDropDownExpanded
            }
        )

        Box(){
            //CategoryDropdown
            DropdownMenu(
                expanded = categoryDropDownExpanded,
                onDismissRequest = {categoryDropDownExpanded = false},
                offset = DpOffset(x = (-230).dp, y= 0.dp),
                modifier = Modifier
                    .background(color = if(isAppInDarkTheme()){ LocalColor.current.tertiary} else {LocalColor.current.primary})
                    .width(180.dp)
            ) {

                //Sorting Options
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = "Movies and Shows",
                                fontSize = headerSize,
                                fontWeight = weightBold,
                                fontFamily = fontFamily,
                                color = if(isAppInDarkTheme()){ LocalColor.current.secondary} else {LocalColor.current.backgroundLight},
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    onClick = {
                        //On click logic for dropdown menu
                        handleSortChange(ShowSortOptions.MOVIESANDSHOWS)
                        categoryDropDownExpanded = false
                    }
                )

                //Watching
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = "Movies",
                                fontSize = headerSize,
                                fontWeight = weightBold,
                                fontFamily = fontFamily,
                                color = if(isAppInDarkTheme()){ LocalColor.current.secondary} else {LocalColor.current.backgroundLight},
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    onClick = {
                        //On click logic for dropdown menu
                        handleSortChange(ShowSortOptions.MOVIES)
                        categoryDropDownExpanded = false
                    }
                )


                //Completed
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = "Shows",
                                fontSize = headerSize,
                                fontWeight = weightBold,
                                fontFamily = fontFamily,
                                color = if(isAppInDarkTheme()){ LocalColor.current.secondary} else {LocalColor.current.backgroundLight},
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    onClick = {
                        //On click logic for dropdown menu
                        handleSortChange(ShowSortOptions.SHOWS)
                        categoryDropDownExpanded = false
                    }
                )
            }
        }
    }

}


fun GenerateHeaderText (
    sortOptions: ShowSortOptions
): String {
    if (sortOptions == ShowSortOptions.MOVIES) {
        return "Shared movies"
    } else if (sortOptions == ShowSortOptions.SHOWS) {
        return "Shared shows"
    }
    return "Shared shows and movies"
}