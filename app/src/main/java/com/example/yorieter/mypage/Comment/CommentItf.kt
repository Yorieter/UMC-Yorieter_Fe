package com.example.yorieter.mypage.Comment

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

interface CommentItf {
    @DELETE("/recipes/{recipeId}/comments/{commentId}")
    fun deleteComment(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: Int,
        @Path("commentId") commentId: Int
    ): Call<UserCommentResponse>


    @DELETE("/comments/{commentId}")
    fun deleteComment2(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Int
    ): Call<UserCommentResponse>

}