package com.example.yorieter.home.API

import com.google.gson.annotations.SerializedName

// 소연
data class HomeRecipesResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: HomeResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class HomeResult(
    @SerializedName(value = "recipeList") var recipeList: List<HomeRecipe>
)

data class HomeRecipe(
    @SerializedName(value = "memberId") val memberId: Int,
    @SerializedName(value = "recipeId") val recipeId: Int,
    @SerializedName(value = "title") val title: String,
    @SerializedName(value = "description") val description: String,
    @SerializedName(value = "calories") val calories: Int,
    @SerializedName(value = "imageUrl") val imageUrl: String,
    @SerializedName(value = "ingredientNames") val ingredientNames: List<String>,
    @SerializedName(value = "createdAt") val createdAt: String,
    @SerializedName(value = "updatedAt") val updatedAt: String,
    @SerializedName(value = "liked") var liked: Boolean
)