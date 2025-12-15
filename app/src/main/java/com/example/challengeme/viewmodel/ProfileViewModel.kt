package com.example.challengeme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.challengeme.data.UserEntity
import com.example.challengeme.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    fun loadUserData(userId: Int) {
        viewModelScope.launch {
            _user.value = repository.getUserById(userId)
        }
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun updateFirstName(firstName: String) {
        _user.value = _user.value?.copy(firstName = firstName)
    }

    fun updateLastName(lastName: String) {
        _user.value = _user.value?.copy(lastName = lastName)
    }

    fun updateGender(gender: String) {
        _user.value = _user.value?.copy(gender = gender)
    }

    fun updateDateOfBirth(dateOfBirth: String) {
        _user.value = _user.value?.copy(dateOfBirth = dateOfBirth)
    }

    fun updateAddress(address: String) {
        _user.value = _user.value?.copy(address = address)
    }

    fun saveProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _user.value?.let { currentUser ->
                if (repository.updateUser(currentUser)) {
                    onSuccess()
                } else {
                    onError("Failed to update profile")
                }
            } ?: onError("No user data available")
        }
    }

    fun deleteProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _user.value?.let { currentUser ->
                if (repository.deleteUser(currentUser)) {
                    onSuccess()
                } else {
                    onError("Failed to delete account")
                }
            } ?: onError("No user data available")
        }
    }
}

class ProfileViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}