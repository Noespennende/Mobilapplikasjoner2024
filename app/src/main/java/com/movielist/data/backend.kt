package com.movielist.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.movielist.model.User





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