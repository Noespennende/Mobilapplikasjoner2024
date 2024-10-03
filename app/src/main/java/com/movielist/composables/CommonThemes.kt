package com.movielist.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movielist.R
import com.movielist.data.NavbarOptions
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkGrayTransparent
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.bottomPhoneIconsOffset
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.showImageHeight
import com.movielist.ui.theme.showImageWith
import com.movielist.ui.theme.topNavBaHeight
import com.movielist.ui.theme.topPhoneIconsBackgroundHeight
import com.movielist.ui.theme.weightBold
import com.movielist.ui.theme.weightRegular

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
fun BottomNavbarAndMobileIconsBackground (
    color: Color = Gray
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height(bottomNavBarHeight)
                .align(Alignment.BottomCenter)
        )
    }

}

@Composable
fun TopNavbarBackground (
    color: Color = Gray,
    sizeMultiplier: Float = 1f
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height((topPhoneIconsBackgroundHeight+ topNavBaHeight)*sizeMultiplier)
                .align(Alignment.TopCenter)
        )
    }

}

@Composable
fun BottomNavBar(
    activeColor: Color = Purple,
    inactiveColor: Color = LightGray,
    sizeMultiplier: Float = 1f
){
    val buttonSize: Dp = (35*sizeMultiplier).dp



    var homeButtonColor by remember {
        mutableStateOf(activeColor)
    }
    var listButtonColor by remember {
        mutableStateOf(inactiveColor)
    }
    var searchButtonColor by remember {
        mutableStateOf(inactiveColor)
    }
    var reviewButtonColor by remember {
        mutableStateOf(inactiveColor)
    }
    var profileButtonColor by remember {
        mutableStateOf(inactiveColor)
    }
    var activeButton by remember {
        mutableStateOf(NavbarOptions.HOME)
    }


    //wrapper
    Box (
        modifier = Modifier.fillMaxSize()
    )
    {
        //Navbar content
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.BottomCenter)
            .padding(
                bottom = bottomPhoneIconsOffset,
                start = horizontalPadding-10.dp,
                end = horizontalPadding-10.dp
            )
        ) {
            //Home button
            Button(
                onClick = {
                    //Home button onClick logic
                    if(activeButton != NavbarOptions.HOME){
                        homeButtonColor = activeColor
                        listButtonColor = inactiveColor
                        searchButtonColor = inactiveColor
                        reviewButtonColor = inactiveColor
                        profileButtonColor = inactiveColor
                        activeButton = NavbarOptions.HOME
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                //Home button icon
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(homeButtonColor),
                    modifier = Modifier
                        .size(buttonSize)
                        .align(alignment = Alignment.CenterVertically)
                )

            }

            //List button
            Button(
                onClick = {
                    //List button onClick logic
                    if(activeButton != NavbarOptions.LIST){
                        homeButtonColor = inactiveColor
                        listButtonColor = activeColor
                        searchButtonColor = inactiveColor
                        reviewButtonColor = inactiveColor
                        profileButtonColor = inactiveColor
                        activeButton = NavbarOptions.LIST
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                //List button icon
                Image(
                    painter = painterResource(id = R.drawable.list),
                    contentDescription = "List",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(listButtonColor),
                    modifier = Modifier
                        .size(buttonSize)
                        .align(alignment = Alignment.CenterVertically)
                )

            }

            //Search button
            Button(
                onClick = {
                    //Search button onClick logic
                    if(activeButton != NavbarOptions.SEARCH){
                        homeButtonColor = inactiveColor
                        listButtonColor = inactiveColor
                        searchButtonColor = activeColor
                        reviewButtonColor = inactiveColor
                        profileButtonColor = inactiveColor
                        activeButton = NavbarOptions.SEARCH
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                //Search button icon
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(searchButtonColor),
                    modifier = Modifier
                        .size(buttonSize)
                        .align(alignment = Alignment.CenterVertically)
                )

            }

            //Review button
            Button(
                onClick = {
                    //Review button onClick logic
                    if(activeButton != NavbarOptions.REVIEW){
                        homeButtonColor = inactiveColor
                        listButtonColor = inactiveColor
                        searchButtonColor = inactiveColor
                        reviewButtonColor = activeColor
                        profileButtonColor = inactiveColor
                        activeButton = NavbarOptions.REVIEW
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                //Review button icon
                Image(
                    painter = painterResource(id = R.drawable.review),
                    contentDescription = "Review",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(reviewButtonColor),
                    modifier = Modifier
                        .size(buttonSize)
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            //Profile button
            Button(
                onClick = {
                    //Profile button onClick logic
                    if(activeButton != NavbarOptions.PROFILE){
                        homeButtonColor = inactiveColor
                        listButtonColor = inactiveColor
                        searchButtonColor = inactiveColor
                        reviewButtonColor = inactiveColor
                        profileButtonColor = activeColor
                        activeButton = NavbarOptions.PROFILE
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(buttonSize)
                    .wrapContentWidth()
            )
            {
                //Profile button icon
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Review",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(profileButtonColor),
                    modifier = Modifier
                        .size(buttonSize)
                        .align(alignment = Alignment.CenterVertically)
                )
            }
        }
    }


}

@Composable
fun ShowImage (
    imageID: Int  = R.drawable.noimage,
    imageDescription: String = "Image not available",
    sizeMultiplier: Float = 1.0f
) {
    Image(
        painter = painterResource(id = imageID),
        contentDescription = imageDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(showImageWith*sizeMultiplier)
            .height(showImageHeight*sizeMultiplier)
    )
}

@Composable
fun ProfileImage(
    imageID: Int,
    userName: String,
    sizeMultiplier: Float = 1.0f
) {
    Image(
        painter = painterResource(id = imageID),
        contentDescription = userName,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size((30*sizeMultiplier).dp)
            .clip(CircleShape)
    )
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
                colorFilter = ColorFilter.tint(LightGray),
                modifier = Modifier
                    .size((11*sizeMultiplier).dp)
            )
            Text(
                text = "Unrated",
                fontFamily = fontFamily,
                fontWeight = weightBold,
                fontSize = 16.sp,
                color = LightGray
            )
        }

    } else {
        Text(
            text = "No score",
            fontFamily = fontFamily,
            fontWeight = weightRegular,
            fontSize = 12.sp,
            color = LightGray
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