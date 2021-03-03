package com.example.codingchallenge.fragments.homePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codingchallenge.api_data.AppleResult
import com.example.codingchallenge.repository.AppleRepository
import com.example.codingchallenge.retrofit.AppleApi
import com.example.codingchallenge.room.AppleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val appleRepository: AppleRepository,
    private val appleApi: AppleApi
) : ViewModel() {

    private val _dataList = MutableLiveData<List<AppleEntity>>()
    var dataList: LiveData<List<AppleEntity>> = _dataList

    suspend fun retrieveAllAppleData() {
        val appleList = appleApi.getAppleData()
        if (appleList.isSuccessful) {
            val numberOfData = appleList.body()?.resultCount
            numberOfData?.let { totalNumber ->
                repeat(totalNumber) {
                    val appleData = appleList.body()?.results?.get(it)
                    val importantData = retrieveImportantData(appleData)
                    insertAppleData(importantData)
                }
            }
        }
    }

    private fun retrieveImportantData(appleData: AppleResult?): AppleEntity? {
        appleData?.let {
            return AppleEntity(
                appleData.trackName,
                appleData.artworkUrl100,
                appleData.trackPrice,
                appleData.trackRentalPrice,
                appleData.releaseDate,
                appleData.primaryGenreName,
                appleData.longDescription,
                appleData.trackTimeMillis,
                appleData.contentAdvisoryRating
            )
        }
        return null
    }

    private suspend fun insertAppleData(appleEntity: AppleEntity?) {
        appleEntity?.let {
            appleRepository.insertAppleData(it)
        }
    }


    suspend fun searchWithFilter(search: String, genreFilter: String) {
        val filterQuery = "%$genreFilter%"
        val searchQuery = "%$search%"
        val filteredList = appleRepository.filterWithSearchData(searchQuery, filterQuery)
        withContext(Main) {
            _dataList.value = filteredList
        }
    }
}





















