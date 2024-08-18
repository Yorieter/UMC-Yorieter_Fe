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
import com.example.yorieter.mypage.Comment.CommentItf
import com.example.yorieter.mypage.Comment.UserCommentResponse
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
    private lateinit var mycommentRVAdapter: MycommentRVAdapter
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
        //댓글 삭제
        mycommentRVAdapter = MycommentRVAdapter(mycommentDatas) { position ->
            // 삭제 버튼 클릭 시 호출되는 콜백
            val comment = mycommentDatas[position]
            Log.d("fragment 활동중", "리스트 제거 전")

            deleteComment(comment.commentId) // API 호출
            Log.d("fragment 활동중", "에이피아이 호출 후")

            mycommentDatas.removeAt(position) // 리스트에서 제거
            Log.d("fragment 활동중", "리스트 제거 됨")

            mycommentRVAdapter.notifyItemRemoved(position) // 어댑터에 변경사항 반영
        }

        // 어댑터와 데이터 리스트(더미데이터) 연결
        //mycommentRVAdapter = MycommentRVAdapter(mycommentDatas)

        // 리사이클러뷰에 어댑터를 연결
        binding.mycommentContentVp.adapter = mycommentRVAdapter

        // 레이아웃 매니저 설정
        binding.mycommentContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 리사이클러뷰에 간격 설정
        binding.mycommentContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

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
            myCommentService.getMyComments("Bearer $token", memberId, page).enqueue(object: Callback<GetMyCommentResponse> {
                override fun onResponse(
                    call: Call<GetMyCommentResponse>,
                    response: Response<GetMyCommentResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: GetMyCommentResponse = response.body()!!
                    if (resp != null) {
                        if(resp.isSuccess) { // 응답 성공 시
                            Log.d("MYCOMMENT/SUCCESS", "댓글 조회 성공")

                            // 서버에서 받은 댓글 데이터를 mycommentDatas에 추가
                            val comments = resp.result.commentList.map { comment ->
                                Mycomment(
                                    coverImg = R.drawable.mypage_ic_yorieter_profile, // 고정된 이미지 사용
                                    comment = comment.content,
                                    date = "작성일자: ${comment.createdAt}",
                                    commentId = comment.commendId
                                )
                            }

                            // 리스트에 새 데이터 추가
                            mycommentDatas.addAll(comments)
                            mycommentRVAdapter.notifyDataSetChanged() // 데이터 변경 알림

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

    fun deleteComment(commentId: Int) {
        val token = getToken() ?: return
        val deleteService = MypageObj.getRetrofit().create(CommentItf::class.java)

        Log.d("API_CALL", "Deleting comment with commentId: $commentId")

        deleteService.deleteComment2("Bearer $token", commentId)
            .enqueue(object : Callback<UserCommentResponse> {
                override fun onResponse(
                    call: Call<UserCommentResponse>,
                    response: Response<UserCommentResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.isSuccess) {
                            Log.d("DELETE_API 성공", "Comment deleted successfully")
                        } else {
                            Log.e(
                                "DELETE_API 실패",
                                "Failed to delete comment: ${responseBody?.message}"
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "DELETE_API 실패",
                            "Failed to delete comment: ${response.code()} - $errorBody"
                        )
                    }
                }

                override fun onFailure(call: Call<UserCommentResponse>, t: Throwable) {
                    Log.e("DELETE_API 실패", "Error deleting comment", t)
                }
            })
    }
}