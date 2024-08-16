package com.example.yorieter.home.API

import com.google.gson.annotations.SerializedName

data class LikeResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: Any,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)
