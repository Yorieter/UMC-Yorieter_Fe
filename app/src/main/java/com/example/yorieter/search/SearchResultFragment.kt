package com.example.yorieter.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.data.RecipeViewModel
import com.example.yorieter.databinding.FragmentSearchResultBinding
import com.example.yorieter.search.SearchAPI.RestrofitImpl
import com.example.yorieter.search.SearchAPI.SearchItf
import com.example.yorieter.search.SearchAPI.SearchRequest
import com.example.yorieter.search.SearchAPI.SearchResponse
import com.example.yorieter.search.SearchLogAPI.SearchLogItf
import com.example.yorieter.search.SearchLogAPI.SearchLogRequest
import com.example.yorieter.search.SearchLogAPI.SearchLogResponse
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultFragment: Fragment() {
    lateinit var binding: FragmentSearchResultBinding
    private var recipeDatas = ArrayList<Recipe>()
    private val viewModel: RecipeViewModel by activityViewModels()
    private lateinit var adapter : SearchAdapter


    // 토큰 값 가져오기
    private fun getToken(): String?{
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)

//        recipeDatas.apply {
//            add(
//                Recipe(
//                    13,
//                    2,
//                    "맛있는 샐러드",
//                    "샐러드",
//                    7,
//                    "https://umc-yorieter.s3.ap-northeast-2.amazonaws.com/KakaoTalk_20240803_150023916.png",
//                    listOf("양배추", "계란"),
//                    "2024-08-11T14:02:32.366265",
//                    "2024-08-11T14:40:15.783811",
//                    false
//                )
//            )
//        }

        // 선택한 chips 나타나게 함 / 선택하지 않았으면 안나타나게
        val selectedChips = arguments?.getString("selectedChips")
        val chipList: List<String>

        if (selectedChips.isNullOrEmpty()) {
            binding.filteredChipGroup.visibility = View.GONE
            chipList = emptyList()
        } else {
            // Convert the comma-separated string to a list
//            selectedChips.split(", ").forEach { chipText ->
//                val chip = Chip(requireContext()).apply {
//                    text = chipText
//                    isClickable = false
//                    chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1) // 배경 컬러 설정
//                }
//                binding.filteredChipGroup.addView(chip)
//            }

            chipList = selectedChips.split(", ").map { it.trim() }
            chipList.forEach { chipText ->
                val chip = Chip(requireContext()).apply {
                    text = chipText
                    isClickable = false
                    chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1) // Background color
                }
                binding.filteredChipGroup.addView(chip)
            }

//            // Pass the chip list to the adapter
//            searchAdapter.setSearchParams(
//                query = arguments?.getString("query"),
//                selectedChips = chipList,
//                minCalories = arguments?.getInt("minCalories", 0) ?: 0,
//                maxCalories = arguments?.getInt("maxCalories", 0) ?: 0
//            )
        }

        // 검색어 불러오기
        val searchWord = arguments?.getString("query")

        // 칼로리 불러오기 / 선택하지 않았으면 안보이게
        val minCalories = 0
        val maxCalories = 500

        // API Token
        val token = getToken()

        if (searchWord.isNullOrEmpty()) {
            binding.searchWordTv.visibility = View.GONE

            // 필터링 검색 API
            // Construct the request body
            val requestBody = SearchRequest(
                maxCalorie = maxCalories,
                minCalorie = minCalories,
                ingredientNames = chipList
            )

            val searchService = RestrofitImpl.getRetrofit().create(SearchItf::class.java)

            searchService.search("Bearer $token", requestBody).enqueue(object :
                Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    Log.d("Search/SUCCESS", response.toString())
                    val resp = response.body()
                    if (resp != null) {
                        if (resp.isSuccess) {
                            Log.d("Search/Success", "레시피 검색 성공")

                            // 서버로부터 받은 레시피 데이터를 RecyclerView에 추가
                            val newRecipes = resp.result.recipeList.map { searchRecipe ->
                                Recipe(
                                    memberId = searchRecipe.memberId,
                                    recipeId = searchRecipe.recipeId,
                                    title = searchRecipe.title,
                                    description = searchRecipe.description,
                                    calories = searchRecipe.calories,
                                    imageUrl = searchRecipe.imageUrl,
                                    ingredientNames = searchRecipe.ingredientNames,
                                    createdAt = searchRecipe.createdAt,
                                    updatedAt = searchRecipe.updatedAt
                                )
                            }

                            // 리사이클러 뷰 바인딩
                            adapter =
                                SearchAdapter(recipeDatas, requireActivity().supportFragmentManager, requireActivity(), viewModel)
                            binding.searchResultRV.adapter = adapter
                            binding.searchResultRV.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                            // 어댑터에 데이터 업데이트
                            adapter.updateRecipes(newRecipes)
                        } else {
                            Log.e(
                                "Search/FAILURE",
                                "Response Code: ${resp.code}, Message: ${resp.message}"
                            )
                        }
                    } else {
                        Log.e("Search/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    Log.e("Search/FAILURE", t.message.toString())
                }
            })
        }
        else {
            binding.searchWordTv.text = "$searchWord"

            // 검색어 입력 검색 API
            // Construct the request body
            val requestBody = SearchLogRequest(
                title = searchWord
            )
            val searchLogService = RestrofitImpl.getRetrofit().create(SearchLogItf::class.java)

            searchLogService.searchLog("Bearer $token", requestBody).enqueue(object :
                Callback<SearchLogResponse> {
                override fun onResponse(
                    call: Call<SearchLogResponse>,
                    response: Response<SearchLogResponse>
                ) {
                    Log.d("Search/SUCCESS", response.toString())
                    val resp = response.body()
                    if (resp != null) {
                        if (resp.isSuccess) {
                            Log.d("Search/Success", "레시피 검색 성공")

                            // 서버로부터 받은 레시피 데이터를 RecyclerView에 추가
                            val newRecipes = resp.result.recipeList.map { searchLogRecipe ->
                                Recipe(
                                    memberId = searchLogRecipe.memberId,
                                    recipeId = searchLogRecipe.recipeId,
                                    title = searchLogRecipe.title,
                                    description = searchLogRecipe.description,
                                    calories = searchLogRecipe.calories,
                                    imageUrl = searchLogRecipe.imageUrl,
                                    ingredientNames = searchLogRecipe.ingredientNames,
                                    createdAt = searchLogRecipe.createdAt,
                                    updatedAt = searchLogRecipe.updatedAt
                                )
                            }

                            // 리사이클러 뷰 바인딩
                            adapter =
                                SearchAdapter(recipeDatas, requireActivity().supportFragmentManager, requireActivity(), viewModel)
                            binding.searchResultRV.adapter = adapter
                            binding.searchResultRV.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                            // 어댑터에 데이터 업데이트
                            adapter.updateRecipes(newRecipes)
                        } else {
                            Log.e(
                                "Search/FAILURE",
                                "Response Code: ${resp.code}, Message: ${resp.message}"
                            )
                        }
                    } else {
                        Log.e("Search/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<SearchLogResponse>, t: Throwable) {
                    Log.e("Search/FAILURE", t.message.toString())
                }
            })
        }

        // 뒤로가기 버튼을 누르면 다시 검색창으로 돌아감
        binding.searchBackIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frm, SearchFragment())
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}
