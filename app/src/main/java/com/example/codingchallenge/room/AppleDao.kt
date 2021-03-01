package com.example.codingchallenge.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppleDao {

    @Query("SELECT * FROM apple_table ORDER BY timeInserted ASC")
    fun retrieveAllData() : List<AppleEntity>

    @Query("SELECT * FROM apple_table WHERE trackName LIKE :search OR genre LIKE :search")
    fun searchAppleData(search : String) : List<AppleEntity>

    @Insert
    suspend fun insertAppleData(apple : AppleEntity)
}