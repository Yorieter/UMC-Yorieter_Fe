package com.example.yorieter.mypage.dataclass

import com.google.gson.annotations.SerializedName

data class RecipeRequest2(
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("calories") var calories: Int,
    @SerializedName("ingredientList") var ingredientList: List<Ingredient2>
)
