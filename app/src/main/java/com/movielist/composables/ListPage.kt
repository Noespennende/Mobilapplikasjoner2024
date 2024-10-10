package com.movielist.composables

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import backend.AuthViewModel
import backend.QueryViewModel
import com.movielist.data.ListItem
import com.movielist.data.ListOptions
import com.movielist.data.Movie
import com.movielist.data.TVShow
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.topNavBaHeight
import com.movielist.ui.theme.topPhoneIconsBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import java.util.Calendar


@Composable

fun ListPage (authViewModel : AuthViewModel)
{
    //Temporary code: DELETE THIS CODE
    /*
    val listItems = mutableListOf<ListItem>()
    for (i in 1..12) {
        listItems.add(
            ListItem(
                production = Movie(
                    imdbID = "123",
                    title = "Silo",
                    description = "TvShow Silo description here",
                    genre = "Action",
                    releaseDate = Calendar.getInstance(),
                    actors = emptyList(),
                    rating = 4,
                    reviews = ArrayList(),
                    posterUrl = R.drawable.silo,
                    lengthMinutes = 127,
                    trailerUrl = "trailerurl.com"
                ),
                currentEpisode = 0,
                score = Random.nextInt(0, 10)

            )

            /*
            * production = TVShow(
                    imdbID = "123",
                    title = "Silo",
                    description = "TvShow Silo description here",
                    genre = "Action",
                    releaseDate = Calendar.getInstance(),
                    actors = emptyList(),
                    rating = 4,
                    reviews = ArrayList(),
                    posterUrl = R.drawable.silo,
                    episodes = listOf("01", "02", "03", "04", "05", "06",
                                    "07", "08", "09", "10", "11", "12"),
                    seasons = listOf("1", "2", "3")
                ),*/
        )
    }

    val reviewList = mutableListOf<Review>()
    val user = User(
        userName = "User Userson",
        email = "test@email.no",
        friendList = mutableListOf(),
        myReviews = mutableListOf(),
        favoriteCollection = mutableListOf(),
        profileImageID = R.drawable.profilepicture,
        completedShows = listItems,
        wantToWatchShows = listItems,
        droppedShows = listItems,
        currentlyWatchingShows = listItems
    )
    for (i in 0..6) {
        reviewList.add(
            Review(
                score = Random.nextInt(0, 10), //<- TEMP CODE: PUT IN REAL CODE
                reviewer = user,
                show = listItems[1].production,
                reviewBody = "It’s reasonably well-made, and visually compelling," +
                        "but it’s ultimately too derivative, and obvious in its thematic execution," +
                        "to recommend..",
                postDate = Calendar.getInstance(),
                likes = Random.nextInt(0, 100) //<- TEMP CODE: PUT IN REAL CODE
            )
        )
    }
    //^^^KODEN OVENFOR ER MIDLERTIDIG. SLETT DEN.^^^^
    */

    /*
        Bruker QueryViewModel (bør nok renames), for backend-logikken.
        Da er det ikke komponentens jobb å utføre store logikkblokker,
        men ViewModel, som dermed kan holde på informasjonen mellom sider osv.
        QueryViewModel gjør henter f.eks watchedCollection etter forespørsel fra en side,
        (f.eks ListPage). Hvis bruker så bytter til FrontPage, og watchedCollection skal vises der også,
        trenger ikke QueryViewModel
    */

    val loggedInUser by remember { mutableStateOf(true) }
    authViewModel.checkUserStatus()
    val user by authViewModel.currentUser.collectAsState()

    val queryViewModel: QueryViewModel = viewModel()

    val watchingCollection by queryViewModel.watchingCollection.collectAsState()

    // Disse to kan brukes for ting - mest relevant er kanskje isLoading
    val isLoading by queryViewModel.isLoading.collectAsState()
    val hasError by queryViewModel.hasError.collectAsState()

    // Hvis watchingCollection er tom, gjør et kall for å hente data fra databasen
    if (watchingCollection.isEmpty()) {
        LaunchedEffect(Unit) {
            queryViewModel.fetchUserWatchingCollection(user!!.uid)
        }
    }
    
    val watchingCollectionListItems = queryViewModel.createProductionListItems(watchingCollection)

    //Graphics

    //List
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsBackgroundHeight + topNavBaHeight,
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = bottomNavBarHeight+20.dp
        ),
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            ListPageList(
                loggedInUsersList = loggedInUser,
                listItemList = watchingCollectionListItems
            )
        }
    }

    TopNavBarListPage()
}

