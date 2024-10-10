package backend

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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