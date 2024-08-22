package com.example.yorieter.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyLikeBinding
import com.example.yorieter.home.HomeFragment
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MylikeRVAdapter
import com.example.yorieter.mypage.api.MypageItf
import com.example.yorieter.mypage.api.MypageObj
import com.example.yorieter.mypage.api.ResponseData.GetMyCommentResponse
import com.example.yorieter.mypage.api.ResponseData.GetMyLikeResponse
import com.example.yorieter.mypage.api.ResponseData.GetRecipeResponse
import com.example.yorieter.mypage.dataclass.Mylike
import com.example.yorieter.mypage.dataclass.Mylike2
import com.example.yorieter.mypage.viewModel.MyLikeViewModel
import com.example.yorieter.post.RecipeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyLikeFragment: Fragment() {

    //private var mylikeDatas = ArrayList<Mylike>()
    private var mylikeDatas = ArrayList<Mylike>()
    lateinit var binding: FragmentMyLikeBinding
    private val mylikeViewModel: MyLikeViewModel by activityViewModels()
    var currentPage = 1 // 현재 페이지 번호를 관리하는 변수
    private lateinit var mylikeRVAdapter: MylikeRVAdapter
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
        binding = FragmentMyLikeBinding.inflate(inflater, container, false)

//        // mybookmark 데이터 리스트 생성 (더미 데이터) --> API 받기 전까지 임시 더미데이터 생성
//        mylikeDatas.apply {
//            add(Mylike(R.drawable.mypage_bookmark_image1, "여름에 무조건 먹어야 하는 냉면"))
//            add(Mylike(R.drawable.mypage_bookmark_image2, "지중해식 샐러드"))
//            add(Mylike(R.drawable.mypage_bookmark_image1, "여름에 무조건 먹어야 하는 냉면"))
//            add(Mylike(R.drawable.mypage_bookmark_image2, "지중해식 샐러드"))
//        }
//
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
                mylikeDatas.clear() // 데이터 초기화
                loadMyLikedRecipes(currentPage)
                delay(1500)
                isLoading(false)
                swipeRefreshLayout.isRefreshing = false // 새로고침 완료 시 리프레시 아이콘 감추기
            }
        }

        // 어댑터와 데이터 리스트(더미데이터) 연결
        mylikeRVAdapter = MylikeRVAdapter(mylikeDatas)

        // 리사이클러뷰에 어댑터를 연결
        binding.mylikeContentVp.adapter = mylikeRVAdapter

//        // GridLayoutManager 설정
//        val layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
//        binding.mylikeContentVp.layoutManager = layoutManager
        binding.mylikeContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 리사이클러뷰에 간격 설정
        binding.mylikeContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // 어댑터 클릭 리스너 설정
        mylikeRVAdapter.itemClickListner = object: MylikeRVAdapter.OnItemClickListener{
            // 레시피 프래그먼트로 이동
            override fun onItemClick(view: View, position: Int) {
                val selectedRecipeId = mylikeDatas[position].recipeId
                Log.d("MyLikeFragment 레시피 아이디", selectedRecipeId.toString())
                // 레시피 프래그먼트로 아이디 전달
                val recipeFragment =  RecipeFragment.newInstance(selectedRecipeId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, recipeFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 초기 데이터 로드
        //loadMyLikedRecipes(currentPage)

        // 스크롤 리스너 추가
        binding.mylikeContentVp.addOnScrollListener(object: RecyclerView.OnScrollListener(){
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

    override fun onResume() {
        super.onResume()
        // 현재 페이지를 1로 리셋하고 데이터를 다시 로드
        currentPage = 1
        mylikeDatas.clear() // 기존 데이터를 초기화
        loadMyLikedRecipes(currentPage)
    }

    private fun loadMyLikedRecipes(page: Int){

        val token = getToken()
        val memberId = getUserId()

        val myLikeService = MypageObj.getRetrofit().create(MypageItf::class.java)

        if (token != null){
            // 내가 좋아요한 레시피 목록 조회 API 연동
            myLikeService.getMyRecipeLikes("Bearer $token", memberId, page).enqueue(object:
                Callback<GetMyLikeResponse> {
                override fun onResponse(
                    call: Call<GetMyLikeResponse>,
                    response: Response<GetMyLikeResponse>
                ) {
                    Log.d("RETROFIT/SUCCESS", response.toString())
                    val resp: GetMyLikeResponse = response.body()!!
                    Log.d("리스트", resp.result.recipeLikeList.toString())
                    if (resp != null) {
                        if(resp.isSuccess) { // 응답 성공 시
                            Log.d("MYLIKE/SUCCESS", "좋아요 조회 성공")
                            Log.d("리스트", resp.result.recipeLikeList.toString())

                            val newLikes = resp.result.recipeLikeList.map { recipe ->
                                Mylike(
                                    coverImg = recipe.imageUrl,
                                    title = recipe.title,
                                    date = "작성일자: ${recipe.createdAt}",
                                    recipeId = recipe.recipeId)
                            }
                            mylikeDatas.addAll(newLikes)

                            mylikeRVAdapter.notifyDataSetChanged()

                        } else {
                            Log.e("MYLIKE/FAILURE", "응답 코드: ${resp.code}, 응답메시지: ${resp.message}")
                        }
                    } else {
                        Log.d("MYLIKE/FAILURE", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<GetMyLikeResponse>, t: Throwable) {
                    Log.d("RETROFIT/FAILURE", t.message.toString())
                }
            })
        }
    }

    // 새로고침 상태 여부에 따른 shimmer effect 설정
    private fun isLoading(isLoading: Boolean){
        if(isLoading){
            binding.shimmerLayout.startShimmer()
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.mylikeContentVp.visibility = View.GONE
        }
        else {
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.mylikeContentVp.visibility = View.VISIBLE
        }
    }

//    override fun onStart(){
//        super.onStart()
//        initMyLikeRecyclerView()
//    }

//    private fun initMyLikeRecyclerView(){
//        // viewModel에 어댑터를 연결
//        val mylikeRVAdapter = MylikeRVAdapter(mylikeViewModel)
//
//        // 리사이클러뷰에 어댑터를 연결
//        binding.mylikeContentVp.adapter = mylikeRVAdapter
//        binding.mylikeContentVp.setHasFixedSize(true)
//
//        // GridLayoutManager 설정
//        val layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
//        binding.mylikeContentVp.layoutManager = layoutManager
//
//        // 리사이클러뷰에 간격 설정
//        binding.mylikeContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정
//
//        // ViewModel의 데이터 변경을 관찰
//        mylikeViewModel.mylikeList.observe(viewLifecycleOwner, { mylikeList ->
//            mylikeRVAdapter.submitList(mylikeList)
//        })
//
//        // 어댑터 클릭 리스너 설정
//        mylikeRVAdapter.itemClickListner = object: MylikeRVAdapter.OnItemClickListener{
//            // 레시피 프래그먼트로 이동
//            override fun onItemClick(view: View, position: Int) {
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, RecipeFragment())
//                    .addToBackStack(null)
//                    .commit()
//
//                // 바텀 네비게이션의 선택 상태 변경
//                //activity?.findViewById<BottomNavigationView>(R.id.main_bnv)?.selectedItemId = R.id.homeFragment
//            }
//        }
//    }
}