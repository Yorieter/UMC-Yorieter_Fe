package com.example.yorieter.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.databinding.ItemMylikeBinding
import com.example.yorieter.mypage.dataclass.Mylike


class MylikeRVAdapter(private val mylikeList: ArrayList<Mylike>): RecyclerView.Adapter<MylikeRVAdapter.ViewHolder>() {

    interface OnItemClickListener { // 클릭리스너 인터페이스 생성
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListner: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MylikeRVAdapter.ViewHolder {
        val binding: ItemMylikeBinding = ItemMylikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MylikeRVAdapter.ViewHolder, position: Int) {
        holder.bind(mylikeList[position])
    }

    override fun getItemCount(): Int = mylikeList.size

    inner class ViewHolder(val binding: ItemMylikeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mylike: Mylike){
            binding.itemMylikeIv.setImageResource(mylike.coverImg!!)
            binding.itemMylikeTitleTv.text = mylike.title

            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }
        }
    }

}