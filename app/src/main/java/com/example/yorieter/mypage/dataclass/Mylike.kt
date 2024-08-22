package com.example.yorieter.mypage.dataclass

data class Mylike(
    var coverImg: String? = null,
    var title: String? = "",
    var date: String? = "",
    var recipeId: Int,
    var mypagelike: Boolean
)