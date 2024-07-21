package com.example.yorieter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.data.Recipe
import com.example.yorieter.databinding.ItemRecipeBinding

class HomeRecommendRecipeAdapter(val recipes: MutableList<Recipe>) : RecyclerView.Adapter<HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder>() {
    //private val recipes = ArrayList<Recipe>()

    override fun getItemCount(): Int = recipes.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeRecommendRecipeHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder,
        position: Int
    ) {
        holder.bind(recipes[position])
    }

    inner class HomeRecommendRecipeHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipeImgIV.setImageResource(recipe.recipeImg!!)
            binding.recipeNameTV.text = recipe.name
        }
    }
}