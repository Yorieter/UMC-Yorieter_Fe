package com.example.yorieter.post

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.yorieter.mypage.api.ResponseData.DetailResponse
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentRecipeUserBinding
import com.example.yorieter.login.api.UserRetrofitObj
import com.example.yorieter.mypage.PostEditFragment
import com.example.yorieter.mypage.RecipeDeleteDialogActivity
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.viewModel.PostEdit
import com.example.yorieter.mypage.viewModel.PostEditViewModel
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeUserFragment : Fragment() {
    lateinit var binding: FragmentRecipeUserBinding
    private val posteditviewModel: PostEditViewModel by activityViewModels()

    companion object {
        private const val DELETE_RECIPE_REQUEST_CODE = 1
        private const val ARG_RECIPE_ID = "recipeId"

        fun newInstance(recipeId: Int): RecipeUserFragment {
            val fragment = RecipeUserFragment()
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

    // 토큰 값 가져오기
    private fun getToken(): String?{
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeUserBinding.inflate(inflater, container, false)

        // 삭제 버튼 클릭 시
        binding.deleteTxt.setOnClickListener {
            val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
            Log.d("RecipeUserFragment 레시피 아이디 전달 받음", recipeId.toString())

            // RecipeDeleteDialogActivity 호출
            val intent = Intent(requireContext(), RecipeDeleteDialogActivity::class.java)
            intent.putExtra("recipeId", recipeId) // 레시피 아이디를 Intent에 추가
            startActivity(intent)
        }

        // 수정 버튼 클릭 시
        binding.repostTxt.setOnClickListener {
            val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
            Log.d("RecipeUserFragment 레시피 아이디 전달 받음", recipeId.toString())

            // Bundle 생성 및 레시피 아이디 추가
            val bundle = Bundle()
            bundle.putInt("recipeId", recipeId)

            val postEditFragment = PostEditFragment()
            postEditFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                    )
                .replace(R.id.main_frm, postEditFragment) // 게시물 수정! 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
            }

//        binding.commentPageBtn.setOnClickListener {
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(this, FragmentComment)
//            transaction?.addToBackStack(null)
//            transaction?.commit()
//        }

        binding.commentPageBtn.setOnClickListener {
            val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
            Log.d("RecipeUserFragment 레시피 아이디 전달 받음", recipeId.toString())

            // Bundle 생성 및 레시피 아이디 추가
            val bundle = Bundle()
            bundle.putInt("recipeId", recipeId)

            val commentFragment = CommentFragment()
            commentFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, commentFragment) // 댓글 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 백버튼 클릭 시
        binding.backIconIb.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 전달받은 레시피 아이디 가져오기
        val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
        Log.d("RecipeUserFragment에서 레시피 아이디 전달 받음", recipeId.toString())

        // 레시피 상세 조회 API
        val token = getToken()

        // 레시피 아이디는 임의로 넣었어요.
        // 모든 레시피 조회할 때 받은 레시피 아이디 저장해서 이 프래그먼트에 전달하면 될 것 같아요.

        val homeService = UserRetrofitObj.getRetrofit().create(MypageItf::class.java)

        if (token != null){
            homeService.getRecipeDetail("Bearer $token",recipeId).enqueue(object:
                Callback<DetailResponse> {
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    Log.e("EDITRECIPE/ERROR", "Error body: ${response.errorBody()?.string()}")
                    val resp: DetailResponse = response.body()!!
                    if (resp != null){
                        if (resp.isSuccess){
                            Log.d("DETAIL/SUCCESS", "레시피 상세 조회 성공")
                            // ViewModel에 레시피 데이터 저장
                            val postEdit = PostEdit(
                                title = resp.result.title ?: "No Title Available",
                                description = resp.result.description ?: "No Recipe Description Available",
                                imageUrl = resp.result.imageUrl ?: "",
                                ingredientNames = resp.result.ingredientNames ?: emptyList(),
                                calories = resp.result.calories?.toString() ?: "N/A"
                            )
                            posteditviewModel.setRecipe(postEdit)

                            // 타이틀 적용
                            binding.recipeMainUserTitle.text = resp.result.title ?: "No Title Available"

//                            // 사진 적용
//                            Glide.with(this@RecipeUserFragment)
//                                .load(resp.result.imageUrl)
//                                .into(binding.foodPhoto)

//                            Glide.with(this@RecipeUserFragment)
//                                .load(resp.result.imageUrl)
//                                .error(R.drawable.mypage_ic_yorieter_profile) // 에러 시 표시할 기본 이미지
//                                .into(binding.foodPhoto)

                            Glide.with(this@RecipeUserFragment)
                                .load(resp.result.imageUrl)
                                .placeholder(R.drawable.mypage_ic_yorieter_profile) // 로드 중일 때 표시할 이미지
                                .error(R.drawable.mypage_ic_yorieter_profile) // 에러 시 표시할 이미지
                                .into(binding.foodPhoto)


//                            // 식재료 적용
//                            val ingredientNames = resp.result.ingredientNames
//                            for (ingredient in ingredientNames){
//                                val chip = Chip(requireContext())
//                                chip.text = ingredient
//                                chip.isClickable = false
//                                chip.isCheckable = false
//                                binding.recipeUserChipGroup.addView(chip)
//                            }

                            // 식재료 적용
                            val ingredientNames = resp.result.ingredientNames
                            if (ingredientNames.isNullOrEmpty()) {
                                // 식재료가 없을 때 임의의 텍스트를 추가
                                val chip = Chip(requireContext())
                                chip.text = "No Ingredients Available"
                                chip.isClickable = false
                                chip.isCheckable = false
                                binding.recipeUserChipGroup.addView(chip)
                            } else {
                                // 식재료가 있을 때 처리
                                for (ingredient in ingredientNames) {
                                    val chip = Chip(requireContext())
                                    chip.text = ingredient
                                    chip.isClickable = false
                                    chip.isCheckable = false
                                    chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.mainColor) // Background color
                                    binding.recipeUserChipGroup.addView(chip)
                                }
                            }

                            // 레시피 내용 적용
                            //binding.recipeMain.text = resp.result.description
                            binding.recipeMain.text = resp.result.description ?: "No Recipe Description Available"

                        } else {
                            Log.e("DETAIL/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.e("DETAIL/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    Log.e("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }

        return binding.root
    }

    // onActivityResult에서 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_RECIPE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // RecipeUserFragment 종료하고 MypostFragment로 돌아가기
            parentFragmentManager.popBackStack()
        }
    }

}