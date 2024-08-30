package com.example.reporductordemusica.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reporductordemusica.domain.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    fun registerUser(email: String, password: String, username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            userRepository.registerUser(email, password, username, onSuccess, onFailure)
        }
    }
}
