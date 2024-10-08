package com.example.yorieter.post

import com.google.gson.annotations.SerializedName

data class CommunitylikeResponse (
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Any,
    @SerializedName("isSuccess") val isSuccess: Boolean
)