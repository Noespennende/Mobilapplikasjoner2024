package com.movielist.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.movielist.composables.GenerateListOptionName
import com.movielist.composables.LineDevider
import com.movielist.composables.ScoreGraphics
import com.movielist.composables.ShowImage
import com.movielist.composables.YouTubeVideoEmbed
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ListOptions
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.Review
import com.movielist.model.TVShow
import com.movielist.ui.theme.DarkPurple
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ProductionScreen (controllerViewModel: ControllerViewModel){
    /*
    Temp Kode Slett dette
     */
    var movie = Movie(
        title = "Silo",
        genre = listOf("Action", "Mystery"),
        rating = 8,
        releaseDate = Calendar.getInstance(),
        actors = listOf("Willem Dafoe", "Robert Patterson"),
        reviews = emptyList<String>(),
        posterUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTKyUqVUU-pLcxDphlKjZHxWxpgbhicbbX_Qg&s",
        lengthMinutes = 120,
        trailerUrl = "https://www.youtube.com/watch?v=8ZYhuvIv1pA",
        description = "\n" +
            "What is Lorem Ipsum?\n" +
            "\n" +
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
            "Why do we use it?\n" +
            "\n" +
            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
            "\n" +
            "Where does it come from?\n" +
            "\n" +
            "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
            "\n" +
            "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.\n")
    /*
    Temp Kode Slett Over
     */

    //Variables
    var production by remember { mutableStateOf(movie) }
    var memberOfUserList by remember { mutableStateOf(ListOptions.WATCHING) }
    var userScore by remember { mutableIntStateOf(8) }
    var listOfReviews by remember { mutableStateOf(mutableListOf<Review>()) }

    //Graphics:
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsBackgroundHeight + 20.dp,
            bottom = bottomNavBarHeight +20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ){
        //Top info
        item {
            ImageAndName(
                production = production,
            )
        }

        item {
            LineDevider()
        }

        item {
            ListInfo(
                memberOfUserList = memberOfUserList,
                userScore = userScore
            )
        }

        item {
            LineDevider()
        }


        //Stat stection
        item {
            statsSection(
                production = production,
            )
        }

        item {
            LineDevider()
        }

        item {
            if (production.trailerUrl != null
                && production.trailerUrl.lowercase().contains("youtube")
                ){
                YouTubeVideoEmbed(
                    videoUrl = ExtractYoutubeVideoIDFromUrl(production.trailerUrl),
                    lifeCycleOwner = LocalLifecycleOwner.current
                )
            }
        }

        item {
            productionDescription(
                description = production.description
            )
        }

        item {
            ReviewsSection(
                reviewList = listOfReviews,
                header = "Reviews for " + production.title
            )
        }

    }
}

@Composable
fun ImageAndName(
    production: Production,
    customModefier: Modifier = Modifier
){

    //Variables

    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(production.releaseDate.time)
    //graphics
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = customModefier
            .fillMaxWidth()
    ) {
        //Image
        ShowImage(
            imageID = production.posterUrl,
            imageDescription = production.title,
        )

        //Title
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
    production: Production?,
){

    var productionAsType = if(production != null && production.type == "Movie") {production as Movie} else {production as TVShow}
    val formattedScore = if(production.rating != null){production.rating as Int} else {0}

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ){
        //number of Seasons
        if(productionAsType is TVShow) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
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
            ScoreGraphics(
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
){

    var dropDownExpanded by remember { mutableStateOf(false) }
    var dropDownButtonText by remember { mutableStateOf(GenerateListOptionName(memberOfUserList))}
    var userScoreFormatted by remember { if (userScore != null) {mutableIntStateOf(userScore)} else {mutableIntStateOf(0) }}

    var handleListCategoryChange: (listOption: ListOptions) -> Unit = {
        dropDownExpanded = false
        dropDownButtonText = GenerateListOptionName(it)
        //Gjør Kontroller kall her:

    }
    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //If production not in users list
        if (memberOfUserList != null && memberOfUserList != ListOptions.REMOVEFROMLIST){
            //User rating
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
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
                ScoreGraphics(
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