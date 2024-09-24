package com.movielist.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movielist.R
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.White
import com.movielist.ui.theme.*

@Composable
fun FrontPage () {
    //Background color for page
    Background(
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CurrentlyWatchingCard(
            imageId = R.drawable.silo,
            imageDescription = "No Image",
            title = "Chernobyl",
            showLenght = 12,
            episodesWatched = 5)
    }

}

@Composable
fun CurrentlyWatchingScroller () {
    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
    }
}

@Composable
fun CurrentlyWatchingCard (
    imageId: Int,
    imageDescription: String,
    title: String,
    showLenght: Int,
    episodesWatched: Int,
    modifier: Modifier = Modifier

    ) {

    var watchedEpisodesCount: Int by remember {
        mutableIntStateOf(episodesWatched)
    }

    var buttonText by remember {
        mutableStateOf(generateButtonText(episodesWatched, showLenght))
    }



    //Card container
    Card (
        modifier = modifier
            .fillMaxWidth(.9f),
        shape = RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Gray)

    ){
        //card content
        Column(modifier = Modifier
            .height(260.dp)
            .padding(horizontal = 20.dp, vertical = 5.dp))
        {
            //Main image
            Image(
                painter = painterResource(id = imageId),
                contentDescription = imageDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp))

            //Content under image
            Column(modifier = Modifier
                .fillMaxSize()
                )
            {
                //Title and episodes watched
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    //Title
                    Text(
                        title,
                        style = TextStyle(
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = weightRegular
                            )
                    )
                    //Episodes watched
                    Text (
                        "Ep $watchedEpisodesCount of $showLenght",
                        style = TextStyle(
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = weightLight
                        )
                    )
                }

                //Progress bar
                ProgressBar(currentNumber = watchedEpisodesCount, endNumber = showLenght)

                //Mark as watched button
                Button(
                    onClick = {
                        //Button onclick function
                        if ( watchedEpisodesCount < showLenght) {
                            watchedEpisodesCount++
                        }

                        buttonText = generateButtonText(watchedEpisodesCount, showLenght)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Purple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                ) {
                    //Button text
                    Text(
                        buttonText,
                        fontSize = 16.sp,
                        fontWeight = weightRegular,
                        color = DarkGray
                    )
                }
            }

        }
    }
}

//Utility Functions
fun generateButtonText(episodesWatched: Int, showLenght: Int): String {
    if (episodesWatched+1 == showLenght) {
        return "Mark as completed"
    } else if ( episodesWatched == showLenght){
        return "Show Completed"
    }
    else {
        return "Mark episode ${episodesWatched + 1} as watched"
    }

}