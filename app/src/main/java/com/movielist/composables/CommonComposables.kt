package com.movielist.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
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
import com.movielist.model.ProductionType
import com.movielist.model.ShowSortOptions
import com.movielist.ui.theme.LocalColor
import com.movielist.ui.theme.LocalConstraints
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.isAppInDarkTheme
import com.movielist.ui.theme.paragraphSize
import com.movielist.ui.theme.showImageHeight
import com.movielist.ui.theme.showImageWith
import com.movielist.ui.theme.sliderColors
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightLight
import com.movielist.ui.theme.weightRegular
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun Background () {
    Box(
        modifier = Modifier
            .background(LocalColor.current.background)
            .fillMaxSize()
    )
}


@Composable
fun ProgressBar (
    currentNumber: Int,
    endNumber: Int,
    foregroundColor: Color = LocalColor.current.primary,
    backgroundColor: Color = if(isAppInDarkTheme())LocalColor.current.tertiary else LocalColor.current.primaryLight,
    strokeWith: Float = 20f,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    flip: Boolean = false
)
{
    var percentage: Float = currentNumber.toFloat()/endNumber.toFloat()

    if (percentage > 1f) {
        percentage = 1f
    }

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
            var lineY = size.height/2

            if (flip){
                val lineStart = Offset(x = size.width, y = lineY)
                val backgroundLineEnd = Offset(x = 0f, y = lineY)
                val foregroundLineEnd = Offset(x = size.width * (1 - curPercentage.value), y = lineY)


                //background line
                drawLine(
                    color = backgroundColor,
                    start = lineStart,
                    end = backgroundLineEnd,
                    strokeWidth = strokeWith,
                    StrokeCap.Round,
                )
                //foreground line
                drawLine(
                    color = foregroundColor,
                    start = lineStart,
                    end = foregroundLineEnd,
                    strokeWidth = strokeWith,
                    StrokeCap.Round,
                )
            } else {
                val lineStart = Offset(x = 0f, y = lineY)
                val backgroundLineEnd = Offset(x = size.width, y = lineY)
                val foregroundLineEnd = Offset(x = size.width * curPercentage.value, y = lineY)
                //background line
                drawLine(
                    color = backgroundColor,
                    start = lineStart,
                    end = backgroundLineEnd,
                    strokeWidth = strokeWith,
                    StrokeCap.Round,
                )
                //foreground line
                drawLine(
                    color = foregroundColor,
                    start = lineStart,
                    end = foregroundLineEnd,
                    strokeWidth = strokeWith,
                    StrokeCap.Round,
                )
            }

        }

    }

}

@Composable
fun LineDevider (
    color: Color = if(isAppInDarkTheme())LocalColor.current.backgroundLight else LocalColor.current.secondaryLight,
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
fun LineDividerVertical (
    color: Color = LocalColor.current.secondary,
    strokeWith: Float = 5f,
    modifier: Modifier = Modifier
)
{


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
    ){
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
        )  {
            //Line parameters
            val lineStart = 0.dp.toPx()
            val lineEnd = size.height
            val lineX = size.width/2
            //line
            drawLine(
                color = color,
                start = Offset(x = lineX, y= lineStart),
                end = Offset(x= lineX , y= lineEnd),
                strokeWidth = strokeWith,
                StrokeCap.Round,
            )
        }

    }

}

@Composable
fun TopMobileIconsBackground (
    color: Color = LocalColor.current.backgroundAlternative,
) {
    Box(
        modifier = Modifier
            .background(color)
            .fillMaxWidth()
            .height(topPhoneIconsAndNavBarBackgroundHeight)
    )
}



