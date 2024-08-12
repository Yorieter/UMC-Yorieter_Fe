package com.example.yorieter.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyCommentBinding
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MycommentRVAdapter
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.GetMyCommentResponse
import com.example.yorieter.mypage.dataclass.Mycomment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCommentFragment: Fragment() {

    lateinit var binding: FragmentMyCommentBinding
    private var mycommentDatas = ArrayList<Mycomment>()
    var currentPage = 1 // 현재 페이지 번호를 관리하는 변수

    // 토큰 값 가져오기
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
        binding = FragmentMyCommentBinding.inflate(inflater, container, false)

//        // mycomment 데이터 리스트 생성 (더미 데이터) --> API 받기 전까지 임시 더미데이터 생성
//        mycommentDatas.apply {
//            add(Mycomment(R.drawable.mypage_ic_yorieter_profile, "너무 유용한 레시피 잘 보고 갑니다!", "작성일자: 2024-02-13"))
//            add(Mycomment(R.drawable.mypage_ic_yorieter_profile, "집에 있는 재료들로 만들어 먹을 수 있는 요리여서 더 자주 만들어 먹게 되는 것 같", "작성일자: 2024-02-09"))
//        }
//
//        // 어댑터와 데이터 리스트(더미데이터) 연결
//        val mycommentRVAdapter = MycommentRVAdapter(mycommentDatas)
//
//        // 리사이클러뷰에 어댑터를 연결
//        binding.mycommentContentVp.adapter = mycommentRVAdapter
//
//        // 레이아웃 매니저 설정
//        binding.mycommentContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//        // 리사이클러뷰에 간격 설정
//        binding.mycommentContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // 초기 데이터 로드
        loadMyLikedRecipes(currentPage)

        // 스크롤 리스너 추가
        binding.mycommentContentVp.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem == totalItemCount -1){
                    currentPage++
                    loadMyLikedRecipes(currentPage)
                }
            }
        })

        // 백버튼 클릭 시
        binding.backButtonIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun loadMyLikedRecipes(page: Int){

        val token = getToken()
        val memberId = getUserId()

        val myCommentService = MypageObj.getRetrofit().create(MypageItf::class.java)

        // 내가 작성한 댓글 목록 조회 API 연동
        if (token != null){
            myCommentService.getMyComments(token, memberId, page).enqueue(object: Callback<GetMyCommentResponse> {
                override fun onResponse(
                    call: Call<GetMyCommentResponse>,
                    response: Response<GetMyCommentResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: GetMyCommentResponse = response.body()!!
                    if (resp != null) {
                        if(resp.isSuccess) { // 응답 성공 시
                            Log.d("MYCOMMENT/SUCCESS", "댓글 조회 성공")
                            // 리사이클러뷰 적용

                        } else {
                            Log.e("MYCOMMENT/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.d("MYCOMMENT/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<GetMyCommentResponse>, t: Throwable) {
                    Log.d("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }
    }
}