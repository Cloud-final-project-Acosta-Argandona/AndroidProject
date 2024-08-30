package com.example.reporductordemusica.domain

import android.util.Log
import com.example.reporductordemusica.Model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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

}
