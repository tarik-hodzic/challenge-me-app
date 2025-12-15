package com.example.challengeme.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UserID")
    val userId: Int = 0,

    @ColumnInfo(name = "FirstName")
    val firstName: String? = null,

    @ColumnInfo(name = "LastName")
    val lastName: String? = null,

    @ColumnInfo(name = "Username")
    val username: String? = null,

    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Password")
    val password: String,

    @ColumnInfo(name = "Gender")
    val gender: String? = null,

    @ColumnInfo(name = "DateOfBirth")
    val dateOfBirth: String? = null,

    @ColumnInfo(name = "Address")
    val address: String? = null,

    @ColumnInfo(name = "Streak_days")
    val streak_days: Int = 0,

    @ColumnInfo(name = "AchievementID")
    val achievementId: Int? = null,

    @ColumnInfo(name = "LastCompletedDate")
    val lastCompletedDate: String = ""
)
