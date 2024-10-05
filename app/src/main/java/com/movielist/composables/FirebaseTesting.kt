package com.movielist.composables

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import backend.createUserWithEmailAndPassword
import backend.getUserInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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

    // Komponenter for testing.
    // Bytt på hvilket komponent som vises for testing

    CreateUser()
    //GetData()
}

@Composable
fun CreateUser() {


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // UI for input og knapp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Tekstfelt for e-post
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tekstfelt for passord
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Knapp for å opprette bruker
        Button(
            onClick = {
                createUserWithEmailAndPassword(email, password, { successMessage = it }, { errorMessage = it })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create User")
        }

        // Viser feilmelding hvis opprettelse feiler
        errorMessage?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Viser suksessmelding hvis opprettelse er vellykket
        successMessage?.let {
            Text(
                text = "Success: $it",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }



}



@Composable
fun GetData() {

    var userData by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    val userid = "qwVPkcZhvKooSRfDHpIV"

    // Henter bruker-info fra "backend" som henter fra databasen
    getUserInfo(userid) { data ->
        userData = data
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
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
