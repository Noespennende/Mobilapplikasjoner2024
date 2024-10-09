package com.movielist.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.createUserWithEmailAndPassword
import backend.getSignedInUser
import backend.getUserInfo
import backend.logInWithEmailAndPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.weightRegular

@Preview
@Composable
fun FirebaseTesting() {

    // Komponenter for testing.
    // Bytt på hvilket komponent du vil teste, eller vis dem alle


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, // Juster horisontalt
        verticalArrangement = Arrangement.Center // Sentrer vertikalt
    ) {

        LogInLogic()

        //CreateUser()

        //GetData()

    }

}

@Composable
fun LogInLogic() {

    val firebaseAuth = FirebaseAuth.getInstance()

    // Variabel for å kontrollere om innlogging er vellykket
    var isLoggedIn by remember { mutableStateOf(false) }

    // Bruker LaunchedEffect for å ikke sjekke/sette login-status flere ganger
    // ved f.eks rerendring av komponentet
    // -> Unngår dermed at vi gjør kall til Firebase unødvendig mye
    LaunchedEffect(Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()
        isLoggedIn = firebaseAuth.currentUser != null
    }

        // Vis LogInUser hvis ikke innlogget
        if (!isLoggedIn) {

            LogInUser(onLoginSuccess = { isLoggedIn = true })

        }
        // Viser LoginSuccessful hvis logget inn
    if (isLoggedIn) {
        LoginSuccessful(onLogout = {
            isLoggedIn = false // Oppdater tilstand for å vise innloggingsskjerm etter utlogging
        })
    }

}

@Composable
fun LogInUser(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // UI for input og knapp
    Column(
        modifier = Modifier
            .padding(16.dp),
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

        // Knapp for innlogging
        Button(
            onClick = {
                logInWithEmailAndPassword(email, password, {
                    // Hvis innloggingen er vellykket, kaller vi callback
                    onLoginSuccess()
                }, { errorMessage = it })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log In")
        }

        // Viser feilmelding hvis innlogging mislykkes
        // Akkurat nå viser den bare det Firebase returnerer for testing
        errorMessage?.let {
            Text(
                text = "Error: $it",
                color = Purple,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
@Composable
fun LoginSuccessful(onLogout: () -> Unit) {

    val user: FirebaseUser? = getSignedInUser()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text(
                text = "Login successful",
                color = White,
                fontSize = 25.sp,
                fontWeight = weightRegular,
            )
            Text(
                text = user.uid,
                color = White,
                fontSize = 25.sp,
                fontWeight = weightRegular,
            )
        }
        else {
            Text(
                text = "Login successful but no user?",
                color = White,
                fontSize = 25.sp,
                fontWeight = weightRegular,
            )
        }
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut() // Logg ut brukeren
                onLogout() // Kall callback for å oppdatere UI etter utlogging
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Log out")
        }
    }

}


@Composable
fun CreateUser() {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // UI for input og knapp
    Column(
        modifier = Modifier
            .padding(16.dp),
    ) {

        // Tekstfelt for brukernavn
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth()
        )

        // Tekstfelt for e-post
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )

        //Spacer(modifier = Modifier.height(16.dp))

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
                createUserWithEmailAndPassword(username, email, password, { successMessage = it }, { errorMessage = it })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create User")
        }

        // Viser feilmelding hvis opprettelse feiler
        errorMessage?.let {
            Text(
                text = "Error: $it",
                color = Purple,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Viser suksessmelding hvis opprettelse er vellykket
        successMessage?.let {
            Text(
                text = "Success: $it",
                color = White,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }



}



@Composable
fun GetData() {

    var userData by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    val userid = "qwVPkcZhvKooSRfDHpIV" // <-- Eksempel brukerid

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
