package com.example.yorieter.home

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.databinding.FragmentHomeBinding
import com.example.yorieter.search.SearchFragment

class HomeFragment: Fragment() {

    lateinit var binding: FragmentHomeBinding
    // lateinit var recommendRecipe: RecipeDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // recommendRecipe = RecipeDatabase.getInstance(requireContext())!!

        // 더보기 텍스트나 아이콘을 누르면 레시피 추천 더보기 창으로 넘어감
        binding.homeMoreIV.setOnClickListener {
            startActivity(Intent(activity, HomeMoreActivity::class.java))
        }
        binding.homeMoreTV.setOnClickListener {
            startActivity(Intent(activity, HomeMoreActivity::class.java))
        }

        binding.homeSearchIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frm, SearchFragment())
                .commitAllowingStateLoss()
        }

        return binding.root
    }
// 리사이클러 뷰 데이터 바인딩
    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        // 임시 데이터
        val itemList = mutableListOf<Recipe>()
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))

        // 리사이클러 뷰 바인딩
        binding.homeRecommendRV.layoutManager = GridLayoutManager(context, 2)

        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(itemList)
        binding.homeRecommendRV.adapter = recommendRecipeRVAdapter

        // 북마크 추가
    }
}