package com.example.yorieter.mypage.api.ResponseData

import com.google.gson.annotations.SerializedName

data class DeleteRecipeResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: Any,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)