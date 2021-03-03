package com.example.codingchallenge.retrofit

import com.example.codingchallenge.api_data.AppleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppleApi {


    /*
    * Interface to use for the Retrofit instance. Used in retrieving JSON from API
    * Can be used to query other API if wanted.*/

    @GET("search")
    suspend fun getAppleData(
        @Query("term")
        term : String = "star",
        @Query("country")
        country : String = "au",
        @Query("media")
        media : String = "movie"
    ) : Response<AppleResponse>
}