package com.example.yorieter.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yorieter.R

class RecipeViewModel: ViewModel() {
    private val _recipes = MutableLiveData<MutableList<Recipe>>()
    val recipes: LiveData<MutableList<Recipe>> get() = _recipes

    init {
        val initialRecipes = mutableListOf(
            Recipe("샐러드", R.drawable.food_example02, false),
            Recipe("샐러드", R.drawable.food_example02, false),
            Recipe("샐러드", R.drawable.food_example02, false),
            Recipe("샐러드", R.drawable.food_example02, false),
            Recipe("샐러드", R.drawable.food_example02, false),
            Recipe("샐러드", R.drawable.food_example02, false)
        )
        _recipes.value = initialRecipes
    }

    fun clickLike(position: Int) {
        _recipes.value?.let {
            val recipe = it[position]
            recipe.like = !recipe.like
            _recipes.value = it
        }
    }
}