package com.example.yorieter.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentRecipeBinding
import com.example.yorieter.mypage.MyCommentFragment

class RecipeFragment : Fragment() {
    lateinit var binding: FragmentRecipeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)

//        binding.commentPageBtn.setOnClickListener {
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(this, FragmentComment)
//            transaction?.addToBackStack(null)
//            transaction?.commit()
//        }

        binding.commentPageBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_in_left,
                )
                .replace(R.id.main_frm, CommentFragment()) // 댓글 프래그먼트로 이동
                .addToBackStack(null) // 백 스택 추가
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}