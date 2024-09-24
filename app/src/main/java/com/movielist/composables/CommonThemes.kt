package com.movielist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.movielist.ui.theme.DarkGray

@Composable
fun Background () {
    Box(
        modifier = Modifier
            .background(DarkGray)
            .fillMaxSize()
    )
}