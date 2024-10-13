package backend

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.movielist.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel for å håndtere bruker-logikk
class UserViewModel : ViewModel() {

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> get() = _loggedInUser

    private val _otherUser = MutableStateFlow<User?>(null)
    val otherUser: StateFlow<User?> get() = _otherUser

    // Funksjon for å hente User-objekt for innloggede bruker
    fun fetchLoggedInUser(uid: String) {
        getUser(uid) { fetchedUser ->
            _loggedInUser.value = fetchedUser
        }
    }

    // Funksjon for å hente *ANDRE* brukere - for å vise deres profiler, lister osv.
    fun fetchOtherUser(uid: String) {
        getUser(uid) { fetchedUser ->
            _otherUser.value = fetchedUser
        }
    }

}