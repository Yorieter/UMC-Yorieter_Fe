package com.example.yorieter.home

import android.content.Context
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRecommendRecipeAdapter(private val viewModel: RecipeViewModel, private val fragmentManager: FragmentManager, private val context: Context) : RecyclerView.Adapter<HomeRecommendRecipeAdapter.HomeRecommendRecipeHolder>() {
    private var recipes: List<Recipe> = listOf()

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
        // 토큰 값 가져오기
        private fun getToken(): String? {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return sharedPref?.getString("token", null)
        }

        private val token = getToken()

        fun bind(recipe: Recipe, pos: Int) {
            // 이미지 로드
            recipe.imageUrl?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .placeholder(R.drawable.food_example02)
                    .into(binding.recipeImgIV)
            } ?: binding.recipeImgIV.setImageResource(R.drawable.food_example02)

            // 타이틀 적용
            binding.recipeNameTV.text = recipe.title

//            // 좋아요 버튼 클릭 시 이벤트 처리
//            binding.recipeLikeIV.setOnClickListener {
//                Log.d("recipe", recipe.recipeId.toString())
//                val likeService = LikeRetrofitObj.getRetrofit().create(LikeItf::class.java)
//                if (!recipe.like) {
//                    if (token != null) {
//                        likeService.likeRecipe("Bearer $token", recipe.recipeId).enqueue(object :
//                            Callback<LikeResponse> {
//                            override fun onResponse(
//                                call: Call<LikeResponse>,
//                                response: Response<LikeResponse>
//                            ) {
//                                val resp = response.body()
//                                if (resp != null) {
//                                    if (resp.isSuccess) {
//                                        recipe.like = true
//                                        viewModel.clickLike(recipe.recipeId) // 뷰모델에 변경사항 반영
//                                        binding.recipeLikeIV.setImageResource(R.drawable.like_check)
//                                    }
//                                } else {
//                                    Log.e("Like/FAILURE", "Response code: ${response.code()}, Message: ${response.message()}, Error Body: ${response.errorBody()?.string()}")
//                                }
//                            }
//
//                            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
//                                Log.e("RETROFIT/FAILURE", t.message.toString())
//                            }
//                        })
//                    }
//                } else {
//                    if (token != null) {
//                        likeService.unlikeRecipe("Bearer $token", recipe.recipeId)
//                            .enqueue(object : Callback<UnLikeResponse> {
//                                override fun onResponse(
//                                    call: Call<UnLikeResponse>,
//                                    response: Response<UnLikeResponse>
//                                ) {
//                                    val resp = response.body()
//                                    if (resp != null) {
//                                        if (resp.isSuccess) {
//                                            recipe.like = false
//                                            viewModel.clickLike(recipe.recipeId)// 뷰모델에 변경사항 반영
//                                            binding.recipeLikeIV.setImageResource(R.drawable.like_no_check)
//                                        }
//                                    } else {
//                                        Log.e(
//                                            "Like/FAILURE",
//                                            "Response code: ${response.code()}, Message: ${response.message()}"
//                                        )
//                                    }
//                                }
//
//                                override fun onFailure(call: Call<UnLikeResponse>, t: Throwable) {
//                                    Log.e("RETROFIT/FAILURE", t.message.toString())
//                                }
//                            })
//                    }
//                }
//            }

            // 초기 상태 설정
//            binding.recipeLikeIV.setImageResource(
//                if (recipe.like) R.drawable.like_check else R.drawable.like_no_check
//            )

            // 좋아요 버튼 클릭 시 이벤트 처리
//            binding.recipeLikeIV.setOnClickListener {
//                if (recipe.like == false) {
//                    recipe.like = true
//                    viewModel.clickLike(pos) // 뷰모델에 변경사항 반영
//                    binding.recipeLikeIV.setImageResource(R.drawable.like_check)
//                }
//                else {
//                    recipe.like = true
//                    viewModel.clickLike(pos) // 뷰모델에 변경사항 반영
//                    binding.recipeLikeIV.setImageResource(R.drawable.like_no_check)
//                }
//                Log.d("like", recipe.like.toString())
//                Log.d("recipe", recipe.recipeId.toString())
//                val likeService = LikeRetrofitObj.getRetrofit().create(LikeItf::class.java)
//                if (!recipe.like) {
//                    // 좋아요 클릭
//                    if (token != null) {
//                        likeService.likeRecipe("Bearer $token", recipe.recipeId).enqueue(object :
//                            Callback<LikeResponse> {
//                            override fun onResponse(
//                                call: Call<LikeResponse>,
//                                response: Response<LikeResponse>
//                            ) {
//                                if (response.isSuccessful) {
//                                    recipe.like = true
//                                    viewModel.clickLike(recipe.recipeId) // 뷰모델에 변경사항 반영
//                                    binding.recipeLikeIV.setImageResource(R.drawable.like_check)
//                                } else {
//                                    Log.e(
//                                        "Like/FAILURE",
//                                        "Response code: ${response.code()}, Message: ${response.message()}, Error Body: ${
//                                            response.errorBody()?.string()
//                                        }"
//                                    )
//                                }
//                            }
//
//                            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
//                                Log.e("RETROFIT/FAILURE", t.message.toString())
//                            }
//                        })
//                    }
//                } else {
//                    // 좋아요 해제 클릭
//                    if (token != null) {
//                        likeService.unlikeRecipe("Bearer $token", recipe.recipeId)
//                            .enqueue(object : Callback<UnLikeResponse> {
//                                override fun onResponse(
//                                    call: Call<UnLikeResponse>,
//                                    response: Response<UnLikeResponse>
//                                ) {
//                                    if (response.isSuccessful) {
//                                        recipe.like = false
//                                        viewModel.clickLike(recipe.recipeId) // 뷰모델에 변경사항 반영
//                                        binding.recipeLikeIV.setImageResource(R.drawable.like_no_check)
//                                    } else {
//                                        Log.e(
//                                            "Unlike/FAILURE",
//                                            "Response code: ${response.code()}, Message: ${response.message()}, Error Body: ${
//                                                response.errorBody()?.string()
//                                            }"
//                                        )
//                                    }
//                                }
//
//                                override fun onFailure(call: Call<UnLikeResponse>, t: Throwable) {
//                                    Log.e("RETROFIT/FAILURE", t.message.toString())
//                                }
//                            })
//                    }
//                }
//            }

            updateLikeButton(binding.recipeLikeIV, recipe.like)
            binding.recipeLikeIV.setOnClickListener {
                viewModel.clickLike(recipe.recipeId)
            }

            // 레시피 이미지 클릭 시 상세 페이지로 이동
            binding.recipeImgIV.setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RecipeFragment())
                    .addToBackStack(null)
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
        this.recipes = newRecipes
        notifyDataSetChanged()
    }

}