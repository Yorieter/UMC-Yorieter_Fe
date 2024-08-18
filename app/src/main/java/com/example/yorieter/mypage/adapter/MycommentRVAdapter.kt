package com.example.yorieter.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemMycommentBinding
import com.example.yorieter.mypage.dataclass.Mycomment


class MycommentRVAdapter(private val mycommentList: ArrayList<Mycomment>, private val onDeleteClick: (Int) -> Unit): RecyclerView.Adapter<MycommentRVAdapter.ViewHolder>() {

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

        // 삭제 버튼 클릭 이벤트 처리
        holder.binding.myCommentDelete.setOnClickListener {
            onDeleteClick(position) // 삭제 콜백 호출
        }
    }

    override fun getItemCount(): Int = mycommentList.size

    inner class ViewHolder(val binding: ItemMycommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mycomment: Mycomment){
            Glide.with(binding.itemMycommentImgIv.context)
                .load(mycomment.coverImg)
                .error(R.drawable.mypage_ic_yorieter_profile)
                .into(binding.itemMycommentImgIv)
            binding.itemMycommentTv.text = mycomment.comment
            binding.mycommentDate.text = mycomment.date

            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }
        }
    }
}