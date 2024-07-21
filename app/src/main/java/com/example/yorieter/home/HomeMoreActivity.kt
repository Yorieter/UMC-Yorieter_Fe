package com.example.yorieter.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yorieter.R
import com.example.yorieter.data.Recipe
import com.example.yorieter.databinding.ActivityHomeMoreBinding
import com.example.yorieter.search.SearchFragment

class HomeMoreActivity: AppCompatActivity() {
    lateinit var binding: ActivityHomeMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemList = mutableListOf<Recipe>()
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))
        itemList.add(Recipe("샐러드", 0, 0))

        // 리사이클러 뷰 바인딩
        binding.homeRecommendMoreRV.layoutManager = GridLayoutManager(this, 2)

        val recommendRecipeRVAdapter = HomeRecommendRecipeAdapter(itemList)
        binding.homeRecommendMoreRV.adapter = recommendRecipeRVAdapter

        // 전환 오류
        binding.homeSearchMoreIV.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}