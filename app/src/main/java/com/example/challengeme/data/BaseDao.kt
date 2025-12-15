package com.example.challengeme.data

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: T): Long

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int
}