@Composable
fun ProductionImage(
    imageID: String? = null, // Endret til nullable for å håndtere URL-er
    placeholderID: Int = R.drawable.noimage,
    imageDescription: String = "Image not available",
    sizeMultiplier: Float = 1.0f,
    modifier: Modifier = Modifier
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
            modifier = modifier
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
    imageID: String?,
    userName: String,
    sizeMultiplier: Float = 1.0f,
    handleProfileImageClick: () -> Unit = {}
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
                .clickable {
                    handleProfileImageClick()
                }
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
fun RatingsGraphics(
    score: Int,
    sizeMultiplier: Float = 1.0f,
    color: Color = LocalColor.current.secondary,
    loggedInUsersScore: Boolean = false,
    modifier: Modifier = Modifier
) {
    //Generate stars if score is greater than 0
    if (score > 0){
        var scoreNumber: Int = score
        if (scoreNumber > 10) { scoreNumber = 10}
        //Graphics
        Row (
            modifier = modifier
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
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_star),
                contentDescription = "Star",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(LocalColor.current.quinary),
                alignment = Alignment.Center,
                modifier = Modifier
                    .size((11*sizeMultiplier).dp)
            )
            Text(
                text = "Unrated",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = 11.sp* sizeMultiplier,
                color = LocalColor.current.quinary,
                textAlign = TextAlign.Center
            )
        }

    } else {
        Text(
            text = "No rating",
            fontFamily = fontFamily,
            fontWeight = weightRegular,
            fontSize = 11.sp * sizeMultiplier,
            color = LocalColor.current.quinary
        )
    }
}

@Composable
fun LikeButton (
    sizeMultiplier: Float = 1f,
    liked: Boolean = false,
    handleLikeClick: () -> Unit
) {
    val primaryColor = LocalColor.current.primary
    val quinaryColor = LocalColor.current.quinary

    var buttonText by remember {
        mutableStateOf("Like")
    }

    var buttonColor by remember {
        mutableStateOf(quinaryColor)
    }

    var buttonClicked by remember {
        mutableStateOf(liked)
    }

    var heartIcon by remember {
        mutableStateOf(R.drawable.heart_hollow)
    }

    val handleLikeButtonClick: () -> Unit = {
        if (buttonClicked) {
            buttonColor = quinaryColor
            buttonClicked = false
            buttonText = "Like"
            heartIcon = R.drawable.heart_hollow

        } else {
            buttonColor = primaryColor
            buttonClicked = true
            buttonText = "Liked"
            heartIcon = R.drawable.heart_filled
        }

        handleLikeClick()
    }

    Button(
        onClick = {
            handleLikeButtonClick()
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
fun FavoriteButton (
    sizeMultiplier: Float = 1f,
    favorited: Boolean = false,
    handleFavoriteClick: () -> Unit
) {

    val primaryColor = LocalColor.current.primary
    val quinaryColor = LocalColor.current.quinary

    var buttonText by remember {
        mutableStateOf("Add to favorites")
    }

    var buttonColor by remember {
        mutableStateOf(quinaryColor)
    }

    var buttonClicked by remember {
        mutableStateOf(favorited)
    }

    var heartIcon by remember {
        mutableStateOf(R.drawable.heart_hollow)
    }

    val handleFavoriteButtonClick: () -> Unit = {
        if (buttonClicked) {
            buttonColor = quinaryColor
            buttonClicked = false
            buttonText = "Add to favorites"
            heartIcon = R.drawable.heart_hollow

        } else {
            buttonColor = primaryColor
            buttonClicked = true
            buttonText = "Favorite"
            heartIcon = R.drawable.heart_filled
        }
        handleFavoriteClick()
    }

    Button(
        onClick = {
            handleFavoriteButtonClick()
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
    textModifier: Modifier = Modifier,
    handleImageClick: (showID: String, productionType: ProductionType) -> Unit
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
            color = LocalColor.current.secondary,
            modifier = textModifier
        )
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = LocalConstraints.current.mainContentHorizontalPadding, end = LocalConstraints.current.mainContentHorizontalPadding)
        ){
            items (listOfShows.size) {i ->
                ProductionImage(
                    imageID = listOfShows[i].posterUrl,
                    imageDescription = listOfShows[i].title + " Poster",
                    modifier = Modifier
                        .clickable {
                            handleImageClick(listOfShows[i].imdbID, listOfShows[i].type)
                        }
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
    textModifier: Modifier = Modifier,
    handleImageClick: (productionID: String, productionType: ProductionType) -> Unit
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
            color = LocalColor.current.secondary,
            modifier = textModifier
        )
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(start = LocalConstraints.current.mainContentHorizontalPadding, end = LocalConstraints.current.mainContentHorizontalPadding)
        ){
            if(listOfShows.isNotEmpty()){
                items (listOfShows.size) {i ->
                    ProductionImage(
                        imageID = listOfShows[i].production.posterUrl,
                        imageDescription = listOfShows[i].production.title + " Poster",
                        modifier = Modifier
                            .clickable {
                                handleImageClick(listOfShows[i].production.imdbID, listOfShows[i].production.type)
                            }
                    )
                }
            } else {
                item {
                    Text(
                        text = "There doesn't seem to be anything here :(",
                        fontFamily = fontFamily,
                        fontSize = headerSize,
                        fontWeight = weightRegular,
                        textAlign = TextAlign.Center,
                        color = LocalColor.current.quaternary,
                        modifier = textModifier
                            .fillMaxWidth()
                    )
                }

            }

        }

    }
}


@Composable
fun RoundProgressBar (
    percentage: Float = 1f,
    startAngle: Float = -90f,
    sweepAngle: Float = 360f,
    color: Color = LocalColor.current.primary,
    strikeWith: Dp = 8.dp,
    radius: Dp = 100.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    strokeCap: StrokeCap = StrokeCap.Round,
    easing: Easing = Ease
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    var currentPercentage = animateFloatAsState(
        targetValue = if(animationPlayed) {percentage} else {0f},
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay,
            easing = easing,
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
                startAngle = startAngle,
                sweepAngle = sweepAngle* currentPercentage.value,
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

    val currentVideoUrl by rememberUpdatedState(videoUrl)

    AndroidView(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = LocalConstraints.current.mainContentHorizontalPadding)
        ,
        factory = { context ->
            YouTubePlayerView(context = context).apply {
                lifeCycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object: AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(currentVideoUrl, 0f)
                    }
                })
            }
        }
    )
}

