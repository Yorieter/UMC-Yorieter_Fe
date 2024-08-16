package com.example.yorieter.login.api.ResponseData

import com.google.gson.annotations.SerializedName

// 로그인 응답 데이터 클래스
data class LoginResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: LoginResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)
data class LoginResult(
    @SerializedName(value = "grantType") var grantType: String,
    @SerializedName(value = "accessToken") var accessToken: String,
    @SerializedName(value = "tokenExpiresIn") var tokenExpiresIn: Long,
    @SerializedName(value = "username") var username: String,
    @SerializedName(value = "nickname") var nickname: String,
    @SerializedName(value = "id") var id: Int
)
