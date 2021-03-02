package com.example.codingchallenge.repository

import com.example.codingchallenge.room.AppleDao
import com.example.codingchallenge.room.AppleEntity
import javax.inject.Inject

class AppleRepository @Inject constructor(private val appleDao: AppleDao) {

    fun searchAppleData(search : String) = appleDao.searchAppleData(search)
    suspend fun insertAppleData(apple: AppleEntity) = appleDao.insertAppleData(apple)
    fun filterWithSearchData(search: String, genreFilter : String) = appleDao.filterWithSearchData(search, genreFilter)
}