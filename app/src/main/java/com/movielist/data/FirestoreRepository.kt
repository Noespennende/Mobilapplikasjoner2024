package com.movielist.data

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.movielist.model.Episode
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.TVShow
import com.movielist.model.User
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID

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

                Log.d(
                    "FirebaseSuccess",
                    "Document ID: $documentID, First Name: $firstName, Last Name: $lastName"
                )

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

    fun updateUserField(userId: String, updates: Map<String, Any>) {
        val user = db.collection("users").document(userId)

        user.update(updates)

            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to update user field", exception)
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

    suspend fun fetchAllUsers(): List<Map<String, Any>>? {
        val db = Firebase.firestore

        return try {
            val document = db.collection("users").get().await()

            val users = document.documents.map { document ->
                document.data ?: emptyMap()
            }

            users
        } catch (exception: Exception) {
            Log.w("FirebaseFailure", "Error getting document", exception)
            null
        }
    }

    suspend fun fetchUsersFromFirebase(query: String): List<User>? {
        val db = Firebase.firestore

        return try {
            val querySnapshot = db.collection("users")
                .whereGreaterThanOrEqualTo("userName", query)
                .whereLessThanOrEqualTo("userName", query)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                val email = document.getString("email") ?: ""
                val userName = document.getString("userName") ?: ""

                val profileImageID = document.get("profileImageID")
                val profileImageIDString = when (profileImageID) {
                    is String -> profileImageID
                    else -> ""
                }

                User(
                    email = email,
                    userName = userName,
                    profileImageID = profileImageIDString
                )
            }
        } catch (exception: Exception) {
            Log.w("FirebaseFailure", "Error fetching users", exception)
            null
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

    fun addToFavorites(
        userID: String,
        listItemMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDoc = Firebase.firestore.collection("users").document(userID)

        Log.d("FirestoreUpdate", "Adding to favoriteCollection: $listItemMap")

        userDoc.update("favoriteCollection", FieldValue.arrayUnion(listItemMap))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun removeFromFavorites(
        userID: String,
        listItem: ListItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onNotFound: () -> Unit
    ) {
        removeFromCollectionHelper(userID, listItem, "favoriteCollection", onSuccess, onFailure, onNotFound)
    }

    fun batchUpdateFavoriteStatusAllCollections(
        userID: String,
        listItemID: String,
        isFavorite: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocRef = db.collection("users").document(userID)

        val collections = listOf("completedCollection", "currentlyWatchingCollection", "droppedCollection", "favoriteCollection", "wantToWatchCollection")

        val batch = db.batch()

        // Gå gjennom alle kolleksjon og finn og oppdater listItem
        collections.forEach { collection ->
            userDocRef.get()
                .addOnSuccessListener { document ->
                    val collectionList = document.get(collection) as? List<Map<String, Any>> ?: emptyList()

                    val updatedCollection = collectionList.map { item ->
                        if (item["id"] == listItemID) {
                            // Her oppdaterer vi kun "loggedInUsersFavorite"
                            item.toMutableMap().apply { put("loggedInUsersFavorite", isFavorite) }
                        } else {
                            item
                        }
                    }

                    // Hvis det er en endring, legg til oppdateringen i batchen
                    if (updatedCollection != collectionList) {
                        val updateData = mapOf(collection to updatedCollection)
                        batch.update(userDocRef, updateData)
                        Log.d("Firestore", "$collection updated in batch")
                    }

                    // Når alle oppdateringene er samlet, commit batchen
                    if (collections.indexOf(collection) == collections.size - 1) {
                        batch.commit()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Batch update completed successfully.")
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error committing batch update", exception)
                                onFailure(exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching document for $collection", exception)
                    onFailure(exception)
                }
        }
    }

    fun updateCurrentEpisodeField(
        userID: String,
        listItemID: String,
        currentEpisodeValue: Int,
        collection: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val userDocRef = db.collection("users").document(userID)


        userDocRef.get()
            .addOnSuccessListener { document ->

                val collectionList = document.get(collection) as? List<Map<String, Any>> ?: emptyList()

                val updatedCollection = collectionList.map { item ->
                    if (item["id"] == listItemID) {

                        item.toMutableMap().apply { put("currentEpisode", currentEpisodeValue) }
                    } else {
                        item
                    }
                }

                val updateData = mapOf(collection to updatedCollection)
                userDocRef.update(updateData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "$collection updated successfully.")
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error updating $collection", exception)
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching document for $collection", exception)
                onFailure(exception)
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

    suspend fun getReviewById(
        reviewID: String,        // ID for den spesifikke review-en
        productionType: String,  // Type produksjon (Movie eller TVShow)
        productionID: String    // ID for produksjonen (f.eks. 533535 for en film)
    ): Map<String, Any>? {
        val db = FirebaseFirestore.getInstance()

        // Bestem hvilken samling som skal brukes basert på productionType
        val collectionType = when (productionType) {
            "Movie" -> "movieReviews"
            "TVShow" -> "tvShowReviews"
            else -> {
                throw IllegalArgumentException("Invalid production type: $productionType")
            }
        }

        return try {

            val pathProduction = "reviews/$collectionType/$productionID"

            // Naviger til dokumentet i pathen (f.eks reviews/moveReviews/533535/{reviewID})
            val reviewDocument = db.collection(pathProduction)
                .document(reviewID)

            // Hent anmeldelsen fra dokumentet
            val reviewData = reviewDocument.get().await()

            if (reviewData.exists()) {
                reviewData.data  // Returner anmeldelsesdataene som et Map<String, Any>

            } else {
                Log.d("Firestore-Reviews", "Review not found")
                null
            }
        } catch (exception: Exception) {

            Log.e("Firestore-Reviews", "Failed to fetch review with ID $reviewID for $productionType and ID $productionID", exception)
            throw exception
        }
    }

    suspend fun getReviewsByUser(collectionID: String, productionID: String, reviewerID: String): List<Map<String, Any>> {
        val db = FirebaseFirestore.getInstance()

        // Resultatvariabel som holder alle reviews
        val reviewsList = mutableListOf<Map<String, Any>>()

        try {
            // Hent anmeldelsene basert på reviewerID
            val result = db.collection("reviews") // Hovedsamlingen
                .document(collectionID) // Dokument for produksjonstype
                .collection(productionID) // Samlingen for spesifik produksjonsID
                .whereEqualTo("reviewerID", reviewerID) // Filtrere for reviewerID som samsvarer med reviewerID
                .get() // Hent alle dokumentene som matcher
                .await() // Bruker await() for å vente på resultatet

            if (!result.isEmpty) {
                // Hvis result er ikke tomt, legg til dataene til reviewsList
                for (document in result) {
                    // Legg til dokumentdata som et Map (dokumentet som er hentet fra Firestore)
                    reviewsList.add(document.data)
                }
                // Logge for debugging
                Log.d("Firestore-Reviews", "Funnet ${reviewsList.size} reviews.")
            } else {
                Log.d("Firestore-Reviews", "Ingen reviews funnet for reviewerID: $reviewerID.")
            }
        } catch (e: Exception) {
            // Håndter feil
            Log.d("Firestore-Reviews", "Feil ved henting av reviews: $e")
        }

        // Returner listen med dokumenter som Map<String, Any>
        return reviewsList
    }

    suspend fun getReviewsFromPastWeek(): List<Map<String, Any>> {
        val db = FirebaseFirestore.getInstance()
        val allReviews = mutableListOf<Map<String, Any>>()

        /* Beregn starten og slutten av inneværende uke */

        val currentDate = Calendar.getInstance()

        val pastWeek = currentDate.clone() as Calendar
        pastWeek.add(Calendar.DATE, -7)

        val startOfPeriod = pastWeek.time
        val endOfPeriod = currentDate.time

        /**/

        try {

            val topLevelCollections = listOf("movieReviews", "tvShowReviews")

            for (collection in topLevelCollections) {

                // Hent dokumentet med productionIDs (henter movieReviews/tvShowReviews)
                val metaDocument = db.collection("reviews")
                    .document(collection)
                    .get()
                    .await()

                // er felt direkte i movieReviews/tvShowReviews
                if (metaDocument.exists()) {
                    val productionIDs = metaDocument.get("productionIDs") as? List<*>
                        ?: emptyList<String>()


                    for (productionID in productionIDs) {

                        // Hent anmeldelser i sub-kolleksjonen

                        val reviews = db.collection("reviews")
                            .document(collection)
                            .collection(productionID.toString())
                            .whereGreaterThanOrEqualTo("postDate", startOfPeriod)
                            .whereLessThan("postDate", endOfPeriod)
                            .get()
                            .await()

                        for (review in reviews.documents) {
                            review.data?.let { allReviews.add(it) }
                        }
                    }
                }
            }

            return allReviews
        } catch (e: Exception) {
            throw e // Bør nok håndteres annerledes :D
        }
    }

    suspend fun getReviewsFromThisMonth(): List<Map<String, Any>> {
        val db = FirebaseFirestore.getInstance()
        val allReviews = mutableListOf<Map<String, Any>>()

        /* Beregn starten og slutten av inneværende uke */

        val currentDate = Calendar.getInstance()

        // Beregn starten av måneden
        val startOfMonth = currentDate.clone() as Calendar
        startOfMonth.apply {
            set(Calendar.DAY_OF_MONTH, 1) // Starten av denne måneden
            set(Calendar.HOUR_OF_DAY, 0)  // Start på dagen
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Beregn slutten av måneden
        val endOfMonth = currentDate.clone() as Calendar
        endOfMonth.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) // Siste dag i måneden
            set(Calendar.HOUR_OF_DAY, 23) // Slutt på dagen
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val startOfPeriod = startOfMonth.time
        val endOfPeriod = endOfMonth.time

        Log.d("Revieww", "startofPeriod: $startOfPeriod")
        Log.d("Revieww", "endOfPeriod: $endOfPeriod")

        /**/

        try {

            val topLevelCollections = listOf("movieReviews", "tvShowReviews")

            for (collection in topLevelCollections) {

                // Hent dokumentet med productionIDs (henter movieReviews/tvShowReviews)
                val metaDocument = db.collection("reviews")
                    .document(collection)
                    .get()
                    .await()

                // er felt direkte i movieReviews/tvShowReviews
                if (metaDocument.exists()) {
                    val productionIDs = metaDocument.get("productionIDs") as? List<*>
                        ?: emptyList<String>()


                    for (productionID in productionIDs) {

                        // Hent anmeldelser i sub-kolleksjonen

                        val reviews = db.collection("reviews")
                            .document(collection)
                            .collection(productionID.toString())
                            .whereGreaterThanOrEqualTo("postDate", startOfPeriod)
                            .whereLessThan("postDate", endOfPeriod)
                            .get()
                            .await()

                        for (review in reviews.documents) {
                            review.data?.let { allReviews.add(it) }
                        }
                    }
                }
            }

            return allReviews
        } catch (e: Exception) {
            throw e // Bør nok håndteres annerledes :D
        }
    }


    suspend fun uploadProfileImage(imageUri: Uri?): String {
        if (imageUri == null) throw IllegalArgumentException("Ingen bilde valgt")

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Bruker er ikke logget inn")

        val storageRef = FirebaseStorage.getInstance().reference

        val imageRef = storageRef.child("profile_pictures/$userId/${UUID.randomUUID()}.jpg")

        // Laster opp bilde
        imageRef.putFile(imageUri).await()

        // Returnerer URL-en
        return imageRef.downloadUrl.await().toString()
    }

    suspend fun saveImageUrlToUserDoc(imageUrl: String) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Bruker er ikke logget inn")

        val db = FirebaseFirestore.getInstance()

        val userDocRef = db.collection("users").document(userId)
        val userProfileData = mapOf("profileImageID" to imageUrl)

        userDocRef.update(userProfileData).await()
    }

}
