package com.example.yorieter.mypage

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentPostEditBinding
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.EditRecipeResponse
import com.example.yorieter.mypage.dataclass.Ingredient2
import com.example.yorieter.mypage.dataclass.RecipeRequest2
import com.example.yorieter.post.CalorieFragment1
import com.example.yorieter.post.viewModel.PostViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PostEditFragment: Fragment() {

    lateinit var binding: FragmentPostEditBinding
    private val PICK_IMAGE_REQUEST = 1
    private var title : String = ""
    private var description : String = ""
    private var calories : Int = 0
    private lateinit var ingredientNames: List<String>
    private var image : String = ""
    private val postViewModel : PostViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null // 선택된 이미지의 URI를 저장하기 위한 변수
    private var recipeId: Int = -1 // 전달받은 레시피 아이디 저장

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
        binding = FragmentPostEditBinding.inflate(inflater, container, false)

        // Bundle에서 recipeId 가져오기
        recipeId = arguments?.getInt("recipeId") ?: -1
        Log.d("PostEditFragment", "Received recipeId: $recipeId")

        val ingredients = mutableListOf<String>()
        ingredients.add("ingredient1")
        ingredients.add("ingredient2")
        ingredients.add("ingredient3")
        ingredientNames = ingredients

        // 배경 클릭 시 키보드 숨기기
        binding.postEditFragment.setOnClickListener {
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

//        // chip 적용
//        val ingredientsChipGroup: ChipGroup = binding.ingredientsChipGroup
//        val testList = listOf("닭고기", "돼지고기", "어류", "버섯", "채소", "기타")
//
//        // chipGroup 설정
//        testList.forEachIndexed { index, s ->
//            val chip: Chip = Chip(requireContext()).apply {
//                text = s
//                id = index
//                isCheckable = true  // Chip을 선택 가능하게 설정
//                checkedIcon
//                chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1)
//            }
//
//            chip.setOnCloseIconClickListener {
//                ingredientsChipGroup.removeView(chip)
//            }
//
//            chip.setOnClickListener {
//                chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.mainColor)
//            }
//
//            ingredientsChipGroup.addView(chip)
//        }
//
//        // 뷰 펼치기 닫기
//        binding.selectIngredientsIV.setOnClickListener {
//            if (binding.ingredientsChipGroup.visibility == View.GONE) {
//                ingredientsChipGroup.visibility = View.VISIBLE
//                binding.selectIngredientsIV.setImageResource(R.drawable.arrow_up)
//            } else {
//                ingredientsChipGroup.visibility = View.GONE
//                binding.selectIngredientsIV.setImageResource(R.drawable.arrow_down)
//            }
//        }

        // 수정 버튼 클릭 시
        binding.editPostBtn.setOnClickListener {
            editPosts()
        }

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

            // 이미지 압축 및 리사이즈
            val compressedUri = getCompressedImageUri(imageUri)

            // 선택된 이미지 URI를 사용하여 ImageView에 설정
            //binding.recipeImgIv.setImageURI(imageUri)

            // 압축된 이미지를 ImagView에 설정
            binding.recipeImgIv.setImageURI(compressedUri)

            // Glide를 사용하여 원본 이미지 첨부
//            Glide.with(this)
//                .load(imageUri)
//                .into(binding.recipeImgIv)
            Glide.with(this)
                .load(compressedUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.recipeImgIv)

            // ViewModel에 선택된 이미지 URI를 설정하여 저장
            //postViewModel.setPostImageUri(imageUri)
            //postViewModel.setProfileImageUri(compressedUri)

            selectedImageUri = compressedUri
        }
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

    // Uri에서 File 객체를 생성하는 함수
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        Log.d("EditPostFragment", "Temp file path: ${tempFile.absolutePath}")
        return tempFile
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

//    private fun getSelectedIngredients(): List<Ingredient2> {
//        val selectedIngredients = mutableListOf<Ingredient2>()
//        val chipGroup = binding.ingredientsChipGroup
//
//        for (i in 0 until chipGroup.childCount) {
//            val chip = chipGroup.getChildAt(i) as? Chip
//            if (chip != null && chip.isChecked) {
//                // 각 Chip의 이름을 추출하여 Ingredient 객체를 생성
//                selectedIngredients.add(Ingredient2(name = chip.text.toString(), quantity = 1)) // quantity는 기본값 1로 설정
//            }
//        }
//
//        return selectedIngredients
//    }

    // 사용자 입력에서 쉼표를 기준으로 식재료를 분리하고 Ingredient2 객체 리스트로 변환하는 함수
    private fun parseIngredients(input: String): List<Ingredient2> {
        // 쉼표를 기준으로 입력 문자열을 분리
        val ingredientNames = input.split(",").map { it.trim() }

        // 식재료 리스트 생성
        val ingredientList = mutableListOf<Ingredient2>()

        // 각 식재료 이름을 Ingredient2 객체로 변환 (수량은 기본값 1로 설정)
        for (name in ingredientNames) {
            if (name.isNotEmpty()) {
                ingredientList.add(Ingredient2(name = name, quantity = 1))
            }
        }

        return ingredientList
    }

    private fun editPosts() {

        Log.d("editPosts", "함수호출")

        // 데이터 저장
        title = binding.editPostTitle.text.toString() // 제목
        description = binding.editPostContext.text.toString() // 게시글 내용
        Log.d("타이틀", title)
        Log.d("내용", description)

        // 선택된 식재료 리스트 가져오기
        //val selectedIngredient = getSelectedIngredients()

        // 사용자가 입력한 식재료 문자열을 가져옵니다.
        val ingredientInput = binding.postIngredient.text.toString()

        // 식재료 리스트 변환
        val ingredientList = parseIngredients(ingredientInput)

        // RecipeRequest2 객체 생성
        val recipeRequest = RecipeRequest2(
            title = title,
            description = description,
            calories = calories,
            ingredientList = ingredientList
        )

        Log.d("recipeRequest", recipeRequest.toString())

        // Gson 객체를 사용하여 객체를 JSON 문자열로 변환
        val gson = Gson()
        val requestJson = gson.toJson(recipeRequest)

        // JSON 문자열을 RequestBody로 변환
        val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())

        // 이미지 파일이 있을 경우 MultipartBody.Part로 변환
        val imagePart: MultipartBody.Part? = selectedImageUri?.let { uri ->
//            val file = File(getRealPathFromURI(uri))
//            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//            MultipartBody.Part.createFormData("image", file.name, requestFile)
            val file = getFileFromUri(uri)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val token = getToken()
        val editRecipeService = MypageObj.getRetrofit().create(MypageItf::class.java)

        // 레시피 수정 API 호출
        if (token != null) {
            editRecipeService.editRecipe("Bearer $token", recipeId, requestBody, imagePart)
                .enqueue(object :
                    Callback<EditRecipeResponse> {
                    override fun onResponse(
                        call: Call<EditRecipeResponse>,
                        response: Response<EditRecipeResponse>
                    ) {
                        //Log.d("RETROFIT/SUCCESS", response.toString())
                        if (response.isSuccessful) {
                            val resp = response.body()
                            if (resp != null) {
                                Log.e(
                                    "EDITRECIPE/FAILURE",
                                    "응답 코드: ${resp.code}, 응답메시지: ${resp.message}"
                                )

                                if (resp.isSuccess) { // 응답 성공 시
                                    Log.d("EDITRECIPE/SUCCESS", "레시피 수정 성공")
                                } else {
                                    Log.e(
                                        "EDITRECIPE/FAILURE",
                                        "응답 코드: ${resp.code}, 응답메시지: ${resp.message}"
                                    )
                                }
                            } else {
                                Log.d("EDITRECIPE/FAILURE", "Response body is null")
                            }
                        } else {
                            Log.e("EDITRECIPE/ERROR", "Response code: ${response.code()}")
                            Log.e("EDITRECIPE/ERROR", "Error body: ${response.errorBody()?.string()}")
                        }
//                        Log.e(
//                            "EDITRECIPE/FAILURE",
//                            "응답 코드: ${resp.code}, 응답메시지: ${resp.message}"
//                        )
//                        if (resp != null) {
//                            if (resp.isSuccess) { // 응답 성공 시
//                                Log.d("EDITRECIPE/SUCCESS", "레시피 수정 성공")
//                            } else {
//                                Log.e(
//                                    "EDITRECIPE/FAILURE",
//                                    "응답 코드: ${resp.code}, 응답메시지: ${resp.message}"
//                                )
//                            }
//                        } else {
//                            Log.d("EDITRECIPE/FAILURE", "Response body is null")
//                        }
                    }

                    override fun onFailure(call: Call<EditRecipeResponse>, t: Throwable) {
                        Log.d("RETROFIT/FAILURE", t.message.toString())
                    }
                })
        }
    }
}