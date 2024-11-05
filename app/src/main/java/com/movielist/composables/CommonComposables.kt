package com.movielist.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.movielist.R
import com.movielist.model.ListItem
import com.movielist.model.ListOptions
import com.movielist.model.Production
import com.movielist.model.ShowSortOptions
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkGrayTransparent
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.darkWhite
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.showImageHeight
import com.movielist.ui.theme.showImageWith
import com.movielist.ui.theme.sliderColors
import com.movielist.ui.theme.topPhoneIconsBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun Background () {
    Box(
        modifier = Modifier
            .background(DarkGray)
            .fillMaxSize()
    )
}


@Composable
fun ProgressBar (
    currentNumber: Int,
    endNumber: Int,
    lenght: Dp = 50.dp,
    foregroundColor: Color = Purple,
    backgroundColor: Color = DarkPurple,
    strokeWith: Float = 20f,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
)
{
    var percentage: Float = currentNumber.toFloat()/endNumber.toFloat()

    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercentage = animateFloatAsState(
        targetValue = if(animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    //Progress bar graphics
    //ProgressBarContainer
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .padding(top = 5.dp, bottom = 5.dp)
    ){
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )  {
            //drawing the progress bar
            val lineStart = 4.dp.toPx()
            val lineEnd = size.width * curPercentage.value
            val lineY = size.height/2
            //background line
            drawLine(
                color = backgroundColor,
                start = Offset(x = lineStart, y= lineY),
                end = Offset(x= size.width , y= lineY),
                strokeWidth = strokeWith,
                StrokeCap.Round,
            )
            //foreground line
            drawLine(
                color = foregroundColor,
                start = Offset(x = lineStart, y= lineY),
                end = Offset(x= lineEnd , y= lineY),
                strokeWidth = strokeWith,
                StrokeCap.Round,
            )
        }

    }

}

@Composable
fun LineDevider (
    color: Color = Gray,
    strokeWith: Float = 5f,
)
{
    //Progress bar graphics
    //ProgressBarContainer
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .padding(top = 5.dp, bottom = 5.dp)
    ){
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )  {
            //Line parameters
            val lineStart = 4.dp.toPx()
            val lineEnd = size.width
            val lineY = size.height/2
            //line
            drawLine(
                color = color,
                start = Offset(x = lineStart, y= lineY),
                end = Offset(x= lineEnd , y= lineY),
                strokeWidth = strokeWith,
                StrokeCap.Round,
            )
        }

    }

}

@Composable
fun TopMobileIconsBackground (
    color: Color = DarkGrayTransparent,
) {
    Box(
        modifier = Modifier
            .background(color)
            .fillMaxWidth()
            .height(topPhoneIconsBackgroundHeight)
    )
}



@Composable
fun ShowImage(
    imageID: String? = null, // Endret til nullable for å håndtere URL-er
    placeholderID: Int = R.drawable.noimage,
    imageDescription: String = "Image not available",
    sizeMultiplier: Float = 1.0f
) {
    //val showImageWidth = 100.dp // Juster dette til ønsket bredde
    //val showImageHeight = 150.dp // Juster dette til ønsket høyde

    if (imageID != null) {
        // Last inn bildet fra URL
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageID)
                .placeholder(placeholderID) // placeholder når bildet lastes
                .error(placeholderID) // vis samme placeholder ved feil
                .build(),
            contentDescription = imageDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(showImageWith * sizeMultiplier)
                .height(showImageHeight * sizeMultiplier)
                .clip(RoundedCornerShape(5.dp))
        )
    } else {
        // Vis fallback-bildet
        Image(
            painter = painterResource(id = placeholderID),
            contentDescription = imageDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(showImageWith * sizeMultiplier)
                .height(showImageHeight * sizeMultiplier)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}

@Composable
fun ProfileImage(
    imageID: Int?,
    userName: String,
    sizeMultiplier: Float = 1.0f
) {

    val placeholderID = R.drawable.profilepicture

    if (imageID != null) {
        // Last inn bildet fra URL
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageID)
                .placeholder(placeholderID) // placeholder når bildet lastes
                .error(placeholderID) // vis samme placeholder ved feil
                .build(),
            contentDescription = userName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size((30*sizeMultiplier).dp)
                .clip(CircleShape)
        )
    } else {
        // Vis fallback-bildet
        Image(
            painter = painterResource(id = placeholderID),
            contentDescription = userName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size((30*sizeMultiplier).dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun ScoreGraphics(
    score: Int,
    sizeMultiplier: Float = 1.0f,
    color: Color = White,
    loggedInUsersScore: Boolean = false
) {
    //Generate stars if score is greater than 0
    if (score > 0){
        var scoreNumber: Int = score
        if (scoreNumber > 10) { scoreNumber = 10}
        //Graphics
        Row (
        ) {
            //Generate full stars
            for (i in 1..score) {
                if (i % 2 == 0) {
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Star",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(color),
                        modifier = Modifier
                            .size((11*sizeMultiplier).dp)
                    )
                }
            }
            //add half star at the end for half scores
            if (score % 2 != 0)  {
                Image(
                    painter = painterResource(id = R.drawable.half_star),
                    contentDescription = "Star",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier
                        .size((11*sizeMultiplier).dp)
                )
            }

        }
    } else if (loggedInUsersScore) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_star),
                contentDescription = "Star",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(darkWhite),
                modifier = Modifier
                    .size((11*sizeMultiplier).dp)
            )
            Text(
                text = "Unrated",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = 16.sp,
                color = darkWhite
            )
        }

    } else {
        Text(
            text = "No score",
            fontFamily = fontFamily,
            fontWeight = weightRegular,
            fontSize = 12.sp,
            color = darkWhite
        )
    }
}

