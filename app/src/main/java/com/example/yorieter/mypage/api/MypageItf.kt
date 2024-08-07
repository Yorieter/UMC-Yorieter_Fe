package com.example.yorieter.mypage.api

import com.example.yorieter.mypage.api.ResponseData.GetMyCommentResponse
import com.example.yorieter.mypage.api.ResponseData.GetMyProfileEditResponse
import com.example.yorieter.mypage.api.ResponseData.GetMypageResponse
import com.example.yorieter.mypage.api.ResponseData.GetRecipeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface MypageItf {

    // 멤버 프로필 조회
    @GET("my-page/{memberId}")
    fun getMyProfile(@Path(value = "memberId") memberId: Int): Call<GetMypageResponse>

    // 멤버 프로필 수정
    @PATCH("my-page/{memberId}")
    fun changeMyProfile(
        @Header("Authorization") token: String,
        @Path(value = "memberId") memberId: Int,
        @Body myPage: MyPage
    ): Call<GetMyProfileEditResponse>

    // 내가 작성한 레시피 목록 조회
    @GET("my-page/{memberId}/recipes")
    fun getMyRecipes(
        @Path(value = "memberId") memberId: Int,
        @Query(value = "page") page: Int
    ): Call<GetRecipeResponse>

    // 내가 좋아요한 레시피 목록 조회
    @GET("my-page/{memberId}/recipeLikes")
    fun getMyRecipeLikes(
        @Path(value = "memberId") memberId: Int,
        @Query(value = "page") page: Int
    ): Call<GetRecipeResponse>

    // 내가 작성한 댓글 목록 조회
    @GET("my-page/{memberId}/comments")
    fun getMyComments(
        @Path(value = "memberId") memberId: Int,
        @Query(value = "page") page: Int
    ): Call<GetMyCommentResponse>
}