package com.example.yorieter.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemRecipeBinding
import com.example.yorieter.databinding.ItemRecipeCommentBinding

class CommentRVAdapter (private val commentlist : ArrayList<CommentData>) : RecyclerView.Adapter<CommentRVAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentRVAdapter.MyViewHolder {
        val binding: ItemRecipeCommentBinding =
            ItemRecipeCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }
    interface CommentItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    var mItemClickListener: CommentItemClickListener ? = null

    override fun onBindViewHolder(holder: CommentRVAdapter.MyViewHolder, position: Int) {
        holder.bind(commentlist[position])
    }

    override fun getItemCount(): Int = commentlist.size

    inner class MyViewHolder(val binding: ItemRecipeCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(commentData: CommentData) {
            //binding.itemRecipeImg.=recipeData.recipe_image
            //binding.itemCommentUserImg.setImageResource(commentData.comment_user_image!!)
            Glide.with(binding.itemCommentUserImg.context)
                .load(commentData.comment_user_image)
                .apply(RequestOptions()
                    .skipMemoryCache(true) // 메모리 캐시 무시
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 무시
                    .signature(ObjectKey(System.currentTimeMillis())) // 고유한 키를 사용하여 강제 리로드
                )
                .error(R.drawable.mypage_ic_yorieter_profile) // 기본 이미지
                .into(binding.itemCommentUserImg)

            binding.commentUserName.text = commentData.comment_user_name
            binding.commentDate.text = commentData.comment_date
            binding.commentContent.text = commentData.comment_content
        }
    }
}