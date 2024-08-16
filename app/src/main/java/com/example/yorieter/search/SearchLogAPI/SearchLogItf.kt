package com.example.yorieter.search.SearchLogAPI

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SearchLogItf {
    @POST("search/title")
    fun searchLog(@Header("Authorization") token: String,
               @Body requestBody: SearchLogRequest
    ): Call<SearchLogResponse>
}