@Composable
fun RatingSlider (
    rating: Int = 0,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (score: Int) -> Unit
){
    var scoreInput by remember { mutableIntStateOf(rating) }

    if (visible){
        Popup (
            onDismissRequest = {
                onValueChangeFinished(rating)
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
                        .background(LocalColor.current.backgroundLight, shape = RoundedCornerShape(5.dp))
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                ){
                    //Content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        //Stars
                        RatingsGraphics(
                            score = scoreInput,
                            color = LocalColor.current.primary,
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

fun generateListOptionName (
    listOption: ListOptions?
): String
{
    return when (listOption) {
        ListOptions.WANTTOWATCH -> {
            "Want to watch"
        }
        null -> {
            "Add to library"
        }
        ListOptions.REMOVEFROMLIST -> {
            "Remove from list"
        }
        else -> {
            listOption.toString().lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}

@Composable
fun LogoWithName (
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Logo()
        Row(){
            Text(
                text = "Movie",
                textAlign = TextAlign.Center,
                fontFamily = fontFamily,
                fontSize = fontSize,
                fontWeight = weightLight,
                color = LocalColor.current.primary
            )
            Text(
                text = "List",
                textAlign = TextAlign.Center,
                fontFamily = fontFamily,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                color = LocalColor.current.primary
            )
        }

    }
}


@Composable
fun Logo (
    modifier: Modifier = Modifier,
){
    Image(
        painter = painterResource(R.drawable.logo_m),
        contentDescription = "MovieList",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
    )
}

@Composable
fun ProductionSortSelectButton (
    handleSortChange: (activeCategory: ShowSortOptions) -> Unit
) {
    //Category button

    var dropDownExpanded by remember {
        mutableStateOf(false)
    }
    var dropDownButtonText by remember{
        mutableStateOf("Movies & Shows")
    }
    val sortOptions = listOf(
        ShowSortOptions.MOVIESANDSHOWS, ShowSortOptions.MOVIES, ShowSortOptions.SHOWS
    )


    //Wrapper
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //CategorySelectButton
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .width(200.dp)
                .align(Alignment.Center)
                .clickable {
                    //dropdown menu button logic
                    dropDownExpanded = true
                }
        ){
            //BUTTON TEXT
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.Center)

            )

            {
                Text(
                    text = "$dropDownButtonText",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = if(isAppInDarkTheme()){ LocalColor.current.primary} else {LocalColor.current.primary},
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "V",
                    fontSize = paragraphSize,
                    fontWeight = weightLight,
                    fontFamily = fontFamily,
                    color = if(isAppInDarkTheme()){ LocalColor.current.primary} else {LocalColor.current.primary},
                )

            }

            //MENU
            DropdownMenu(
                expanded = dropDownExpanded,
                onDismissRequest = {dropDownExpanded = false},
                offset = DpOffset(x = 50.dp, y= 0.dp),
                modifier = Modifier
                    .background(color = if(isAppInDarkTheme()){ LocalColor.current.tertiary} else {LocalColor.current.primary})
                    .width(100.dp)
            ) {
                sortOptions.forEach{
                        option -> DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                        ){
                            //MENU ITEM TEXT
                            Text(
                                text = GenerateShowSortOptionName(option),
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
                        dropDownExpanded = false
                        dropDownButtonText = GenerateShowSortOptionName(option)
                        handleSortChange(option)
                    })
                }
            }

        }
    }
}


@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    sizeMultiplier: Float = 1f,
    iconColor: Color = LocalColor.current.secondary,
    backgroundColor: Color = LocalColor.current.tertiary,
    filled: Boolean = false,
    handleSettingsButtonClick: () -> Unit
) {

    Image(
        painter = if(filled){painterResource(R.drawable.cog_filled)}else{painterResource(R.drawable.cog)},
        contentDescription = "Settings and user info",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(iconColor),
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(100)
            )
            .padding(5.dp)
            .height(15.dp * sizeMultiplier)
            .width(15.dp * sizeMultiplier)
            .clickable {
                handleSettingsButtonClick()
            }
    )
}


