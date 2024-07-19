package com.example.yorieter.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.databinding.ItemMybookmarkBinding
import com.example.yorieter.mypage.data.Mybookmark


class MybookmarkRVAdapter(private val mybookmarkList: ArrayList<Mybookmark>): RecyclerView.Adapter<MybookmarkRVAdapter.ViewHolder>() {

    interface OnItemClickListener { // 클릭리스너 인터페이스 생성
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListner: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MybookmarkRVAdapter.ViewHolder {
        val binding: ItemMybookmarkBinding = ItemMybookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MybookmarkRVAdapter.ViewHolder, position: Int) {
        holder.bind(mybookmarkList[position])
    }

    override fun getItemCount(): Int = mybookmarkList.size

    inner class ViewHolder(val binding: ItemMybookmarkBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mybookmark: Mybookmark){
            binding.itemMybookmarkIv.setImageResource(mybookmark.coverImg!!)
            binding.itemMybookmarkTitleTv.text = mybookmark.title

            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }
        }
    }

}