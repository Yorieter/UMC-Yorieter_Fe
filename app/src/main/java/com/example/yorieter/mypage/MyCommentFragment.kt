package com.example.yorieter.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyCommentBinding
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MycommentRVAdapter
import com.example.yorieter.mypage.data.Mycomment

class MyCommentFragment: Fragment() {

    lateinit var binding: FragmentMyCommentBinding

    private var mycommentDatas = ArrayList<Mycomment>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommentBinding.inflate(inflater, container, false)

        // mycomment 데이터 리스트 생성 (더미 데이터) --> API 받기 전까지 임시 더미데이터 생성
        mycommentDatas.apply {
            add(Mycomment(R.drawable.mypage_ic_yorieter_profile, "너무 유용한 레시피 잘 보고 갑니다!", "작성일자: 2024-02-13"))
            add(Mycomment(R.drawable.mypage_ic_yorieter_profile, "집에 있는 재료들로 만들어 먹을 수 있는 요리여서 더 자주 만들어 먹게 되는 것 같", "작성일자: 2024-02-09"))
        }

        // 어댑터와 데이터 리스트(더미데이터) 연결
        val mycommentRVAdapter = MycommentRVAdapter(mycommentDatas)

        // 리사이클러뷰에 어댑터를 연결
        binding.mycommentContentVp.adapter = mycommentRVAdapter

        // 레이아웃 매니저 설정
        binding.mycommentContentVp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 리사이클러뷰에 간격 설정
        binding.mycommentContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // 어댑터 클릭 리스너 설정
//        mycommentRVAdapter.itemClickListner = object: MycommentRVAdapter.OnItemClickListener{
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