package com.example.yorieter.data

data class Recipe(
    val memberId: Int,
    val recipeId: Int,
    val title: String,
    val description: String,
    val calories: Int?,
    val imageUrl: String?,
    val ingredientNames: List<String>,
    val createdAt: String,
    val updatedAt: String,
    var like: Boolean = false // 기본 값으로 추가
 )
