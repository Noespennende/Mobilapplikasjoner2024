package com.movielist.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

//Main content section constraints
val horizontalPadding = 25.dp
val verticalPadding = 20.dp

//Bottom navnbar constraints
val bottomNavBarHeight = 110.dp
val bottomPhoneIconsOffset = 55.dp

//Top phone icon constraints
val topPhoneIconsAndNavBarBackgroundHeight = 80.dp

//top navbar constraints
val topNavBarContentStart = topPhoneIconsAndNavBarBackgroundHeight + 10.dp
val topNavBarHeight = 85.dp

//Content start
val contentStart = topPhoneIconsAndNavBarBackgroundHeight + topNavBarHeight

//Show image constraints
val showImageHeight = 133.dp
val showImageWith = 90.dp


data class Constraints (
    val topUniversalNavbarHeight: Dp = topNavBarHeight,
    val topUniversalNavbarContentStart: Dp = topPhoneIconsAndNavBarBackgroundHeight,
    val universalNavbarHorizontalPadding: Dp = horizontalPadding -10.dp,
    val topLocalScreenNavbarContentStart: Dp = topNavBarContentStart,
    val mainContentStart: Dp = contentStart,
    val mainContentHorizontalPadding: Dp = horizontalPadding,
    val mainContentHorizontalPaddingAlternative: Dp = horizontalPadding,
    val bottomUniversalNavbarHeight: Dp = bottomNavBarHeight,
    val bottomUniversalNavbarContentStart: Dp = bottomPhoneIconsOffset

)


val LocalConstraints = compositionLocalOf { Constraints() }

val portraitConstraints = Constraints()
val landscapeConstraints = Constraints(
    topUniversalNavbarHeight = 60.dp,
    topUniversalNavbarContentStart = 60.dp,
    universalNavbarHorizontalPadding = 80.dp,
    bottomUniversalNavbarContentStart = 5.dp,
    bottomUniversalNavbarHeight = 55.dp,
    mainContentHorizontalPaddingAlternative = 150.dp,
    mainContentHorizontalPadding = 80.dp,
    mainContentStart = 60.dp
)