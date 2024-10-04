package com.movielist.composables

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.getUserInfo
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.movielist.ui.theme.White
import com.movielist.ui.theme.weightRegular
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.White
import com.movielist.ui.theme.*

@Preview
@Composable
fun FirebaseTesting() {

    var userData by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    val userid = "qwVPkcZhvKooSRfDHpIV"

    // Henter bruker-info fra "backend" som henter fra databasen
    getUserInfo(userid) { data ->
        userData = data
    }

    //Front page graphics
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        //Front page content
        item {
            Text(
                userData["firstName"] ?: "null",
                style = TextStyle(
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = weightRegular
                )
            )
        }
        item {
            Text(
                userData["lastName"] ?: "null",
                style = TextStyle(
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = weightRegular
                )
            )
        }
        item {
            Text(
                userData["documentID"] ?: "null",
                style = TextStyle(
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = weightRegular
                )
            )
        }
    }

}
