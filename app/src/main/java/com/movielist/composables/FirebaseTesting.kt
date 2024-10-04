package com.movielist.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movielist.ui.theme.White
import com.movielist.ui.theme.weightRegular
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.White
import com.movielist.ui.theme.*

@Preview
@Composable
fun FirebaseTesting() {


    //Front page graphics
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Fyll hele skjermen
            .padding(50.dp) // Legg til padding rundt innholdet
    ) {
        //Front page content
        item {
            Text(
                "title",
                style = TextStyle(
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = weightRegular
                )
            )
        }
    }

}
