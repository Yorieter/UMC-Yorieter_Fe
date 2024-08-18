package com.example.yorieter.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yorieter.R
import com.example.yorieter.data.RecipeViewModel
import com.example.yorieter.databinding.FragmentHomeMoreBinding
import com.example.yorieter.search.SearchFragment

class HomeMoreFragment: Fragment() {
    lateinit var binding: FragmentHomeMoreBinding
    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeMoreBinding.inflate(inflater, container, false)

        // 검색 버튼 전환 오류
        binding.homeMoreSearchIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeMoreFragment, SearchFragment())
                .commitAllowingStateLoss()
        }

        // 뒤로가기
        binding.homeBackIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // 리사이클러 뷰 데이터 바인딩
    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {

        // 리사이클러 뷰 바인딩
        binding.homeRecommendMoreRV.layoutManager = GridLayoutManager(context, 2)

        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(requireActivity().supportFragmentManager, requireActivity())
        binding.homeRecommendMoreRV.adapter = recommendRecipeRVAdapter

        // ViewModel의 데이터 변경을 관찰
        viewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            // 레시피 리스트를 최대 6개로 제한
            val limitedRecipes = recipes.take(10)
            recommendRecipeRVAdapter.updateRecipes(limitedRecipes)
        })
    }
}