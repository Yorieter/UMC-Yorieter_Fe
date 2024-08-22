package com.example.yorieter.search

import android.content.Context
import android.net.Uri
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
import com.example.yorieter.databinding.ItemSearchRecipeBinding
import com.example.yorieter.home.API.LikeItf
import com.example.yorieter.home.API.LikeResponse
import com.example.yorieter.home.API.LikeRetrofitObj
import com.example.yorieter.home.API.UnLikeResponse
import com.example.yorieter.post.RecipeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchAdapter(
    private val recipeList: ArrayList<Recipe>,
    private val fragmentManager: FragmentManager,
    private val context: Context
): RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    override fun getItemCount() = recipeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val binding = ItemSearchRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bind(recipeList[position], position)
    }

    inner class SearchHolder(val binding: ItemSearchRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // 토큰 값 가져오기
        private fun getToken(): String? {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return sharedPref?.getString("token", null)
        }

        fun bind(recipeData: Recipe, pos: Int) {
            Glide.with(binding.recipeIV.context)
                .load(Uri.parse(recipeData.imageUrl))
                .placeholder(R.drawable.recipe) // 이미지 로드 전 임시 이미지
                .error(R.drawable.recipe) // 이미지 로드 실패 시 대체 이미지
                .into(binding.recipeIV)

            binding.titleTV.text = recipeData.title
            binding.ingredientNamesTV.text = recipeData.ingredientNames.toString()

            binding.recipeIV.setOnClickListener {
                val recipeFragment = RecipeFragment.newInstance(recipeData.recipeId)
                fragmentManager.beginTransaction()
                    .replace(R.id.main_frm, recipeFragment)
                    .addToBackStack(null) // 백 스택 추가
                    .commitAllowingStateLoss()
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
        recipeList.clear()
        recipeList.addAll(newRecipes)
        notifyDataSetChanged()
    }
}
