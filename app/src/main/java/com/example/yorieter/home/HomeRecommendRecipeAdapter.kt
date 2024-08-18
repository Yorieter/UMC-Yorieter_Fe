package com.example.yorieter.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.data.RecipeViewModel
import com.example.yorieter.databinding.ItemHomeRecipeBinding
import com.example.yorieter.home.API.LikeItf
import com.example.yorieter.home.API.LikeResponse
import com.example.yorieter.home.API.LikeRetrofitObj
import com.example.yorieter.home.API.UnLikeResponse
import com.example.yorieter.post.RecipeFragment
import com.example.yorieter.post.RecipeRVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRecommendRecipeAdapter(private val fragmentManager: FragmentManager, private val context: Context) : RecyclerView.Adapter<HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder>() {
    private var recipes: List<Recipe> = listOf()

    companion object {
        private const val ARG_RECIPE_ID = "recipeId"

        fun newInstance(recipeId: Int): RecipeFragment {
            val fragment = RecipeFragment()
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getItemCount(): Int = recipes.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder {
        val binding = ItemHomeRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeRecommendRecipeHolder(binding)
    }

    interface RecipeItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    var mItemClickListener: RecipeItemClickListener? = null

    override fun onBindViewHolder(
        holder: HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder,
        position: Int
    ) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(recipe)
        }
    }

    inner class HomeRecommendRecipeHolder(val binding: ItemHomeRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        // 토큰 값 가져오기
        private fun getToken(): String? {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return sharedPref?.getString("token", null)
        }

        fun bind(recipe: Recipe) {
            // 이미지 로드
            recipe.imageUrl?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .placeholder(R.drawable.food_example02)
                    .into(binding.recipeImgIV)
            } ?: binding.recipeImgIV.setImageResource(R.drawable.food_example02)

            // 타이틀 적용
            binding.recipeNameTV.text = recipe.title

            //레시피 아이디 받아오기

            binding.recipeImgIV.setOnClickListener {
                val recipeFragment = RecipeFragment.newInstance(recipe.recipeId)
                fragmentManager.beginTransaction()
                    .replace(R.id.main_frm, recipeFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun updateRecipes(newRecipes: List<Recipe>) {
        this.recipes = newRecipes
        notifyDataSetChanged()
    }

}