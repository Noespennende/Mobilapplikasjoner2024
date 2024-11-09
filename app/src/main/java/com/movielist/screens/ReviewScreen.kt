package com.movielist.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.LikeButton
import com.movielist.composables.LineDevider
import com.movielist.composables.ProfileImage
import com.movielist.composables.ScoreGraphics
import com.movielist.composables.ShowImage
import com.movielist.controller.ControllerViewModel
import com.movielist.model.Review
import com.movielist.ui.theme.White
import com.movielist.ui.theme.darkWhite
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import java.util.Calendar
import kotlin.random.Random

@Composable
fun ReviewScreen (controllerViewModel: ControllerViewModel, navController: NavController, reviewID: String?){

    val reviewID by remember { mutableStateOf(reviewID)} /* <- IDen til reviewen som skal hentes ut av kontroller */
    val review by remember { mutableStateOf(null) } /* <- Denne må settes til Review objektet som matcher IDen over */

    val HandleLikeClick: () -> Unit = {
        //Kontroller funksjon for like her:
    }
    val HandleUserClick: (userID: String) -> (Unit) = {userID ->
        navController.navigate(Screen.ListScreen.route)
    }
    val HandleProductionClick: (showID: String) -> (Unit) = {showID ->
        navController.navigate((Screen.ProductionScreen.withArguments(showID)))
    }


    LazyColumn (
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = topNavBarContentStart+ 10.dp)
    ){
        item {
            review?.let {
                Review(
                    review = it,
                    handleLikeClick = HandleLikeClick,
                    handleUserClick = HandleUserClick,
                    handleProductionClick = HandleProductionClick
                )
            }
        }

    }

}


@Composable
fun Review (
    review: Review,
    handleLikeClick: () -> Unit,
    handleUserClick: (String) -> Unit,
    handleProductionClick: (String) -> Unit
) {
    //Main container
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ShowImage(
                imageID = review.show.posterUrl,
                modifier = Modifier
                    .clickable {
                        handleProductionClick(review.show.imdbID)
                    }
            )
            //Review header, score and body
            Column (
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .padding(
                        start = 5.dp
                    )
            ) {
                Row (
                ){
                    //Review Header and user section
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    {
                        //Review header and score
                        Column (
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        )
                        {
                            //Header
                            Text(
                                text = "${review.show.title} (${review.show.releaseDate.get(Calendar.YEAR)})",
                                fontSize = paragraphSize,
                                fontFamily = fontFamily,
                                fontWeight = weightBold,
                                color = White
                            )
                            //Score
                            ScoreGraphics(
                                review.score
                            )
                        }

                        //Userinfo and review date
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clickable {
                                    handleUserClick(review.reviewer.id)
                                }
                        ){
                            //Username and review date
                            Column (
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                horizontalAlignment = Alignment.End
                            ){
                                //Username
                                Text(
                                    text = review.reviewer.userName,
                                    fontSize = paragraphSize,
                                    fontFamily = fontFamily,
                                    fontWeight = weightBold,
                                    color = White
                                )
                                //review date
                                Text(
                                    text = "${review.postDate.get(Calendar.DATE)}/${review.postDate.get(
                                        Calendar.MONTH)}/${review.postDate.get(Calendar.YEAR)}",
                                    fontSize = paragraphSize,
                                    fontFamily = fontFamily,
                                    fontWeight = weightRegular,
                                    color = darkWhite
                                )
                            }
                            //profile picture
                            ProfileImage(
                                imageID = review.reviewer.profileImageID,
                                userName = review.reviewer.userName
                            )
                        }

                    }
                }

                //Body
                Column (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                {
                    Text(
                        text = review.reviewBody,
                        fontSize = paragraphSize,
                        fontFamily = fontFamily,
                        fontWeight = weightRegular,
                        color = darkWhite,
                        modifier = Modifier
                            .fillMaxWidth(.8f)
                            .height(60.dp)
                    )

                    Text(
                        text = "${review.likes} likes",
                        fontSize = paragraphSize,
                        fontFamily = fontFamily,
                        fontWeight = weightBold,
                        color = White,
                    )

                }
            }
        }

        LineDevider()

        //LIKE BUTTON
        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            LikeButton(
                handleLikeClick = handleLikeClick
            )
        }
        LineDevider()
    }

}