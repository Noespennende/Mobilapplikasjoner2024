package backend

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore

fun getUserInfo(userID: String, onSuccess: (Map<String, String?>) -> Unit) {

    val db = Firebase.firestore

    //val docRef: DocumentReference = FirebaseFirestore.getInstance().document("users/testuser")
    /*
    *  Kan også bruke denne i stedet for db.collection().document().get()
    *  Så blir det basically docRef.get().addOnsuccessListener
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

fun createUserWithEmailAndPassword(
    email: String,
    password: String,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Brukeren ble opprettet
                val user = auth.currentUser
                onSuccess("User created with UID: ${user?.uid}")
                Log.d("FirebaseAuth", "User created with UID: ${user?.uid}")
            } else {
                // Opprettelse feilet
                val exceptionMessage = task.exception?.message ?: "Unknown error"
                onFailure(exceptionMessage)
                Log.w("FirebaseAuth", "User creation failed", task.exception)
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

fun getSignedInUser(): FirebaseUser? {
    val auth = FirebaseAuth.getInstance()

    return auth.currentUser
}