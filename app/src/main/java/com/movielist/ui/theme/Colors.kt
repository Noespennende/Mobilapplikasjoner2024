package com.movielist.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.SliderColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class Colors (
    val primary: Color = Purple,
    val primaryLight: Color = LightPurple,
    val secondary: Color = White,
    val secondaryLight: Color = BlueGray,
    val tertiary: Color = DarkPurple,
    val quaternary: Color = LightGray,
    val quaternaryLight: Color = LightBlueGray,
    val quinary: Color = DarkWhite,
    val backgroundLight: Color = Gray,
    val background: Color = DarkGray,
    val backgroundDark: Color = LightBlack,
    val backgroundAlternative: Color = DarkGrayTransparent,

    val complimentaryOne: Color = Yellow,
    val complimentaryTwo: Color = Teal,
    val complimentaryThree: Color = Red,
    val ComplimentaryFour: Color = Green,


    val error: Color = Red
)

data class InputFieldColors(
    val textFieldColors: TextFieldColors = textFieldColors()
)


val LocalColor = compositionLocalOf { Colors() }
val LocalTextFieldColors = compositionLocalOf { InputFieldColors() }

//Main colors
val Purple = Color(0xFFc38fff)
val DeepPurple = Color(0xFF8e55d0)
val LightPurple = Color(0xFFdcbeff)
val DarkPurple = Color(0xFF40344f)
val LightBlack = Color(0xFF191919)
val DarkGray = Color(0xFF1c1c1c)
val Gray = Color(0xFF2c2c2c)
val LightGray = Color(0xFF555555)
val DarkWhite = Color(0xFFaeaeae)
val White = Color(0xFFffffff)
val DarkGrayTransparent = Color(0xE6101010)
val BlueWhite = Color(0xFFedf1f2)
val BlueGray = Color(0xFFb1bbbe)
val LightBlueGray = Color(0xffd9e2eb)

//Complimentary colors
val Yellow = Color(0xFFfcb969)
val Teal = Color(0xFF54b4ae)
val Red = Color(0xFFbf5656)
val Green = Color(0xFF74c276)

fun textFieldColors(): TextFieldColors = TextFieldColors(
    focusedTextColor = BlueWhite,
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

fun textFieldColorsLightTheme(): TextFieldColors = TextFieldColors(
    focusedTextColor = DarkGray,
    unfocusedTextColor = DarkGray,
    disabledTextColor = Gray,
    errorTextColor = Red,
    focusedContainerColor = BlueWhite,
    unfocusedContainerColor = BlueWhite,
    disabledPrefixColor = BlueWhite,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Purple,
    textSelectionColors = TextSelectionColors(handleColor = DarkGray, backgroundColor = Gray),
    cursorColor = DarkGray,
    disabledContainerColor = BlueWhite,
    disabledIndicatorColor = Gray,
    disabledLabelColor = Gray,
    disabledLeadingIconColor = Gray,
    disabledSuffixColor = Gray,
    disabledPlaceholderColor = Gray,
    disabledSupportingTextColor = Gray,
    disabledTrailingIconColor = Gray,
    errorContainerColor = BlueWhite,
    errorCursorColor = DarkGray,
    errorPlaceholderColor = Red,
    errorIndicatorColor = Red,
    errorLabelColor = BlueWhite,
    errorLeadingIconColor = Red,
    errorPrefixColor = Red,
    errorSuffixColor = Red,
    errorSupportingTextColor = DarkGray,
    errorTrailingIconColor = Red,
    focusedLabelColor = DarkGray,
    focusedPrefixColor = DarkGray,
    focusedSuffixColor = DarkGray,
    focusedPlaceholderColor = DarkGray,
    focusedTrailingIconColor = DarkGray,
    focusedLeadingIconColor = DarkGray,
    focusedSupportingTextColor = DarkGray,
    unfocusedLabelColor = DarkGray,
    unfocusedPrefixColor = DarkGray,
    unfocusedSuffixColor = DarkGray,
    unfocusedPlaceholderColor = DarkGray,
    unfocusedLeadingIconColor = DarkGray,
    unfocusedTrailingIconColor = DarkGray,
    unfocusedSupportingTextColor = DarkGray
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

val darkColorScheme = Colors()
val lightColorScheme = Colors(
    primary = DeepPurple,
    secondary = DarkGray,
    background = BlueWhite,
    backgroundLight = White,
    backgroundAlternative = White,
    backgroundDark = White,
    quinary = Gray
)



