package com.example.yorieter.post

import android.content.Context
import android.content.Intent
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
import com.example.yorieter.mypage.MyCommentFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityFragment : Fragment() {
    lateinit var binding: FragmentCommunityBinding
    private var recipeDatas = ArrayList<Recipe>()
    private lateinit var adapter : RecipeRVAdapter //adapter객체 먼저 선언해주기!

    private fun getToken(): String?{
        // SharedPreferences에서 토큰 값 가져오기
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        getAllPosts() // api 연동 호출

//        recipeDatas.apply {
//            add(RecipeData(R.drawable.recipe, "여름철 먹는 냉면", "2023-07-22"))
//            add(RecipeData(R.drawable.recipe, "겨울철 먹는 냉면", "2023-07-23"))
//            add(RecipeData(R.drawable.recipe, "가을에 먹는 돈까스", "2023-07-24"))
//        }

        //리사이클러 뷰에 연결
        adapter = RecipeRVAdapter(recipeDatas)
        binding.recipeRv.adapter = adapter
        binding.recipeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 클릭 리스너 설정
        adapter.mItemClickListener = object : RecipeRVAdapter.RecipeItemClickListener {
            override fun onItemClick(recipe: Recipe) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RecipeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    private fun getAllPosts() {
        val call = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
        val BEARER_TOKEN = getToken()

        call.getAllPosts("Bearer $BEARER_TOKEN")
            .enqueue(object : Callback<CommunityResponse> {
                override fun onResponse(
                    call: Call<CommunityResponse>,
                    response: Response<CommunityResponse>
                ) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d("API 응답함 ", "성공...^^")

                        response.body()?.result?.recipeList?.let { recipes ->
                            recipeDatas.clear()
                            recipeDatas.addAll(recipes) // 받아온 데이터를 recipeDatas에 추가
                            adapter.notifyDataSetChanged() // 데이터 변경 후 어댑터에 알리기
                        }
                    } else {
                        Log.e("API 오류", "Error code: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                    Log.e("api 연동 자체가 안됨", "Failed to fetch posts: ${t.message}")
                }
            })
    }
}