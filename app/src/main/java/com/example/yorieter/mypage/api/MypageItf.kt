package com.example.yorieter.mypage.api

import com.example.yorieter.mypage.api.ResponseData.GetMyCommentResponse
import com.example.yorieter.mypage.api.ResponseData.GetMypageResponse
import com.example.yorieter.mypage.api.ResponseData.GetRecipeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface MypageItf {

    // 멤버 프로필 조회
    @GET("my-page/{memberId}")
    fun getMyProfile(
        @Header("Authorization") token: String,
        @Path(value = "memberId") memberId: Int
    ): Call<GetMypageResponse>

    // 멤버 프로필 수정
    @Multipart
    @PATCH("my-page/{memberId}")
    fun changeMyProfile(
        @Header("Authorization") token: String,
        @Path(value = "memberId") memberId: Int,
        @Part image: MultipartBody.Part?,
        @Part("request") requestBody: RequestBody
    ): Call<GetMypageResponse>

    // 내가 작성한 레시피 목록 조회
    @GET("my-page/{memberId}/recipes")
    fun getMyRecipes(
        @Header("Authorization") token: String,
        @Path(value = "memberId") memberId: Int,
        @Query(value = "page") page: Int
    ): Call<GetRecipeResponse>

    // 내가 좋아요한 레시피 목록 조회
    @GET("my-page/{memberId}/recipeLikes")
    fun getMyRecipeLikes(
        @Header("Authorization") token: String,
        @Path(value = "memberId") memberId: Int,
        @Query(value = "page") page: Int
    ): Call<GetRecipeResponse>

    // 내가 작성한 댓글 목록 조회
    @GET("my-page/{memberId}/comments")
    fun getMyComments(
        @Header("Authorization") token: String,
        @Path(value = "memberId") memberId: Int,
        @Query(value = "page") page: Int
    ): Call<GetMyCommentResponse>
}