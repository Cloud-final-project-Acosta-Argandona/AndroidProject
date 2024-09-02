package com.example.reporductordemusica.Model

data class UserModel(
    val email: String = "",
    val username: String = "",
    val idSongs: List<String> = emptyList()
)
