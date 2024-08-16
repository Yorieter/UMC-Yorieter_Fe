package com.example.yorieter.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yorieter.databinding.FragmentEditProfileBinding
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.viewModel.ProfileViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.bumptech.glide.signature.ObjectKey
import com.example.yorieter.mypage.api.ResponseData.GetMypageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream

// FileUtil 유틸리티 클래스
//object FileUtil {
//    fun createTempFile(context: Context, fileName: String): File {
//        val storageDir: File? = context.getExternalFilesDir(null)
//        return File(storageDir, fileName)
//    }
//
//    fun compressAndSave(context: Context, uri: Uri, file: File) {
//        val inputStream = context.contentResolver.openInputStream(uri)
//        val bitmap = BitmapFactory.decodeStream(inputStream)
//        val outputStream = FileOutputStream(file)
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
//        outputStream.flush()
//        outputStream.close()
//    }
//}
//
//// UriUtil 유틸리티 클래스
//object UriUtil {
//    fun toFile(context: Context, uri: Uri): File {
//        val fileName = getFileName(context, uri)
//        val file = FileUtil.createTempFile(context, fileName)
//        FileUtil.compressAndSave(context, uri, file)
//        return file
//    }
//
//    private fun getFileName(context: Context, uri: Uri): String {
//        val name = uri.toString().split("/").last()
//        val ext = context.contentResolver.getType(uri)!!.split("/").last()
//        return "$name.$ext"
//    }
//}

class EditProfileFragment: Fragment() {

    lateinit var binding: FragmentEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1
    private var nickname: String = ""
    private var introduction: String = ""
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null // 선택된 이미지의 URI를 저장하기 위한 변수

    // 토큰 값 가져오기
    private fun getToken(): String?{
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    // SharedPreferences에서 사용자 ID 가져오기
    private fun getUserId(): Int {
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return  sharedPref.getInt("UserId", -1) // 기본값으로 -1 설정
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        // 백 버튼 클릭 시
        binding.backButtonIv.setOnClickListener {
            parentFragmentManager.popBackStack() // 이전 프래그먼트(마이페이지 프래그먼트)로 돌아감
        }

        // 프로필 편집 아이콘 클릭 시
        binding.editProfileIv.setOnClickListener {
            openGallery() // 갤러리에서 이미지 선택하는 함수 호출
        }

        // 저장 버튼 클릭 시
        binding.editSaveBt.setOnClickListener {
            nickname = binding.editNameEt.text.toString()
            introduction = binding.editIntroductionEt.text.toString()

            // 입력한 닉네임을 ViewModel에 저장
            profileViewModel.setNickname(nickname)
            profileViewModel.setIntroduction(introduction)

            Log.d("입력한 값 뷰모델에 저장 닉네임", profileViewModel.nickname.toString())
            Log.d("입력한 값 뷰모델에 저장 한줄소개", profileViewModel.introduction.toString())

            val requestJson = """
                {
                    "nickname": "$nickname",
                    "description": "$introduction"
                }
            """.trimIndent()

            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), requestJson)

            // 이미지 파일이 있을 경우 MultipartBody.Part로 변환
            val imagePart: MultipartBody.Part? = selectedImageUri?.let { uri ->
//                val file = File(getRealPathFromURI(uri))
//                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val file = getFileFromUri(uri)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }

            // 수정한 것
//            val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())
//
//            val imagePart: MultipartBody.Part? = selectedImageUri?.let { uri ->
//                val file = UriUtil.toFile(requireContext(), uri)
//                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                MultipartBody.Part.createFormData("image", file.name, requestFile)
//            }

            // API 호출
            updateProfile(imagePart, requestBody)

            //parentFragmentManager.popBackStack()

        }

        // 배경 클릭 시 키보드 숨기기
        binding.editProfileFragment.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){

            // 선택된 이미지의 URI를 가져옴
            val imageUri: Uri = data.data!!

            // 이미지뷰의 이전 이미지를 제거!!
            binding.profileIv.setImageDrawable(null)

            // 이미지 압축 및 리사이즈
            val compressedUri = getCompressedImageUri(imageUri)

            // 선택된 이미지 URI를 사용하여 ImageView에 설정
            //binding.profileIv.setImageURI(imageUri)

            // 압축된 이미지를 ImagView에 설정
            binding.profileIv.setImageURI(compressedUri)

            // Glide를 사용하여 원형 이미지로 로드
//            Glide.with(this)
//                .load(imageUri)
//                .apply(RequestOptions.circleCropTransform())
//                .into(binding.profileIv)
            Glide.with(this)
                .load(compressedUri)
                .apply(RequestOptions.circleCropTransform())
                .signature(ObjectKey(System.currentTimeMillis().toString())) // 캐시 무효화
                .into(binding.profileIv)

            // ViewModel에 선택된 이미지 URI를 설정하여 저장
            //profileViewModel.setProfileImageUri(imageUri)
            profileViewModel.setProfileImageUri(compressedUri)

            // selectedImageUri를 선택된 이미지의 URI로 업데이트
            //selectedImageUri = imageUri
            selectedImageUri = compressedUri
        }
    }

    // Uri를 실제 경로로 변환하는 함수
