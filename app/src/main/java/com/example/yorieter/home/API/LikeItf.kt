package com.example.yorieter.home.API

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeItf {
    // 좋아요 추가
    @POST("recipes/{recipeId}/liked")
    fun likeRecipe(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: Int
    ): Call<LikeResponse>

    // 좋아요 해제
    @DELETE("recipes/{recipeId}/delete")
    fun unlikeRecipe(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: Int
    ): Call<UnLikeResponse>
}