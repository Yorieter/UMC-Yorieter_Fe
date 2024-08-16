package com.example.yorieter.search.SearchAPI

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SearchItf {
    @POST("search")
    fun search(@Header("Authorization") token: String,
               @Body requestBody: SearchRequest
    ): Call<SearchResponse>
}