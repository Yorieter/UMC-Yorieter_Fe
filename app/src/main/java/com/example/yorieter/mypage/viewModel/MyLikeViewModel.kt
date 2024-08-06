package com.example.yorieter.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yorieter.R
import com.example.yorieter.mypage.dataclass.Mylike

class MyLikeViewModel: ViewModel() {
    private val _mylikeList = MutableLiveData<List<Mylike>>()
    val mylikeList: LiveData<List<Mylike>> get() = _mylikeList

    init {
        // 초기 더미 데이터 설정
        val initialMyLike = mutableListOf(
            Mylike(R.drawable.mypage_bookmark_image1, "여름에 무조건 먹어야 하는 냉면", true),
            Mylike(R.drawable.mypage_bookmark_image2, "지중해식 샐러드", true),
            Mylike(R.drawable.mypage_bookmark_image1, "여름에 무조건 먹어야 하는 냉면", true),
            Mylike(R.drawable.mypage_bookmark_image2, "지중해식 샐러드", true)
        )
        _mylikeList.value = initialMyLike
    }

    fun clickMyLike(position: Int){
        _mylikeList.value = _mylikeList.value?.mapIndexed{ index, mylike ->
            if (index == position){
                mylike.copy(isLiked = !mylike.isLiked)
            } else {
                mylike
            }
        }
    }

}