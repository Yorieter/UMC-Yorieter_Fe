package com.example.yorieter.login.api.ResponseData

import com.google.gson.annotations.SerializedName

// 회원가입 응답 데이터 클래스
data class SignUpResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: SignUpResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)
data class SignUpResult(
    @SerializedName(value = "id") var id: Int,
    @SerializedName(value = "username") var username: String
)
