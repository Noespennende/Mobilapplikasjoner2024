package com.movielist.screens

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.GenerateListOptionName
import com.movielist.composables.LineDevider
import com.movielist.composables.RatingSlider
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.ShowImage
import com.movielist.composables.YouTubeVideoEmbed
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ListOptions
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.Review
import com.movielist.model.TVShow
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topPhoneIconsBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular

@Composable
fun ProductionScreen (navController: NavController, controllerViewModel: ControllerViewModel, productionID: String?, productionType: String?){

    //Variables
    //val productionID by remember { if (productionID != null){mutableStateOf(productionID)} else {mutableStateOf("")} } /* <- Denne variablen holder på ID til filmen eller serien som skal hentes ut*/

    //val production by controllerViewModel.movieData.collectAsState()
    //val production by remember { mutableStateOf<Production?>(null) }
    var memberOfUserList by remember { mutableStateOf<ListOptions?>(null) } /* <-ListOption enum som sier hvilken liste filmen/serien ligger i i logged inn users liste. Hvis den ikke ligger i en liste set den til null.*/
    var userScore by remember { mutableIntStateOf(0) } /* <-Int fra 1-10 som sier hvilken rating logged inn user har gitt filmen/serien. Hvis loggedInUser ikke har ratet serien sett verdien til 0*/
    var listOfReviews by remember { mutableStateOf(mutableListOf<Review>()) } /* <-Liste med Review objekter med alle reviews av filmen/serien*/

    var productionID by remember { mutableStateOf(productionID.orEmpty()) } /* <- Denne variablen holder på ID til filmen eller serien som skal hentes ut*/

    /* Lytter etter endring i movieData fra ControllerViewModel */
    val production by controllerViewModel.singleProductionData.observeAsState() /* <- Film eller TVserie objekt av filmen/serien som matcher ID i variablen over*/

    // Trengs for å laste inn

    LaunchedEffect(productionID) {

        //controllerViewModel.nullifySingleProductionData()
        if (productionID.isNotEmpty()) {
            Log.d("Test1", production.toString())  // Før kall
            Log.d("TestType", "Production type is: '$productionType'")  // Logg `productionType`

            controllerViewModel.nullifySingleProductionData()

            when (productionType) {
                "Movie" -> {
                    Log.d("Test1111", production.toString())
                    controllerViewModel.getMovieById(productionID)

                }
                "TVShow" -> {
                    Log.d("Test2222", productionType)
                    controllerViewModel.getTVShowById(productionID)
                }
                else -> {
                    // Hvis productionType ikke er Movie eller TVShow, nullifiser data
                    // Hvis noe går galt, så ønsker vi ikke å vise siste production objekt,
                    // men kanskje en error side eller noe slikt?
                    // Passer på at  if (production == null) trigges i LazyColumn
                    controllerViewModel.nullifySingleProductionData()
                }
            }

            //productionID = "";
        }
    }


    val handleScoreChange: (score: Int) -> Unit = { score ->
        userScore = score
        //Kontroller kall her:
    }

    val handleUserListCategoryChange: (userListCategory: ListOptions?) -> Unit = {userListCategory ->
        memberOfUserList = userListCategory
        //Kontroller kall her:
    }

    val handleLikeClick: (like: String) -> Unit = { like ->
        Log.d("Temp", "Temp")
        //Kontroller kall her:
    }

    val handleProductionClick: (productionID: String, productionType: String)
    -> Unit = {productionID, productionType ->
        navController.navigate(Screen.ProductionScreen.withArguments(productionID, productionType))
    }

    val handleProfilePictureClick: (profileID: String) -> Unit = {profileID ->
        navController.navigate(Screen.ProfileScreen.withArguments(productionID))
    }

    val handleReviewClick: (reviewID: String) -> Unit = {reviewID ->
        navController.navigate(Screen.ReviewScreen.withArguments(reviewID))
    }


    //Graphics:
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsBackgroundHeight + 20.dp,
            bottom = bottomNavBarHeight +20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        //Top info
        if (production == null) {
            item {
                Text(text = "test")
            }
        } else {
            item {
                production?.let { production ->
                    ImageAndName(
                        production = production,
                    )

                    Log.d("Tester3", production.title)
                }
            }

            item {
                LineDevider()
            }

            //User score and list option
            item {
                ListInfo(
                    memberOfUserList = memberOfUserList,
                    userScore = userScore,
                    handleScoreChange = { score ->
                        handleScoreChange(score)
                    },
                    handleUserListChange = {listOption ->
                        handleUserListCategoryChange(listOption)
                    }

                )
            }

            item {
                LineDevider()
            }


            //Stat stection
            item {
                production?.let { production ->
                    statsSection(
                        production = production,
                    )
                }
            }

            item {
                LineDevider()
            }

            item{
                production?.let { production ->
                    GenreSection(
                        production = production
                    )
                }
            }

            production?.let { production ->
                //Youtube trailer embed
                item {
                    val trailerUrl = production.trailerUrl
                    if (trailerUrl != null && trailerUrl.lowercase().contains("youtube")) {
                        YouTubeVideoEmbed(
                            videoUrl = ExtractYoutubeVideoIDFromUrl(trailerUrl),
                            lifeCycleOwner = LocalLifecycleOwner.current
                        )
                    }
                }
            }

            //Project desciption
            item {
                production?.let { production ->
                    productionDescription(
                        description = production.description
                    )
                }
            }

            //Project desciption
            item {
                production?.let { production ->
                    ActorsSection(
                        production = production
                    )
                }
            }

            //Project reviews
            item {
                production?.let { production ->
                    ReviewsSection(
                        reviewList = listOfReviews,
                        header = "Reviews for " + production.title,
                        handleProductionImageClick = handleProductionClick,
                        handleLikeClick =  { reviewID ->
                            handleLikeClick(reviewID)
                        },
                        handleProfilePictureClick = handleProfilePictureClick,
                        handleReviewClick = handleReviewClick
                    )
                }
            }
        }

    }
}