@Composable
fun HamburgerButton(
    modifier: Modifier = Modifier,
    sizeMultiplier: Float = 1f,
    iconColor: Color = LocalColor.current.backgroundLight,
    backgroundColor: Color = LocalColor.current.primary,
    handleHamburgerButtonClick: () -> Unit
) {

    Image(
        painter = painterResource(R.drawable.hamburger),
        contentDescription = "Settings and user info",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(iconColor),
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(100)
            )
            .padding(5.dp)
            .height(15.dp * sizeMultiplier)
            .width(15.dp * sizeMultiplier)
            .clickable {
                handleHamburgerButtonClick()
            }
    )
}


@Composable
fun SearchButton(
    modifier: Modifier = Modifier,
    sizeMultiplier: Float = 1f,
    iconColor: Color = LocalColor.current.backgroundLight,
    backgroundColor: Color = LocalColor.current.primary,
    handleHamburgerButtonClick: () -> Unit
) {

    Image(
        painter = painterResource(R.drawable.search),
        contentDescription = "Settings and user info",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(iconColor),
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(100)
            )
            .padding(5.dp)
            .height(15.dp * sizeMultiplier)
            .width(15.dp * sizeMultiplier)
            .clickable {
                handleHamburgerButtonClick()
            }
    )
}


@Composable
fun LoadingCircle(
    color: Color = LocalColor.current.primary,
    strokeWidth: Dp = 6.dp,
    radius: Dp = 100.dp,
    animationDuration: Int = 1000,
) {
    val circleOneSweepAngle = remember { Animatable(0f) }
    val circleOneStartAngle = remember { Animatable(270f) }
    val circleTwoSweepAngle = remember { Animatable(-360f) }
    val circleTwoStartAngle = remember { Animatable(-90f) }
    var animationPhase by remember { mutableStateOf(true) }


    LaunchedEffect(animationPhase) {
        when (animationPhase) {
            true -> {
                circleOneSweepAngle.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = EaseIn
                    )
                )
                // Transition to retract phase
                animationPhase = false
            }

            false -> {
                circleTwoSweepAngle.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = EaseOut
                    )
                )
                // Reset for next cycle
                circleOneSweepAngle.snapTo(0f)
                circleTwoSweepAngle.snapTo(-360f)
                animationPhase = true // Start filling again
            }
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.logo_m),
            contentDescription = "MovieList",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(radius)
        ) {
            Canvas(
                modifier = Modifier
                    .size(radius)
            ) {
                if (animationPhase){
                    drawArc(
                        color = color,
                        startAngle = circleOneStartAngle.value,
                        sweepAngle = circleOneSweepAngle.value,
                        useCenter = false,
                        style = Stroke(
                            strokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                } else {
                    drawArc(
                        color = color,
                        startAngle = circleTwoStartAngle.value,
                        sweepAngle = circleTwoSweepAngle.value,
                        useCenter = false,
                        style = Stroke(
                            strokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }

            }
        }
    }
}

