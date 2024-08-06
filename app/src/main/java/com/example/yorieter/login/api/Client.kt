package com.example.yorieter.login.api

import com.google.gson.annotations.SerializedName

// 회원가입 요청 데이터 클래스
data class SignUpClient (
    @SerializedName(value = "username") var username: String,
    @SerializedName(value = "password") var password: String,
    @SerializedName(value = "nickname") var nickname: String
)

// 로그인 요청 데이터 클래스
data class LoginClient(
    @SerializedName(value = "username") var username: String,
    @SerializedName(value = "password") var password: String
)