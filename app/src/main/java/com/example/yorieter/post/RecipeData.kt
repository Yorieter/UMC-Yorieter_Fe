package com.example.yorieter.post

import android.net.Uri
import java.io.Serializable

//data class Post(val title: String, val context: String, val imageUri: Uri)

class RecipeData(
    var recipe_image : Int? = null,
    var recipe_title : String? = "",
    var recipe_date : String? =""
    ) : Serializable