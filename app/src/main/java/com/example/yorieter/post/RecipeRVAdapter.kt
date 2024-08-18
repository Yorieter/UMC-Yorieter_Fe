package com.example.yorieter.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemRecipeBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeRVAdapter(private val context: Context, private val recipelist: ArrayList<Recipe>) : RecyclerView.Adapter<RecipeRVAdapter.MyViewHolder>() {

    private fun getToken(): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    private fun saveLikeState(recipeId: Int, isLiked: Boolean) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("liked_$recipeId", isLiked)
            apply()
            Log.d("SharedPreferences", "Saved like state: recipeId=$recipeId, isLiked=$isLiked")
        }
    }

    private fun getLikeState(recipeId: Int): Boolean {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLiked = sharedPref.getBoolean("liked_$recipeId", false)
        Log.d("SharedPreferences", "Retrieved like state: recipeId=$recipeId, isLiked=$isLiked")
        return isLiked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemRecipeBinding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    interface RecipeItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    var mItemClickListener: RecipeItemClickListener? = null

    fun setRecipeItemClickListener(itemClickListener: RecipeItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(recipelist[position])
        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(recipelist[position])
        }
    }

    override fun getItemCount(): Int = recipelist.size

    inner class MyViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            recipe.imageUrl?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .placeholder(R.drawable.food_example02)
                    .into(binding.itemRecipeImg)
            } ?: binding.itemRecipeImg.setImageResource(R.drawable.food_example02)

            binding.titleTv.text = recipe.title
            binding.dateTv.text = recipe.createdAt

            // 초기 좋아요 상태 설정
            val isLiked = getLikeState(recipe.recipeId)
            recipe.communityLike = isLiked
            updateLikeUI(isLiked)

            binding.communityLikeIv.setOnClickListener {
                if (!recipe.communityLike) {
                    updateLikeUI(true)  // UI를 먼저 업데이트
                    likeRecipe(recipe.recipeId, recipe)
                } else {
                    updateLikeUI(false) // UI를 먼저 업데이트
                    unlikeRecipe(recipe.recipeId, recipe)
                }
            }
        }

        private fun updateLikeUI(isLiked: Boolean) {
            binding.communityLikeIv.setImageResource(
                if (isLiked) R.drawable.like_check else R.drawable.like_no_check
            )
        }

        private fun likeRecipe(recipeId: Int, recipe: Recipe) {
            val likeService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
            val token = getToken()

            if (token != null) {
                likeService.recipesLike("Bearer $token", recipeId).enqueue(object : Callback<CommunitylikeResponse> {
                    override fun onResponse(call: Call<CommunitylikeResponse>, response: Response<CommunitylikeResponse>) {
                        if (response.isSuccessful) {
                            recipe.communityLike = true
                            saveLikeState(recipeId, true)
                            updateLikeUI(true)
                            Log.d("좋아요/SUCCESS", "좋아요 성공")
                        } else {
                            handleLikeError(response, recipe)
                        }
                    }

                    override fun onFailure(call: Call<CommunitylikeResponse>, t: Throwable) {
                        Log.e("좋아요 API/FAILURE", "Network failure: ${t.message}")
                    }
                })
            }
        }

        private fun unlikeRecipe(recipeId: Int, recipe: Recipe) {
            val likeService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
            val token = getToken()

            if (token != null) {
                likeService.recipeDelete("Bearer $token", recipeId).enqueue(object : Callback<CommunitylikeResponse> {
                    override fun onResponse(call: Call<CommunitylikeResponse>, response: Response<CommunitylikeResponse>) {
                        if (response.isSuccessful) {
                            recipe.communityLike = false
                            saveLikeState(recipeId, false)
                            updateLikeUI(false)
                            Log.d("좋아요/SUCCESS", "좋아요 취소 성공")
                        } else {
                            handleLikeError(response, recipe)
                        }
                    }

                    override fun onFailure(call: Call<CommunitylikeResponse>, t: Throwable) {
                        Log.e("좋아요 API/FAILURE", "Network failure: ${t.message}")
                    }
                })
            }
        }

        private fun handleLikeError(response: Response<CommunitylikeResponse>, recipe: Recipe) {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorJson = JSONObject(errorBody)
                val errorCode = errorJson.getString("code")

                when (errorCode) {
                    "RECIPE409" -> {
                        // 이미 상태가 일치하는 경우
                        Log.e("좋아요/ERROR", "이미 좋아요 상태가 맞습니다.")
                        recipe.communityLike = !recipe.communityLike
                        saveLikeState(recipe.recipeId, recipe.communityLike)
                        updateLikeUI(recipe.communityLike)
                    }
                    else -> {
                        Log.e("좋아요/ERROR", "Error: $errorBody")
                    }
                }
            }
        }
    }
}
