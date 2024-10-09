package backend

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FireBaseAuth {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getSignedInUser(): FirebaseUser? {

        return firebaseAuth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logOut() {
        firebaseAuth.signOut()
    }
}