package com.example.yorieter.post

import com.google.gson.annotations.SerializedName

data class HomeRecipesResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: DetailResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class DetailResult(
    @SerializedName(value = "memberId") val memberId: Int,
    @SerializedName(value = "recipeId") val recipeId: Int,
    @SerializedName(value = "title") val title: String,
    @SerializedName(value = "description") val description: String,
    @SerializedName(value = "calories") val calories: Int,
    @SerializedName(value = "imageUrl") val imageUrl: String,
    @SerializedName(value = "ingredientNames") val ingredientNames: List<String>,
    @SerializedName(value = "createdAt") val createdAt: String,
    @SerializedName(value = "updatedAt") val updatedAt: String
)