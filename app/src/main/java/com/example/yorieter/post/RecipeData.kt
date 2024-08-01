package com.example.yorieter.post

import java.io.Serializable

class RecipeData(
    var recipe_image : Int? = null,
    var recipe_title : String? = "",
    var recipe_date : String? =""
    ) : Serializable