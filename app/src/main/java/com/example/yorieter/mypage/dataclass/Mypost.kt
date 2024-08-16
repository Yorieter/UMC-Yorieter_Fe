package com.example.yorieter.mypage.dataclass

data class Mypost(
    var coverImg: Int? = null,
    var title: String? = "",
    var date: String? = "",
    var recipeId: Int // 레시피 아이디 추가
)
