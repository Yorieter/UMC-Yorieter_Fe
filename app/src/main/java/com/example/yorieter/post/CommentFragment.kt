package com.example.yorieter.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentCommentBinding
import com.example.yorieter.mypage.MyCommentFragment
import com.google.android.material.internal.ViewUtils.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentFragment : Fragment()  {
    lateinit var binding: FragmentCommentBinding
    private var commentDatas = ArrayList<CommentData>()
    private lateinit var adapter : CommentRVAdapter

    // 토큰 값 가져오기
    private fun getToken(): String?{
        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(inflater, container, false)

        commentDatas.apply {
            add(
                CommentData(
                    R.drawable.recipe,
                    "불타는 여우",
                    "5일전",
                    "넣고 싶은 야채들을 모두 넣을 수 있어서 냉장고 털이할 때 딱이겠네요!"
                )
            )
        }

//        //리사이클러 뷰에 연결
//        val commentRVAdapter = CommentRVAdapter(commentDatas)
//
//        binding.commentRv.adapter = commentRVAdapter
//
//        binding.commentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 댓글 조회 API
        val token = getToken()

        val commentService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)

        if (token != null){
            commentService.getAllComment("Bearer $token",2).enqueue(object:
                Callback<CommentsResponse> {
                override fun onResponse(
                    call: Call<CommentsResponse>,
                    response: Response<CommentsResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: CommentsResponse = response.body()!!
                    if (resp != null){
                        if (resp.isSuccess){
                            Log.d("COMMENT/SUCCESS", "댓글 조회 성공")

                            // 서버에서 받은 댓글 데이터를 commentDatas에 추가
                            resp.result?.forEach { comment ->
                                commentDatas.add(
                                    CommentData(
                                        comment_user_image = R.drawable.mypage_ic_yorieter_profile, // 고정된 이미지 사용
                                        comment_user_name = "User ${comment.memberId}", // 멤버 ID로 이름 대체
                                        comment_date = comment.createdAt, // 문자열 형태의 날짜
                                        comment_content = comment.content
                                    )
                                )
                            }

                            // RecyclerView 설정
                            adapter = CommentRVAdapter(commentDatas)
                            binding.commentRv.adapter = adapter
                            binding.commentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


                        } else {
                            Log.e("COMMENT/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.e("COMMENT/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<CommentsResponse>, t: Throwable) {
                    Log.e("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }

        // 키보드 숨기기
        binding.commentFragment.setOnClickListener {
            hideKeyboard()
        }

        // 뒤로가기
        binding.commentBackIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, RecipeFragment())
                .addToBackStack(null)
                .commit()
        }

        // 댓글 등록하기 버튼 클릭 시
        binding.commentUpBt.setOnClickListener {
            var commentText = binding.commentBox.text.toString()
            addComments(commentText)
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun addComments(comment: String){

        // 댓글 등록 API
        val token = getToken()

        val commentService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)

        if (token != null){
            commentService.postComments("Bearer $token",2, CommentRequest(comment)).enqueue(object:
                Callback<PostCommentResponse> {
                override fun onResponse(
                    call: Call<PostCommentResponse>,
                    response: Response<PostCommentResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: PostCommentResponse = response.body()!!
                    if (resp != null){
                        if (resp.isSuccess){
                            Log.d("COMMENT/SUCCESS", "댓글 등록 성공")

                            // 서버로부터 받은 댓글 데이터를 RecyclerView에 추가
                            val newComment = CommentData(
                                comment_user_image = R.drawable.mypage_ic_yorieter_profile,  // 고정된 이미지 사용
                                comment_user_name = "User ${resp.result.memberId}",  // 멤버 ID로 사용자 이름 대체
                                comment_date = resp.result.updatedAt,  // 업데이트 날짜 사용
                                comment_content = resp.result.content  // 댓글 내용
                            )

                            // RecyclerView의 데이터셋에 추가
                            commentDatas.add(newComment)
                            adapter.notifyItemInserted(commentDatas.size - 1)  // 새로운 아이템이 추가되었음을 알림
                            binding.commentRv.scrollToPosition(commentDatas.size - 1)  // 가장 최신 댓글로 스크롤

                            // 댓글 입력 필드 초기화
                            binding.commentBox.text.clear()


                        } else {
                            Log.e("COMMENT/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.e("COMMENT/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<PostCommentResponse>, t: Throwable) {
                    Log.e("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }
    }
}


//class CommentFragment : Fragment() {
//    lateinit var binding: FragmentCommentBinding
//    private var commentDatas = ArrayList<Comment>()
//    private lateinit var adapter: CommentRVAdapter
//
//    private fun getToken(): String? {
//        // SharedPreferences에서 토큰 값 가져오기
//        val sharedPref = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        return sharedPref?.getString("token", null)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentCommentBinding.inflate(inflater, container, false)
//
//
////        commentDatas.apply {
////            add(
////                CommentData(
////                    R.drawable.recipe,
////                    "불타는 여우",
////                    "5일전",
////                    "넣고 싶은 야채들을 모두 넣을 수 있어서 냉장고 털이할 때 딱이겠네요!"
////                )
////            )
////        }
//
//        //리사이클러 뷰에 연결
////        val commentRVAdapter = CommentRVAdapter(commentDatas)
////        binding.commentRv.adapter = commentRVAdapter
////        binding.commentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//
////    private fun hideKeyboard() {
////        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
////        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
////    }
//
//    // 댓글 조회 API
//    val token = getToken()
//
//    val commentService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
//
//    if (token != null)
//    {
//        commentService.getAllComment("Bearer $token", 2).enqueue(object :
//            Callback<CommentsResponse> {
//            override fun onResponse(
//                call: Call<CommentsResponse>,
//                response: Response<CommentsResponse>
//            ) {
//                Log.d("RETROFIT/SUCCESS", response.toString())
//                val resp: CommentsResponse = response.body()!!
//                if (resp != null) {
//                    if (resp.isSuccess) {
//                        Log.d("COMMENT/SUCCESS", "댓글 조회 성공")
//
//                        // 서버에서 받은 댓글 데이터를 commentDatas에 추가
//                        resp.result.forEach { comment ->
//                            commentDatas.add(
//                                CommentData(
//                                    comment_user_image = R.drawable.mypage_ic_yorieter_profile, // 고정된 이미지 사용
//                                    comment_user_name = "User ${comment.memberId}", // 멤버 ID로 이름 대체
//                                    comment_date = comment.createdAt, // 문자열 형태의 날짜
//                                    comment_content = comment.content
//                                )
//                            )
//                        }
//
//                        // RecyclerView 설정
//                        adapter = CommentRVAdapter(commentDatas)
//                        binding.commentRv.adapter = adapter
//                        binding.commentRv.layoutManager =
//                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//
//                    } else {
//                        Log.e("COMMENT/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
//                    }
//                } else {
//                    Log.e("COMMENT/FAILURE", "Response body is null")
//                }
//            }
//
//            override fun onFailure(call: Call<CommentsResponse>, t: Throwable) {
//                Log.e("RETROFIT/FAILURE", t.message.toString())
//            }
//
//    }
//
//    // 키보드 숨기기
//    binding.commentFragment.setOnClickListener {
//        hideKeyboard()
//    }
//
//    // 뒤로가기
//    binding.commentBackIv.setOnClickListener {
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.main_frm, RecipeFragment())
//            .addToBackStack(null)
//            .commit()
//    }
//
//    // 댓글 등록하기 버튼 클릭 시
//    binding.commentUpBt.setOnClickListener {
//        var commentText = binding.commentBox.text.toString()
//        addComments(commentText)
//    }
//
//    return binding.root
//}
//
//private fun hideKeyboard() {
//    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
//}
//
//private fun addComments(comment: String){
//
//    // 댓글 등록 API
//    val token = getToken()
//
//    val commentService = UserRetrofitObj.getRetrofit().create(HomeItf::class.java)
//
//    if (token != null) {
//        commentService.postComments("Bearer $token", 2, CommentRequest(comment)).enqueue(object :
//            Callback<PostCommentResponse> {
//            override fun onResponse(
//                call: Call<PostCommentResponse>,
//                response: Response<PostCommentResponse>
//            ) {
//                Log.d("RETROFIT/SUCCESS", response.toString())
//                val resp: PostCommentResponse = response.body()!!
//                if (resp != null) {
//                    if (resp.isSuccess) {
//                        Log.d("COMMENT/SUCCESS", "댓글 등록 성공")
//
//                        // 서버로부터 받은 댓글 데이터를 RecyclerView에 추가
//                        val newComment = CommentData(
//                            comment_user_image = R.drawable.mypage_ic_yorieter_profile,  // 고정된 이미지 사용
//                            comment_user_name = "User ${resp.result.memberId}",  // 멤버 ID로 사용자 이름 대체
//                            comment_date = resp.result.updatedAt,  // 업데이트 날짜 사용
//                            comment_content = resp.result.content  // 댓글 내용
//                        )
//
//                        // RecyclerView의 데이터셋에 추가
//                        commentDatas.add(newComment)
//                        adapter.notifyItemInserted(commentDatas.size - 1)  // 새로운 아이템이 추가되었음을 알림
//                        binding.commentRv.scrollToPosition(commentDatas.size - 1)  // 가장 최신 댓글로 스크롤
//
//                        // 댓글 입력 필드 초기화
//                        binding.commentBox.text.clear()
//
//
//                    } else {
//                        Log.e("COMMENT/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
//                    }
//                } else {
//                    Log.e("COMMENT/FAILURE", "Response body is null")
//                }
//            }
//
//            override fun onFailure(call: Call<PostCommentResponse>, t: Throwable) {
//                Log.e("RETROFIT/FAILURE", t.message.toString())
//            }
//
//        })
//
////    private fun getAllComments(recipeId: Int) {
////        val call = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
////        val BEARER_TOKEN = getToken()
////
////        call.getAllComment("Bearer $BEARER_TOKEN", recipeId)
////            .enqueue(object : Callback<CommentResponse> {
////                override fun onResponse(
////                    call: Call<CommentResponse>,
////                    response: Response<CommentResponse>
////                ) {
////                    if (response.isSuccessful && response.body()?.isSuccess == true) {
////                        Log.d("Comment API 응답함 ", "성공...^^")
////
////                        response.body()?.result?.let { comments ->
////                            commentDatas.clear()
////                            commentDatas.addAll(comments)
////                            adapter.notifyDataSetChanged() // 데이터 변경 후 어댑터에 알리기
////                        }
////
////                    } else {
////                        Log.e("Comment API 오류", "Error code: ${response.code()} - ${response.message()}")
////                    }
////                }
////
////                override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
////                    Log.e("Comment api 연동 자체가 안됨", "Failed to fetch posts: ${t.message}")
////                }
////            })
//
//    }
