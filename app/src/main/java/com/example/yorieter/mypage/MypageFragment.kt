package com.example.yorieter.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMypageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yorieter.login.LogoutDialogActivity
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.GetMypageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment: Fragment() {

    lateinit var binding: FragmentMypageBinding

    private val textMessages = listOf(
        "다이어트는\n요리어터와 함께!",
        "건강을 위한\n스마트한 선택!",
        "매일매일\n새로운 요리",
        "당신의 건강을\n지키는 방법",
        "지금 시작하세요,\n건강한 식사!"
    )

    private var currentMessageIndex = 0

    private val textChangeHandler = Handler(Looper.getMainLooper())
    private val textChangeRunnable = object : Runnable {
        override fun run() {
            updateTextMessage()
            textChangeHandler.postDelayed(this, 3000) // Change text every 3 seconds
        }
    }

    // SharedPreferences에서 토큰 값 가져오기
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

        // 게시물 아이콘 버튼 클릭 시
        binding.myPostIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, MyPostFragment()) // 게시물 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 게시물 텍스트 버튼 클릭 시
        binding.myPostTv.setOnClickListener {
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

        // 댓글 텍스트 버튼 클릭 시
        binding.myCommentTv.setOnClickListener {
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

        // 저장한 텍스트 아이콘 클릭 시
        binding.myLikeTv.setOnClickListener {
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
            // LogoutDialogActivity 호출
            val intent = Intent(requireContext(), LogoutDialogActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 텍스트 클릭 시
        binding.logoutTv.setOnClickListener {
            // LogoutDialogActivity 호출
            val intent = Intent(requireContext(), LogoutDialogActivity::class.java)
            startActivity(intent)
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

        // 이용약관 텍스트 클릭 시
        binding.agreementTv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, AgreementFragment()) // 이용약관 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        // 회원탈퇴 아이콘 클릭 시
        binding.deleteAccountIv.setOnClickListener {
            // DeleteUserDialogActivity 호출
            val intent = Intent(requireContext(), DeleteUserDialogActivity::class.java)
            startActivity(intent)
        }

        // 회원탈퇴 텍스트 클릭 시
        binding.deleteAccountTv.setOnClickListener {
            // DeleteUserDialogActivity 호출
            val intent = Intent(requireContext(), DeleteUserDialogActivity::class.java)
            startActivity(intent)
        }

        // 텍스트 애니메이션 적용
//        val fadeInOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_out)
//        binding.diotMent.startAnimation(fadeInOut)
        // 텍스트 애니메이션 시작
        textChangeHandler.post(textChangeRunnable)

        return binding.root
    }

    private fun updateTextMessage() {
        val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

        binding.diotMent.startAnimation(fadeOut)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.diotMent.text = textMessages[currentMessageIndex]
                currentMessageIndex = (currentMessageIndex + 1) % textMessages.size
                binding.diotMent.startAnimation(fadeIn)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textChangeHandler.removeCallbacks(textChangeRunnable) // 메모리 누수 방지를 위해 핸들러 콜백 제거
    }

    // onResume 메서드는 프래그먼트가 사용자와 상호작용을 재개할 때 호출 됨. 즉, 마이페이지 조회 시 최신 정보를 불러옴
    override fun onResume() {
        super.onResume()
        val token = getToken()
        val userId = getUserId()
        if (userId != -1 && token != null) {
            fetchMyProfile(userId) // 마이페이지 조회 API 연동 함수 호출
        }
    }

    private fun fetchMyProfile(userId: Int){

        val BEARER_TOKEN = getToken()

        // 회원정보 조회 API 연동
        val authService = MypageObj.getRetrofit().create(MypageItf::class.java)
        authService.getMyProfile("Bearer $BEARER_TOKEN", userId).enqueue(object: Callback<GetMypageResponse> {
            override fun onResponse(
                call: Call<GetMypageResponse>,
                response: Response<GetMypageResponse>
            ) {
                Log.d("RETROFIT/SUCCESS", response.toString())
                val resp: GetMypageResponse = response.body()!!
                if (resp != null){
                    if(resp.isSuccess){ // 응답 성공 시

                        Log.d("MYPAGE/SUCCESS", response.toString())

                        // 응답 값 확인
                        Log.d("nickname", resp.result.nickname)
                        Log.d("description", resp.result.description)
                        Log.d("profileUrl", resp.result.profileUrl)

                        // UI 적용
                        binding.myProfileNameTv.text = resp.result.nickname
                        binding.myIntroductionTv.text = resp.result.description
                        Glide.with(this@MypageFragment)
                            .load(resp.result.profileUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.myProfileIv)

                    } else {
                        Log.e("MYPAGE/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                    }
                } else {
                    Log.d("MYPAGE/FAILURE", "Response body is null")
                }
            }

            override fun onFailure(call: Call<GetMypageResponse>, t: Throwable) {
                Log.d("RETROFIT/FAILURE", t.message.toString())
            }

        })
    }
}