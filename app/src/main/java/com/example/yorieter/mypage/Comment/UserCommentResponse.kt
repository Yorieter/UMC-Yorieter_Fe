package com.example.yorieter.mypage.Comment

import com.google.gson.annotations.SerializedName

data class UserCommentResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String,
    @SerializedName("isSuccess") val isSuccess: Boolean
)
