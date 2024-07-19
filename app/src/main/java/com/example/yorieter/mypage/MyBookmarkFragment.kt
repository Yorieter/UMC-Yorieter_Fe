package com.example.yorieter.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyBookmarkBinding
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MybookmarkRVAdapter
import com.example.yorieter.mypage.data.Mybookmark

class MyBookmarkFragment: Fragment() {

    private var mybookmarkDatas = ArrayList<Mybookmark>()

    lateinit var binding: FragmentMyBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBookmarkBinding.inflate(inflater, container, false)

        // mybookmark 데이터 리스트 생성 (더미 데이터) --> API 받기 전까지 임시 더미데이터 생성
        mybookmarkDatas.apply {
            add(Mybookmark(R.drawable.mypage_bookmark_image1, "여름에 무조건 먹어야 하는 냉면"))
            add(Mybookmark(R.drawable.mypage_bookmark_image2, "지중해식 샐러드"))
            add(Mybookmark(R.drawable.mypage_bookmark_image1, "여름에 무조건 먹어야 하는 냉면"))
            add(Mybookmark(R.drawable.mypage_bookmark_image2, "지중해식 샐러드"))
        }

        // 어댑터와 데이터 리스트(더미데이터) 연결
        val mybookmarkRVAdapter = MybookmarkRVAdapter(mybookmarkDatas)

        // 리사이클러뷰에 어댑터를 연결
        binding.mybookmarkContentVp.adapter = mybookmarkRVAdapter

        // GridLayoutManager 설정
        val layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        binding.mybookmarkContentVp.layoutManager = layoutManager

        // 리사이클러뷰에 간격 설정
        binding.mybookmarkContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // 어댑터 클릭 리스너 설정
//        mybookmarkRVAdapter.itemClickListner = object: MybookmarkRVAdapter.OnItemClickListener{
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

        return binding.root
    }
}