@Composable
fun ImageAndName(
    production: Production,
    customModefier: Modifier = Modifier
){

    //Variables

    val formattedDate = "test"
    //val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(production?.releaseDate?.time

    //graphics
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = customModefier
            .fillMaxWidth()
    ) {
        //Image
        if (production != null) {
            ShowImage(
                imageID = production.posterUrl,
                imageDescription = production.title,
            )
        }

        //Title
        if (production != null) {
            Text(
                text = production.title,
                fontFamily = fontFamily,
                fontSize = headerSize * 1.3,
                fontWeight = weightBold,
                textAlign = TextAlign.Center,
                color = White,
                modifier = Modifier
                    .padding(top= 10.dp)
            )
        }
        //Date
        Text(
            text = formattedDate,
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightRegular,
            textAlign = TextAlign.Center,
            color = White
        )

    }
}

@Composable
fun statsSection(
    production: Production,
){

    var productionAsType = if(production != null && production.type == "Movie") {production as Movie} else {production as TVShow}
    val formattedScore = if(production.rating != null){production.rating as Int} else {0}

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ){
        //number of Seasons
        if(productionAsType is TVShow) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Number of seasons:",
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightRegular,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 5.dp)
                )
                Text(
                    text = productionAsType.seasons.size.toString(),
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightRegular,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding()
                )
            }
        }
        //Comunity score
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Comunity score:",
                fontFamily = fontFamily,
                fontSize = headerSize,
                fontWeight = weightRegular,
                color = White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 5.dp)
            )
            RatingsGraphics(
                score = formattedScore,
                sizeMultiplier = 1.5f
            )

        }

        //MovieLengt
        if(productionAsType is Movie){
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Runtime:",
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightRegular,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 5.dp)
                )
                Text(
                    text = productionAsType.lengthMinutes.toString() + " minutes",
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightRegular,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding()
                )
            }
            //number of episodes
            if(productionAsType is TVShow) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Number of episodes:",
                        fontFamily = fontFamily,
                        fontSize = headerSize,
                        fontWeight = weightRegular,
                        color = White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 5.dp)
                    )
                    Text(
                        text = productionAsType.episodes.size.toString(),
                        fontFamily = fontFamily,
                        fontSize = headerSize,
                        fontWeight = weightRegular,
                        color = White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding()
                    )
                }
            }
        }


    }

}


