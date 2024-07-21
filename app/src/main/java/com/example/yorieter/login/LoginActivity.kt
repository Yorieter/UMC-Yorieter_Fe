package com.example.yorieter.login

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.yorieter.MainActivity
import com.example.yorieter.R
import com.example.yorieter.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "회원가입을 아직 하지 않으셨나요?" 텍스트에 밑줄 추가
        binding.signUpMessageTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // "회원가입을 아직 하지 않으셨나요?" 텍스트 클릭 시
        binding.signUpMessageTv.setOnClickListener {

            // 회원가입 화면으로 이동
            val intent = Intent(this, SignUpActivity::class.java)

            // 슬라이드 애니메이션 적용
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_in_left)
            startActivity(intent, options.toBundle())
        }

        // 로그인 버튼 클릭 시
        binding.loginBt.setOnClickListener {

            // 로그인 확인하는 함수 호출
            if (loginCheck()){
                moveMainActivity() // 메인 화면 이동하는 함수 호출
            }
        }

        // 아이디 입력 칸 클릭 시 오류 메시지 없애기
        binding.loginIdEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.loginErTv.visibility = View.GONE
            }
        }

        // 비밀번호 입력 칸 클릭 시 오류 메시지 없애기
        binding.loginPwEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.loginErTv.visibility = View.GONE
            }
        }

        // 배경화면 클릭 시 키보드 숨기기
        binding.loginActivity.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    view.clearFocus()
                }
            }
            true
        }
    }

    private fun loginCheck(): Boolean {
        // 입력하지 않은 경우 (빈칸)
        if (binding.loginIdEt.text.toString().isEmpty() || binding.loginPwEt.text.toString().isEmpty()){
            binding.loginErTv.visibility = View.VISIBLE

            return false
        }

        return true
    }

    private fun moveMainActivity(){
        // 메인 화면으로 이동
        val intent = Intent(this, MainActivity::class.java)

        // 페이드 효과 적용
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
        Toast.makeText(this, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()
    }
}