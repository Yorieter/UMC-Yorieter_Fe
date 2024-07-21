package com.example.yorieter.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.yorieter.R
import com.example.yorieter.databinding.ActivitySignUpBinding

class SignUpActivity: AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 약관 전체 동의 체크박스 클릭 시 전체 체크 박스 ON
        binding.checkBox1.setOnCheckedChangeListener { _, isChecked ->
            binding.checkBox2.isChecked = isChecked
            binding.checkBox3.isChecked = isChecked
            binding.checkBox4.isChecked = isChecked
        }

        // 가입 버튼 클릭 시
        binding.signUpBt.setOnClickListener {

            // 체크박스가 체크되어 있는지 확인
            if(!binding.checkBox1.isChecked && !binding.checkBox2.isChecked && !binding.checkBox3.isChecked){
                Toast.makeText(this, "이용약관 및 개인정보처리방침에 대해 동의해야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = binding.signUpIdEt.text.toString()
            val password = binding.signUpPwEt.text.toString()

            // 아이디와 비밀번호 조건 검사
            if (isInputValid(id, password)){

                // 닉네임 화면으로 이동
                val intent = Intent(this, NicknameActivity::class.java)

                // 슬라이드 애니메이션 적용
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_in_left)
                startActivity(intent, options.toBundle())
            } else {
                // 오류 메시지 표시
                binding.signUpErTv.visibility = View.VISIBLE
            }

        }

        // 아이디 입력 칸 클릭 시 오류 메시지 없애기
        binding.signUpIdEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.signUpErTv.visibility = View.GONE
            }
        }

        // 비밀번호 입력 칸 클릭 시 오류 메시지 없애기
        binding.signUpPwEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.signUpErTv.visibility = View.GONE
            }
        }

        // 배경화면 클릭 시 키보드 숨기기
        binding.signUpActivity.setOnTouchListener { _, event ->
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

    // 입력된 아이디와 비밀번호의 유효성 검사
    private fun isInputValid(id: String, password: String): Boolean {
        return id.length in 5..13 && password.length >=8
    }
}