@Composable
fun ListInfo (
    memberOfUserList: ListOptions?,
    userScore: Int?,
    handleScoreChange: (Int) -> (Unit),
    handleUserListChange: (ListOptions) -> (Unit)
){

    var dropDownExpanded by remember { mutableStateOf(false) }
    var dropDownButtonText by remember { mutableStateOf(GenerateListOptionName(memberOfUserList))}
    var userScoreFormatted by remember { if (userScore != null) {mutableIntStateOf(userScore)} else {mutableIntStateOf(0) }}
    var ratingsSliderIsVisible by remember { mutableStateOf(false) }

    var handleListCategoryChange: (listOption: ListOptions) -> Unit = {
        dropDownExpanded = false
        dropDownButtonText = GenerateListOptionName(it)
        handleUserListChange(it)
    }

    val handleScoreButtonClick: () -> Unit = {
        ratingsSliderIsVisible = !ratingsSliderIsVisible
    }

    val handleScoreSliderChange: (score: Int) -> Unit = { score ->
        userScoreFormatted = score
        ratingsSliderIsVisible = false
        handleScoreChange(score)
    }

    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        //If production not in users list
        if (memberOfUserList != null && memberOfUserList != ListOptions.REMOVEFROMLIST){
            //User rating

            RatingSlider(
                visible = ratingsSliderIsVisible,
                rating = userScoreFormatted,
                onValueChangeFinished = { score ->
                    handleScoreSliderChange(score)
                }
            )

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                            handleScoreButtonClick()
                    }
            ) {
                Text(
                    text = "Your score:",
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightRegular,
                    color = Purple,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                )


                RatingsGraphics(
                    score = userScoreFormatted,
                    sizeMultiplier = 1.5f,
                    loggedInUsersScore = true,
                    color = Purple
                )



            }
        }

        //Add to or edit list button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
                .padding(vertical = 5.dp)
                .background(
                    color = DarkPurple,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable {
                    //dropdown menu button logic
                    dropDownExpanded = true
                }
        ) {
            //BUTTON TEXT
            Text(
                text = "$dropDownButtonText",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = White,
                textAlign = TextAlign.Center
            )
            //DROP DOWN MENU
            DropdownMenu(
                expanded = dropDownExpanded,
                onDismissRequest = { dropDownExpanded = false },
                offset = DpOffset(x = 0.dp, y = 0.dp),
                modifier = Modifier
                    .background(color = DarkPurple)
                    .width(150.dp)
            ) {
                ListOptions.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = GenerateListOptionName(option),
                                    fontSize = headerSize,
                                    fontWeight = weightBold,
                                    fontFamily = fontFamily,
                                    color = White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        },
                        onClick = {
                            //On click logic for dropdown menu
                            handleListCategoryChange(option)
                        })
                }
            }
        }

    }

}

@Composable
fun productionDescription(
    description: String,
    modifier: Modifier = Modifier
){
  Text(
      text = description,
      fontFamily = fontFamily,
      fontSize = paragraphSize,
      fontWeight = weightRegular,
      textAlign = TextAlign.Start,
      color = White,
      modifier = modifier
          .padding(horizontal = horizontalPadding)
  )
}

fun ExtractYoutubeVideoIDFromUrl ( url: String): String{
    return url.substringAfter("?v=")
}

@Composable
fun GenreSection(
    production: Production,
    modifier: Modifier = Modifier,
    boxColor: Color = Gray
){
    if (production!!.genre.size > 0){
        LazyRow (
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .fillMaxWidth()
        ){
            items(production.genre) { genre ->
                Box(
                    modifier = Modifier
                        .background(boxColor, RoundedCornerShape(5.dp))
                        .padding(horizontal = 0.dp, vertical = 5.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = genre,
                        fontFamily = fontFamily,
                        fontSize = paragraphSize,
                        fontWeight = weightRegular,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )

                }
            }
        }
    }
}


@Composable
fun ActorsSection(
    production: Production,
    modifier: Modifier = Modifier,
    boxColor: Color = Gray
){
    if (production.genre.size > 0){
        Column(
            modifier = modifier
                .fillMaxWidth()
        ){
            Text(
                text = "Cast",
                fontFamily = fontFamily,
                fontSize = headerSize,
                fontWeight = weightRegular,
                textAlign = TextAlign.Start,
                color = White,
                modifier = Modifier
                    .padding(start = horizontalPadding, bottom = 10.dp)
            )

            LazyRow (
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(10.dp),

                ){
                items(production.actors) {actor ->
                    Box(
                        modifier = Modifier
                            .background(boxColor, RoundedCornerShape(5.dp))
                            .padding(horizontal = 0.dp, vertical = 5.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            text = actor,
                            fontFamily = fontFamily,
                            fontSize = paragraphSize,
                            fontWeight = weightRegular,
                            textAlign = TextAlign.Center,
                            color = White,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )

                    }
                }
            }
        }
    }
}