@Composable
fun TopNavBarListPage(){

    Box(
        modifier = Modifier.wrapContentSize()
    ){
        TopNavbarBackground()
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(top = topNavBarContentStart)
        ) {
            MovieShowSortingOptions()
            ListCategoryOptions()
        }
    }
}

@Composable
fun MovieShowSortingOptions(
    sizeMultiplier: Float = 1f
) {

    var buttonText by remember {
        mutableStateOf("Movies & Shows")
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
            },
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .height((20 * sizeMultiplier).dp)
                .wrapContentWidth()
        )
        {
            Row(
            ) {
                //Button content
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                )
                {
                    Text(
                        text = buttonText,
                        fontSize = (16 * sizeMultiplier).sp,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        color = Purple,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "v",
                        fontSize = (16 * sizeMultiplier).sp,
                        fontFamily = fontFamily,
                        fontWeight = weightLight,
                        color = Purple,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

            }
        }
    }
}

@Composable
fun ListCategoryOptions (
    activeButtonColor: Color = Purple,
    inactiveButtonColor: Color = LightGray,
    watchedListCount: Int = 0,
    completedListCount: Int = 0,
    wantToWatchListCount: Int = 0,
    droppedListCount: Int = 0,
){

    //Button graphics logic
    var watchingButtonColor by remember {
        mutableStateOf(activeButtonColor)
    }
    var completedButtonColor by remember {
        mutableStateOf(inactiveButtonColor)
    }
    var wantToWatchButtonColor by remember {
        mutableStateOf(inactiveButtonColor)
    }
    var droppedButtonColor by remember {
        mutableStateOf(inactiveButtonColor)
    }
    var activeButton by remember {
        mutableStateOf(ListOptions.WATCHING)
    }

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
                    .clickable {
                        //OnClickFunction
                        if (activeButton != ListOptions.WATCHING) {

                            activeButton = ListOptions.WATCHING
                            watchingButtonColor = activeButtonColor
                            completedButtonColor = inactiveButtonColor
                            wantToWatchButtonColor = inactiveButtonColor
                            droppedButtonColor = inactiveButtonColor
                        }
                    }
                    .background(
                        color = watchingButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Watching ($watchedListCount)",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = DarkGray
                )
            }
        }

        item {
            //Completed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != ListOptions.COMPLETED) {

                            activeButton = ListOptions.COMPLETED
                            watchingButtonColor = inactiveButtonColor
                            completedButtonColor = activeButtonColor
                            wantToWatchButtonColor = inactiveButtonColor
                            droppedButtonColor = inactiveButtonColor
                        }
                    }
                    .background(
                        color = completedButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Completed ($completedListCount)",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = DarkGray
                )
            }
        }

        item {
            //Want to watch
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        if (activeButton != ListOptions.WANTTOWATCH) {

                            activeButton = ListOptions.WANTTOWATCH
                            watchingButtonColor = inactiveButtonColor
                            completedButtonColor = inactiveButtonColor
                            wantToWatchButtonColor = activeButtonColor
                            droppedButtonColor = inactiveButtonColor
                        }

                    }
                    .background(
                        color = wantToWatchButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Want to watch ($wantToWatchListCount)",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = DarkGray
                )
            }
        }

        item {
            //Dropped
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        //OnClickFunction
                        //OnClickFunction
                        if (activeButton != ListOptions.DROPPED) {

                            activeButton = ListOptions.DROPPED
                            watchingButtonColor = inactiveButtonColor
                            completedButtonColor = inactiveButtonColor
                            wantToWatchButtonColor = inactiveButtonColor
                            droppedButtonColor = activeButtonColor
                        }
                    }
                    .background(
                        color = droppedButtonColor,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(150.dp)
                    .height(30.dp)
            ) {
                Text(
                    "Dropped ($droppedListCount)",
                    fontSize = paragraphSize,
                    fontWeight = weightBold,
                    color = DarkGray
                )
            }
        }
    }

}

@Composable
fun ListPageList (
    loggedInUsersList: Boolean,
    listItemList: List<ListItem>
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
                            //OnClickFunction
                        }
                        .background(
                            color = LightGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(150.dp)
                        .height(30.dp)
                ) {
                    Text(
                        "Compare to your list",
                        fontSize = paragraphSize,
                        fontWeight = weightBold,
                        color = DarkGray
                    )
                }
            }

        }

        //List items
        for (listItem in listItemList){
            ListPageListItem(
                listItem = listItem,
                loggedInUsersList = loggedInUsersList
            )
        }
    }

}

