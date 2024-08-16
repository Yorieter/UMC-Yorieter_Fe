package com.example.yorieter.post

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PostRetrofitObj {
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