package com.example.yorieter.mypage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.yorieter.mypage.api.MyPage
import com.example.yorieter.mypage.api.MyPageRequest
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.GetMypageResponse
import com.example.yorieter.mypage.dataclass.Mypost
import com.example.yorieter.mypage.viewModel.ProfileViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment: Fragment() {

    lateinit var binding: FragmentMypageBinding
    private val profileViewModel: ProfileViewModel by activityViewModels()

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

        val userId = getUserId()
        if (userId != -1){
            Log.d("사용자 아이디 가져오기 성공", userId.toString())
        }

        fetchMyProfile(userId) // 마이페이지 조회 API 연동 함수 호출

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        profileViewModel.nickname.observe(viewLifecycleOwner, { nickname ->
            Log.d("MypageFragment", "닉네임 업데이트: $nickname")
            binding.myProfileNameTv.text = nickname
        })

        profileViewModel.introduction.observe(viewLifecycleOwner, { introduction ->
            Log.d("MypageFragment", "한줄 소개 업데이트: $introduction")
            binding.myIntroductionTv.text = introduction
        })

        profileViewModel.profileImageUri.observe(viewLifecycleOwner, { uri ->
            Log.d("MypageFragment", "프로필 이미지 URI 업데이트: $uri")
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.myProfileIv)
        })
    }

    private fun fetchMyProfile(userId: Int){

        // 회원정보 조회 API 연동
        val authService = MypageObj.getRetrofit().create(MypageItf::class.java)
        authService.getMyProfile(userId).enqueue(object: Callback<GetMypageResponse> {
            override fun onResponse(
                call: Call<GetMypageResponse>,
                response: Response<GetMypageResponse>
            ) {
                Log.d("MYPAGE/SUCCESS", response.toString())
                val resp: GetMypageResponse = response.body()!!
                if (resp != null){
                    if(resp.isSuccess){ // 응답 성공 시
                        // 닉네임 적용
                        Log.d("nickname", resp.result.nickname)
                        val resultNickname = resp.result.nickname
                        binding.myProfileNameTv.text = resultNickname
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