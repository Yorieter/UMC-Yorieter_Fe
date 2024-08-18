package com.example.yorieter.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.data.RecipeViewModel
import com.example.yorieter.databinding.FragmentHomeBinding
import com.example.yorieter.home.API.HomeItf
import com.example.yorieter.home.API.HomeRecipesResponse
import com.example.yorieter.home.API.LikeItf
import com.example.yorieter.home.API.LikeResponse
import com.example.yorieter.home.API.LikeRetrofitObj
import com.example.yorieter.home.API.UnLikeResponse
import com.example.yorieter.login.api.UserRetrofitObj
import com.example.yorieter.post.HomeRecipeResponse
import com.example.yorieter.post.PostRetrofitItf
import com.example.yorieter.post.PostRetrofitObj
import com.example.yorieter.post.RecipeFragment
import com.example.yorieter.post.RecipeRVAdapter
import com.example.yorieter.post.RecipeUserFragment
import com.example.yorieter.search.SearchFragment
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment: Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: RecipeViewModel by activityViewModels()
    var homeWeekLike: Boolean = false
    private var firstRecipeId: Int? = null


    // 토큰 값 가져오기
    private fun getToken(): String? {
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    // 수정 다시
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // recommendRecipe = RecipeDatabase.getInstance(requireContext())!!

        // 더보기 텍스트나 아이콘을 누르면 레시피 추천 더보기 창으로 넘어감
        binding.homeMoreIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frm, HomeMoreFragment())
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }
        binding.homeMoreTV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frm, HomeMoreFragment())
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        binding.homeSearchIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.homeFragment, SearchFragment())
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        binding.homeRecipeWeekIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.homeFragment, RecipeFragment())
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 좋아요 구현
//        binding.homeWeekLikeIV.setOnClickListener {
//            if (!homeWeekLike) {
//                binding.homeWeekLikeIV.setImageResource(R.drawable.like_check)
//                homeWeekLike = true
//            } else {
//                binding.homeWeekLikeIV.setImageResource(R.drawable.like_no_check)
//                homeWeekLike = false
//            }
//        }

        // API Token
        val token = getToken()

        //모든 레시피 조회 API
        val homeService = UserRetrofitObj.getRetrofit().create(HomeItf::class.java)
        if (token != null){
            homeService.getRecipes("Bearer $token").enqueue(object: Callback<HomeRecipesResponse>{
                override fun onResponse(
                    call: Call<HomeRecipesResponse>,
                    response: Response<HomeRecipesResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: HomeRecipesResponse = response.body()!!
                    if (resp != null){
                        if (resp.isSuccess){
                            Log.d("HOME/SUCCESS", "레시피 조회 성공")

                            // 첫 번째 레시피 데이터 가져오기
                            val firstRecipe = resp.result.recipeList.firstOrNull()
                            firstRecipe?.let {
                                firstRecipeId = 2

                                // 금주의 레시피 UI 업데이트
                                // 이미지를 로드하는 라이브러리(Glide 등)를 사용해서 이미지 설정
                                Glide.with(this@HomeFragment)
                                    .load(it.imageUrl)
                                    .into(binding.homeRecipeWeekIV)
                            }

                            // ViewModel에 레시피 데이터 추가 (HomeRecipe를 Recipe2로 변환)
                            val recipeList = resp.result.recipeList.map { homeRecipe ->
                                Recipe(
                                    memberId = homeRecipe.memberId,
                                    recipeId = homeRecipe.recipeId,
                                    title = homeRecipe.title,
                                    description = homeRecipe.description,
                                    calories = homeRecipe.calories,
                                    imageUrl = homeRecipe.imageUrl,
                                    ingredientNames = homeRecipe.ingredientNames,
                                    createdAt = homeRecipe.createdAt,
                                    updatedAt = homeRecipe.updatedAt
                                )
                            }

                            // ViewModel에 레시피 데이터 추가
                            viewModel.setRecipes(recipeList)
                        } else {
                            Log.e("HOME/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.e("HOME/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<HomeRecipesResponse>, t: Throwable) {
                    Log.e("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }

        return binding.root
    }

    // 리사이클러 뷰 데이터 바인딩
    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        // 리사이클러 뷰 바인딩
        binding.homeRecommendRV.layoutManager = GridLayoutManager(context, 2)

        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(viewModel, requireActivity().supportFragmentManager, requireActivity())
        binding.homeRecommendRV.adapter = recommendRecipeRVAdapter
        binding.homeRecommendRV.setHasFixedSize(true)

        recommendRecipeRVAdapter.mItemClickListener = object : HomeRecommendRecipeAdapter.RecipeItemClickListener {
            override fun onItemClick(recipe: Recipe) {
                val recipeFragment = RecipeFragment.newInstance(recipe.recipeId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, HomeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        // ViewModel의 데이터 변경을 관찰
        // ViewModel의 데이터 변경을 관찰
        viewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            // 레시피 리스트를 최대 6개로 제한
            val limitedRecipes = recipes.take(6)
            recommendRecipeRVAdapter.updateRecipes(limitedRecipes)
        })
    }
}