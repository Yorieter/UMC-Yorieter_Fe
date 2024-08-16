package com.example.yorieter.mypage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yorieter.databinding.ActivityDeleteUserDialogBinding
import com.example.yorieter.login.LoginActivity

class DeleteUserDialogActivity: AppCompatActivity() {
    lateinit var binding: ActivityDeleteUserDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteUserDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이얼로그 제목을 빈 문자열로 설정
        setTitle("")

        // 취소 버튼 클릭 시
        binding.deleteCancelBtn.setOnClickListener {
            finish() // 다이얼로그 닫기
        }

        // 확인 버튼 클릭 시
        binding.deleteCheckBtn.setOnClickListener {
            val intent = Intent(this@DeleteUserDialogActivity, LoginActivity::class.java) // 로그인 액티비티로 이동
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }
}