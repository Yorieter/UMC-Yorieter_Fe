package com.example.yorieter.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyPostBinding
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MypostRVAdapter
import com.example.yorieter.mypage.dataclass.Mypost

class MyPostFragment: Fragment() {

    lateinit var binding: FragmentMyPostBinding

    private var mypostDatas = ArrayList<Mypost>()

    private lateinit var mypostRVAdapter: MypostRVAdapter
    private var filteredPosts = ArrayList<Mypost>() // 필터링된 게시물 리스트
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        // mypost 데이터 리스트 생성 (더미 데이터) --> API 받기 전까지 임시 더미데이터 생성
        mypostDatas.apply {
            add(Mypost(R.drawable.mypage_mypost_image1, "누구나 손쉽게 만들 수 있는 두부 샐러드", "작성일자: 2024-06-29"))
            add(Mypost(R.drawable.mypage_mypost_image2, "간단한 루꼴라 샌드위치", "작성일자: 2024-03-12"))
            add(Mypost(R.drawable.mypage_mypost_basic_image, "완벽한 한끼 식사! 두부 유부초밥", "작성일자: 2024-02-24"))
            add(Mypost(R.drawable.mypage_mypost_image3, "냉장고 털이 계란 두부 카레덮밥", "작성일자: 2024-01-07"))
            add(Mypost(R.drawable.mypage_mypost_image1, "누구나 손쉽게 만들 수 있는 두부 샐러드", "작성일자: 2024-06-29"))
            add(Mypost(R.drawable.mypage_mypost_image2, "간단한 루꼴라 샌드위치", "작성일자: 2024-03-12"))
            add(Mypost(R.drawable.mypage_mypost_basic_image, "완벽한 한끼 식사! 두부 유부초밥", "작성일자: 2024-02-24"))
            add(Mypost(R.drawable.mypage_mypost_image3, "냉장고 털이 계란 두부 카레덮밥", "작성일자: 2024-01-07"))
            add(Mypost(R.drawable.mypage_mypost_image1, "누구나 손쉽게 만들 수 있는 두부 샐러드", "작성일자: 2024-06-29"))
            add(Mypost(R.drawable.mypage_mypost_image2, "간단한 루꼴라 샌드위치", "작성일자: 2024-03-12"))
            add(Mypost(R.drawable.mypage_mypost_basic_image, "완벽한 한끼 식사! 두부 유부초밥", "작성일자: 2024-02-24"))
            add(Mypost(R.drawable.mypage_mypost_image3, "냉장고 털이 계란 두부 카레덮밥", "작성일자: 2024-01-07"))
            add(Mypost(R.drawable.mypage_mypost_image1, "누구나 손쉽게 만들 수 있는 두부 샐러드", "작성일자: 2024-06-29"))
            add(Mypost(R.drawable.mypage_mypost_image2, "간단한 루꼴라 샌드위치", "작성일자: 2024-03-12"))
            add(Mypost(R.drawable.mypage_mypost_basic_image, "완벽한 한끼 식사! 두부 유부초밥", "작성일자: 2024-02-24"))
            add(Mypost(R.drawable.mypage_mypost_image3, "냉장고 털이 계란 두부 카레덮밥", "작성일자: 2024-01-07"))
        }

        // 어댑터와 데이터 리스트(더미데이터) 연결
        filteredPosts.addAll(mypostDatas)
        mypostRVAdapter = MypostRVAdapter(filteredPosts)

        // 리사이클러뷰에 어댑터를 연결
        binding.mypostContentVp.adapter = mypostRVAdapter

        // 레이아웃 매니저 설정
        binding.mypostContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 리사이클러뷰에 간격 설정
        binding.mypostContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // 어댑터 클릭 리스너 설정
//        mypostRVAdapter.itemClickListner = object: MypostRVAdapter.OnItemClickListener{
//            // 내 게시물 프래그먼트로 이동 (지금은 임의 HomeFragment)
//            override fun onItemClick(view: View, position: Int) {
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, HomeFragment())
//                    .addToBackStack(null)
//                    .commit()
//
//                // 바텀 네비게이션의 선택 상태 변경
//                activity?.findViewById<BottomNavigationView>(R.id.main_bnv)?.selectedItemId = R.id.homeFragment
//            }
//        }

        // 백버튼 클릭 시
        binding.backButtonIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // SearchView 설정
        binding.mypostSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterPosts(query)
                return true
            }
        })

        return binding.root
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
}