@Composable
fun ListPageListItem (
    listItem: ListItem,
    loggedInUsersList: Boolean
){

    //Graphics logic
    var watchedEpisodesCount: Int by remember {
        mutableIntStateOf(listItem.currentEpisode)
    }

    var showScore: Int by remember {
        mutableIntStateOf(listItem.score)
    }

    //Graphics
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
    )
    {
        LineDevider()

        //List item
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShowImage(
                imageID = listItem.production.posterUrl,
                imageDescription = listItem.production.title + " Poster",
            )
            //List Item information
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                //Show title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ){
                    //Title
                    Text(
                        text = listItem.production.title,
                        fontSize = headerSize,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        color = White
                    )
                    //ReleaseYear
                    Text(
                        text = "(${listItem.production.releaseDate.get(Calendar.YEAR)})",
                        fontSize = headerSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = White
                    )
                }

                //Pluss and minus buttons
                if (loggedInUsersList) {
                    //Buttons
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // Minus button
                        Button(
                            onClick = {
                                //Button onclick function
                                if (watchedEpisodesCount > 0){
                                    watchedEpisodesCount--
                                    listItem.currentEpisode = watchedEpisodesCount

                                    // Log utskrift for å dobbeltsjekke at begge variablene oppdateres
                                    //Log.d("MinusBtn_VariableTest", "currentEpisode: " + listItem.currentEpisode.toString())
                                    //Log.d("MinusBtn_VariableTest", "watchedEpisodesCount: $watchedEpisodesCount")
                                }
                            },
                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                bottomStart = 10.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(Gray),
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth(.5f)
                        ) {
                            //Button text
                            Text(
                                text = "-",
                                fontSize = 20.sp,
                                fontWeight = weightBold,
                                color = White
                            )
                        }

                        // Plus button
                        Button(
                            onClick = {

                                when (val production = listItem.production) {
                                    is TVShow -> {
                                        // For TV-serier: Sjekk om det er flere episoder igjen å se
                                        if (watchedEpisodesCount < production.episodes.size) {
                                            watchedEpisodesCount++
                                            listItem.currentEpisode = watchedEpisodesCount

                                        }
                                    }
                                    is Movie -> {
                                        // For filmer: Siden en film ikke har episoder, setter vi watchedEpisodesCount til 1
                                        if (watchedEpisodesCount == 0) {
                                            watchedEpisodesCount = 1
                                            listItem.currentEpisode = watchedEpisodesCount
                                        }
                                    }
                                }
                                // Log utskrift for å dobbeltsjekke at begge variablene oppdateres
                                //Log.d("PlusBtn_VariableTest", "currentEpisode: " + listItem.currentEpisode.toString())
                                //Log.d("PlusBtn_VariableTest", "watchedEpisodesCount: $watchedEpisodesCount")
                            },
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 10.dp,
                                bottomEnd = 10.dp
                            ),
                            colors = ButtonDefaults.buttonColors(Gray),
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth(1f)
                        ) {
                            //Button text
                            Text(
                                text = "+",
                                fontSize = 20.sp,
                                fontWeight = weightBold,
                                color = White
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
                                    "Ep $watchedEpisodesCount of ${listItem.production.episodes.size}"
                                }
                                is Movie -> {
                                    // For filmer: vis lengden på filmen i minutter
                                    "Ep $watchedEpisodesCount of 1"
                                }
                                else -> {
                                    "Ep $watchedEpisodesCount of 1"
                                }},
                            fontSize = headerSize,
                            fontWeight = weightLight,
                            fontFamily = fontFamily,
                            color = White
                        )
                        //Episode Rating
                        if(loggedInUsersList){
                            //Clickable score button
                            Box (
                                modifier = Modifier
                                    .width(90.dp)
                                    .wrapContentHeight()
                                    .clickable {
                                        //button logic
                                        ///TEMP ADD LOGIC HERE
                                    }
                            )
                            {
                                //Wrapper to align content to the right
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    //Score stars
                                    ScoreGraphics(
                                        color = Purple,
                                        score = showScore,
                                        sizeMultiplier = 1.5f,
                                        loggedInUsersScore = loggedInUsersList
                                    )
                                }

                            }
                        } else {
                            //Score stars
                            ScoreGraphics(
                                color =  White,
                                score = showScore,
                                sizeMultiplier = 1.5f
                            )
                        }


                    }
                    ProgressBar(
                        currentNumber = watchedEpisodesCount,
                        endNumber = when (listItem.production) {
                            is TVShow -> listItem.production.episodes.size // Antall episoder for TV-serie
                            is Movie -> 1
                            else -> 0
                        },
                        foregroundColor = if(loggedInUsersList){Purple}else{LightGray},
                        backgroundColor = if(loggedInUsersList){DarkPurple}else{Gray}
                    )
                }
            }
        }
    }
}

