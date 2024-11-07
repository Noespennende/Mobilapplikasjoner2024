package com.movielist.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.R
import com.movielist.Screen
import com.movielist.composables.LineDevider
import com.movielist.composables.ListItemListSidesroller
import com.movielist.composables.ProfileImage
import com.movielist.composables.RoundProgressBar
import com.movielist.composables.TopNavbarBackground
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ListItem
import com.movielist.model.Review
import com.movielist.model.TVShow
import com.movielist.model.User
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.darkWhite
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.green
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.red
import com.movielist.ui.theme.teal
import com.movielist.ui.theme.topNavBaHeight
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.topPhoneIconsBackgroundHeight
import com.movielist.ui.theme.verticalPadding
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import com.movielist.ui.theme.yellow
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ProfilePage (controllerViewModel: ControllerViewModel, navController: NavController){

    // TEMP CODE DELETE BELOW
    val exampleUser: User = User(
        id = "IDfromFirebase",
        userName = "Example User",
        email = "Example@Email.com",
        profileImageID = R.drawable.profilepicture
    )

    val exampleReviews: MutableList<Review> = mutableListOf()
    val exampleShows: MutableList<ListItem> = mutableListOf()
    val exampleFavShows: MutableList<ListItem> = mutableListOf()

    val handleProductionClick: (productionID: String, productionType: String)
        -> Unit = { productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType))
    }

    for (i in 0 .. 10){
        exampleShows.add(
            ListItem(
                production = TVShow(
                    imdbID = "123",
                    title = "Silo",
                    description = "TvShow Silo description here",
                    genre = listOf("Action"),
                    releaseDate = Calendar.getInstance(),
                    actors = emptyList(),
                    rating = 4,
                    reviews = ArrayList(),
                    posterUrl = "https://image.tmdb.org/t/p/w500/2asxdpNtVQhbuUJlNSQec1eprP.jpg",
                    episodes = listOf("01", "02", "03", "04", "05", "06",
                                     "07", "08", "09", "10", "11", "12"),
                    seasons = listOf("1", "2", "3")
                ),
                currentEpisode = i,
                score = Random.nextInt(0, 10)

            )
        )
        exampleReviews.add(
            Review(
                score = Random.nextInt(0, 10),
                reviewer = exampleUser,
                show = exampleShows[i].production,
                reviewBody = "This is a review of the show",
            )
        )
    }

    exampleUser.myReviews.addAll(exampleReviews)
    exampleUser.currentlyWatchingCollection.addAll(exampleShows)
    exampleUser.completedCollection.addAll(exampleShows)
    exampleUser.wantToWatchCollection.addAll(exampleShows)
    exampleUser.droppedCollection.addAll(exampleShows)

    exampleFavShows.addAll(exampleShows)


    // TEMP CODE DELETE ABOVE

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()

    val usersFavoriteMovies = controllerViewModel.getUsersFavoriteMovies(loggedInUser)

    val usersFavoriteTVShows = controllerViewModel.getUsersFavoriteTVShows(loggedInUser)


    //function variables:
    val user by remember(loggedInUser) {
        mutableStateOf(loggedInUser ?: exampleUser)
    }
    val isLoggedInUser by remember {
        mutableStateOf(true)
    }

    val handleReviewButtonLikeClick: (reviewID: String) -> Unit = {
        //Kontroller funksjon her
    }

    //Graphics
    //Main Content
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsBackgroundHeight + topNavBaHeight + 20.dp,
            bottom = bottomNavBarHeight +20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    )
    {
        //Biosection
        item{
            //Wrapper for horisontal padding
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = horizontalPadding
                    )
            )
            {
                ProfileInfoSection( user = user)
            }

        }

        //Favorite Series
        item {
            ListItemListSidesroller(
                header = "Favorite series",
                listOfShows = usersFavoriteTVShows,
                handleImageClick = handleProductionClick,
                textModifier = Modifier
                    .padding(
                        start = verticalPadding,
                        bottom = 15.dp
                    )

            )
        }

        //Line devider
        item {
            //Wrapper for the line devider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ){
                LineDevider()
            }

        }

        //Favorite Movies
        item {
            ListItemListSidesroller(
                header = "Favorite movies",
                listOfShows = usersFavoriteMovies,
                handleImageClick = handleProductionClick,
                textModifier = Modifier
                    .padding(
                        start = horizontalPadding,
                        bottom = 15.dp
                    )
            )
        }

        //Line devider
        item {
            //Wrapper for the line devider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ){
                LineDevider()
            }

        }

        //Statistics section
        item {
            StatisticsSection(
                //TEMP CODE
                showGenreToPercentageMap = mapOf("Action" to 30, "Drama" to 25, "Sci-fi" to 20, "Fantacy" to 15),
                movieGenreToPercentageMap = mapOf("Musical" to 30, "Drama" to 25, "Sci-fi" to 20, "Fantacy" to 15)
            )
        }

        //Line devider
        item {
            //Wrapper for the line devider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ){
                LineDevider()
            }

        }

        //Review section
        item {
            ReviewsSection(
                reviewList = exampleReviews,
                header = "Reviews",
                handleLikeClick = handleReviewButtonLikeClick,
                handleProductionImageClick = handleProductionClick

            )
        }

    }

    //Navigation
    TopNavBarProfilePage(
        user = user,
        loggedInUser = isLoggedInUser
    )
}

