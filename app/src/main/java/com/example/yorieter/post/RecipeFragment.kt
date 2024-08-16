package com.example.yorieter.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentRecipeBinding
import com.example.yorieter.mypage.EditProfileFragment
import com.example.yorieter.mypage.MyCommentFragment
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {
    lateinit var binding: FragmentRecipeBinding

    // 토큰 값 가져오기
    private fun getToken(): String? {
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)

        binding.backRecipeBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, CommunityFragment()) // 프로필 편집 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }
//        binding.commentPageBtn.setOnClickListener {
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(this, FragmentComment)
//            transaction?.addToBackStack(null)
//            transaction?.commit()
//        }


//        binding.commentPageBtn.setOnClickListener {
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(this, FragmentComment)
//            transaction?.addToBackStack(null)
//            transaction?.commit()
//        }

        binding.commentPageBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, CommentFragment()) // 댓글 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }


//        val recipeId = arguments?.getInt("recipeId") ?: return binding.root
//        Log.d("레시피에서 레시피 아이디 확인", recipeId.toString())

        val token = getToken()

        // 레시피 아이디는 임의로 넣었어요.
        // 모든 레시피 조회할 때 받은 레시피 아이디 저장해서 이 프래그먼트에 전달하면 될 것 같아요.

        val homeService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
//        val recipeNumber = binding.postTitle.text.toString() // 제목
//
//        val detailResult = DetailResult(
//            recipeId = recipeNumber
//        )

//
        if (token != null) {
            homeService.getRecipeDetail("Bearer $token", 2)
                .enqueue(object : Callback<HomeRecipesResponse> {
                    override fun onResponse(
                        call: Call<HomeRecipesResponse>,
                        response: Response<HomeRecipesResponse>
                    ) {
                        Log.d("RETROFIT/SUCCESS", response.toString())
                        val resp: HomeRecipesResponse = response.body()!!
                        if (resp != null) {
                            if (resp.isSuccess) {
                                Log.d("DETAIL/SUCCESS", "레시피 상세 조회 성공")

                                // 타이틀 적용
                                binding.recipeMainTitle.text = resp.result.title

                                // 사진 적용
                                Glide.with(this@RecipeFragment)
                                    .load(resp.result.imageUrl)
                                    .into(binding.foodPhoto)

                                // 식재료 적용
                                val ingredientNames = resp.result.ingredientNames
                                for (ingredient in ingredientNames) {
                                    val chip = Chip(requireContext())
                                    chip.text = ingredient
                                    chip.isClickable = false
                                    chip.isCheckable = false
                                    binding.recipeChipGroup.addView(chip)
                                }

                                // 칼로리 적용
                                binding.caloryMainTxt.text =
                                    resp.result.calories.toString() + "Kcal"

                                // 레시피 내용 적용
                                binding.recipeMain.text = resp.result.description


                            } else {
                                Log.e(
                                    "DETAIL/FAILURE",
                                    "응답 코드: ${resp.code}, 응답메시지: ${resp.message}"
                                )
                            }
                        } else {
                            Log.e("DETAIL/FAILURE", "Response body is null")
                        }
                    }

                    override fun onFailure(call: Call<HomeRecipesResponse>, t: Throwable) {
                        Log.e("RETROFIT/FAILURE", t.message.toString())
                    }

                })
        }
        return binding.root
    }
}