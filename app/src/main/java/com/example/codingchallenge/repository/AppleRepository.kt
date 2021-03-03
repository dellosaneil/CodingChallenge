package com.example.codingchallenge.repository

import com.example.codingchallenge.room.AppleDao
import com.example.codingchallenge.room.AppleEntity
import javax.inject.Inject

class AppleRepository @Inject constructor(private val appleDao: AppleDao) {

    suspend fun insertAppleData(apple: AppleEntity) = appleDao.insertMovieData(apple)
    fun filterWithSearchData(search: String, genreFilter : String) = appleDao.searchMoviesWithFilter(search, genreFilter)
}