package com.example.yorieter.mypage.api.ResponseData

import com.google.gson.annotations.SerializedName

data class EditRecipeResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: EditRecipeResult,
    @SerializedName("isSuccess") val isSuccess: Boolean
)

data class EditRecipeResult(
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("recipeId") val recipeId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("calories") val calories: Int,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("ingredientNames") val ingredientNames: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)
