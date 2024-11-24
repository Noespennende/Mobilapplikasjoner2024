package com.movielist.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.SliderColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


//Main colors
val Purple = Color(0xFFc38fff)
val DarkPurple = Color(0xFF40344f)
val LightBlack = Color(0xFF191919)
val DarkGray = Color(0xFF1c1c1c)
val Gray = Color(0xFF2c2c2c)
val LightGray = Color(0xFF555555)
val darkWhite = Color(0xFFaeaeae)
val White = Color(0xFFffffff)
val DarkGrayTransparent = Color(0xE6101010)

//Complimentary colors
val yellow = Color(0xFFfcb969)
val teal = Color(0xFF54b4ae)
val red = Color(0xFFbf5656)
val green = Color(0xFF74c276)

val textFieldColors = TextFieldColors(
    focusedTextColor = White,
    unfocusedTextColor = White,
    disabledTextColor = Gray,
    errorTextColor = Color.Red,
    focusedContainerColor = DarkGray,
    unfocusedContainerColor = DarkGray,
    disabledPrefixColor = DarkGray,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Purple,
    textSelectionColors = TextSelectionColors(handleColor = White, backgroundColor = Gray),
    cursorColor = White,
    disabledContainerColor = DarkGray,
    disabledIndicatorColor = Gray,
    disabledLabelColor = Gray,
    disabledLeadingIconColor = Gray,
    disabledSuffixColor = Gray,
    disabledPlaceholderColor = Gray,
    disabledSupportingTextColor = Gray,
    disabledTrailingIconColor = Gray,
    errorContainerColor = DarkGray,
    errorCursorColor = White,
    errorPlaceholderColor = Color.Red,
    errorIndicatorColor = Color.Red,
    errorLabelColor = DarkGray,
    errorLeadingIconColor = Color.Red,
    errorPrefixColor = Color.Red,
    errorSuffixColor = Color.Red,
    errorSupportingTextColor = White,
    errorTrailingIconColor = Color.Red,
    focusedLabelColor = White,
    focusedPrefixColor = White,
    focusedSuffixColor = White,
    focusedPlaceholderColor = White,
    focusedTrailingIconColor = White,
    focusedLeadingIconColor = White,
    focusedSupportingTextColor = White,
    unfocusedLabelColor = White,
    unfocusedPrefixColor = White,
    unfocusedSuffixColor = White,
    unfocusedPlaceholderColor = White,
    unfocusedLeadingIconColor = White,
    unfocusedTrailingIconColor = White,
    unfocusedSupportingTextColor = White
)

val sliderColors = SliderColors(
    activeTrackColor = Purple,
    activeTickColor = Purple,
    thumbColor = Purple,
    disabledActiveTrackColor = LightGray,
    disabledThumbColor = LightGray,
    disabledActiveTickColor = LightGray,
    disabledInactiveTickColor = LightGray,
    disabledInactiveTrackColor = LightGray,
    inactiveTrackColor = LightGray,
    inactiveTickColor = LightGray
)