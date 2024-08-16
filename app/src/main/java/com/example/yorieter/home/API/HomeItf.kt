package com.example.yorieter.home.API

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeItf {
    // 모든 레시피 조회
    @GET("recipes")
    fun getRecipes(@Header("Authorization") token: String): Call<HomeRecipesResponse>
}