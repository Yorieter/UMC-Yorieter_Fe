package com.example.yorieter.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentCommunityBinding
import com.example.yorieter.mypage.MyCommentFragment

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

        //리사이클러 뷰에 연결
        val recipeRVAdapter = RecipeRVAdapter(recipeDatas)
        adapter = RecipeRVAdapter(recipeDatas)

        binding.recipeRv.adapter = recipeRVAdapter

        binding.recipeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        // 어댑터 클릭 리스너 설정
        recipeRVAdapter.mItemClickListener = object: RecipeRVAdapter.RecipeItemClickListener{
            // 레시피 프래그먼트로 이동
            override fun onItemClick(recipeData: RecipeData) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RecipeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

}