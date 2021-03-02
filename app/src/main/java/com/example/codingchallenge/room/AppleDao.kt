package com.example.codingchallenge.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppleDao {

    @Query("SELECT * FROM apple_table WHERE trackName LIKE :search OR genre LIKE :search ORDER BY timeInserted ASC")
    fun searchAppleData(search : String) : List<AppleEntity>

    @Insert
    suspend fun insertAppleData(apple : AppleEntity)
}