package com.example.yorieter.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentCommentBinding
import com.example.yorieter.mypage.MyCommentFragment


class CommentFragment : Fragment()  {
    lateinit var binding: FragmentCommentBinding
    private var commentDatas = ArrayList<CommentData>()
    private lateinit var adapter : CommentRVAdapter

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

        //리사이클러 뷰에 연결
        val commentRVAdapter = CommentRVAdapter(commentDatas)

        binding.commentRv.adapter = commentRVAdapter

        binding.commentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        return binding.root
    }
}