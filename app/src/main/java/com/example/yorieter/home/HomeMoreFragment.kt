package com.example.yorieter.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.databinding.FragmentHomeMoreBinding
import com.example.yorieter.search.SearchFragment

class HomeMoreFragment: Fragment() {
    lateinit var binding: FragmentHomeMoreBinding

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
        val itemList = mutableListOf<Recipe>()
        itemList.add(Recipe("샐러드", 0, false))
        itemList.add(Recipe("샐러드", 0, false))
        itemList.add(Recipe("샐러드", 0, false))
        itemList.add(Recipe("샐러드", 0, false))
        itemList.add(Recipe("샐러드", 0, false))
        itemList.add(Recipe("샐러드", 0, false))

        // 리사이클러 뷰 바인딩
        binding.homeRecommendMoreRV.layoutManager = GridLayoutManager(context, 2)

        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(itemList)
        binding.homeRecommendMoreRV.adapter = recommendRecipeRVAdapter
    }
}