@Composable
fun TopNavBarProfilePage(
    user: User,
    loggedInUser: Boolean
) {
    TopNavbarBackground()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        UsernameHeadline(
            user = user,
            loggedInUser = loggedInUser
        )

        ProfileCategoryOptions()

    }

}

@Composable
fun UsernameHeadline (
    user: User,
    loggedInUser: Boolean
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topNavBarContentStart)
    ){
        ProfileImage(
            imageID = user.profileImageID,
            userName = user.userName,
            sizeMultiplier = .8f
        )

        Text(
            text = user.userName,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = if(loggedInUser){Purple} else {LightGray},
            modifier = Modifier
                .padding(horizontal = 10.dp)
        )
    }
}

@Composable
fun ProfileCategoryOptions(
    activeButtonColor: Color = Purple,
    inactiveButtonColor: Color = LightGray,
){

        //Button graphics logic
        var summaryButtonColor by remember {
            mutableStateOf(activeButtonColor)
        }
        var libaryButtonColor by remember {
            mutableStateOf(inactiveButtonColor)
        }
        var reviewsButtonColor by remember {
            mutableStateOf(inactiveButtonColor)
        }

        var activeButton by remember {
            mutableStateOf(com.movielist.model.ProfileCategoryOptions.SUMMARY)
        }

        //Graphics
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding)
        ){
            item {
                //Summary
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable {
                            //OnClickFunction
                            if (activeButton != com.movielist.model.ProfileCategoryOptions.SUMMARY) {

                                activeButton = com.movielist.model.ProfileCategoryOptions.SUMMARY
                                summaryButtonColor = activeButtonColor
                                libaryButtonColor = inactiveButtonColor
                                reviewsButtonColor = inactiveButtonColor
                            }
                        }
                        .background(
                            color = summaryButtonColor,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(150.dp)
                        .height(30.dp)
                ) {
                    Text(
                        "Summary",
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
                            if (activeButton != com.movielist.model.ProfileCategoryOptions.LIBRARY) {

                                activeButton = com.movielist.model.ProfileCategoryOptions.LIBRARY
                                summaryButtonColor = inactiveButtonColor
                                libaryButtonColor = activeButtonColor
                                reviewsButtonColor = inactiveButtonColor
                            }
                        }
                        .background(
                            color = libaryButtonColor,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(150.dp)
                        .height(30.dp)
                ) {
                    Text(
                        "Library",
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
                            if (activeButton != com.movielist.model.ProfileCategoryOptions.REVIEWS) {

                                activeButton = com.movielist.model.ProfileCategoryOptions.REVIEWS
                                summaryButtonColor = inactiveButtonColor
                                libaryButtonColor = inactiveButtonColor
                                reviewsButtonColor = activeButtonColor
                            }

                        }
                        .background(
                            color = reviewsButtonColor,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(150.dp)
                        .height(30.dp)
                ) {
                    Text(
                        "Reviews",
                        fontSize = paragraphSize,
                        fontWeight = weightBold,
                        color = DarkGray
                    )
                }
            }
        }
}

@Composable
fun ProfileInfoSection (
    user: User
){
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
    ){
        //Bio Section
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ){
            LeftProfileSection(
                gender = user.gender,
                location = user.location,
                website = user.website
            )
            BioSection(
                userBio = user.bio
            )
        }

        //Summary section content wrapper for distance to content above
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 15.dp)
        ){
            //Summary section
            SummarySection(
                filmCount = 1530, //TEMP CODE DELETE THIS
                showCount = 500, //TEMP CODE DELETE THIS
                followingCount = 200, //TEMP CODE DELETE THIS
                followersCount = 2453, //TEMP CODE DELETE THIS
            )
        }

        LineDevider()
    }

}

@Composable
fun LeftProfileSection(
    gender: String,
    location: String,
    website: String
){
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .width(160.dp)
    ) {
        //Gender
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(17.dp)
            )
            Text(
                text = "Gender:",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

            Text(
                text = gender,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = paragraphSize,
                color = darkWhite
            )
        }

        //Location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(17.dp)
            )
            Text(
                text = "Location:",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

            Text(
                text = location,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = paragraphSize,
                color = darkWhite
            )
        }

        //Website
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(17.dp)
            )
            Text(
                text = "Website:",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

            Text(
                text = website,
                fontFamily = fontFamily,
                fontWeight = weightLight,
                fontSize = paragraphSize,
                color = darkWhite
            )
        }
    }
}

