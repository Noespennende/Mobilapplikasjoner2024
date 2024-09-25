package com.movielist.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movielist.R
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkGrayTransparent
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.LightBlack
import com.movielist.ui.theme.LightGray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.paragraphSize
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
    lenght: Dp = 50.dp,
    foregroundColor: Color = Gray,
    backgroundColor: Color = DarkPurple,
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
                color = foregroundColor,
                start = Offset(x = lineStart, y= lineY),
                end = Offset(x= lineEnd , y= lineY),
                strokeWidth = 5f,
                StrokeCap.Round,
            )
        }

    }

}

@Composable
fun TopMobileIconsBackground () {
    Box(
        modifier = Modifier
            .background(DarkGrayTransparent)
            .fillMaxWidth()
            .height(25.dp)
    )
}

@Composable
fun BottomMobileIconsBackground () {
    Box(
        modifier = Modifier
            .background(DarkGray)
            .fillMaxWidth()
            .height(20.dp)
    )
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
            .width((90*sizeMultiplier).dp)
            .height((133*sizeMultiplier).dp)
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
    sizeMultiplier: Float = 1.0f
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
                    modifier = Modifier
                        .size((11*sizeMultiplier).dp)
                )
            }

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

    Button(
        onClick = {
            if (buttonClicked) {
                buttonColor = LightGray
                buttonClicked = false
                buttonText = "Like"

            } else {
                buttonColor = Purple
                buttonClicked = true
                buttonText = "Liked"
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