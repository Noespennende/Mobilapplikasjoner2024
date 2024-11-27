package com.movielist.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.LikeButton
import com.movielist.composables.LineDevider
import com.movielist.composables.ProfileImage
import com.movielist.composables.RatingsGraphics
import com.movielist.composables.ProductionImage
import com.movielist.controller.ControllerViewModel
import com.movielist.model.ReviewDTO
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalConstraints
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topNavBarContentStart
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import java.util.Calendar

@Composable
fun ReviewScreen (controllerViewModel: ControllerViewModel, navController: NavController, reviewID: String?) {

    //val reviewID by remember { mutableStateOf(reviewID) } /* <- IDen til reviewen som skal hentes ut av kontroller */
    //val review by remember { mutableStateOf(null) } /* <- Denne må settes til Review objektet som matcher IDen over */

    //val productionType by remember { mutableStateOf("Movie") }
    //val productionID by remember { mutableStateOf("") }

    val reviewDTO by controllerViewModel.singleReviewDTOData.collectAsState()
    val production by controllerViewModel.singleProductionData.collectAsState() /* <- Film eller TVserie objekt av filmen/serien som matcher ID i variablen over*/

    LaunchedEffect(reviewID) {
        controllerViewModel.loadReviewData(reviewID)
    }

    val HandleLikeClick: () -> Unit = {
        //Kontroller funksjon for like her:
    }
    val HandleUserClick: (userID: String) -> (Unit) = { userID ->
        navController.navigate(Screen.ProfileScreen.withArguments(userID))
    }
    val HandleProductionClick: (showID: String) -> (Unit) = { showID ->
        navController.navigate((Screen.ProductionScreen.withArguments(showID)))
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = LocalConstraints.current.mainContentHorizontalPadding,
            vertical = topNavBarContentStart + 10.dp
        )
    ) {
        item {
            reviewDTO?.let {
                Review(
                    reviewDTO = reviewDTO,
                    handleLikeClick = HandleLikeClick,
                    handleUserClick = HandleUserClick,
                    handleProductionClick = HandleProductionClick
                )
            }
        }

    }
}


@Composable
fun Review(
    reviewDTO: ReviewDTO?,
    handleLikeClick: () -> Unit,
    handleUserClick: (String) -> Unit,
    handleProductionClick: (String) -> Unit
) {
    var likes by remember { mutableIntStateOf(reviewDTO?.likes ?: 0) }
    //Main container
    Column {
        if (reviewDTO != null) {
            ProductionImage(
                imageID = reviewDTO.productionPosterUrl,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .clickable {
                        handleProductionClick(reviewDTO.productionID)
                    }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                //Review header, score and body
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier
                        .padding(
                            start = 5.dp
                        )
                ) {
                    Row(
                    ) {
                        //Review Header and user section
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        {
                            //Review header and score
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier
                                    .fillMaxWidth(.5f)
                            )
                            {
                                //Header
                                Text(
                                    text = "${reviewDTO.productionTitle} (${
                                        reviewDTO.productionReleaseDate.get(
                                            Calendar.YEAR
                                        )
                                    })",
                                    fontSize = paragraphSize,
                                    fontFamily = fontFamily,
                                    fontWeight = weightBold,
                                    color = LocalColor.current.secondary
                                )
                                //Score
                                RatingsGraphics(
                                    reviewDTO.score
                                )
                            }

                            //Userinfo and review date
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        handleUserClick(reviewDTO.reviewerID)
                                    }
                            ) {
                                //Username and review date
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(3.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    //Username
                                    Text(
                                        text = reviewDTO.reviewerUserName,
                                        fontSize = paragraphSize,
                                        fontFamily = fontFamily,
                                        fontWeight = weightBold,
                                        color = LocalColor.current.secondary
                                    )
                                    //review date
                                    Text(
                                        text = "${reviewDTO.postDate.get(Calendar.DATE)}/${
                                            reviewDTO.postDate.get(Calendar.MONTH)}/" +
                                                "${reviewDTO.postDate.get(Calendar.YEAR)}",
                                        fontSize = paragraphSize,
                                        fontFamily = fontFamily,
                                        fontWeight = weightRegular,
                                        color = LocalColor.current.quinary
                                    )
                                }

                                //profile picture
                                Box(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                ){
                                    ProfileImage(
                                        imageID = reviewDTO.reviewerProfileImage,
                                        userName = reviewDTO.reviewerUserName
                                    )
                                }

                            }

                        }
                    }

                    //Body
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(top = 10.dp)
                    )
                    {
                        Text(
                            text = reviewDTO.reviewBody,
                            fontSize = paragraphSize,
                            fontFamily = fontFamily,
                            fontWeight = weightRegular,
                            color = LocalColor.current.quinary,
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                        )


                    }

                }

            }

            Text(
                text = "${likes} likes",
                fontSize = paragraphSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = LocalColor.current.secondary,
                modifier = Modifier
                    .align(Alignment.End)
            )

            LineDevider()

            //LIKE BUTTON
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LikeButton(
                    handleLikeClick = { liked ->
                        if (liked){
                            likes += 1
                        } else {
                            likes -= 1
                        }
                        handleLikeClick()
                    }
                )
            }
            LineDevider()
        }
    }
}