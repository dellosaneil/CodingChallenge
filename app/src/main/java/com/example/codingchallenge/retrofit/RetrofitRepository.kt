package com.example.codingchallenge.retrofit

import com.example.codingchallenge.api_data.AppleResponse
import retrofit2.Response
import javax.inject.Inject

class RetrofitRepository {

    @Inject
    lateinit var appleApi: AppleApi

    suspend fun appleData(term: String, country: String, media: String): Response<AppleResponse> =
        appleApi.getAppleData(term, country, media)
}