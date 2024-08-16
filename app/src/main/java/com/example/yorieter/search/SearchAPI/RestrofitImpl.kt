package com.example.yorieter.search.SearchAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RestrofitImpl {
    // base url
    private const val BASE_URL = "https://umc.yorieter.shop/" // 요리어터 기본 url

    fun getRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}