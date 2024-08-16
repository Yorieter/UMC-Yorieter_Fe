package com.example.yorieter.search.SearchAPI

import com.google.gson.annotations.SerializedName

data class SearchRequest(
    @SerializedName(value = "maxCalorie") val maxCalorie: Int,
    @SerializedName(value = "minCalorie") val minCalorie: Int,
    @SerializedName(value = "ingredientNames") val ingredientNames: List<String>
)

data class SearchResponse(
    @SerializedName(value = "code") val code: String,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "result") val result: RecipeResult,
    @SerializedName(value = "isSuccess") val isSuccess: Boolean
)

data class RecipeResult(
    @SerializedName(value = "recipeList") val recipeList: List<SearchRecipe>
)

data class SearchRecipe(
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
