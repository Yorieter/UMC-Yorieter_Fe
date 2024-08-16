package com.example.yorieter.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.yorieter.databinding.ActivityLogoutDialogBinding
import com.example.yorieter.login.api.ResponseData.LogoutResponse
import com.example.yorieter.login.api.UserRetrofitItf
import com.example.yorieter.login.api.UserRetrofitObj
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutDialogActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogoutDialogBinding

    // SharedPreferences에서 토큰 값 가져오기
    private fun getToken(): String?{
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이얼로그 제목을 빈 문자열로 설정
        setTitle("")

        // 취소 버튼 클릭 시
        binding.logoutCancelBtn.setOnClickListener {
            finish() // 다이얼로그 닫기
        }

        // 확인 버튼 클릭 시
        binding.logoutCheckBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout(){
        val BEARER_TOKEN = getToken()
        val authService = UserRetrofitObj.getRetrofit().create(UserRetrofitItf::class.java)

        // 로그아웃 API 연동
        authService.logout("Bearer $BEARER_TOKEN").enqueue(object: Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                Log.d("RETROFIT/SUCCESS", response.toString())
                val resp: LogoutResponse = response.body()!!
                if (resp != null) {
                    if(resp.isSuccess) { // 응답 성공 시
                        Log.d("LOGOUT/SUCCESS", "로그아웃 성공")

                        val intent = Intent(this@LogoutDialogActivity, LoginActivity::class.java) // 로그인 액티비티로 이동
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // 현재 액티비티 종료

                    } else {
                        Log.e("LOGOUT/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                    }
                } else {
                    Log.d("LOGOUT/FAILURE", "Response body is null")
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Log.d("RETROFIT/FAILURE", t.message.toString())
            }

        })
    }
}