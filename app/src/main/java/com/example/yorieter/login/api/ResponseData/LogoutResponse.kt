package com.example.yorieter.login.api.ResponseData

import com.google.gson.annotations.SerializedName

// 로그아웃 응답 데이터 클래스
data class LogoutResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: Any,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)
