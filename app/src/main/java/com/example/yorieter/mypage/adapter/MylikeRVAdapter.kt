package com.example.yorieter.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemMylikeBinding
import com.example.yorieter.mypage.dataclass.Mylike
import com.example.yorieter.mypage.viewModel.MyLikeViewModel


class MylikeRVAdapter(private val viewModel: MyLikeViewModel): RecyclerView.Adapter<MylikeRVAdapter.ViewHolder>() {

    private var mylikeList: List<Mylike> = emptyList()
    interface OnItemClickListener { // 클릭리스너 인터페이스 생성
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListner: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MylikeRVAdapter.ViewHolder {
        val binding: ItemMylikeBinding = ItemMylikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MylikeRVAdapter.ViewHolder, position: Int) {
        holder.bind(mylikeList[position], position)
    }

    override fun getItemCount(): Int = mylikeList.size

    inner class ViewHolder(val binding: ItemMylikeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mylike: Mylike, pos: Int){
            binding.itemMylikeIv.setImageResource(mylike.coverImg!!)
            binding.itemMylikeTitleTv.text = mylike.title

            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }

            // 좋아요 유무
            updateLikeIcon(binding.likeIv, mylike.isLiked)
            binding.likeIv.setOnClickListener {
                viewModel.clickMyLike(pos)
            }
        }

        private fun updateLikeIcon(likeIV: ImageView, isLiked: Boolean){
            if (isLiked){
                likeIV.setImageResource(R.drawable.like_check)
            } else {
                likeIV.setImageResource(R.drawable.like_no_check)
            }
        }
    }

    fun submitList(list: List<Mylike>){
        mylikeList = list
        notifyDataSetChanged()
    }

}