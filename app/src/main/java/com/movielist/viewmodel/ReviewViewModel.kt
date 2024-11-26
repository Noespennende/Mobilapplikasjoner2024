package com.movielist.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.movielist.data.FirebaseTimestampAdapter
import com.movielist.data.FirestoreRepository
import com.movielist.data.UUIDAdapter
import com.movielist.model.Production
import com.movielist.model.ProductionType
import com.movielist.model.Review
import com.movielist.model.ReviewDTO
import com.movielist.model.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ReviewViewModel(
) : ViewModel() {

    private val firestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance())


    suspend fun getReviewsAllTime(pageSize: Long = 10, lastVisible: Any? = null): Pair<List<Review>, Boolean> {
        return try {
            // Hent anmeldelser fra firestoreRepository
            val (reviewsRaw, hasMoreData) = firestoreRepository.getReviewsAllTime(pageSize, lastVisible)

            // Konverter den rå JSON-en til Review-objekter
            val reviewsObjects = reviewsRaw.mapNotNull { convertReviewJsonToReviewObject(it) }

            // Returner listen med Review-objekter og om det er flere anmeldelser
            Pair(reviewsObjects, hasMoreData)

        } catch (exception: Exception) {
            Log.d("ReviewViewModel", "Failed to fetch reviews: $exception")
            Pair(emptyList(), false)  // Returner en tom liste og false for flere data
        }
    }

    suspend fun getReviewsFromPastWeek(): List<Review> {
        return try {
            // Henter anmeldelser for siste uken
            val reviewsRaw = firestoreRepository.getReviewsFromPastWeek()
            val reviewsObjects = reviewsRaw.mapNotNull { convertReviewJsonToReviewObject(it) }

            // Returner listen med Review-objekter
            reviewsObjects
        } catch (exception: Exception) {
            Log.d("ReviewViewModel", "Failed to fetch reviews: $exception")
            emptyList()  // Returner en tom liste ved feil
        }
    }

    suspend fun getReviewsFromThisMonth(): List<Review> {
        return try {
            // Henter anmeldelser for siste uken
            val reviewsRaw = firestoreRepository.getReviewsFromThisMonth()
            val reviewsObjects = reviewsRaw.mapNotNull { convertReviewJsonToReviewObject(it) }

            // Returner listen med Review-objekter
            reviewsObjects
        } catch (exception: Exception) {
            Log.d("ReviewViewModel", "Failed to fetch reviews: $exception")
            emptyList()  // Returner en tom liste ved feil
        }
    }

    suspend fun getReviewsByProduction(productionID: String, productionType: String): List<Review> {
        return try {
            // Hent anmeldelser fra Firestore
            val reviewsRaw = firestoreRepository.getReviewByProduction(productionID, productionType)
            val reviewsObjects = reviewsRaw.mapNotNull { convertReviewJsonToReviewObject(it) }

            Log.d("DEBUG", "GetReviewsByProduction returning $reviewsObjects")

            // Returner listen med Review-objekter
            reviewsObjects
        } catch (exception: Exception) {
            Log.d("ReviewViewModel", "Failed to fetch reviews: $exception")
            emptyList()  // Returner en tom liste ved feil
        }
    }

    suspend fun getReviewByID(reviewID: String, productionType: ProductionType, productionID: String): Review? {
        return try {
            // Hent anmeldelser fra Firestore
            Log.d("problem", "RVM - $productionType")
            val reviewRaw = firestoreRepository.getReviewById(reviewID, productionType, productionID)
            val reviewObject = requireNotNull(convertReviewJsonToReviewObject(reviewRaw)) {
                return null
            }

            reviewObject
        } catch (exception: Exception) {
            Log.d("ReviewViewModel", "Failed to fetch reviews: $exception")
            return null
        }
    }

    suspend fun getReviewsByUser(collectionID: String, productionID: String, userID: String): List<Review> {
        return try {
            // Hent anmeldelser fra Firestore
            val reviewsRaw = firestoreRepository.getReviewsByUser(collectionID, productionID, userID)
            val reviewsObjects = reviewsRaw.mapNotNull { convertReviewJsonToReviewObject(it) }

            // Returner listen med Review-objekter
            reviewsObjects
        } catch (exception: Exception) {
            Log.d("ReviewViewModel", "Failed to fetch reviews: $exception")
            emptyList()  // Returner en tom liste ved feil
        }
    }



    private fun convertReviewJsonToReviewObject(reviewJson: Map<String, Any>?): Review? {
        if (reviewJson == null) return null

        val moshi = Moshi.Builder()
            .add(FirebaseTimestampAdapter()) // Adapter for Firestore Timestamps
            .add(UUIDAdapter())              // Adapter for UUID
            .addLast(KotlinJsonAdapterFactory()) // For Kotlin-klasser
            .build()

        // Konverter Map til JSON-streng
        val jsonAdapter = moshi.adapter(Review::class.java)
        val json = mapToJson(reviewJson, moshi)

        // Deserialiser JSON-streng til Review-objekt
        return jsonToReview(json, jsonAdapter)
    }

    private fun mapToJson(map: Map<String, Any>, moshi: Moshi): String {
        return moshi.adapter(Map::class.java).toJson(map)
    }

    private fun jsonToReview(json: String, jsonAdapter: JsonAdapter<Review>): Review? {
        return jsonAdapter.fromJson(json)
    }

    fun createReviewDTO(review: Review, reviewer: User, production: Production): ReviewDTO {
        return ReviewDTO(
            reviewID = review.reviewID,
            score = review.score,
            productionID = review.productionID,
            reviewerID = review.reviewerID,
            reviewBody = review.reviewBody,
            postDate = review.postDate,
            likes = review.likes,
            reviewerUserName = reviewer.userName,
            reviewerProfileImage = reviewer.profileImageID,
            productionPosterUrl = production.posterUrl,
            productionTitle = production.title,
            productionReleaseDate = production.releaseDate,
            productionType = production.type
        )
    }

    fun splitReviewID(reviewID: String): Triple<String, String, String> {

        val parts = reviewID.split("_")

        if (parts.size != 3) {
            Log.d("splitReviewID", "Feil format på reviewID: $reviewID")
        }


        val collectionType = parts[0]  // "RMOV" for movie reviews, "RTV" for TV shows, etc.
        val productionID = parts[1]   // "53344" for the production ID
        val uuid = parts[2]           // UUID delen

        // Returner de tre delene
        return Triple(collectionType, productionID, uuid)
    }


}