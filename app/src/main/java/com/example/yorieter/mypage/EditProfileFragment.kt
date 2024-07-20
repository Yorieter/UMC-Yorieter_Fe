package com.example.yorieter.mypage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentEditProfileBinding
import com.example.yorieter.mypage.viewModel.ProfileViewModel


class EditProfileFragment: Fragment() {

    lateinit var binding: FragmentEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1
    private var nickname: String = ""
    private var introduction: String = ""
    private val profileViewModel: ProfileViewModel by activityViewModels()

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

            parentFragmentManager.popBackStack() // 이전 프래그먼트인 MyPageFragment로 이동
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

            // 선택된 이미지 URI를 사용하여 ImageView에 설정
            binding.profileIv.setImageURI(imageUri)

            // Glide를 사용하여 원형 이미지로 로드
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileIv)

            // ViewModel에 선택된 이미지 URI를 설정하여 저장
            profileViewModel.setProfileImageUri(imageUri)
        }
    }
}