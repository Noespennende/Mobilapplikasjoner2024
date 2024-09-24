package com.movielist.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkPurple
import com.movielist.ui.theme.Purple

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
    strokeWith: Dp = 8.dp,
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
                strokeWidth = 20f,
                StrokeCap.Round,
            )
            //foreground line
            drawLine(
                color = foregroundColor,
                start = Offset(x = lineStart, y= lineY),
                end = Offset(x= lineEnd , y= lineY),
                strokeWidth = 20f,
                StrokeCap.Round,
            )
        }

    }

}

