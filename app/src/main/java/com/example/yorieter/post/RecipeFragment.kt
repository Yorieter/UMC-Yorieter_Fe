package com.example.yorieter.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentRecipeBinding
import com.example.yorieter.home.API.HomeRecipesResponse
import com.example.yorieter.mypage.EditProfileFragment
import com.example.yorieter.mypage.MyCommentFragment
import com.example.yorieter.mypage.adapter.MypostRVAdapter
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {
    lateinit var binding: FragmentRecipeBinding

    companion object {
        private const val ARG_RECIPE_ID = "recipeId"
        fun newInstance(recipeId: Int): RecipeFragment {
            val fragment = RecipeFragment()
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

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

//        binding.backRecipeBtn.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.anim.slide_in_right,
//                    R.anim.slide_in_left,
//                )
//                .replace(R.id.main_frm, CommunityFragment()) // 프로필 편집 프래그먼트로 이동
//                .addToBackStack(null) // 백 스택 추가
//                .commitAllowingStateLoss()
//        }

        binding.backRecipeBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

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

        val token = getToken()

        // 전달받은 레시피 아이디 가져오기
        val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
        Log.d("전달받은 레시피 아이디 확인", recipeId.toString())

        val homeService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)

        if (token != null) {
            homeService.getRecipeDetail("Bearer $token", recipeId)
                .enqueue(object : Callback<HomeRecipeResponse> {
                    override fun onResponse(
                        call: Call<HomeRecipeResponse>,
                        response: Response<HomeRecipeResponse>
                    ) {
                        Log.d("RETROFIT/SUCCESS", response.toString())
                        val resp: HomeRecipeResponse = response.body()!!
                        if (resp != null) {
                            if (resp.isSuccess) {
                                Log.d("DETAIL/SUCCESS", "레시피 상세 조회 성공")

                                // 타이틀 적용
                                binding.recipeMainTitle.text = resp.result.title

                                // 사진 적용
                                Glide.with(this@RecipeFragment)
                                    .load(resp.result.imageUrl)
                                    .error(R.drawable.mypage_ic_yorieter_profile) // 에러 시 표시할 이미지
                                    .into(binding.foodPhoto)

                                // 식재료 적용
                                val ingredientNames = resp.result.ingredientNames
                                for (ingredient in ingredientNames) {
                                    val chip = Chip(requireContext())
                                    chip.text = ingredient
                                    chip.isClickable = false
                                    chip.isCheckable = false
                                    chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.mainColor) // Background color
                                    binding.recipeChipGroup.addView(chip)
                                }

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

                    override fun onFailure(call: Call<HomeRecipeResponse>, t: Throwable) {
                        Log.e("RETROFIT/FAILURE", t.message.toString())
                    }

                })
        }
        return binding.root
    }
}