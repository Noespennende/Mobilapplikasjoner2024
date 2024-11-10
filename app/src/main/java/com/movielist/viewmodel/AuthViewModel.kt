package com.movielist.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.movielist.data.FireBaseAuth
import com.movielist.data.addUserToDatabase
import com.movielist.data.isUsernameUnique
import com.movielist.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel for å håndtere autentisering med Firebase
class AuthViewModel : ViewModel() {

    // Instans av FirebaseAuth-hjelperklassen
    private val authHelper = FireBaseAuth()

    /* Innloggingsstatus */
    // _ for å vise at den er privat, og kan kun endres/brukes internt
    // isLoggedIn er en "State" av _isLoggedIn, for å hente verdien uten å kunne endre den direkte.
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> get() = _isLoggedIn

    /* Bruker */
    // _currentUser er privat og kun for intern logikk (kan endres - Mutable...)
    // Mens currentUser er en "State" av _currentUser, og kan kun leses (ikke mutable)
    // Den er en StateFlow for å være asynkron og siden den kan oppdateres over tid
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    /* Sjekke status (logget inn / ikke logget inn) */
    fun checkUserStatus() {
        // Henter den (mulige) innloggede brukeren via FirebaseAuth-hjelperen
        val user = authHelper.getSignedInUser() // Hent brukeren

        _currentUser.value = user

        _isLoggedIn.value = user != null // Hvis bruker finnes, er de logget inn
    }

    /* Logge ut bruker */
    // Her kommer de private variablene inn - og hvorfor vi har to forskjellige variabler,
    // så vi kan logge ut bruker
    // Den logger ut brukeren via logikken i FirebaseAuth, og setter _currentUser og _isLoggedIn til null og false.
    fun logOut() {
        // Logger ut brukeren via FirebaseAuth-hjelperen
        authHelper.logOut()
        // Setter _currentUser til null siden ingen bruker er logget inn lenger
        _currentUser.value = null
        // Setter _isLoggedIn til false siden brukeren nå er logget ut
        _isLoggedIn.value = false
    }

    fun createUserWithEmailAndPassword(
        username: String,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()

        if (username.contains(" ")) {
            onFailure("Username cannot contain spaces between characters")
            return
        }

        isUsernameUnique(username) { isUnique ->
            if (isUnique) {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Brukeren ble opprettet
                            val user = auth.currentUser

                            val profileUpdates = userProfileChangeRequest {
                                displayName = username
                            }

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        val newUser = User(
                                            id = user.uid,
                                            userName = username,
                                            email = email,
                                            friendList = mutableListOf(),
                                            myReviews = mutableListOf(),
                                            favoriteCollection = mutableListOf(),
                                            completedCollection = mutableListOf(),
                                            wantToWatchCollection = mutableListOf(),
                                            droppedCollection = mutableListOf(),
                                            currentlyWatchingCollection = mutableListOf()
                                        )
                                        addUserToDatabase(newUser)

                                        onSuccess("User created with UID: ${user.uid} and username: ${user.displayName}")
                                        Log.d("FirebaseAuth", "User created with UID: ${user.uid}")
                                    }
                                }
                        } else {

                            // Opprettelse feilet
                            val exceptionMessage = task.exception?.message ?: "Unknown error"
                            onFailure(exceptionMessage)
                            Log.w("FirebaseAuth", "User creation failed", task.exception)
                        }
                    }
            } else {
                onFailure("Username already exists")
            }
        }

    }
}
