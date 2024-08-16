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
    private val context: Context,
    private val viewModel: RecipeViewModel
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

        private val token = getToken()
        fun bind(recipeData: Recipe, pos: Int) {
            Glide.with(binding.recipeIV.context)
                .load(Uri.parse(recipeData.imageUrl))
                .placeholder(R.drawable.recipe) // 이미지 로드 전 임시 이미지
                .error(R.drawable.recipe) // 이미지 로드 실패 시 대체 이미지
                .into(binding.recipeIV)

            binding.titleTV.text = recipeData.title
            binding.ingredientNamesTV.text = recipeData.ingredientNames.toString()
//            binding.likeIV.setImageResource(R.drawable.like_no_check)

            // 좋아요 버튼 클릭 시 이벤트 처리
//            binding.likeIV.setOnClickListener {
//                if (recipeList[pos].like == false) {
//                    recipeList[position].like = true
//                    viewModel.clickLike(pos) // 뷰모델에 변경사항 반영
//                    binding.likeIV.setImageResource(R.drawable.like_check)
//                }
//                else {
//                    recipeList[pos].like = true
//                    viewModel.clickLike(pos) // 뷰모델에 변경사항 반영
//                    binding.likeIV.setImageResource(R.drawable.like_no_check)
//                }
//                val likeService = LikeRetrofitObj.getRetrofit().create(LikeItf::class.java)
//                if (token != null) {
//                    if (!recipeList[position].like) {
//                        likeService.likeRecipe("Bearer $token", recipeData.recipeId).enqueue(object :
//                            Callback<LikeResponse> {
//                            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
//                                if (response.isSuccessful) {
//                                    binding.likeIV.setImageResource(R.drawable.like_check)
//                                    recipeList[position].like = true
//                                } else {
//                                    Log.e("Like/FAILURE", "Response code: ${response.code()}, Message: ${response.message()}")
//                                }
//                            }
//
//                            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
//                                Log.e("RETROFIT/FAILURE", t.message.toString())
//                            }
//                        })
//                    } else {
//                        likeService.unlikeRecipe("Bearer $token", recipeData.recipeId).enqueue(object :
//                            Callback<UnLikeResponse> {
//                            override fun onResponse(call: Call<UnLikeResponse>, response: Response<UnLikeResponse>) {
//                                if (response.isSuccessful) {
//                                    binding.likeIV.setImageResource(R.drawable.like_no_check)
//                                    recipeList[position].like = false
//                                } else {
//                                    Log.e("Like/FAILURE", "Response code: ${response.code()}, Message: ${response.message()}")
//                                }
//                            }
//
//                            override fun onFailure(call: Call<UnLikeResponse>, t: Throwable) {
//                                Log.e("RETROFIT/FAILURE", t.message.toString())
//                            }
//                        })
//                    }
//                }
//            }

            updateLikeButton(binding.likeIV, recipeList[pos].like)
            binding.likeIV.setOnClickListener {
                viewModel.clickLike(recipeList[pos].recipeId)
            }

            binding.recipeIV.setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RecipeFragment())
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
