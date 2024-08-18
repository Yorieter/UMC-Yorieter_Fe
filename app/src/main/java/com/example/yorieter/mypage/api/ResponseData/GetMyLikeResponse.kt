package com.example.yorieter.mypage.api.ResponseData

import com.google.gson.annotations.SerializedName

data class GetMyLikeResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: LikeResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class LikeResult(
    @SerializedName(value = "recipeLikeList") var recipeLikeList: List<LikeList>,
    @SerializedName(value = "listSize") var listSize: Int,
    @SerializedName(value = "totalPage") var totalPage: Int,
    @SerializedName(value = "totalElements") var totalElements: Int,
    @SerializedName(value = "isFirst") var isFirst: Boolean,
    @SerializedName(value = "isLast") var isLast: Boolean
)

data class LikeList(
    @SerializedName(value = "title") var title: String,
    @SerializedName(value = "recipeId") var recipeId: Int,
    @SerializedName(value = "imageUrl") var imageUrl: String,
    @SerializedName(value = "createdAt") var createdAt: String
)
