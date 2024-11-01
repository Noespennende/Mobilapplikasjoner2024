package com.movielist.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.movielist.model.Episode
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.TVShow
import com.movielist.model.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

fun getUserInfo(userID: String, onSuccess: (Map<String, String?>) -> Unit) {

    val db = Firebase.firestore



    //val docRef: DocumentReference = FirebaseFirestore.getInstance().document("users/testuser")
    /*
    *  Kan også bruke denne i stedet for db.collection().document().get()
    *  Så blir det basically docRef.get().addOnSuccessListener
    * */

    db.collection("users")
        .document(userID)
        .get()
        .addOnSuccessListener { document ->
            val documentID = document.id
            val firstName = document.getString("firstName")
            val lastName = document.getString("lastName")

            Log.d("FirebaseSuccess", "Document ID: $documentID, First Name: $firstName, Last Name: $lastName")

            val userData = mapOf(
                "documentID" to documentID,
                "firstName" to firstName,
                "lastName" to lastName
            )

            onSuccess(userData)

        }
        .addOnFailureListener { exception ->
            Log.w("FirebaseFailure", "Error getting document", exception)
        }

}

fun getUsersCompletedCollection(userID: String, onSuccess: (List<String>) -> Unit) {
    val db = Firebase.firestore

    db.collection("users")
        .document(userID)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {

                val rawCompletedCollection = (document.get("completedShows") as? List<*>)
                val completedCollection = rawCompletedCollection?.filterIsInstance<String>()

                // Returner gyldige strenger eller en tom liste
                onSuccess(completedCollection ?: emptyList())
            }
        }
        .addOnFailureListener { exception ->
            Log.e("TAG", "Error fetching document", exception)
        }
}

fun getUsersWatchingCollection(
    userID: String,
    onSuccess: (List<String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = Firebase.firestore

    db.collection("users")
        .document(userID)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {

                val rawWatchingCollection = (document.get("currentlyWatchingShows") as? List<*>)
                val watchingCollection = rawWatchingCollection?.filterIsInstance<String>()

                // Returner gyldige strenger eller en tom liste
                onSuccess(watchingCollection ?: emptyList())
            } else {
                onFailure(Exception("Document not found"))
            }
        }
        .addOnFailureListener { exception ->
            Log.e("TAG", "Error fetching document", exception)
            onFailure(exception)
        }
}

fun fetchFirebaseUser(userID: String, onSuccess: (Map<String, Any>?) -> Unit) {
    val db = Firebase.firestore

    db.collection("users")
        .document(userID)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val userJson = document.data // Map<String, Any>? - Firebase dokumentdata
                onSuccess(userJson) // Returner JSON til den som kaller funksjonen
            } else {
                println("Document not found")
                onSuccess(null) // Ingen dokument funnet
            }
        }
        .addOnFailureListener { exception ->
            Log.w("FirebaseFailure", "Error getting document", exception)
            onSuccess(null) // Returner null ved feil
        }
}

fun addCurrentlyWatchingShow(
    userID: String,
    listItem: ListItem,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val db = Firebase.firestore

    // Konverter produksjon til Map
    val productionMap = when (val production = listItem.production) {
        is Movie -> mapOf(
            "type" to production.type,
            "imdbID" to production.imdbID,
            "title" to production.title,
            "description" to production.description,
            "genre" to production.genre,
            "releaseDate" to production.releaseDate.time, // Konverter til long
            "actors" to production.actors,
            "rating" to production.rating,
            "reviews" to production.reviews,
            "posterUrl" to production.posterUrl,
            "lengthMinutes" to production.lengthMinutes,
            "trailerUrl" to production.trailerUrl
        )
        is Episode -> mapOf(
            "type" to production.type,
            "imdbID" to production.imdbID,
            "title" to production.title,
            "description" to production.description,
            "genre" to production.genre,
            "releaseDate" to production.releaseDate.time, // Konverter til long
            "actors" to production.actors,
            "rating" to production.rating,
            "reviews" to production.reviews,
            "posterUrl" to production.posterUrl,
            "lengthMinutes" to production.lengthMinutes,
            "seasonNr" to production.seasonNr,
            "episodeNr" to production.episodeNr
        )
        is TVShow -> mapOf(
            "type" to production.type,
            "imdbID" to production.imdbID,
            "title" to production.title,
            "description" to production.description,
            "genre" to production.genre,
            "releaseDate" to production.releaseDate.time, // Konverter til long
            "actors" to production.actors,
            "rating" to production.rating,
            "reviews" to production.reviews,
            "posterUrl" to production.posterUrl,
            "episodes" to production.episodes,
            "seasons" to production.seasons
        )
        else -> null
    }

    // Hvis produksjon ikke er gyldig, avslutt funksjonen
    if (productionMap == null) {
        onFailure("Invalid production type")
        return
    }

    // Konverter ListItem til Map
    val listItemMap = mapOf(
        "id" to listItem.id,
        "currentEpisode" to listItem.currentEpisode,
        "score" to listItem.score,
        "production" to productionMap,
        "lastUpdated" to listItem.lastUpdated.time // Konverter til long
    )

    // Oppdater dokumentet til brukeren
    db.collection("users")
        .document(userID)
        .update("completedShows", FieldValue.arrayUnion(listItemMap))
        .addOnSuccessListener {
            onSuccess() // Kall onSuccess når elementet er lagt til
        }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Unknown error") // Kall onFailure med feilmelding
        }
}



