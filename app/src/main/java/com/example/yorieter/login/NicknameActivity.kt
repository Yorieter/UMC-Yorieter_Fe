package com.example.yorieter.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.yorieter.MainActivity
import com.example.yorieter.R
import com.example.yorieter.databinding.ActivityNicknameBinding
import com.example.yorieter.login.api.ResponseData.SignUpResponse
import com.example.yorieter.login.api.SignUpClient
import com.example.yorieter.login.api.UserRetrofitItf
import com.example.yorieter.login.api.UserRetrofitObj
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicknameActivity: AppCompatActivity() {
    lateinit var binding: ActivityNicknameBinding
    private var username: String = ""
    private var password: String = ""
    private var nickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SignUpActivity에서 username과 password 전달 받음
        username = intent.getStringExtra("username") ?: ""
        password = intent.getStringExtra("password") ?: ""
//        // 잘 전달되었는지 확인
//        Log.d("user", username)
//        Log.d("user2", password)

        // 확인 버튼 클릭 시
        binding.saveBt.setOnClickListener {
            nickname = binding.nicknameEt.text.toString()
            checkNickname() // 닉네임 확인하는 함수 호출
        }

        // 배경화면 클릭 시 키보드 숨기기
        binding.nicknameActivity.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    view.clearFocus()
                }
            }
            true
        }

        // 닉네임 입력 칸 클릭 시 오류 메시지 없애기
        binding.nicknameEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nicknanmeErTv.visibility = View.GONE
            }
        }
    }

    private fun checkNickname(){

        // 닉네임을 입력하지 않은 경우
        if (binding.nicknameEt.text.toString().isEmpty()){
            Toast.makeText(this, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 닉네임을 10글자 이상 입력한 경우
        if (!(nickname.length <= 10)){
            // 오류 메시지 출력
            Toast.makeText(this, "10자 이내로 작성해야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 회원가입 API 연동
        val authService = UserRetrofitObj.getRetrofit().create(UserRetrofitItf::class.java)
        authService.signUp(SignUpClient(username, password, nickname)).enqueue(object: Callback<SignUpResponse>{
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                val resp: SignUpResponse = response.body()!!
                if (resp != null){
                    Log.d("SIGNUP/SUCCESS22", response.toString())
                    if (resp.isSuccess){
                        Log.d("SIGNUP/SUCCESS333", response.toString())
                        moveLoginActivity() // 회원가입 진행
                    } else {
                        errormessage() // 오류 메시지 출력
                        Log.e("SIGNUP/FAILURE",
                            "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        //return
                    }
                } else {
                    Log.d("SIGNUP/FAILURE", "Response body is null")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Log.d("RETROFIT/FAILURE", t.message.toString())
            }

        })
    }

    private fun errormessage(){
        // "이미 사용 중인 닉네임" 텍스트
        binding.nicknanmeErTv.visibility = View.VISIBLE

        // EditText 비워줌 (사용자가 입력한 닉네임 삭제)
        binding.nicknameEt.text.clear()
    }

    private fun moveLoginActivity(){
        // 로그인 화면으로 이동
        val intent = Intent(this, LoginActivity::class.java)

        // 슬라이드 효과 적용
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_in_left)
        startActivity(intent, options.toBundle())
    }
}