package com.example.yorieter.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.databinding.ItemMypostBinding
import com.example.yorieter.mypage.dataclass.Mypost

class MypostRVAdapter(private val mypostList: ArrayList<Mypost>): RecyclerView.Adapter<MypostRVAdapter.ViewHolder>() {

    interface OnItemClickListener { // 클릭리스너 인터페이스 생성
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListner: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MypostRVAdapter.ViewHolder {
        val binding: ItemMypostBinding = ItemMypostBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MypostRVAdapter.ViewHolder, position: Int) {
        holder.bind(mypostList[position])
    }

    override fun getItemCount(): Int = mypostList.size

    inner class ViewHolder(val binding: ItemMypostBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mypost: Mypost){
            //binding.itemMypostImgIv.setImageResource(mypost.coverImg!!)
            binding.itemMypostTitleTv.text = mypost.title
            binding.mypostDate.text = mypost.date

            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }
        }
    }
}