@Composable
fun LikeButton (
    sizeMultiplier: Float = 1f
) {
    var buttonText by remember {
        mutableStateOf("Like")
    }

    var buttonColor by remember {
        mutableStateOf(LightGray)
    }

    var buttonClicked by remember {
        mutableStateOf(false)
    }

    var heartIcon by remember {
        mutableStateOf(R.drawable.heart_hollow)
    }

    Button(
        onClick = {
            if (buttonClicked) {
                buttonColor = LightGray
                buttonClicked = false
                buttonText = "Like"
                heartIcon = R.drawable.heart_hollow

            } else {
                buttonColor = Purple
                buttonClicked = true
                buttonText = "Liked"
                heartIcon = R.drawable.heart_filled
            }
        },
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .height((20*sizeMultiplier).dp)
            .wrapContentWidth()
    )
    {
        Row(
        ) {
            //Button content
            Row (
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            )
            {
                Image(
                    painter = painterResource(id = heartIcon),
                    contentDescription = "heart icon",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(buttonColor),
                    modifier = Modifier
                        .size((15*sizeMultiplier).dp)
                        .align(alignment = Alignment.CenterVertically)
                )

                Text(
                    text = buttonText,
                    fontSize = (15*sizeMultiplier).sp,
                    fontFamily = fontFamily,
                    fontWeight = weightBold,
                    color = buttonColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

        }
    }
}



@Composable
fun ProductionListSidesroller (
    header: String,
    listOfShows: List<Production>,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        //Header
        Text(
            header,
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightBold,
            color = White,
            modifier = textModifier
        )
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = horizontalPadding, end = horizontalPadding)
        ){
            items (listOfShows.size) {i ->
                ShowImage(
                    imageID = listOfShows[i].posterUrl,
                    imageDescription = listOfShows[i].title + " Poster"
                )
            }
        }

    }
}

@Composable
fun ListItemListSidesroller (
    header: String,
    listOfShows: List<ListItem>,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        //Header
        Text(
            header,
            fontFamily = fontFamily,
            fontSize = headerSize,
            fontWeight = weightBold,
            color = White,
            modifier = textModifier
        )
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = horizontalPadding, end = horizontalPadding)
        ){
            items (listOfShows.size) {i ->
                ShowImage(
                    imageID = listOfShows[i].production.posterUrl,
                    imageDescription = listOfShows[i].production.title + " Poster"
                )
            }
        }

    }
}


@Composable
fun RoundProgressBar (
    percentage: Float = 1f,
    fontSize: TextUnit = 28.sp,
    color: Color = Purple,
    strikeWith: Dp = 8.dp,
    radius: Dp = 100.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currentPercentage = animateFloatAsState(
        targetValue = if(animationPlayed) {percentage} else {0f},
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )

    LaunchedEffect( key1 = true) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius)
    ){
        Canvas(
            modifier = Modifier
                .size(radius)
        ){
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360* currentPercentage.value,
                useCenter = false,
                style = Stroke(strikeWith.toPx(), cap = strokeCap)
            )
        }
    }

}

@Composable
fun YouTubeVideoEmbed(
    videoUrl: String,
    lifeCycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
){
    AndroidView(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = horizontalPadding)
        ,
        factory = { context ->
            YouTubePlayerView(context = context).apply {
                lifeCycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object: AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoUrl, 0f)
                    }
                })
            }
        }
    )
}

@Composable
fun RatingSlider (
    score: Int = 0,
    visible: Boolean ,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (Int) -> Unit
){
    var scoreInput by remember { mutableIntStateOf(score) }

    if (visible){
        Popup (
            onDismissRequest = {
                onValueChangeFinished(score)
            },
            alignment = Alignment.Center,

        ) {
            //Outer paddding
            Box (
                modifier = Modifier
                    .padding(horizontal = 30.dp)
            ) {
                //Background box
                Box(
                    modifier = modifier
                        .background(Gray, shape = RoundedCornerShape(5.dp))
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                ){
                    //Content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        //Stars
                        ScoreGraphics(
                            score = scoreInput,
                            color = Purple,
                            loggedInUsersScore = true,
                            sizeMultiplier = 2f
                        )
                        //slider
                        Slider(
                            value = scoreInput.toFloat(),
                            onValueChange = {scoreInput = it.toInt()},
                            enabled = true,
                            valueRange = 0f..10f,
                            colors = sliderColors,
                            onValueChangeFinished = {
                                onValueChangeFinished(scoreInput)
                            }
                        )
                    }
                }
            }
        }
    }

}

fun GenerateShowSortOptionName (
    showSortOptions: ShowSortOptions
): String
{
    if(showSortOptions== ShowSortOptions.MOVIESANDSHOWS)
    {
        return "Movies & Shows"
    }
    else
    {
        return showSortOptions.toString().lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun GenerateListOptionName (
    listOption: ListOptions?
): String
{
    if(listOption == ListOptions.WANTTOWATCH)
    {
        return "Want to watch"
    }
    else if (listOption == null) {
        return "Add to library"
    }
    else if (listOption == ListOptions.REMOVEFROMLIST){
        return "Remove from list"
    }
    else
    {
        return listOption.toString().lowercase().replaceFirstChar { it.uppercase() }
    }
}