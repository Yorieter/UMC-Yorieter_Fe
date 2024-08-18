package com.example.yorieter.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yorieter.databinding.ActivityRecipeDeleteDialogBinding
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.DeleteRecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecipeDeleteDialogActivity: AppCompatActivity() {
    lateinit var binding: ActivityRecipeDeleteDialogBinding
    private var recipeId: Int = -1 // 전달받은 레시피 아이디 저장

    // 토큰 값 가져오기
    private fun getToken(): String? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 레시피 아이디 가져오기
        recipeId = intent.getIntExtra("recipeId", -1)
        Log.d("RecipeDeleteDialog", "Received recipeId: $recipeId")

        // 다이얼로그 제목을 빈 문자열로 설정
        setTitle("")

        // 취소 버튼 클릭 시
        binding.recipeDeleteCancelBtn.setOnClickListener {
            finish() // 다이얼로그 닫기
        }

        // 확인 버튼 클릭 시
        binding.recipeDeleteCheckBtn.setOnClickListener {
            deleteRecipe()
        }
    }

    private fun deleteRecipe() { // 레시피 삭제 API

        val token = getToken()

        val deleteRecipeService = MypageObj.getRetrofit().create(MypageItf::class.java)

        if (token != null) {
            deleteRecipeService.deleteRecipe("Bearer $token", recipeId).enqueue(object :
                Callback<DeleteRecipeResponse> {
                override fun onResponse(
                    call: Call<DeleteRecipeResponse>,
                    response: Response<DeleteRecipeResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: DeleteRecipeResponse = response.body()!!
                    if (resp != null) {
                        if (resp.isSuccess) {
                            Log.d("DELETERECIPE/SUCCESS", "레시피 삭제 성공")
                            Toast.makeText(this@RecipeDeleteDialogActivity, "레시피가 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                            // 액티비티 종료
                            finish()

                        } else {
                            Log.e(
                                "DELETERECIPE/FAILURE",
                                "응답 코드: ${resp.code}, 응답메시지: ${resp.message}"
                            )
                        }
                    } else {
                        Log.e("DELETERECIPE/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<DeleteRecipeResponse>, t: Throwable) {
                    Log.e("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }
    }
}