package com.example.codingchallenge.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppleDao {

    @Query("SELECT * FROM apple_table WHERE trackName LIKE :search ORDER BY timeInserted ASC")
    fun searchAppleData(search : String) : List<AppleEntity>

    @Insert
    suspend fun insertAppleData(apple : AppleEntity)

    @Query("SELECT * FROM apple_table WHERE trackName LIKE :search AND genre LIKE :genreFilter ORDER BY timeInserted ASC ")
    fun filterWithSearchData(search : String, genreFilter: String) : List<AppleEntity>
}