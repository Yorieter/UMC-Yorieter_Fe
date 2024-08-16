package com.example.yorieter.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yorieter.R

class RecipeViewModel: ViewModel() {
    private val _recipes = MutableLiveData<MutableList<Recipe>>()
    val recipes: LiveData<MutableList<Recipe>> get() = _recipes

    // 초기 레시피 데이터 설정
    fun setRecipes(newRecipes: List<Recipe>) {
        _recipes.value = newRecipes.toMutableList()
    }

    // 좋아요 클릭 이벤트 처리
    fun clickLike(recipeId: Int) {
        _recipes.value?.let { recipes ->
            val index = recipes.indexOfFirst { it.recipeId == recipeId }
            if (index != -1) {
                val recipe = recipes[index]
                recipe.like = !recipe.like
                // Update the list to trigger LiveData change
                _recipes.value = recipes
            }
        }
    }
}