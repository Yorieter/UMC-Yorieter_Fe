package com.example.yorieter.post

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int
)

data class RecipeRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    val ingredientList: List<Ingredient>
)

data class PostResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: PostResult,
    @SerializedName("isSuccess") val isSuccess: Boolean
)

data class PostResult(
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

// 레시피 작성
//data class PostRecipeClient (
//    @SerializedName("title") val title: String,
//    @SerializedName("description") val description: String,
//    @SerializedName("calories") val calories: Int,
//    @SerializedName("ingredientNames") val ingredientNames: List<String>
//)
//
//data class PostRecipeRequest(
//    @SerializedName("request") val request: PostRecipeClient,
//    @SerializedName("image") val image: String
//)
//
//data class PostRecipeResponse(
//    @SerializedName("code") val code: String,
//    @SerializedName("message") val message: String,
//    @SerializedName("result") val result: PostRecipeResult,
//    @SerializedName("isSuccess") val isSuccess: Boolean
//)
//
//data class PostRecipeResult (
//    @SerializedName("memberId") val memberId: Int,
//    @SerializedName("recipeId") val recipeId: Int,
//    @SerializedName("title") val title: String,
//    @SerializedName("description") val description: String,
//    @SerializedName("calories") val calories: Int,
//    @SerializedName("imageUrl") val imageUrl: String,
//    @SerializedName("ingredientNames") val ingredientNames: List<String>
//)