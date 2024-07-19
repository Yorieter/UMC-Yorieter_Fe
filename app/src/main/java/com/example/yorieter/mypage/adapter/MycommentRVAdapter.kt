package com.example.yorieter.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.databinding.ItemMycommentBinding
import com.example.yorieter.mypage.data.Mycomment


class MycommentRVAdapter(private val mycommentList: ArrayList<Mycomment>): RecyclerView.Adapter<MycommentRVAdapter.ViewHolder>() {

    interface OnItemClickListener { // 클릭리스너 인터페이스 생성
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListner: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MycommentRVAdapter.ViewHolder {
        val binding: ItemMycommentBinding = ItemMycommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MycommentRVAdapter.ViewHolder, position: Int) {
        holder.bind(mycommentList[position])
    }

    override fun getItemCount(): Int = mycommentList.size

    inner class ViewHolder(val binding: ItemMycommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mycomment: Mycomment){
            binding.itemMycommentImgIv.setImageResource(mycomment.coverImg!!)
            binding.itemMycommentTv.text = mycomment.comment
            binding.mycommentDate.text = mycomment.date

            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }
        }
    }
}