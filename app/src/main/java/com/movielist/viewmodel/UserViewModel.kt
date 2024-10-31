package com.movielist.viewmodel

import androidx.lifecycle.ViewModel
import com.movielist.data.FirebaseTimestampAdapter
import com.movielist.data.UUIDAdapter
import com.movielist.data.fetchFirebaseUser
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel for å håndtere bruker-logikk
class UserViewModel : ViewModel() {

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
        fetchFirebaseUser(userID) { userJson ->
            val user = convertUserJsonToUserObject(userJson)
            if (user != null) {
                println("User: $user")
            } else {
                println("Failed to deserialize user.")
            }
            onSuccess(user);
        }
    }

    private fun convertUserJsonToUserObject(userJson: Map<String, Any>?): User? {
        if (userJson == null) return null

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
