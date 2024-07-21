package com.example.yorieter.post

import android.graphics.Insets.add
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {
    lateinit var binding: FragmentCommunityBinding
    private var recipeDatas = ArrayList<RecipeData>()
    private lateinit var adapter : RecipeRVAdapter //adapter객체 먼저 선언해주기!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        recipeDatas.apply {
            add(RecipeData(R.drawable.recipe, "여름철 먹는 냉면", "2023-07-22"))
            add(RecipeData(R.drawable.recipe, "겨울철 먹는 냉면", "2023-07-23"))
            add(RecipeData(R.drawable.recipe, "가을에 먹는 돈까스", "2023-07-24"))
        }

        val recipeRVAdapter = RecipeRVAdapter(recipeDatas)

        binding.recipeRv.adapter = recipeRVAdapter

        binding.recipeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root
    }

//    fun initRecipeRecyclerView(){
//        adapter = RecipeRVAdapter()
//        binding.recipeRv.adapter = adapter //리사이클러뷰에 어댑터 연결
//        binding.recipeRv.layoutManager = LinearLayoutManager(requireContext()) //레이아웃 매니저 연결
//
//        Log.d("CommunityFragment", "RecyclerView adapter set")
//    }

}