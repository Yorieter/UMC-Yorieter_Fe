package com.example.yorieter.mypage.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MypageObj {

    // base url
    private const val BASE_URL = "https://umc.yorieter.shop/"

    fun getRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }
}