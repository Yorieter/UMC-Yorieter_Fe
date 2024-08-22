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

    companion object {
        private const val ARG_RECIPE_ID = "recipeId"
        fun newInstance(recipeId: Int): CommentFragment {
            val fragment = CommentFragment()
            val args = Bundle()
            args.putInt(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }

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

        // 전달받은 레시피 아이디 가져오기
        val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
        Log.d("전달받은 레시피 아이디 확인(댓글):", recipeId.toString())

//        //리사이클러 뷰에 연결
//        val commentRVAdapter = CommentRVAdapter(commentDatas)
//        binding.commentRv.adapter = commentRVAdapter
//        binding.commentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//
//        commentRVAdapter.mItemClickListener = object : CommentRVAdapter.CommentItemClickListener {
//            override fun onItemClick(recipe: Recipe) {
//                // recipe 객체를 사용하여 처리
//                val selectedRecipeId = recipe.recipeId
//                val commentFragment = CommentFragment.newInstance(selectedRecipeId)
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, commentFragment)
//                    .addToBackStack(null)
//                    .commit()
//            }
//        }

        // 댓글 조회 API
        val token = getToken()

        val commentService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)

        if (token != null){
            commentService.getAllComment("Bearer $token",recipeId).enqueue(object:
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
        // 전달받은 레시피 아이디 가져오기
        val recipeId = arguments?.getInt(ARG_RECIPE_ID) ?: -1
        Log.d("전달받은 레시피 아이디 확인(댓글): ", recipeId.toString())

        // 댓글 등록 API
        val token = getToken()

        val commentService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)

        if (token != null){
            commentService.postComments("Bearer $token",recipeId, CommentRequest(comment)).enqueue(object:
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

                            // 해당 레시피 아이디 저장하는 함수 호출
                            saveRecipeId(resp)


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

    private fun saveRecipeId(postcomment: PostCommentResponse){
        // 레시피 아이디 저장
        var recipeId = postcomment.result.recipeId

        // MyCommentFragment로 이동하면서 recipeId를 전달하는 Bundle 생성
        val bundle = Bundle().apply {
            putInt("recipe_id", recipeId) // recipeId를 Bundle에 저장
        }
        Log.d("CommentFragment", "Received recipeId: $recipeId")

        // MyCommentFragment 생성
        val myCommentFragment = MyCommentFragment()

        // Bundle을 MyCommentFragment에 전달
        myCommentFragment.arguments = bundle

//        // MyCommentFragment로 이동
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.main_frm, myCommentFragment)
//            .addToBackStack(null)
//            .commit()

    }
}
