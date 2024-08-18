package com.example.yorieter.post

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentPostBinding
import com.example.yorieter.mypage.viewModel.ProfileViewModel
import com.example.yorieter.post.viewModel.PostViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PostFragment: Fragment() {

    lateinit var binding: FragmentPostBinding
    private val PICK_IMAGE_REQUEST = 1
    private var title : String = ""
    private var description : String = ""
    private var calories : Int = 0
    private var image : String = ""
    private val postViewModel : PostViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null // 선택된 이미지의 URI를 저장하기 위한 변수

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
        binding = FragmentPostBinding.inflate(inflater, container, false)

        val ingredientInput = binding.postIngredients.toString()

//        val ingredients = mutableListOf<String>()
//        ingredients.add("ingredient1")
//        ingredients.add("ingredient2")
//        ingredients.add("ingredient3")
//        ingredientNames = ingredients

        // 배경 클릭 시 키보드 숨기기
        binding.postFragment.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        //칼로리 버튼 클릭시 칼로리 추가 창 이동
//        binding.layoutCalory.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.anim.slide_in_right,
//                    R.anim.slide_in_left,
//                )
//                .replace(R.id.main_frm, CalorieFragment1()) // 댓글 프래그먼트로 이동
//                .addToBackStack(null) // 백 스택 추가
//                .commitAllowingStateLoss()
//        }

         binding.addImgBtn.setOnClickListener {
                    openGallery() // 갤러리에서 이미지 선택하는 함수 호출
                }


        //이미지 첨부시 이미지가 나타나게
        binding.recipeImgIv.visibility = View.VISIBLE

        // 업로드 버튼 클릭 시
        binding.addPostBtn.setOnClickListener {
            //parseIngredients(ingredientInput)
            addPosts()

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, RecipeFragment()) // 내가 작성한 화면으로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        return binding.root
    }

    // 재료 입력 받아오기
    private fun parseIngredients(input: String): List<Ingredient> {
        // 쉼표를 기준으로 입력 문자열을 분리
        val ingredientNames = input.split(",").map { it.trim() }

        // 식재료 리스트 생성
        val ingredientList = mutableListOf<Ingredient>()

        // 각 식재료 이름을 Ingredient 객체로 변환 (수량은 기본값 1로 설정)
        for (name in ingredientNames) {
            if (name.isNotEmpty()) {
                ingredientList.add(Ingredient(name = name, quantity = 1))
            }
        }

        return ingredientList
    }

    // 갤러리 여는 함수
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK) // ACTION_PICK 인텐트를 사용하여 갤러리에서 이미지를 선택하는 인텐트 생성
        intent.type = "image/*" // 인텐트의 타입을 "image/*"로 설정하여 모든 이미지 파일을 필터링
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // 갤러리 이미 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 갤러리에서 이미지 선택 후 호출되는 콜백 메서드
        super.onActivityResult(requestCode, resultCode, data) // 부모 클래스의 onActivityResult를 호출

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            // 선택된 이미지의 URI를 가져옴
            val imageUri: Uri = data.data!!

            // 이미지 압축 및 리사이즈
            val compressedUri = getCompressedImageUri(imageUri)

            // Glide를 사용하여 원본 이미지 첨부
            Glide.with(this)
                .load(compressedUri)
                .apply(RequestOptions.circleCropTransform())
                .signature(ObjectKey(System.currentTimeMillis().toString())) // 캐시 무효화
                .into(binding.recipeImgIv)

            selectedImageUri = compressedUri
        }
    }

    // 파일 가져오는 함수
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        Log.d("EditProfileFragment", "Temp file path: ${tempFile.absolutePath}")
        return tempFile
    }
    // Uri를 실제 경로로 변환하는 함수
    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        if (cursor != null){
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            path = cursor.getString(idx)
            cursor.close()
        }
        return path
    }

    // 이미지 압축 함수
    private fun getCompressedImageUri(uri: Uri): Uri {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

        // 이미지 크기를 줄이기 위한 비율 설정 (50%로 설정)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        // 압축된 이미지 파일을 저장할 임시 파일 생성
        val compressedFile = File(requireContext().cacheDir, "compressed_image.jpg")
        val outputStream = FileOutputStream(compressedFile)

        // 압축 품질 설정 (85%로 설정)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(compressedFile)
    }

    private fun addPosts(){
        // 데이터 저장
        title = binding.postTitle.text.toString() // 제목
        description = binding.postContext.text.toString() // 게시글 내용

        val ingredientInput = binding.postIngredients.text.toString()
        val ingredientList = parseIngredients(ingredientInput)
        // 선택된 식재료 리스트 가져오기
        //val selectedIngredient = getSelectedIngredients()

        val recipeRequest = RecipeRequest(
            title = title,
            description = description,
            ingredientList = ingredientList
        )

        // Gson 객체를 사용하여 객체를 JSON 문자열로 변환
        val gson = Gson()
        val requestJson = gson.toJson(recipeRequest)

        // JSON 문자열을 RequestBody로 변환
        val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())


        // 이미지 파일이 있을 경우 MultipartBody.Part로 변환
        val imagePart: MultipartBody.Part? = selectedImageUri?.let { uri ->
            val file = getFileFromUri(uri)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val token = getToken()

        val postService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)

        if (token != null){
            postService.posts("Bearer $token", requestBody, imagePart).enqueue(object:
                Callback<PostResponse> {
                override fun onResponse(
                    call: Call<PostResponse>,
                    response: Response<PostResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: PostResponse = response.body()!!
                    if (resp != null) {
                        if(resp.isSuccess) { // 응답 성공 시
                            val recipeId = resp.result.recipeId // 레시피 번호 받아오기
                            Log.d("POST/SUCCESS", "레시피 작성 성공")
                            Toast.makeText(requireContext(), "게시글 작성 완료:", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("POST/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.d("POST/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                    Log.d("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }
    }
}
