package com.example.yorieter.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyPostBinding
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MypostRVAdapter
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.GetMyCommentResponse
import com.example.yorieter.mypage.api.ResponseData.GetRecipeResponse
import com.example.yorieter.mypage.api.ResponseData.RecipeResult
import com.example.yorieter.mypage.dataclass.Mypost
import com.example.yorieter.post.RecipeUserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostFragment: Fragment() {

    lateinit var binding: FragmentMyPostBinding
    private var mypostDatas = ArrayList<Mypost>()
    private lateinit var mypostRVAdapter: MypostRVAdapter
    private var filteredPosts = ArrayList<Mypost>() // 필터링된 게시물 리스트
    var currentPage = 1 // 현재 페이지 번호를 관리하는 변수
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

//        // mypost 데이터 리스트 생성 (더미 데이터) --> API 받기 전까지 임시 더미데이터 생성
//        mypostDatas.apply {
//            add(Mypost(R.drawable.mypage_mypost_image1, "누구나 손쉽게 만들 수 있는 두부 샐러드", "작성일자: 2024-06-29"))
//            add(Mypost(R.drawable.mypage_mypost_image2, "간단한 루꼴라 샌드위치", "작성일자: 2024-03-12"))
//            add(Mypost(R.drawable.mypage_mypost_basic_image, "완벽한 한끼 식사! 두부 유부초밥", "작성일자: 2024-02-24"))
//            add(Mypost(R.drawable.mypage_mypost_image3, "냉장고 털이 계란 두부 카레덮밥", "작성일자: 2024-01-07"))
//        }
//
//        // 어댑터와 데이터 리스트(더미데이터) 연결
//        filteredPosts.addAll(mypostDatas)
//        mypostRVAdapter = MypostRVAdapter(filteredPosts)

        // 스와이프 리프레쉬 레이아웃 초기화
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(
            // 스와이프 리프레쉬 레이아웃 색깔 변경 -> 블랙, 화이트
            ContextCompat.getColor(requireContext(), R.color.black),
            ContextCompat.getColor(requireContext(), R.color.white)
        )

        // 스와이프 리프레쉬 레이아웃 관련 동작 설정
        swipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch {
                isLoading(true)
                currentPage = 1 // 페이지를 1로 리셋
                mypostDatas.clear() // 데이터 초기화
                loadMyLikedRecipes(currentPage)
                delay(1500)
                isLoading(false)
                swipeRefreshLayout.isRefreshing = false // 새로고침 완료 시 리프레시 아이콘 감추기
            }
        }

//        // 초기 화면은 항상 최신화 상태 유지
//        CoroutineScope(Dispatchers.Main).launch {
//            loadMyLikedRecipes(currentPage)
//        }

        // 어댑터와 데이터 리스트(더미데이터) 연결
        mypostRVAdapter = MypostRVAdapter(mypostDatas)

        // 리사이클러뷰에 어댑터를 연결
        binding.mypostContentVp.adapter = mypostRVAdapter

        // 레이아웃 매니저 설정
        binding.mypostContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 리사이클러뷰에 간격 설정
        binding.mypostContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // 어댑터 클릭 리스너 설정
        // RecipeUserFragment로 이동 (레시피 수정)
        mypostRVAdapter.itemClickListner = object: MypostRVAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val selectedRecipeId = mypostDatas[position].recipeId // 클릭된 아이템의 레시피 아이디 가져오기
                Log.d("MyPostFragment 레시피 아이디", selectedRecipeId.toString())
                val recipeUserFragment = RecipeUserFragment.newInstance(selectedRecipeId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, recipeUserFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 초기 데이터 로드
        //loadMyLikedRecipes(currentPage)

        // 스크롤 리스너 추가
        binding.mypostContentVp.addOnScrollListener(object: RecyclerView.OnScrollListener(){
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

//        // SearchView 설정
//        binding.mypostSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterPosts(newText)
//                return true
//            }
//        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        if (isFirstLoad) {
//            isFirstLoad = false // 첫 로드가 완료되었음을 표시
//            loadMyLikedRecipes(currentPage)
//        }
        // 현재 페이지를 1로 리셋하고 데이터를 다시 로드
        currentPage = 1
        mypostDatas.clear() // 기존 데이터를 초기화
        loadMyLikedRecipes(currentPage)
    }

    private fun loadMyLikedRecipes(page: Int){

        val token = getToken()
        val memberId = getUserId()

        val myRecipeService = MypageObj.getRetrofit().create(MypageItf::class.java)

        // 내가 작성한 레시피 목록 조회 API 연동
        if (token != null){
            myRecipeService.getMyRecipes(token, memberId, page).enqueue(object: Callback<GetRecipeResponse>{
                override fun onResponse(
                    call: Call<GetRecipeResponse>,
                    response: Response<GetRecipeResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: GetRecipeResponse = response.body()!!
                    if (resp != null) {
                        if(resp.isSuccess) { // 응답 성공 시
                            Log.d("MYPOST/SUCCESS", "레시피 목록 조회 성공")

                            // 서버에서 받은 댓글 데이터를 mycommentDatas에 추가
                            val posts = resp.result.recipeList.map { post ->
                                Mypost (
                                    coverImg = post.imageUrl,
                                    title = post.title,
                                    date = "작성일자: ${post.createdAt}",
                                    recipeId = post.recipeId
                                )
                            }

                            // 리스트에 새 데이터 추가
                            mypostDatas.addAll(posts)
                            mypostRVAdapter.notifyDataSetChanged() // 데이터 변경 알림


                        } else {
                            Log.e("MYPOST/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.d("MYPOST/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<GetRecipeResponse>, t: Throwable) {
                    Log.d("RETROFIT/FAILURE", t.message.toString())
                }

            })
        }
    }

    private fun showEmptyState() {
        // 여기에서 빈 상태를 사용자에게 알려주는 로직을 구현합니다.
        // 예를 들어, 리사이클러뷰를 숨기고 빈 데이터 메시지를 표시합니다.
        binding.mypostContentVp.visibility = View.GONE // 리사이클러뷰 숨기기
    }

    private fun filterPosts(query: String?) {
        filteredPosts.clear() // 현재 필터링된 게시물 리스트를 초기화 (이전의 필터링 결과를 지움)

        if (query.isNullOrEmpty()) { // 검색어가 null이거나 빈 문자열인 경우, 모든 게시물을 필터링된 리스트에 추가
            filteredPosts.addAll(mypostDatas)
        } else {
            val lowerCaseQuery = query.toLowerCase() // 검색어를 소문자로 변환하여 대소문자를 구분하지 않고 검색
            for (post in mypostDatas) {
                // title이 null일 수 있으므로, null을 처리하고 빈 문자열을 기본값으로 설정
                val title = post.title?.toLowerCase() ?: ""
                if (title.contains(lowerCaseQuery)) { // 제목이 검색어를 포함하는 경우
                    filteredPosts.add(post) // 해당 게시물을 필터링
                }
            }
        }

        mypostRVAdapter.notifyDataSetChanged() // 어댑터에게 데이터가 변경되었음을 알림
    }

    private fun formatDate(date: String): String {
        return date
    }

    // 새로고침 상태 여부에 따른 shimmer effect 설정
    private fun isLoading(isLoading: Boolean){
        if(isLoading){
            binding.shimmerLayout.startShimmer()
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.mypostContentVp.visibility = View.GONE
        }
        else {
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.mypostContentVp.visibility = View.VISIBLE
        }
    }
}