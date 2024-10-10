package backend

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.movielist.data.User


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
                                        profileImageID = 0,
                                        completedShows = mutableListOf(),
                                        wantToWatchShows = mutableListOf(),
                                        droppedShows = mutableListOf(),
                                        currentlyWatchingShows = mutableListOf()
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


// Funksjon for innlogging
fun logInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onFailure(task.exception?.message ?: "Innlogging mislyktes.")
            }
        }
}


fun isUsernameUnique(username: String, onResult: (Boolean) -> Unit) {

    val db = Firebase.firestore

    db.collection("users")
        .whereEqualTo("userName", username)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                onResult(true)
                Log.d("Testing", "YEY username is free!")
            } else {
                onResult(false)
                Log.d("Testing", "Username already exists")
            }
        }
}

fun addUserToDatabase(user: User) {

    Log.d("Firestore", "username is free!")
    val db = Firebase.firestore

    db.collection("users")
        .document(user.id)
        .set(user)
        .addOnSuccessListener {
            Log.d("Firestore", "user successfully written")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error writing user", e)
        }

}