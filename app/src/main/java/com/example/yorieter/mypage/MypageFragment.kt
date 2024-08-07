package com.example.yorieter.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMypageBinding
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yorieter.login.LoginActivity
import com.example.yorieter.mypage.dataclass.Mypost
import com.example.yorieter.mypage.viewModel.ProfileViewModel

class MypageFragment: Fragment() {

    lateinit var binding: FragmentMypageBinding
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 편집 버튼 클릭 시
        binding.mypageEditTv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, EditProfileFragment()) // 프로필 편집 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // ViewModel
        profileViewModel.nickname.observe(viewLifecycleOwner, { nickname ->
            binding.myProfileNameTv.text = nickname
        })

        profileViewModel.introduction.observe(viewLifecycleOwner, { introduction ->
            binding.myIntroductionTv.text = introduction
        })

        profileViewModel.profileImageUri.observe(viewLifecycleOwner, { uri ->
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.myProfileIv)
        })

        // 더보기 버튼 클릭 시 (내가 작성한 게시물의 더보기)
        binding.moreIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, MyPostFragment()) // 게시물 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 댓글 아이콘 버튼 클릭 시
        binding.myCommentIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, MyCommentFragment()) // 댓글 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 저장한 게시물 아이콘 클릭 시
        binding.myLikeIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, MyLikeFragment()) // 북마크 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 로그아웃 아이콘 클릭 시
        binding.logoutIv.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java) // 로그인 액티비티로 이동
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }

        // 이용약관 아이콘 클릭 시
        binding.agreementIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, AgreementFragment()) // 이용약관 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        return binding.root
    }

}