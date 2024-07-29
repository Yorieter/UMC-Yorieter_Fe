package com.example.yorieter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.data.RecipeViewModel
import com.example.yorieter.databinding.ItemHomeRecipeBinding

class HomeRecommendRecipeAdapter(private val viewModel: RecipeViewModel) : RecyclerView.Adapter<HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder>() {
    private var recipes: List<Recipe> = listOf()
    //private val recipeViewModel: RecipeViewModel

    override fun getItemCount(): Int = recipes.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder {
        val binding = ItemHomeRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeRecommendRecipeHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder,
        position: Int
    ) {
        holder.bind(recipes[position], position)
    }

    inner class HomeRecommendRecipeHolder(val binding: ItemHomeRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe, pos: Int) {
            binding.recipeImgIV.setImageResource(foodID[pos])

            binding.recipeNameTV.text = recipe.name

            // 좋아요 구현
            updateLikeButton(binding.recipeLikeIV, recipe.like)
            binding.recipeLikeIV.setOnClickListener {
                viewModel.clickLike(pos)
            }
        }

        private fun updateLikeButton(likeIV: ImageView, isLiked: Boolean) {
            if (isLiked) {
                likeIV.setImageResource(R.drawable.like_check)
            } else {
                likeIV.setImageResource(R.drawable.like_no_check)
            }
        }
    }

    fun updateRecipes(newRecipes: List<Recipe>) {
        this.recipes = newRecipes
        notifyDataSetChanged()
    }

    val foodID = arrayOf(
        R.drawable.food_example02, R.drawable.food_example02, R.drawable.food_example02,
        R.drawable.food_example02, R.drawable.food_example02, R.drawable.food_example02
    )
}