//    private fun getRealPathFromURI(uri: Uri): String {
//        var path = ""
//        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
//        if (cursor != null){
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            path = cursor.getString(idx)
//            cursor.close()
//        }
//
//        // 경로를 로그로 출력
//        Log.d("EditProfileFragment", "Selected image path: $path")
//
//        // 경로가 유효한지 확인
//        if (path != null) {
//            val file = File(path)
//            if (file.exists()) {
//                Log.d("EditProfileFragment", "File exists at path: $path")
//            } else {
//                Log.e("EditProfileFragment", "File does not exist at path: $path")
//            }
//        } else {
//            Log.e("EditProfileFragment", "Failed to get real path from URI")
//        }
//
//        return path
//    }

    // Uri에서 File 객체를 생성하는 함수
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


//    // Uri에서 이미지를 압축 및 리사이즈하는 함수
//    private fun getCompressedImageUri(uri: Uri): Uri {
//        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
//
//        // 이미지 크기를 줄이기 위한 비율 설정 (여기서는 50%로 설정)
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
//
//        // 압축된 이미지 파일을 저장할 임시 파일 생성
//        val compressedFile = File(requireContext().cacheDir, "compressed_image.jpg")
//        val outputStream = FileOutputStream(compressedFile)
//
//        // 압축 품질 설정 (여기서는 85%로 설정)
//        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
//        outputStream.flush()
//        outputStream.close()
//
//        return Uri.fromFile(compressedFile)
//    }
//
//    private fun getFileFromUri(uri: Uri): File {
//        val inputStream = requireContext().contentResolver.openInputStream(uri)
//        val tempFile = File.createTempFile("temp_image", null, requireContext().cacheDir)
//        inputStream.use { input ->
//            tempFile.outputStream().use { output ->
//                input?.copyTo(output)
//            }
//        }
//        return tempFile
//    }

    private fun updateProfile(imagePart: MultipartBody.Part?, requestBody: RequestBody){
        val token = getToken()
        val memberId = getUserId()

        if (token != null && memberId != -1){

            // 서비스 객체 생성
            val editProfileService = MypageObj.getRetrofit().create(MypageItf::class.java)
            val call = editProfileService.changeMyProfile("Bearer $token", memberId, imagePart, requestBody)

            call.enqueue(object: Callback<GetMypageResponse>{
                override fun onResponse(
                    call: Call<GetMypageResponse>,
                    response: Response<GetMypageResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    Log.e("Response code: ", "${response.code()}")
                    Log.e("Error body: ", "${response.errorBody()?.string()}")

                    when (response.code()){
                        200 -> {
                            // 프로필 편집 성공 시
                            val resp: GetMypageResponse = response.body()!!
                            if (resp != null){

                                Log.d("MYPAGE_UPDATE/SUCCESS", response.toString())
                                Toast.makeText(context, "프로필 수정 완료", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()

                            } else {
                                Log.e("실패", response.message())
                                Log.e("MYPAGE_UPDATE/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")

                                Toast.makeText(context, "프로필 수정 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                        400 -> {
                            // 프로필 수정 실패 이유: 동일한 닉네임 입력
                            Toast.makeText(context, "이미 닉네임이 존재합니다", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.e("Error", "응답 코드: ${response.code()}, 응답 메시지: ${response.message()}")
                            Toast.makeText(context, "프로필 수정 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                override fun onFailure(call: Call<GetMypageResponse>, t: Throwable) {
                    Log.d("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }
    }
}