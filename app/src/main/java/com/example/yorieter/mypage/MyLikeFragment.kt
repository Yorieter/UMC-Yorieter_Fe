package com.example.yorieter.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentMyLikeBinding
import com.example.yorieter.home.HomeFragment
import com.example.yorieter.mypage.adapter.DividerItemDecoration
import com.example.yorieter.mypage.adapter.MylikeRVAdapter
import com.example.yorieter.mypage.dataclass.Mylike
import com.example.yorieter.mypage.viewModel.MyLikeViewModel
import com.example.yorieter.post.RecipeFragment

class MyLikeFragment: Fragment() {

    private var mylikeDatas = ArrayList<Mylike>()

    lateinit var binding: FragmentMyLikeBinding

    private val mylikeViewModel: MyLikeViewModel by activityViewModels()

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
//        // 어댑터와 데이터 리스트(더미데이터) 연결
//        val mylikeRVAdapter = MylikeRVAdapter(mylikeDatas)
//
//        // 리사이클러뷰에 어댑터를 연결
//        binding.mylikeContentVp.adapter = mylikeRVAdapter
//
//        // GridLayoutManager 설정
//        val layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
//        binding.mylikeContentVp.layoutManager = layoutManager
//
//        // 리사이클러뷰에 간격 설정
//        binding.mylikeContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정
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

        // 백버튼 클릭 시
        binding.backButtonIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onStart(){
        super.onStart()
        initMyLikeRecyclerView()
    }

    private fun initMyLikeRecyclerView(){
        // viewModel에 어댑터를 연결
        val mylikeRVAdapter = MylikeRVAdapter(mylikeViewModel)

        // 리사이클러뷰에 어댑터를 연결
        binding.mylikeContentVp.adapter = mylikeRVAdapter
        binding.mylikeContentVp.setHasFixedSize(true)

        // GridLayoutManager 설정
        val layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        binding.mylikeContentVp.layoutManager = layoutManager

        // 리사이클러뷰에 간격 설정
        binding.mylikeContentVp.addItemDecoration(DividerItemDecoration(20)) // 20으로 설정

        // ViewModel의 데이터 변경을 관찰
        mylikeViewModel.mylikeList.observe(viewLifecycleOwner, { mylikeList ->
            mylikeRVAdapter.submitList(mylikeList)
        })

        // 어댑터 클릭 리스너 설정
        mylikeRVAdapter.itemClickListner = object: MylikeRVAdapter.OnItemClickListener{
            // 레시피 프래그먼트로 이동
            override fun onItemClick(view: View, position: Int) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RecipeFragment())
                    .addToBackStack(null)
                    .commit()

                // 바텀 네비게이션의 선택 상태 변경
                //activity?.findViewById<BottomNavigationView>(R.id.main_bnv)?.selectedItemId = R.id.homeFragment
            }
        }
    }
}