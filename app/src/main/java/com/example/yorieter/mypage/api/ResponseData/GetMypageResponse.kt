package com.example.yorieter.mypage.api.ResponseData

import com.google.gson.annotations.SerializedName

// 멤버 프로필 조회 응답 데이터 클래스
data class GetMypageResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: GetMypageResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class GetMypageResult(
    @SerializedName(value = "id") var id: Int,
    @SerializedName(value = "nickname") var nickname: String,
    @SerializedName(value = "description") var description: String,
    @SerializedName(value = "profileUrl") var profileUrl: String
)
