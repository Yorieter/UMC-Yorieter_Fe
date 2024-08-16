package com.example.yorieter.login.api

import com.example.yorieter.login.api.ResponseData.LoginResponse
import com.example.yorieter.login.api.ResponseData.LogoutResponse
import com.example.yorieter.login.api.ResponseData.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserRetrofitItf {

    // 회원가입
    @POST("signup")
    fun signUp(@Body signUpclient: SignUpClient): Call<SignUpResponse>

    // 로그인
    @POST("login")
    fun login(@Body loginClient: LoginClient): Call<LoginResponse>

    // 로그아웃
    @GET("auth/logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>
}