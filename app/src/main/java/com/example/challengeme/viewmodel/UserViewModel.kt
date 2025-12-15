package com.example.challengeme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.challengeme.data.UserEntity
import com.example.challengeme.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            onResult(user)
        }
    }

    fun register(user: UserEntity, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (repository.isEmailTaken(user.email)) {
                onError("Email already exists.")
            } else {
                repository.register(user)
                onSuccess()
            }
        }
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