@Composable
fun BioSection (
    userBio: String
){
    var bio = userBio

    if (bio.length < 1){
        bio = "(There doesn't seem to be anything here)"
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .width(160.dp)
    ) {
        Text(
            text = "Bio:",
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = paragraphSize,
            color = White
        )
        Text(
            text = bio,
            fontFamily = fontFamily,
            fontWeight = weightLight,
            fontSize = paragraphSize,
            color = darkWhite,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun SummarySection (
    filmCount: Int,
    showCount: Int,
    followingCount: Int,
    followersCount: Int
){
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ){
        //Films
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = filmCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )
            Text(
                text = "Films",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

        }

        //Shows
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = showCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )
            Text(
                text = "Films",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

        }

        //following
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = followingCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )
            Text(
                text = "Films",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

        }

        //following
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Text(
                text = followersCount.toString(),
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )
            Text(
                text = "Films",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = White
            )

        }
    }
}

@Composable
fun StatisticsSection(
    showGenreToPercentageMap: Map<String, Int>,
    movieGenreToPercentageMap: Map<String, Int>,
    header: String = "Statistics",
){
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        //Header
        Text(
            text = header,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = White,
            textAlign = TextAlign.Start
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ){
            //Show statistics
            Statistics(
                genreToPercentageMap = showGenreToPercentageMap,
                header = "Shows"
            )
            //Movie statistics
            Statistics(
                genreToPercentageMap = movieGenreToPercentageMap,
                header = "Movies"
            )
        }

    }
}

@Composable
fun Statistics(
    genreToPercentageMap: Map<String, Int>,
    header: String
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ){
        StatisticsPieChart(
            genreToPercentageMap = genreToPercentageMap,
            header = header
        )
        StatisticsList(
            genreToPercentageMap= genreToPercentageMap
        )
    }
}

@Composable
fun StatisticsPieChart (
    genreToPercentageMap: Map<String, Int>,
    header: String
){

    //Sorts the genreToPercentageMap from lowest to highest based on the values
    val sortedMap = genreToPercentageMap.toList().sortedBy { (_, value) -> value }.toMap()
    var restPercentage = 1f
    var index = 0
    val colorList: Array<Color> = arrayOf(Purple, yellow, teal, red)

    val pieChartRadius = 70.dp
    val pieChartStrokeWith = 8.dp

    Box()
    {
        //Progress bar for the first value in the map
        RoundProgressBar(
            strokeCap = StrokeCap.Butt,
            strikeWith = pieChartStrokeWith,
            radius = pieChartRadius,
            color = green
        )

        //Progress bar for remaining values
        for(percentage in sortedMap.values)
        {
            restPercentage -= (percentage.toFloat())/100f
            println(restPercentage)

            if (index <= 3) {
            RoundProgressBar(
                strokeCap = StrokeCap.Butt,
                strikeWith = pieChartStrokeWith,
                radius = pieChartRadius,
                percentage = restPercentage,
                color = colorList[index]
            )
        }
            if (index >= 3) {
                break
            }

            index++
        }

        Text(
            text = header,
            fontFamily = fontFamily,
            fontWeight = weightBold,
            fontSize = headerSize,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun StatisticsList (
    genreToPercentageMap: Map<String, Int>
)
{
    //Sort genreToPercentageMap from highest to lowest based on the value
    val sortedMap = genreToPercentageMap.toList().sortedByDescending { (_, value) -> value }.toMap()
    val colorList: Array<Color> = arrayOf(teal, yellow, Purple, green)
    var other: Int = 100
    var index = 0

    Column(
        modifier = Modifier
            .width(100.dp)
    )
    {
        for((genre, percentage) in  sortedMap){
            other -= percentage

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Text(
                    text = "${percentage.toString()}%",
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    fontSize = paragraphSize,
                    color = colorList[index],
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = genre,
                    fontFamily = fontFamily,
                    fontWeight = weightRegular,
                    fontSize = paragraphSize,
                    color = White,
                    textAlign = TextAlign.Start,
                )
            }
            if (index >= 3) {
                break
            }
            index++
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            Text(
                text = "${other}%",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = paragraphSize,
                color = red,
                textAlign = TextAlign.Start,
            )
            Text(
                text = "Other",
                fontFamily = fontFamily,
                fontWeight = weightRegular,
                fontSize = paragraphSize,
                color = White,
                textAlign = TextAlign.Start,
            )
        }
    }
}
