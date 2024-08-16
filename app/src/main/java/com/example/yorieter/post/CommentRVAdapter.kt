package com.example.yorieter.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    override fun onBindViewHolder(holder: CommentRVAdapter.MyViewHolder, position: Int) {
        holder.bind(commentlist[position])
    }

    override fun getItemCount(): Int = commentlist.size

    inner class MyViewHolder(val binding: ItemRecipeCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(commentData: CommentData) {
            //binding.itemRecipeImg.=recipeData.recipe_image
            binding.itemCommentUserImg.setImageResource(commentData.comment_user_image!!)
            binding.commentUserName.text = commentData.comment_user_name
            binding.commentDate.text = commentData.comment_date
            binding.commentContent.text = commentData.comment_content
        }
    }
}