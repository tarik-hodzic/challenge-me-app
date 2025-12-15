package com.example.challengeme.data

import androidx.room.*

@Dao
interface UserDao : BaseDao<UserEntity> {
    @Query("SELECT * FROM users WHERE Email = :email AND Password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE Email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE UserID = :userId LIMIT 1")
    suspend fun findById(userId: Int): UserEntity?

    @Query("UPDATE users SET Streak_days = Streak_days + 1 WHERE UserID = :userId")
    suspend fun incrementStreak(userId: Int)

    @Query("UPDATE users SET AchievementID = :achievementId WHERE UserID = :userId")
    suspend fun updateAchievement(userId: Int, achievementId: Int)

    @Query("UPDATE users SET Streak_days = :streak, LastCompletedDate = :date WHERE UserID = :userId")
    suspend fun updateStreak(userId: Int, streak: Int, date: String)

}