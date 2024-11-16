package com.movielist.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.movielist.model.Episode
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.Review
import com.movielist.model.TVShow
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

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

    suspend fun fetchFirebaseUser(userID: String): Map<String, Any>? {
        val db = Firebase.firestore

        return try {
            // Bruk await direkte på Firebase Firestore get()-kallet
            val document = db.collection("users")
                .document(userID)
                .get()
                .await() // Suspenderende kall på get()

            // Returner dokumentdata hvis dokumentet finnes
            document.data
        } catch (exception: Exception) {
            // Håndter eventuelle feil og returner null
            Log.w("FirebaseFailure", "Error getting document", exception)
            null
        }
    }

    fun addToCollection(
        userID: String,
        listItemMap: Map<String, Any>,
        targetCollection: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        when (targetCollection) {
            "completedCollection" -> addToCompletedCollection(userID, listItemMap, onSuccess, onFailure)
            "wantToWatchCollection" -> addToWantToWatchCollection(userID, listItemMap, onSuccess, onFailure)
            "currentlyWatchingCollection" -> addToCurrentlyWatchingCollection(userID, listItemMap, onSuccess, onFailure)
            "droppedCollection" -> addToDroppedCollection(userID, listItemMap, onSuccess, onFailure)
            else -> {
                Log.e("FirestoreAdd", "Unknown target collection: $targetCollection")
                onFailure(Exception("Unknown target collection"))
            }
        }
    }

    // Spesifikke funksjoner for hver samling
    private fun addToCompletedCollection(
        userID: String,
        listItemMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDoc = Firebase.firestore.collection("users").document(userID)

        Log.d("FirestoreUpdate", "Adding to completedCollection: $listItemMap")

        userDoc.update("completedCollection", FieldValue.arrayUnion(listItemMap))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    private fun addToWantToWatchCollection(
        userID: String,
        listItemMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDoc = Firebase.firestore.collection("users").document(userID)

        Log.d("FirestoreUpdate", "Adding to wantToWatchCollection: $listItemMap")

        userDoc.update("wantToWatchCollection", FieldValue.arrayUnion(listItemMap))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    private fun addToCurrentlyWatchingCollection(
        userID: String,
        listItemMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDoc = Firebase.firestore.collection("users").document(userID)

        Log.d("FirestoreUpdate", "Adding to currentlyWatchingCollection: $listItemMap")

        userDoc.update("currentlyWatchingCollection", FieldValue.arrayUnion(listItemMap))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    private fun addToDroppedCollection(
        userID: String,
        listItemMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDoc = Firebase.firestore.collection("users").document(userID)

        Log.d("FirestoreUpdate", "Adding to droppedCollection: $listItemMap")

        userDoc.update("droppedCollection", FieldValue.arrayUnion(listItemMap))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun removeFromCollection(
        userID: String,
        listItem: ListItem,
        collectionName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        when (collectionName) {
            "completedCollection" -> removeFromCompletedCollection(userID, listItem, onSuccess, onFailure, onNotFound)
            "wantToWatchCollection" -> removeFromWantToWatchCollection(userID, listItem, onSuccess, onFailure, onNotFound)
            "currentlyWatchingCollection" -> removeFromCurrentlyWatchingCollection(userID, listItem, onSuccess, onFailure, onNotFound)
            "droppedCollection" -> removeFromDroppedCollection(userID, listItem, onSuccess, onFailure, onNotFound)
            else -> {
                Log.e("FirestoreRemove", "Unknown collection: $collectionName")
                onFailure(Exception("Unknown collection"))
            }
        }
    }

    // Spesifikke funksjoner for å fjerne fra hver samling
    private fun removeFromCompletedCollection(
        userID: String,
        listItem: ListItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        removeFromCollectionHelper(userID, listItem, "completedCollection", onSuccess, onFailure, onNotFound)
    }

    private fun removeFromWantToWatchCollection(
        userID: String,
        listItem: ListItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        removeFromCollectionHelper(userID, listItem, "wantToWatchCollection", onSuccess, onFailure, onNotFound)
    }

    private fun removeFromCurrentlyWatchingCollection(
        userID: String,
        listItem: ListItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        removeFromCollectionHelper(userID, listItem, "currentlyWatchingCollection", onSuccess, onFailure, onNotFound)
    }

    private fun removeFromDroppedCollection(
        userID: String,
        listItem: ListItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        removeFromCollectionHelper(userID, listItem, "droppedCollection", onSuccess, onFailure, onNotFound)
    }

    // Hjelpefunksjon for å håndtere fjerning fra samling
    private fun removeFromCollectionHelper(
        userID: String,
        listItem: ListItem,
        collectionName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        val userDocument = Firebase.firestore.collection("users").document(userID)

        userDocument.get().addOnSuccessListener { document ->
            val collection = document.get(collectionName) as? List<Map<String, Any>> ?: emptyList()

            // Finn objektet som matcher IMDb ID-en
            val itemToRemove = collection.find {
                val production = it["production"] as? Map<*, *>
                production?.get("imdbID") == listItem.production.imdbID
            }

            if (itemToRemove != null) {
                userDocument.update(collectionName, FieldValue.arrayRemove(itemToRemove))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            } else {
                onNotFound()
            }
        }.addOnFailureListener { e ->
            onFailure(e)
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


    suspend fun getReviewByProduction(
        productionID: String,
        productionType: String
    ): List<Map<String, Any>> {
        val db = FirebaseFirestore.getInstance()

        val collection = when (productionType) {
            "Movie" -> "movieReviews"
            "TVShow" -> "tvShowReviews"
            else -> null
        }

        if (collection == null) {
            throw IllegalArgumentException("Invalid production type: $productionType")
        }

        return try {
            // Hent alle reviews fra sub-kolleksjonen basert på productionID
            val reviews = db.collection("reviews")
                .document(collection)
                .collection(productionID)
                .get()
                .await() // Bruker await for å vente på resultatet før vi går videre

            // Samle alle reviews i en liste og returnere som en vanlig liste
            reviews.documents.mapNotNull { it.data }
        } catch (exception: Exception) {
            // Kaster en feil hvis det skjer en unntak
            throw exception
        }
    }

}
