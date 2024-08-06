package com.example.yorieter.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.data.RecipeViewModel
import com.example.yorieter.databinding.FragmentHomeBinding
import com.example.yorieter.search.SearchFragment

class HomeFragment: Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: RecipeViewModel by activityViewModels()
    var homeWeekLike: Boolean = false
    // lateinit var recommendRecipe: RecipeDatabase

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

        // 좋아요 구현
        binding.homeWeekLikeIV.setOnClickListener {
            if (!homeWeekLike) {
                binding.homeWeekLikeIV.setImageResource(R.drawable.like_check)
                homeWeekLike = true
            }
            else {
                binding.homeWeekLikeIV.setImageResource(R.drawable.like_no_check)
                homeWeekLike = false
            }
        }

        return binding.root
    }

    // 리사이클러 뷰 데이터 바인딩
    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

//    private fun initRecyclerView() {
//        // 임시 데이터
//        val itemList = mutableListOf<Recipe>()
//        itemList.add(Recipe("샐러드", 0, false))
//        itemList.add(Recipe("샐러드", 0, false))
//        itemList.add(Recipe("샐러드", 0, false))
//        itemList.add(Recipe("샐러드", 0, false))
//        itemList.add(Recipe("샐러드", 0, false))
//        itemList.add(Recipe("샐러드", 0, false))
//
//        // 리사이클러 뷰 바인딩
//        binding.homeRecommendRV.layoutManager = GridLayoutManager(context, 2)
//
//        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(itemList)
//        binding.homeRecommendRV.adapter = recommendRecipeRVAdapter
//    }
    private fun initRecyclerView() {
        // 리사이클러 뷰 바인딩
        binding.homeRecommendRV.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(viewModel)
        binding.homeRecommendRV.adapter = recommendRecipeRVAdapter
        binding.homeRecommendRV.setHasFixedSize(true)

        // ViewModel의 데이터 변경을 관찰
        viewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            recommendRecipeRVAdapter.updateRecipes(recipes)
        })
    }
}