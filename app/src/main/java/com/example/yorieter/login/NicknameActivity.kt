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
import com.example.yorieter.MainActivity
import com.example.yorieter.R
import com.example.yorieter.databinding.ActivityNicknameBinding

class NicknameActivity: AppCompatActivity() {
    lateinit var binding: ActivityNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 확인 버튼 클릭 시
        binding.saveBt.setOnClickListener {
            val nickname = binding.nicknameEt.text.toString()

            // 닉네임 조건 검사
            if (isInputValid(nickname)){
                // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)

                // 페이드 효과 적용
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                startActivity(intent, options.toBundle())
                Toast.makeText(this, "요리어터에 오신 걸 환영합니다!", Toast.LENGTH_SHORT).show()
            } else {
                // 오류 메시지 표시
                binding.nicknanmeErTv.visibility = View.VISIBLE
            }
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

    // 입력된 아이디와 비밀번호의 유효성 검사
    private fun isInputValid(nickname: String): Boolean {
        return nickname.length >=10
    }
}