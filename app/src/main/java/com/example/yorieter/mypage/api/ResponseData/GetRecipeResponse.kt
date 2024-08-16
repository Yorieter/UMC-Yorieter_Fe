package com.example.yorieter.mypage.api.ResponseData

import com.google.gson.annotations.SerializedName

// 내가 작성한,좋아요한 레시피 조회 응답 데이터 클래스
data class GetRecipeResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: RecipeResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class RecipeResult(
    @SerializedName(value = "recipeList") var recipeList: List<RecipeList>,
    @SerializedName(value = "listSize") var listSize: Int,
    @SerializedName(value = "totalPage") var totalPage: Int,
    @SerializedName(value = "totalElements") var totalElements: Int,
    @SerializedName(value = "isFirst") var isFirst: Boolean,
    @SerializedName(value = "isLast") var isLast: Boolean
)

data class RecipeList(
    @SerializedName(value = "title") var title: String,
    @SerializedName(value = "recipeId") var recipeId: Int,
    @SerializedName(value = "createdAt") var createdAt: String
)
