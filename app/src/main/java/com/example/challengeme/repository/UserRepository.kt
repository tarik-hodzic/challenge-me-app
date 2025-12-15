package com.example.challengeme.repository

import com.example.challengeme.data.UserDao
import com.example.challengeme.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {
    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        userDao.login(email, password)
    }
    suspend fun register(user: UserEntity) = userDao.insert(user) > 0
    suspend fun isEmailTaken(email: String) = userDao.findByEmail(email) != null
    // Profile operations
    suspend fun getCurrentUser(email: String) = userDao.findByEmail(email)
    suspend fun getUserById(userId: Int) = userDao.findById(userId)
    suspend fun updateUser(user: UserEntity) = userDao.update(user) > 0
    suspend fun deleteUser(user: UserEntity) = userDao.delete(user) > 0

    // Progress operations
    suspend fun incrementStreak(userId: Int) = userDao.incrementStreak(userId)
    suspend fun updateAchievement(userId: Int, achievementId: Int) =
        userDao.updateAchievement(userId, achievementId)
}
