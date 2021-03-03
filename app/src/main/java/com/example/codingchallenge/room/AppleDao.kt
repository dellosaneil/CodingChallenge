package com.example.codingchallenge.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppleDao {

    @Insert
    suspend fun insertMovieData(apple : AppleEntity)

    @Query("SELECT * FROM apple_table WHERE trackName LIKE :search AND genre LIKE :genreFilter ORDER BY timeInserted ASC ")
    fun searchMoviesWithFilter(search : String, genreFilter: String) : List<AppleEntity>
}