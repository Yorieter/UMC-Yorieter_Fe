package com.example.yorieter.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class PostEdit(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val ingredientNames: List<String> = emptyList(),
    val calories: String = ""
)
class PostEditViewModel: ViewModel() {
    private val _recipe = MutableLiveData<PostEdit>()
    val recipe: LiveData<PostEdit> get() = _recipe

    fun setRecipe(recipe: PostEdit) {
        _recipe.value = recipe
    }
}