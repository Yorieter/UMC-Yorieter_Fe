package com.example.yorieter.mypage.dataclass

import com.google.gson.annotations.SerializedName

data class Ingredient2(
    @SerializedName(value = "name") var name: String,
    @SerializedName(value = "quantity") var quantity: Int
)
