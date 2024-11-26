package com.movielist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.movielist.Screen
import com.movielist.composables.LineDevider
import com.movielist.composables.ProductionImage
import com.movielist.composables.RatingSlider
import com.movielist.composables.RatingsGraphics
import com.movielist.controller.ControllerViewModel
import com.movielist.model.Production
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalTextFieldColors
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.verticalPadding
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WriteReviewScreen(controllerViewModel: ControllerViewModel, navController: NavController, productionID: String?, productionType: String?){

    val loggedInUser by controllerViewModel.loggedInUser.collectAsState()
    val date = Calendar.getInstance().time
    var errorMessage by remember { mutableStateOf("") }

    val production by controllerViewModel.singleProductionData.collectAsState()
    val updatedProductionID by remember { mutableStateOf(productionID.orEmpty()) }

    //Review content
    var reviewText by remember { mutableStateOf("") }
    var reviewScore by remember { mutableIntStateOf(0) }


    // Håndter produksjonsdata basert på productionType
    if (updatedProductionID.isNotEmpty()) {
        // Nullifiser produksjonsdata før henting
        controllerViewModel.nullifySingleProductionData()
        when (productionType) {
            "Movie" -> {
                controllerViewModel.setMovieById(updatedProductionID)
            }
            "TVShow" -> {
                controllerViewModel.setTVShowById(updatedProductionID)
            }
            else -> {
                // Hvis productionType ikke er Movie eller TVShow, nullifiser data
                // Hvis noe går galt, så ønsker vi ikke å vise siste production objekt,
                // men kanskje en error side eller noe slikt?
                // Passer på at  if (production == null) trigges i LazyColumn
                controllerViewModel.nullifySingleProductionData()

            }
        }
    }
    LaunchedEffect(production) {
        // Venter med å hente reviews til production er oppdatert

        if (productionType != null) {
            controllerViewModel.getReviewByProduction(updatedProductionID, productionType)
        }
    }

    val handleReviewTextUpdate: (updatedReviewText: String) -> Unit = {updatedReviewText ->
        reviewText = updatedReviewText
    }

    val handleReviewScoreUpdate: (updatedReviewScore: Int) -> Unit = {updatedReviewScore ->
        reviewScore = updatedReviewScore
    }

    val handleSubmittReview: () -> Unit = {
        if (reviewText.length < 5){
            errorMessage = "Review must be at least 5 characters long!"
        } else {
            //Kontroller funksjon her

            navController.navigate(Screen.ProductionScreen.withArguments(updatedProductionID, productionType.toString()))
        }

    }


    //Grafikk
    LazyColumn(
        contentPadding = PaddingValues(
            top = topPhoneIconsAndNavBarBackgroundHeight + 20.dp,
            bottom = bottomNavBarHeight +20.dp,
            start = verticalPadding,
            end = verticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {

            if (production != null) {
                production?.let { production ->
                    WriteReviewScreenImageAndName(
                        production = production,
                    )
                }
            } else {
                Text(
                    text = "Loading...",
                    fontFamily = fontFamily,
                    fontSize = headerSize,
                    fontWeight = weightRegular,
                    textAlign = TextAlign.Center,
                    color = LocalColor.current.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        item {
            LineDevider()
        }

        item {
            WriteReviewCurrentDate(
                date = date
            )
        }

        item {
            LineDevider()
        }

        item {
            WriteReview(
                handleReviewTextUpdate = handleReviewTextUpdate
            )
        }

        item {
            LineDevider()
        }

        item {
            ReviewRating(
                score = reviewScore,
                handleReviewScoreUpdate = handleReviewScoreUpdate
            )
        }

        item {
            LineDevider()
        }

        item {
            WriteReviewScreenErrorMessage(message = errorMessage)
        }

        item {
            SubmittReviewButton (
                handleSubmitReview = handleSubmittReview
            )
        }
    }
}



@Composable
fun WriteReviewScreenImageAndName(
    production: Production,
    customModefier: Modifier = Modifier
){

    val formattedDate = SimpleDateFormat("yyyy", Locale.getDefault()).format(production.releaseDate.time)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = customModefier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth(.8f)
        ){
            Text(
                text = production.title,
                fontFamily = fontFamily,
                fontSize = headerSize * 1.3,
                fontWeight = weightBold,
                textAlign = TextAlign.Start,
                color = LocalColor.current.secondary,
                modifier = Modifier
                    .padding(end = 6.dp)
            )
            //Date
            Text(
                text = "(${formattedDate})",
                fontFamily = fontFamily,
                fontSize = headerSize,
                fontWeight = weightRegular,
                textAlign = TextAlign.Start,
                color = LocalColor.current.secondary
            )
        }
        //Image
        ProductionImage(
            imageID = production.posterUrl,
            imageDescription = production.title,
            sizeMultiplier = .4f
        )

    }
}

@Composable
fun WriteReviewCurrentDate(
    date: Date = Calendar.getInstance().time
) {

    val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = "Date: ",
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightBold,
            textAlign = TextAlign.End,
            color = LocalColor.current.secondary,
            modifier = Modifier
                .padding(end = 6.dp)
        )
        //Date
        Text(
            text = "${formattedDate}",
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightRegular,
            textAlign = TextAlign.End,
            color = LocalColor.current.secondary
        )
    }
}

@Composable
fun WriteReview (
    handleReviewTextUpdate: (updatedReviewText: String) -> Unit
){

    var reviewText by remember { mutableStateOf("") }

    //TextField
    OutlinedTextField(
        value = reviewText,
        onValueChange = {
            reviewText = it
            handleReviewTextUpdate(it)
        },
        singleLine = false,
        colors = LocalTextFieldColors.current.textFieldColors,
        textStyle = TextStyle(
            fontSize = headerSize,
            fontFamily = fontFamily,
            fontWeight = weightRegular,
            color = LocalColor.current.secondary,
        ),
        label = {
            Text(
                text = "Write review...",
                fontSize = headerSize,
                fontFamily = fontFamily,
                fontWeight = weightBold,
                color = LocalColor.current.secondary,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
    )
}

@Composable
fun ReviewRating(
    score: Int,
    handleReviewScoreUpdate: (newScore: Int) -> Unit
){
    var scoreSliderVisible by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = "Rating: ",
            fontFamily = fontFamily,
            fontSize = paragraphSize,
            fontWeight = weightBold,
            textAlign = TextAlign.Start,
            color = LocalColor.current.secondary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 6.dp)
        )

        RatingsGraphics(
            color = LocalColor.current.primary,
            score = score,
            loggedInUsersScore = true,
            sizeMultiplier = 1.8f,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable {
                    scoreSliderVisible = !scoreSliderVisible
                }
        )
    }

    RatingSlider(
        null,
        rating = score,
        visible = scoreSliderVisible,
        onValueChangeFinished = { score ->
            scoreSliderVisible = false
            handleReviewScoreUpdate(score)
        }
    )


}

@Composable
fun SubmittReviewButton (
    handleSubmitReview: () -> Unit
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color = LocalColor.current.primary, shape = RoundedCornerShape(5.dp))
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .fillMaxWidth()
            .clickable { handleSubmitReview() }
    ) {
        Text(
            text = "Submit review",
            fontSize = headerSize,
            fontWeight = weightBold,
            fontFamily = fontFamily,
            color = LocalColor.current.background,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun WriteReviewScreenErrorMessage(
    message: String
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = message,
            fontSize = headerSize,
            fontWeight = weightBold,
            fontFamily = fontFamily,
            color = LocalColor.current.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}



