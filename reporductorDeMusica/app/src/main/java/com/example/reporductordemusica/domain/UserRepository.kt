package com.example.reporductordemusica.domain

import android.util.Log
import com.example.reporductordemusica.Model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore

    fun registerUser(email: String, password: String, username: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = UserModel(email, username, emptyList())
                    saveUserToFirestore(user, { onSuccess(email) }, onFailure)
                } else {
                    Log.w("UserRepository", "Authentication failed: ${task.exception?.message}")
                    onFailure(task.exception ?: Exception("Authentication error"))
                }
            }
    }

    fun saveUserToFirestore(user: UserModel, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("users").document(user.email)
            .set(user)
            .addOnSuccessListener {
                onSuccess(user.email)
            }
            .addOnFailureListener { exception ->
                Log.w("UserRepository", "Error saving user: ${exception.message}")
                onFailure(exception)
            }
    }

    fun toggleFavoriteSong(userEmail: String, songId: String, onComplete: (Boolean) -> Unit) {
        val userDocRef = firestore.collection("users").document(userEmail)

        firestore.runTransaction { transaction ->
            val userSnapshot = transaction.get(userDocRef)
            val user = userSnapshot.toObject(UserModel::class.java) ?: return@runTransaction false

            val updatedSongs = if (user.idSongs.contains(songId)) {
                user.idSongs - songId
            } else {
                user.idSongs + songId
            }

            Log.d("UserRepository", "Updating user: $userEmail, songId: $songId, newSongsList: $updatedSongs")

            transaction.update(userDocRef, "idSongs", updatedSongs)
            updatedSongs.contains(songId)
        }.addOnSuccessListener { isAdded ->
            Log.d("UserRepository", "Transaction success, isAdded: $isAdded")
            onComplete(isAdded)
        }.addOnFailureListener { exception ->
            Log.w("UserRepository", "Error updating favorites: ${exception.message}")
            onComplete(false)
        }
    }

    fun getUserDetails(userEmail: String, onSuccess: (UserModel) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("users").document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    if (user != null) {
                        Log.d("UserRepository", "User details retrieved successfully: $user")
                        onSuccess(user)
                    } else {
                        Log.w("UserRepository", "User data conversion failed")
                        onFailure(Exception("User data conversion failed"))
                    }
                } else {
                    Log.w("UserRepository", "No such document")
                    onFailure(Exception("No such document"))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("UserRepository", "Error getting user details: ${exception.message}")
                onFailure(exception)
            }
    }
}
