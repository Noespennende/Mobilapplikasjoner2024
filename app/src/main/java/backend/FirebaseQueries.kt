package backend

import com.movielist.utils.ProductionAdapter
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.movielist.data.Episode
import com.movielist.data.Movie
import com.movielist.data.Production
import com.movielist.data.TVShow
import com.movielist.data.User
import com.movielist.utils.FirebaseTimestampAdapter
import com.movielist.utils.UUIDAdapter
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

fun getUser(userID: String, onSuccess: (User) -> Unit) {

    val db = Firebase.firestore

    val moshi: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Production::class.java, "type")
                .withSubtype(Movie::class.java, "Movie")
                .withSubtype(TVShow::class.java, "TVShow")
                .withSubtype(Episode::class.java, "Episode")
        )
        .add(FirebaseTimestampAdapter())  // Adapter for Firestore Timestamps
        .add(UUIDAdapter())  // Adapter for UUID
        .addLast(KotlinJsonAdapterFactory())  // For Kotlin-klasser
        .build()

    val jsonAdapter = moshi.adapter(User::class.java)

    db.collection("users")
        .document(userID)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val userJson = document.data // Map<String, Any> - Firebase dokumentdata

                val json = moshi.adapter(Map::class.java).toJson(userJson)
                val user = jsonAdapter.fromJson(json)

                if (user != null) {
                    println("User: $user")
                    onSuccess(user)
                } else {
                    println("Failed to deserialize user.")
                }
            } else {
                println("Document not found")
            }
        }
        .addOnFailureListener { exception ->
            Log.w("FirebaseFailure", "Error getting document", exception)
        }
}