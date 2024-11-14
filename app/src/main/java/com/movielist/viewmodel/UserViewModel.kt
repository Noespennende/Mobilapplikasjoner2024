package com.movielist.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.movielist.data.FirebaseTimestampAdapter
import com.movielist.data.UUIDAdapter
import com.movielist.data.FirestoreRepository
import com.movielist.model.Episode
import com.movielist.model.ListItem
import com.movielist.model.Movie
import com.movielist.model.Production
import com.movielist.model.TVShow
import com.movielist.model.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel for å håndtere bruker-logikk
class UserViewModel : ViewModel() {


    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> get() = _loggedInUser

    private val _otherUser = MutableStateFlow<User?>(null)
    val otherUser: StateFlow<User?> get() = _otherUser

    // Funksjon for å sette User-objekt for innloggede bruker
    fun setLoggedInUser(uid: String) {
        getUser(uid) { fetchedUser ->
            _loggedInUser.value = fetchedUser
        }
    }

    // Funksjon for å sette *ANDRE* brukere - for å vise deres profiler, lister osv.
    fun setOtherUser(uid: String) {
        getUser(uid) { fetchedUser ->
            _otherUser.value = fetchedUser
        }
    }

    private fun getUser(userID: String, onSuccess: (User?) -> Unit) {
        firestoreRepository.fetchFirebaseUser(userID) { userJson ->
            val user = convertUserJsonToUserObject(userJson)
            if (user != null) {
                println("User: $user")
            } else {
                println("Failed to deserialize user.")
            }
            onSuccess(user);
        }
    }

    fun getUsersFriends(onComplete: (MutableList<User>) -> Unit) {
        val friendsList: MutableList<User> = mutableListOf()



        val friendIDList = loggedInUser.value?.friendList ?: return onComplete(friendsList)



        val totalFriends = friendIDList.size
        var loadedFriends = 0

        for (friendUID in friendIDList) {
            getUser(friendUID) { friend ->
                friend?.let {
                    friendsList.add(it)
                    loadedFriends++
                    if (loadedFriends == totalFriends) {
                        onComplete(friendsList)
                    }
                }
            }
        }
    }


    fun addOrMoveToUsersCollection(productionID: String, targetCollection: String) {
        val userID = loggedInUser.value?.id
        val user = loggedInUser.value

        if (userID.isNullOrEmpty() || user == null) {
            Log.e("UserViewModel", "UserID or user data is null.")
            return
        }

        // Finn riktig listItem i de forskjellige samlingene
        val listItem = user.currentlyWatchingCollection.find { it.production.imdbID == productionID }
            ?: user.wantToWatchCollection.find { it.production.imdbID == productionID }
            ?: user.droppedCollection.find { it.production.imdbID == productionID }
            ?: user.completedCollection.find { it.production.imdbID == productionID }

        if (listItem == null) {
            Log.e("UserViewModel", "List item with productionID: $productionID not found in any collection.")
            return
        }

        // Sjekk hvilken samling listItem tilhører (kilden)
        val sourceCollection = when {
            user.currentlyWatchingCollection.contains(listItem) -> "currentlyWatchingCollection"
            user.wantToWatchCollection.contains(listItem) -> "wantToWatchCollection"
            user.droppedCollection.contains(listItem) -> "droppedCollection"
            user.completedCollection.contains(listItem) -> "completedCollection"
            else -> null
        }

        if (sourceCollection == null || sourceCollection == targetCollection) {
            Log.e("UserViewModel", "Invalid source or target collection.")
            return
        }

        // Konverter listItem til map for lagring i Firestore
        val listItemMap = listItem.toMap()

        // Legg til i targetCollection og fjern fra sourceCollection
        firestoreRepository.addToCollection(
            userID, listItemMap, targetCollection,
            onSuccess = {
                Log.d("FirestoreAdd", "Successfully added to $targetCollection for user $userID")

                firestoreRepository.removeFromCollection(
                    userID, listItem, sourceCollection,
                    onSuccess = {
                        Log.d("FirestoreRemove", "Successfully removed from $sourceCollection")
                        updateUserCollections(listItem, sourceCollection, targetCollection)
                    },
                    onFailure = {
                        Log.e("FirestoreRemove", "Failed to remove from $sourceCollection")
                        updateUserCollections(listItem, sourceCollection, targetCollection)
                    },
                    onNotFound = {
                        Log.e("FirestoreRemove", "Item not found in $sourceCollection")
                        updateUserCollections(listItem, sourceCollection, targetCollection)
                    }
                )
            },
            onFailure = { e ->
                Log.e("FirestoreAdd", "Failed to add to $targetCollection for user $userID", e)
            }
        )
    }

    private fun updateUserCollections(listItem: ListItem, sourceCollection: String, targetCollection: String) {
        val user = loggedInUser.value

        // Fjern fra sourceCollection og legg til i targetCollection
        when (sourceCollection) {
            "currentlyWatchingCollection" -> user?.currentlyWatchingCollection?.remove(listItem)
            "wantToWatchCollection" -> user?.wantToWatchCollection?.remove(listItem)
            "droppedCollection" -> user?.droppedCollection?.remove(listItem)
            "completedCollection" -> user?.completedCollection?.remove(listItem)
        }

        when (targetCollection) {
            "currentlyWatchingCollection" -> user?.currentlyWatchingCollection?.add(listItem)
            "wantToWatchCollection" -> user?.wantToWatchCollection?.add(listItem)
            "droppedCollection" -> user?.droppedCollection?.add(listItem)
            "completedCollection" -> user?.completedCollection?.add(listItem)
        }
    }


    private fun convertUserJsonToUserObject(userJson: Map<String, Any>?): User? {

        if (userJson == null) return null

        val moshi = Moshi.Builder()
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

        // Konverter Map til JSON-streng
        val jsonAdapter = moshi.adapter(User::class.java)
        val json = mapToJson(userJson, moshi)

        // Deserialiser JSON-streng til User-objekt
        return jsonToUser(json, jsonAdapter)
    }

    private fun mapToJson(map: Map<String, Any>, moshi: Moshi): String {
        return moshi.adapter(Map::class.java).toJson(map)
    }

    private fun jsonToUser(json: String, jsonAdapter: JsonAdapter<User>): User? {
        return jsonAdapter.fromJson(json)
    }




}


