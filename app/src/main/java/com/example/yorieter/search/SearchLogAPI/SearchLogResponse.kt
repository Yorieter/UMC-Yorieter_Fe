package com.example.yorieter.search.SearchLogAPI

import com.google.gson.annotations.SerializedName

data class SearchLogRequest(
    @SerializedName(value = "title") val title: String
)

data class SearchLogResponse(
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: RecipeLogResult,
    @SerializedName(value = "isSuccess") val isSuccess: Boolean
)

data class RecipeLogResult(
    @SerializedName(value = "recipeList") val recipeList: List<SearchLogRecipe>
)

data class SearchLogRecipe(
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
