package com.example.yorieter.post

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentPostBinding
import com.example.yorieter.mypage.viewModel.ProfileViewModel
import com.example.yorieter.post.viewModel.PostViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostFragment: Fragment() {

    lateinit var binding: FragmentPostBinding
    private val PICK_IMAGE_REQUEST = 1
    private var title : String = ""
    private var description : String = ""
    private var calories : Int = 0
    private lateinit var ingredientNames: List<String>
    private var image : String = ""
    private val postViewModel : PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        val ingredients = mutableListOf<String>()
        ingredients.add("ingredient1")
        ingredients.add("ingredient2")
        ingredients.add("ingredient3")
        ingredientNames = ingredients

        // 배경 클릭 시 키보드 숨기기
        binding.postFragment.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        //칼로리 버튼 클릭시 칼로리 추가 창 이동
        binding.layoutCalory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, CalorieFragment1()) // 댓글 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

         binding.addImgBtn.setOnClickListener {
                    openGallery() // 갤러리에서 이미지 선택하는 함수 호출
                }

        //post 추가하기
//        binding.addPostBtn.setOnClickListener {
//            title = binding.postTitle.text.toString()
//            description = binding.postContext.text.toString()
//            //calories = binding.caloriesEditText.text.toString().toIntOrNull() ?: 0
//            val imageUri = postViewModel.postImageUri.value
//
//            if (title.isEmpty() || description.isEmpty()) { //제목이나 내용이 빈칸이면 오류 도출
//                Toast.makeText(requireContext(), "빈칸을 채우세요", Toast.LENGTH_SHORT).show()
//            } else {
//                // 포스트 작성
//                createPost(imageUri.toString())
//            }
//        }

        return binding.root
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK) // ACTION_PICK 인텐트를 사용하여 갤러리에서 이미지를 선택하는 인텐트 생성
        intent.type = "image/*" // 인텐트의 타입을 "image/*"로 설정하여 모든 이미지 파일을 필터링
        startActivityForResult(intent, PICK_IMAGE_REQUEST) // 선택된 이미지의 결과를 받도록 인텐트를 시작
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 갤러리에서 이미지 선택 후 호출되는 콜백 메서드
        super.onActivityResult(requestCode, resultCode, data) // 부모 클래스의 onActivityResult를 호출

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            // 선택된 이미지의 URI를 가져옴
            val imageUri: Uri = data.data!!

            // 선택된 이미지 URI를 사용하여 ImageView에 설정
            binding.recipeImgIv.setImageURI(imageUri)

            // Glide를 사용하여 원본 이미지 첨부
            Glide.with(this)
                .load(imageUri)
                .into(binding.recipeImgIv)

            // ViewModel에 선택된 이미지 URI를 설정하여 저장
            postViewModel.setPostImageUri(imageUri)
        }
    }


    //포스트 작성하기
//    private fun createPost(imageUrl: String) {
//        val postRecipeClient = PostRecipeClient(
//            title = title,
//            description = description,
//            calories = calories,
//            ingredientNames = ingredientNames
//        )
//
//        val postRecipeRequest = PostRecipeRequest(
//            request = postRecipeClient,
//            image = imageUrl
//        )
//
//        sendPostRequest(postRecipeRequest)
//    }

//    private fun sendPostRequest(postRecipeRequest : PostRecipeRequest) {
//        val call = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
//        call.postRecipes(PostRecipeClient(title, description,calories,ingredientNames))
//            .enqueue(object : Callback<PostRecipeResponse> {
//            override fun onResponse(call: Call<PostRecipeResponse>, response: Response<PostRecipeResponse>) {
//                if (response.isSuccessful) {
//                    val apiResponse = response.body()
//                    Toast.makeText(requireContext(), "게시글 작성 완료: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), "게시글 작성에 실패했습니다. ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<PostRecipeResponse>, t: Throwable) {
//                Toast.makeText(requireContext(), "그냥 실패함: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
        })
    }
}