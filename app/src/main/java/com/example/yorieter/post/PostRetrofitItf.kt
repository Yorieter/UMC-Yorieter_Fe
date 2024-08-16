package com.example.yorieter.post

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PostRetrofitItf {
    //레시피 작성
    @Multipart
    @POST("recipes")
    fun posts(
        @Header("Authorization") token: String,
        @Part("request") requestBody: RequestBody,
        @Part image: MultipartBody.Part?,
    ): Call<PostResponse>

    // 레시피 전체 읽어오기
    @GET("recipes")
    fun getAllPosts(
        @Header("Authorization") token: String
    ) : Call<CommunityResponse>

    // 레시피 상세 조회
    @GET("recipes/{recipeId}")
    fun getRecipeDetail(
        @Header("Authorization") token: String,
        @Path(value = "recipeId") recipeId: Int
    ): Call<HomeRecipesResponse>

    // 댓글 전체 읽어오기
    @GET("recipes/{recipeId}/comments")
    fun getAllComment(
        @Header("Authorization") token: String,
        @Path(value = "recipeId") recipeId: Int
    ) : Call<CommentsResponse>

    // 레시피 댓글 작성
    @POST("recipes/{recipeId}/comments")
    fun postComments(
        @Header("Authorization") token: String,
        @Path(value = "recipeId") recipeId: Int,
        @Body commentRequest: CommentRequest
    ): Call<PostCommentResponse>

//    @PATCH("recipes/{recipeId}")
//    fun patchRecipes(@Path("recipeId"))
//
//    @DELETE("recipes/{recipeId}")
//    fun deleteRecipes(@Path("recipeId"))
}