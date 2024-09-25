package com.movielist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.movielist.R

//Import google fonts
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

//Importing Roboto from Google fonts
val fontName = GoogleFont("Roboto")

val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider),
    androidx.compose.ui.text.font.Font(resId = R.font.roboto_regular)
)

// Font weights
val weightRegular = FontWeight(400)
val weightLight = FontWeight(300)
val weightBold = FontWeight(700)

//Font size
val headerSize = 16.sp
val paragraphSize = 12.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)